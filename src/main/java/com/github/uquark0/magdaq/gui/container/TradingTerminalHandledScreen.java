package com.github.uquark0.magdaq.gui.container;

import com.github.uquark0.magdaq.economy.Transaction;
import com.github.uquark0.magdaq.gui.common.Button;
import com.github.uquark0.magdaq.gui.common.ButtonListener;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TradingTerminalHandledScreen extends HandledScreen<TradingTerminalScreenHandler> implements ButtonListener {
    private static final int STOCK_BUTTONS_X_OFFSET = 0;
    private static final int STOCK_BUTTONS_Y_OFFSET = 0;
    private static final int STOCK_BUTTONS_W = 20;
    private static final int STOCK_BUTTONS_H = 20;
    private static final int STOCK_BUTTONS_PADDING = 2;

    private static final int TRANSACTION_PRINTS_X_OFFSET = 216;
    private static final int TRANSACTION_PRINTS_Y_OFFSET = 152;
    private static final int TRANSACTION_PRINTS_PADDING = 2;
    private static final int TRANSACTION_PRINTS_CROP = 6;

    public static void register() {
        ScreenRegistry.register(
                TradingTerminalScreenHandler.TRADING_TERMINAL_SCREEN_HANDLER_TYPE,
                TradingTerminalHandledScreen::new
        );
    }

    private List<StockButton> stockButtons;
    private List<TransactionPrint> transactionPrints;
    private StockButton active;

    public TradingTerminalHandledScreen(TradingTerminalScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 256;
        backgroundHeight = 192;
        handler.requestStocks();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        renderBackground(matrices);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        drawStocks(matrices);
        drawPrints(matrices);
    }

    private void drawStocks(MatrixStack matrices) {
        if (handler.stocks == null)
            return;
        if (stockButtons == null)
            initStockButtons();
        for (StockButton b : stockButtons)
            b.render(matrices);
    }

    private void initStockButtons() {
        stockButtons = new ArrayList<>();
        int y = STOCK_BUTTONS_Y_OFFSET;
        for (Item i : handler.stocks) {
            StockButton b = new StockButton(i, STOCK_BUTTONS_X_OFFSET, y, STOCK_BUTTONS_W, STOCK_BUTTONS_H, client);
            b.addOnClickListener(this);
            y += STOCK_BUTTONS_H + STOCK_BUTTONS_PADDING;
            stockButtons.add(b);
        }
    }

    private void drawPrints(MatrixStack matrices) {
        if (active == null)
            return;
        if (handler.transactions == null)
            return;
        if (handler.popRefreshTransactions() || transactionPrints == null)
            initTransactionPrints();
        for (TransactionPrint p : transactionPrints)
            p.render(matrices);
    }

    private void initTransactionPrints() {
        transactionPrints = new ArrayList<>();
        int y = TRANSACTION_PRINTS_Y_OFFSET;
        for (int i = Math.max(handler.transactions.size() - TRANSACTION_PRINTS_CROP, 0); i < handler.transactions.size(); i++) {
            Transaction t = handler.transactions.get(i);
            TransactionPrint p = new TransactionPrint(t, TRANSACTION_PRINTS_X_OFFSET, y, client, this);
            y += p.getHeight() + TRANSACTION_PRINTS_PADDING;
            transactionPrints.add(p);
        }
    }

    private void initQuotationScreen() {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (stockButtons == null)
            return false;

        int ox = width / 2 - backgroundWidth / 2;
        int oy = height / 2 - backgroundHeight / 2;
        int x = (int) mouseX - ox;
        int y = (int) mouseY - oy;

        for (StockButton b : stockButtons)
            if (b.contains(x, y))
                b.onMousePressed();

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (stockButtons == null)
            return false;

        int ox = width / 2 - backgroundWidth / 2;
        int oy = height / 2 - backgroundHeight / 2;
        int x = (int) mouseX - ox;
        int y = (int) mouseY - oy;

        for (StockButton b : stockButtons)
            if (b.contains(x, y))
                b.onMouseReleased();

        return true;
    }

    @Override
    public void removed() {
        handler.onClose();
    }

    private void updateActive(Button newActive) {
        if (active != null) {
            active.forcePop();
            handler.unsubscribe(active.item);
            transactionPrints = null;
        }
        active = (StockButton) newActive;
        if (active != null) {
            handler.requestTransactions(active.item);
            handler.subscribe(active.item);
        }
    }

    @Override
    public void action(Button sender) {
        if (sender instanceof StockButton)
            if (sender.isPressed())
                updateActive(sender);
            else
                updateActive(null);
    }
}
