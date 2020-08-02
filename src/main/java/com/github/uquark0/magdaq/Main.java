package com.github.uquark0.magdaq;

import com.github.uquark0.magdaq.block.TradingTerminalBlock;
import com.github.uquark0.magdaq.util.Registrable;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements ModInitializer {
    public static final String MODID = "magdaq";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        Registrable[] toRegister = new Registrable[] {
                new TradingTerminalBlock(),
        };

        for (Registrable r : toRegister)
            r.register(LOGGER);
    }
}
