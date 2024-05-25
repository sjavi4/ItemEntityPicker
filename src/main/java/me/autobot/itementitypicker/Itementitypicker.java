package me.autobot.itementitypicker;

import me.autobot.itementitypicker.config.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Itementitypicker implements ModInitializer {
    public static final String MOD_ID = "ITEMENTITYPICKER";
    public static final Logger LOGGER = LoggerFactory.getLogger("ITEMENTITYPICKER");
    public static FabricLoaderImpl LOADER;
    @Override
    public void onInitialize() {
        LOGGER.info("Hello World.");
        LOADER = FabricLoaderImpl.INSTANCE;
        if (LOADER.getEnvironmentType() == EnvType.SERVER) {
            LOGGER.warn("Not A Server Side Mod!");
        }
        LOGGER.info("Config Dir: {}",  LOADER.getConfigDir());
        ConfigManager.loadConfig();
    }
}
