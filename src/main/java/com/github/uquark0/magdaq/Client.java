package com.github.uquark0.magdaq;

import com.github.uquark0.magdaq.gui.HandledScreenManager;
import com.github.uquark0.magdaq.gui.TradingTerminalScreenHandler;
import net.fabricmc.api.ClientModInitializer;

public class Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreenManager.registerAll();
        TradingTerminalScreenHandler.registerS2CPackets();
    }
}
