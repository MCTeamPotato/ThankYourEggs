package me.kall.thankyoureggs;

import me.kall.thankyoureggs.config.EggConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ThankYourEggs.MOD_ID)
public final class ThankYourEggs {
    public static final String MOD_ID = "thankyoureggs";
    public static final String MOD_NAME = "ThankYourEggs";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public ThankYourEggs(IEventBus modBus, Dist dist, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, EggConfig.INSTANCE);
        modBus.addListener(EggConfig::setup);
    }
}
