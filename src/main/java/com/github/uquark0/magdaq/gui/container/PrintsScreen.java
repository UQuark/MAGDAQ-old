package com.github.uquark0.magdaq.gui.container;

import com.github.uquark0.magdaq.economy.Transaction;
import com.github.uquark0.magdaq.gui.common.Label;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class PrintsScreen {
    private final static int PRINT_CROP = 5;
    private final static int PRINT_PADDING = 2;

    private final Label lbPrints;
    private final int x, y;
    private final MinecraftClient client;
    private final DrawableHelper helper;

    public PrintsScreen(int x, int y, MinecraftClient client, DrawableHelper helper) {
        this.x = x;
        this.y = y;
        this.client = client;
        this.helper = helper;

        lbPrints = new Label(x, y, "PRINTS", 0xFFFFFF, client, helper);
    }

    public void render(MatrixStack matrices, List<Transaction> transactions) {
        lbPrints.render(matrices);
        int cy = y + lbPrints.getHeight() + PRINT_PADDING;
        for (int i = Math.max(transactions.size() - PRINT_CROP, 0); i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            String text = String.format("%d.%02d %d", t.price.getWhole(), t.price.getFraction(), t.amount);
            Label label = new Label(x, cy, text, 0xFFFFFF, client, helper);
            label.render(matrices);
            cy += label.getHeight() + PRINT_PADDING;
        }
    }
}
