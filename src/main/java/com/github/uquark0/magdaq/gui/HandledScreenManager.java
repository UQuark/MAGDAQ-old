package com.github.uquark0.magdaq.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(EnvType.CLIENT)
public class HandledScreenManager {
    public static void registerAll() {
        ScreenRegistry.register(
                ScreenHandlerTypeManager.TRADING_TERMINAL_SCREEN_HANDLER_TYPE,
                TradingTerminalHandledScreen::new
        );
    }
}
