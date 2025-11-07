package me.kall.thankyoureggs.mixin;

import me.kall.thankyoureggs.api.EggLayer;
import me.kall.thankyoureggs.goal.LayEggGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(Chicken.class)
public abstract class ChickenMixin implements EggLayer {
    @Shadow public int eggTime;
    @Unique private boolean tye$wannaLayEgg;

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addLayEggGoal(CallbackInfo ci) {
        Chicken chicken = (Chicken) (Object) this;
        chicken.goalSelector.addGoal(5, new LayEggGoal(chicken));
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Chicken;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), cancellable = true)
    private void beforeLayEgg(@NotNull CallbackInfo ci) {
        this.tye$setWannaLayEgg(true);
        ci.cancel();
    }

    @Override
    public boolean tye$wannaLayEgg() {
        return this.tye$wannaLayEgg;
    }

    @Override
    public void tye$setWannaLayEgg(boolean wanna) {
        this.tye$wannaLayEgg = wanna;
    }

    @Override
    public void tye$layEgg() {
        Chicken chicken = (Chicken) (Object) this;
        this.eggTime = ThreadLocalRandom.current().nextInt(6000) + 6000;
        chicken.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (chicken.getRandom().nextFloat() - chicken.getRandom().nextFloat()) * 0.2F + 1.0F);
        chicken.spawnAtLocation(Items.EGG);
        chicken.gameEvent(GameEvent.ENTITY_PLACE);
        this.tye$setWannaLayEgg(false);
        chicken.getNavigation().stop();
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(@NotNull CompoundTag compound, CallbackInfo ci) {
        this.tye$wannaLayEgg = compound.getBoolean("WannaLayEgg");
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void write(@NotNull CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("WannaLayEgg", this.tye$wannaLayEgg);
    }
}
