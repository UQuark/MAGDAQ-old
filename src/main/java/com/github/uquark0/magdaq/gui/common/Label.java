package com.github.uquark0.magdaq.gui.common;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class Label {
    protected int x, y, color;
    protected String text;
    protected MinecraftClient client;
    protected DrawableHelper helper;

    public Label(int x, int y, String text, int color, MinecraftClient client, DrawableHelper helper) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
        this.client = client;
        this.helper = helper;
    }

    public void render(MatrixStack matrices) {
        helper.drawStringWithShadow(matrices, client.textRenderer, text, x, y, color);
    }

    public int getWidth() {
        return client.textRenderer.getWidth(text);
    }

    public int getHeight() {
        return client.textRenderer.fontHeight;
    }
}
