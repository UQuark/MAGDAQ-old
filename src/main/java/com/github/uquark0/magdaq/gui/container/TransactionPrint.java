package com.github.uquark0.magdaq.gui.container;

import com.github.uquark0.magdaq.economy.Transaction;
import com.github.uquark0.magdaq.gui.common.Label;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class TransactionPrint {
    private static final int AMOUNT_PADDING = 4;

    private final Label lbPrice, lbAmount;

    public TransactionPrint(Transaction transaction, int x, int y, MinecraftClient client, DrawableHelper helper) {
        String price = String.format("%d.%02d", transaction.price.getWhole(), transaction.price.getFraction());
        String amount = String.format("%d", transaction.amount);
        lbPrice = new Label(x, y, price, 0xFFFFFF, client, helper);
        lbAmount = new Label(x + Math.max(client.textRenderer.getWidth("00.00"), lbPrice.getWidth()) + AMOUNT_PADDING, y, amount, 0xFFFFFF, client, helper);
    }

    public void render(MatrixStack matrices) {
        lbPrice.render(matrices);
        lbAmount.render(matrices);
    }

    public int getHeight() {
        return lbPrice.getHeight();
    }
}
