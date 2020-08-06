package com.github.uquark0.magdaq;

import com.github.uquark0.magdaq.gui.TradingTerminalHandledScreen;
import com.github.uquark0.magdaq.gui.TradingTerminalScreenHandler;
import net.fabricmc.api.ClientModInitializer;

public class Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TradingTerminalHandledScreen.register();
        TradingTerminalScreenHandler.registerS2CPackets();
    }
}
