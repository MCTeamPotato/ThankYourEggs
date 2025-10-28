package me.kall.thankyoureggs.config;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import me.kall.thankyoureggs.api.ChickensLove;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EggConfig {
    public static final ModConfigSpec INSTANCE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CHICKENS_LOVE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("ThankYourEggs");
        CHICKENS_LOVE = builder.defineList("BlocksThatChickensLove", Lists.newArrayList("create:belt"), () -> "block:registry_name", Predicates.alwaysTrue());
        builder.pop();
        INSTANCE = builder.build();
    }

    public static void setup(@NotNull FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Set<ResourceLocation> chickensLove = CHICKENS_LOVE.get().stream().map(ResourceLocation::parse).collect(Collectors.toSet());
            for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
                ((ChickensLove)entry.getValue()).tye$setIsChickensLove(chickensLove.contains(entry.getKey().location()));
            }
        });
    }
}
