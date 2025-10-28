package me.kall.thankyoureggs.mixin;

import me.kall.thankyoureggs.api.ChickensLove;
import me.kall.thankyoureggs.data.ToLayEgg;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "onBlockStateChange", at = @At("HEAD"))
    private void onBlockChange(BlockPos pos, BlockState oldState, BlockState newState, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;

        ChickensLove oldBlock = (ChickensLove) oldState.getBlock();
        ChickensLove newBlock = (ChickensLove) newState.getBlock();

        ResourceLocation dim = level.dimension().location();
        long chunk = ChunkPos.asLong(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
        long block = pos.asLong();

        if (oldBlock.tye$isChickensLove()) {
            level.getServer().execute(() -> ToLayEgg.get(level).remove(dim, chunk, block));
        }

        if (newBlock.tye$isChickensLove()) {
            level.getServer().execute(() -> ToLayEgg.get(level).add(dim, chunk, block));
        }
    }
}
