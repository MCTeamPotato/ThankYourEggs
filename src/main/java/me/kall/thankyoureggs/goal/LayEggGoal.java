package me.kall.thankyoureggs.goal;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import me.kall.thankyoureggs.api.EggLayer;
import me.kall.thankyoureggs.config.EggConfig;
import me.kall.thankyoureggs.data.ToLayEgg;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class LayEggGoal extends Goal {
    private final Chicken chicken;
    private BlockPos target;

    public LayEggGoal(Chicken chicken) {
        this.chicken = chicken;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return ((EggLayer) this.chicken).tye$wannaLayEgg();
    }

    @Override
    public void start() {
        this.target = getTarget();
    }

    @Override
    public boolean canContinueToUse() {
        return ((EggLayer) this.chicken).tye$wannaLayEgg() && this.target != null && !this.chicken.getNavigation().isDone();
    }

    @Override
    public void tick() {
        if (this.target == null) return;
        this.chicken.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 1.0D);

        if (this.chicken.position().distanceToSqr(Vec3.atCenterOf(target)) < 2.0D) {
            this.chicken.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.chicken.getRandom().nextFloat() - this.chicken.getRandom().nextFloat()) * 0.2F + 1.0F);
            this.chicken.spawnAtLocation(Items.EGG);
            this.chicken.gameEvent(GameEvent.ENTITY_PLACE);
            this.chicken.eggTime = this.chicken.getRandom().nextInt(6000) + 6000;
            ((EggLayer) this.chicken).tye$setWannaLayEgg(false);
        }
    }

    private @Nullable BlockPos getTarget() {
        ServerLevel level = (ServerLevel) this.chicken.level;
        ResourceLocation dim = level.dimension().location();

        Long2ObjectMap<LongSet> dimMap = ToLayEgg.get(level).posMap().get(dim);
        if (dimMap == null || dimMap.isEmpty()) return null;

        ChunkPos chickenChunk = this.chicken.chunkPosition();
        int radius = EggConfig.RADIUS.get();

        for (int chunkX = chickenChunk.x - radius; chunkX <= chickenChunk.x + radius; chunkX++) {
            for (int chunkZ = chickenChunk.z - radius; chunkZ <= chickenChunk.z + radius; chunkZ++) {
                LongSet blocks = dimMap.get(ChunkPos.asLong(chunkX, chunkZ));
                if (blocks == null || blocks.isEmpty()) continue;
                return BlockPos.of(blocks.longIterator().nextLong());
            }
        }
        return null;
    }
}
