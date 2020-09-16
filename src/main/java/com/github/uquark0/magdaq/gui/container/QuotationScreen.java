package com.github.uquark0.magdaq.gui.container;

import com.github.uquark0.magdaq.economy.Quotation;
import com.github.uquark0.magdaq.gui.common.Label;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;

public class QuotationScreen {
    private final static int ASK_PADDING = 4;
    private final static int QUOTATION_CROP = 5;
    private final static int QUOTATION_PADDING = 2;

    private final Label lbBid, lbAsk;
    private final int x, y, askPadding;
    private final MinecraftClient client;
    private final DrawableHelper helper;
    private Quotation lastQuotation = null;
    private ArrayList<Label> labels;

    public QuotationScreen(int x, int y, MinecraftClient client, DrawableHelper helper) {
        askPadding = client.textRenderer.getWidth("00.00 000") + ASK_PADDING;
        this.x = x;
        this.y = y;
        this.client = client;
        this.helper = helper;

        lbBid = new Label(x, y, "BID", 0xFFFFFF, client, helper);
        lbAsk = new Label(x + askPadding, y, "ASK", 0xFFFFFF, client, helper);
    }

    public void render(MatrixStack matrices, Quotation quotation) {
        lbBid.render(matrices);
        lbAsk.render(matrices);

        if (quotation == lastQuotation) {
            for (Label l : labels)
                l.render(matrices);
            return;
        }

        lastQuotation = quotation;
        labels = new ArrayList<>();

        int cy = y + lbBid.getHeight() + QUOTATION_PADDING;
        int drawn = 0;
        int i = 0;
        while (drawn < QUOTATION_CROP && i < quotation.bid.size()) {
            int groupSize = 0;
            int amount = 0;
            for (int j = i; j < quotation.bid.size(); j++) {
                if (quotation.bid.get(i).price.value != quotation.bid.get(j).price.value)
                    break;
                amount += quotation.bid.get(j).amount;
                groupSize++;
            }
            String text = String.format("%d.%02d %d", quotation.bid.get(i).price.getWhole(), quotation.bid.get(i).price.getFraction(), amount);
            Label label = new Label(x, cy, text, 0xFFFFFF, client, helper);
            label.render(matrices);
            labels.add(label);

            cy += label.getHeight() + QUOTATION_PADDING;
            drawn++;
            i += groupSize;
        }

        cy = y + lbAsk.getHeight() + QUOTATION_PADDING;
        drawn = 0;
        i = 0;
        while (drawn < QUOTATION_CROP && i < quotation.ask.size()) {
            int groupSize = 0;
            int amount = 0;
            for (int j = i; j < quotation.ask.size(); j++) {
                if (quotation.ask.get(i).price.value != quotation.ask.get(j).price.value)
                    break;
                amount += quotation.ask.get(j).amount;
                groupSize++;
            }
            String text = String.format("%d.%02d %d", quotation.ask.get(i).price.getWhole(), quotation.ask.get(i).price.getFraction(), amount);
            Label label = new Label(x + askPadding, cy, text, 0xFFFFFF, client, helper);
            label.render(matrices);
            labels.add(label);

            cy += label.getHeight() + QUOTATION_PADDING;
            drawn++;
            i += groupSize;
        }
    }
}
