package me.kall.thankyoureggs.config;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import me.kall.thankyoureggs.api.ChickensLove;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EggConfig {
    public static final ForgeConfigSpec INSTANCE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> CHICKENS_LOVE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("ThankYourEggs");
        CHICKENS_LOVE = builder.defineList("BlocksThatChickensLove", Lists.newArrayList("create:belt"), Predicates.alwaysTrue());
        builder.pop();
        INSTANCE = builder.build();
    }

    public static void setup(@NotNull FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Set<ResourceLocation> chickensLove = CHICKENS_LOVE.get().stream().map(ResourceLocation::parse).collect(Collectors.toSet());
            for (Map.Entry<ResourceKey<Block>, Block> entry : ForgeRegistries.BLOCKS.getEntries()) {
                ((ChickensLove)entry.getValue()).tye$setIsChickensLove(chickensLove.contains(entry.getKey().location()));
            }
        });
    }
}
