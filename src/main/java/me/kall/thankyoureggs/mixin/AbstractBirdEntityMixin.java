package me.kall.thankyoureggs.mixin;

import me.kall.thankyoureggs.api.EggLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.pavocado.exoticbirds.entity.AbstractBirdEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBirdEntity.class)
public class AbstractBirdEntityMixin implements EggLayer {
    @Unique
    private boolean tye$wannaLayEgg;

    @Override
    public boolean tye$wannaLayEgg() {
        return this.tye$wannaLayEgg;
    }

    @Inject(method = "customServerAiStep", at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraftforge/common/ForgeConfigSpec$BooleanValue;get()Ljava/lang/Object;", shift = At.Shift.AFTER), cancellable = true)
    private void onLayEgg(@NotNull CallbackInfo ci) {
        this.tye$setWannaLayEgg(true);
        ci.cancel();
    }

    @Override
    public void tye$setWannaLayEgg(boolean wanna) {
        this.tye$wannaLayEgg = wanna;
    }

    @Override
    public void tye$layEgg() {
        AbstractBirdEntity entity = (AbstractBirdEntity) (Object) this;
        entity.spawnAtLocation(entity.layEgg());
        entity.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F + 1.0F);
        entity.getNavigation().stop();
        this.tye$setWannaLayEgg(false);
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
