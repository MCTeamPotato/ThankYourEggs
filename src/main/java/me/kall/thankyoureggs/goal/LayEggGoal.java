package me.kall.thankyoureggs.goal;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import me.kall.thankyoureggs.api.ChickensLove;
import me.kall.thankyoureggs.api.EggLayer;
import me.kall.thankyoureggs.config.EggConfig;
import me.kall.thankyoureggs.data.ToLayEgg;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class LayEggGoal extends Goal {
    private final Mob eggLayer;
    private BlockPos target;
    private long timeout;

    public LayEggGoal(Mob eggLayer) {
        this.eggLayer = eggLayer;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return ((EggLayer) this.eggLayer).tye$wannaLayEgg();
    }

    @Override
    public void start() {
        this.target = getTarget();
        this.timeout = System.currentTimeMillis() + 30_000;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void tick() {
        if (this.target == null) return;
        this.eggLayer.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 1.0D);

        if (this.eggLayer.position().distanceToSqr(Vec3.atCenterOf(target)) < 2.0D || System.currentTimeMillis() >= this.timeout) {
            ((EggLayer) this.eggLayer).tye$layEgg();
            this.target = null;
        }
    }

    private @Nullable BlockPos getTarget() {
        ServerLevel level = (ServerLevel) this.eggLayer.level();
        ResourceLocation dim = level.dimension().location();

        ToLayEgg data = ToLayEgg.get(level);
        Long2ObjectMap<LongSet> dimMap = data.posMap().get(dim);
        if (dimMap == null || dimMap.isEmpty()) return null;

        ChunkPos chickenChunk = this.eggLayer.chunkPosition();
        int radius = EggConfig.RADIUS.get();

        for (int chunkX = chickenChunk.x - radius; chunkX <= chickenChunk.x + radius; chunkX++) {
            for (int chunkZ = chickenChunk.z - radius; chunkZ <= chickenChunk.z + radius; chunkZ++) {
                long chunk = ChunkPos.asLong(chunkX, chunkZ);
                LongSet blocks = dimMap.get(ChunkPos.asLong(chunkX, chunkZ));
                if (blocks == null || blocks.isEmpty()) continue;
                long block = blocks.longIterator().nextLong();
                BlockPos candidate = BlockPos.of(block);
                if (((ChickensLove)level.getBlockState(candidate).getBlock()).tye$isChickensLove()) {
                    return candidate;
                } else {
                    data.remove(dim, chunk, block);
                }
            }
        }
        return null;
    }
}
