package com.github.uquark0.magdaq.gui;

import com.github.uquark0.magdaq.block.TradingTerminalBlock;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

public class ScreenHandlerTypeManager {
    public static ScreenHandlerType<TradingTerminalScreenHandler> TRADING_TERMINAL_SCREEN_HANDLER_TYPE;

    public static void registerAll() {
        TRADING_TERMINAL_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(
                TradingTerminalBlock.ID,
                (i, playerInventory) -> new TradingTerminalScreenHandler(i, null)
        );
    }
}
