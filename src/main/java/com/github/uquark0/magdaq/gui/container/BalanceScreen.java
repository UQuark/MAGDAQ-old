package com.github.uquark0.magdaq.gui.container;

import com.github.uquark0.magdaq.economy.MoneyAmount;
import com.github.uquark0.magdaq.gui.common.Label;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class BalanceScreen {
    private Label lbBalance;

    private final int x, y;
    private final MinecraftClient client;
    private final DrawableHelper helper;
    private MoneyAmount lastBalance = null;

    public BalanceScreen(int x, int y, MinecraftClient client, DrawableHelper helper) {
        this.x = x;
        this.y = y;
        this.client = client;
        this.helper = helper;
    }

    public void render(MatrixStack matrices, MoneyAmount balance) {
        if (balance == lastBalance) {
            lbBalance.render(matrices);
            return;
        }

        lastBalance = balance;
        String text = String.format("BALANCE: %d.%02d", balance.getWhole(), balance.getFraction());
        lbBalance = new Label(x, y, text, 0xFFFFFF, client, helper);
        lbBalance.render(matrices);
    }
}
