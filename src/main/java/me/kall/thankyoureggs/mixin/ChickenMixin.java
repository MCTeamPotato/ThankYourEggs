package me.kall.thankyoureggs.mixin;

import me.kall.thankyoureggs.api.EggLayer;
import me.kall.thankyoureggs.goal.LayEggGoal;
import net.minecraft.world.entity.animal.Chicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chicken.class)
public abstract class ChickenMixin implements EggLayer {
    @Unique private boolean tye$wannaLayEgg;

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addLayEggGoal(CallbackInfo ci) {
        Chicken chicken = (Chicken) (Object) this;
        chicken.goalSelector.addGoal(5, new LayEggGoal(chicken));
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Chicken;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), cancellable = true)
    private void beforeLayEgg(CallbackInfo ci) {
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
}
