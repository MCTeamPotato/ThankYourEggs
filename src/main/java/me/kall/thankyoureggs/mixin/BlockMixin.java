package me.kall.thankyoureggs.mixin;

import me.kall.thankyoureggs.api.ChickensLove;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
public class BlockMixin implements ChickensLove {
    @Unique private boolean tye$isChickensLove;

    @Override
    public boolean tye$isChickensLove() {
        return this.tye$isChickensLove;
    }

    @Override
    public void tye$setIsChickensLove(boolean chickenLove) {
        this.tye$isChickensLove = chickenLove;
    }
}
