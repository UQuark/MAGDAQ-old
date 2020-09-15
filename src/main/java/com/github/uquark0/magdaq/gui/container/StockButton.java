package com.github.uquark0.magdaq.gui.container;

import com.github.uquark0.magdaq.gui.common.LatchButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;

public class StockButton extends LatchButton {
    public final Item item;

    public StockButton(Item item, int x, int y, int w, int h, MinecraftClient client) {
        super(x, y, w, h, client);
        this.item = item;
    }

    @Override
    public void render(MatrixStack matrices) {
        super.render(matrices);
        client.getItemRenderer().renderInGuiWithOverrides(item.getStackForRender(), x + (w - 16) / 2, y + (h - 16) / 2);
    }

    public void forcePop() {
        pressed = false;
    }

    public void forcePush() {
        pressed = true;
    }
}
