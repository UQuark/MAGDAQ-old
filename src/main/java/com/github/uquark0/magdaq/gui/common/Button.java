package com.github.uquark0.magdaq.gui.common;

import com.github.uquark0.magdaq.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Button {
    private static final Identifier BUTTON_PRESSED_TEXTURE = new Identifier(Main.MODID, "textures/gui/common/button_pressed.png");
    private static final Identifier BUTTON_RELEASED_TEXTURE = new Identifier(Main.MODID, "textures/gui/common/button_released.png");

    protected int x, y, w, h;
    protected boolean pressed;
    protected MinecraftClient client;

    protected List<ButtonListener> listeners = new ArrayList<>();

    public Button(int x, int y, int w, int h, MinecraftClient client) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.client = client;
    }

    public boolean contains(int cx, int cy) {
        return (cx >= x && cx <= x + w && cy >= y && cy <= y + h);
    }

    public void render(MatrixStack matrices) {
        client.getTextureManager().bindTexture(pressed ? BUTTON_PRESSED_TEXTURE : BUTTON_RELEASED_TEXTURE);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, w, h, w, h);
    }

    public void onMousePressed() {
        pressed = true;
        for (ButtonListener l : listeners)
            l.action(this);
    }

    public void onMouseReleased() {
        pressed = false;
    }

    public void addOnClickListener(ButtonListener listener) {
        listeners.add(listener);
    }

    public boolean isPressed() {
        return pressed;
    }
}
