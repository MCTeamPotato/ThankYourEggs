package me.kall.thankyoureggs;

import me.kall.thankyoureggs.config.EggConfig;
import me.kall.thankyoureggs.integration.BirdCompatibility;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ThankYourEggs.MOD_ID)
public final class ThankYourEggs {
    public static final String MOD_ID = "thankyoureggs";
    public static final String MOD_NAME = "ThankYourEggs";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public ThankYourEggs(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, EggConfig.INSTANCE);
        context.getModEventBus().addListener(EggConfig::setup);
        if (FMLLoader.getLoadingModList().getModFileById("exoticbirds") != null) {
            MinecraftForge.EVENT_BUS.addListener(BirdCompatibility::onBirdJoin);
        }
    }
}
