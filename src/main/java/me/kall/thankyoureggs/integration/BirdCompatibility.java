package me.kall.thankyoureggs.integration;

import me.kall.thankyoureggs.goal.LayEggGoal;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.pavocado.exoticbirds.entity.AbstractBirdEntity;
import org.jetbrains.annotations.NotNull;

public class BirdCompatibility {
    public static void onBirdJoin(@NotNull EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AbstractBirdEntity bird && !event.isCanceled() && !bird.level().isClientSide()) {
            bird.goalSelector.addGoal(5, new LayEggGoal(bird));
        }
    }
}
