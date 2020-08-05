package com.github.uquark0.magdaq.gui;

import com.github.uquark0.magdaq.Main;
import com.github.uquark0.magdaq.economy.MoneyAmount;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class TradingTerminalHandledScreen extends HandledScreen<TradingTerminalScreenHandler> {
    private static final int STOCK_BUTTON_X_OFFSET = 0;
    private static final int STOCK_BUTTON_Y_OFFSET = 0;
    private static final int STOCK_BUTTON_H = 18;
    private static final int STOCK_BUTTON_W = 18;
    private static final int STOCK_ICON_X_OFFSET = (STOCK_BUTTON_H - 16) / 2;
    private static final int STOCK_ICON_Y_OFFSET = (STOCK_BUTTON_W - 16) / 2;
    private static final int STOCK_BUTTON_PADDING = 2;
    private static final int PRINT_LINE_X_OFFSET = 192;
    private static final int PRINT_LINE_Y_OFFSET = 128;
    private static final int PRINT_LINE_PADDING = 2;
    private static final int PRINT_LINE_SPACE = 4;

    private static final Identifier INACTIVE_SLOT = new Identifier(Main.MODID, "textures/gui/container/trading_terminal_slot_inactive.png");
    private static final Identifier ACTIVE_SLOT = new Identifier(Main.MODID, "textures/gui/container/trading_terminal_slot_active.png");

    private StockButton[] stockButtons;
    private StockButton active;
    private PrintLine[] printLines;

    private class PrintLine {
        public final int x, y;
        public final MoneyAmount price;
        public final int amount;

        public PrintLine(int x, int y, long price, int amount) {
            this.x = x;
            this.y = y;
            this.price = new MoneyAmount(price);
            this.amount = amount;
        }

        public void render(MatrixStack matrices) {
            String p = String.format("%d.%d", price.getWhole(), price.getFraction());
            String a = String.format("%d", amount);
            int tab = client.textRenderer.getWidth("0.00");
            drawStringWithShadow(matrices, client.textRenderer, p, x, y, 0xFFFFFF);
            drawStringWithShadow(matrices, client.textRenderer, a, x + tab + PRINT_LINE_SPACE, y, 0xFFFFFF);
        }
    }

    private class StockButton {
        public final Item item;
        public final int x, y, w, h;
        public boolean isActive;

        public StockButton(Item item, int x, int y, int w, int h) {
            this.item = item;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public void render(MatrixStack matrices) {
            if (isActive)
                client.getTextureManager().bindTexture(ACTIVE_SLOT);
            else
                client.getTextureManager().bindTexture(INACTIVE_SLOT);
            drawTexture(matrices, x, y, 0, 0, w, h, w, h);
            client.getItemRenderer().renderInGuiWithOverrides(
                    item.getStackForRender(),
                    x+STOCK_ICON_X_OFFSET,
                    y+STOCK_ICON_Y_OFFSET
            );
        }

        public boolean contains(int cx, int cy) {
            return (cx >= x && cx <= x + w && cy >= y && cy <= y + h);
        }
    }

    public TradingTerminalHandledScreen(TradingTerminalScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 256;
        backgroundHeight = 192;
        requestStocks();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        renderBackground(matrices);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        if (stockButtons == null && handler.stocks != null)
            initializeStockButtons();
        if (printLines == null && handler.prints != null)
            initializePrintLines(4);

        if (stockButtons != null)
            for (StockButton s : stockButtons)
                s.render(matrices);
        if (printLines != null)
            for (PrintLine p : printLines)
                if (p != null)
                    p.render(matrices);
    }

    private void requestStocks() {
        stockButtons = null;
        handler.stocks = null;
        handler.getStocks();
    }

    private void requestPrints() {
        printLines = null;
        handler.prints = null;
        handler.getPrints(Registry.ITEM.getRawId(active.item));
    }

    private void initializeStockButtons() {
        stockButtons = new StockButton[handler.stocks.length];
        int y = STOCK_BUTTON_Y_OFFSET;
        for (int i = 0; i < handler.stocks.length; i++) {
            Item item = Registry.ITEM.get(handler.stocks[i]);
            stockButtons[i] = new StockButton(item, STOCK_BUTTON_X_OFFSET, y, STOCK_BUTTON_H, STOCK_BUTTON_W);
            y += STOCK_BUTTON_H + STOCK_BUTTON_PADDING;
        }
    }

    private void initializePrintLines(int crop) {
        printLines = new PrintLine[crop];
        int y = PRINT_LINE_Y_OFFSET;
        int start = Math.max(handler.prints.count-crop, 0);
        for (int i = start; i < handler.prints.count; i++) {
            printLines[i - start] = new PrintLine(PRINT_LINE_X_OFFSET, y, handler.prints.prices[i], handler.prints.amounts[i]);
            y += client.textRenderer.fontHeight + PRINT_LINE_PADDING;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (stockButtons == null)
            return false;
        int ox = width / 2 - backgroundWidth / 2;
        int oy = height / 2 - backgroundHeight / 2;
        int x = (int) mouseX - ox;
        int y = (int) mouseY - oy;
        for (StockButton s : stockButtons)
            if (s.contains(x, y)) {
                if (active != null)
                    active.isActive = false;
                s.isActive = true;
                active = s;
                requestPrints();
                return true;
            }
        return false;
    }

    @Override
    public void removed() {
        handler.unregister();
    }
}
