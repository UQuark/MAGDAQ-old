package com.github.uquark0.magdaq.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;

public class TradingTerminalScreenHandler extends ScreenHandler {
    protected TradingTerminalScreenHandler(int syncId) {
        super(ScreenHandlerTypeManager.TRADING_TERMINAL_SCREEN_HANDLER_TYPE, syncId);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
