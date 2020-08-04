package com.github.uquark0.magdaq.gui;

import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class HandledScreenManager {
    public static TradingTerminalHandledScreen TRADING_TERMINAL_HANDLED_SCREEN;

    public static void registerAll() {
        ScreenRegistry.register(
                ScreenHandlerTypeManager.TRADING_TERMINAL_SCREEN_HANDLER_TYPE,
                TradingTerminalHandledScreen::new
        );
    }
}
