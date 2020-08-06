package com.github.uquark0.magdaq.gui;

import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class TradingTerminalHandledScreen extends HandledScreen<TradingTerminalScreenHandler> {
    public static void register() {
        ScreenRegistry.register(
                TradingTerminalScreenHandler.TRADING_TERMINAL_SCREEN_HANDLER_TYPE,
                TradingTerminalHandledScreen::new
        );
    }

    public TradingTerminalHandledScreen(TradingTerminalScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        handler.requestStocks();
        handler.requestTransactions(Items.DIAMOND);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        renderBackground(matrices);
    }
}
