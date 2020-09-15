package com.github.uquark0.magdaq.gui.common;

import net.minecraft.client.MinecraftClient;

public class LatchButton extends Button {
    public LatchButton(int x, int y, int w, int h, MinecraftClient client) {
        super(x, y, w, h, client);
    }

    @Override
    public void onMousePressed() {
        pressed = !pressed;
        for (ButtonListener l : listeners)
            l.action(this);
    }

    @Override
    public void onMouseReleased() {}
}
