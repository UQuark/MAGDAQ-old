package com.github.uquark0.magdaq.gui.container;

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

    private static final int PRINTS_X_OFFSET = 216;
    private static final int PRINTS_Y_OFFSET = 152;

    private static final int QUOTATION_X_OFFSET = 116;
    private static final int QUOTATION_Y_OFFSET = 152;

    private static final int BALANCE_X_OFFSET = 216;
    private static final int BALANCE_Y_OFFSET = 0;

    public static void register() {
        ScreenRegistry.register(
                TradingTerminalScreenHandler.TRADING_TERMINAL_SCREEN_HANDLER_TYPE,
                TradingTerminalHandledScreen::new
        );
    }

    private List<StockButton> stockButtons;
    private PrintsScreen printsScreen;
    private QuotationScreen quotationScreen;
    private BalanceScreen balanceScreen;
    private StockButton active;

    public TradingTerminalHandledScreen(TradingTerminalScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 256;
        backgroundHeight = 192;
        handler.requestStocks();
        handler.requestBalance();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        renderBackground(matrices);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        drawStocks(matrices);
        drawPrints(matrices);
        drawQuotation(matrices);
        drawBalance(matrices);
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
        if (printsScreen == null)
            initTransactionPrints();
        printsScreen.render(matrices, handler.transactions);
    }

    private void initTransactionPrints() {
        printsScreen = new PrintsScreen(PRINTS_X_OFFSET, PRINTS_Y_OFFSET, client, this);
    }

    private void drawQuotation(MatrixStack matrices) {
        if (active == null)
            return;
        if (handler.quotation == null)
            return;
        if (quotationScreen == null)
            initQuotationScreen();
        quotationScreen.render(matrices, handler.quotation);
    }

    private void initQuotationScreen() {
        quotationScreen = new QuotationScreen(QUOTATION_X_OFFSET, QUOTATION_Y_OFFSET, client, this);
    }

    private void drawBalance(MatrixStack matrices) {
        if (handler.balance == null)
            return;
        if (balanceScreen == null)
            initBalanceScreen();
        balanceScreen.render(matrices, handler.balance);
    }

    private void initBalanceScreen() {
        balanceScreen = new BalanceScreen(BALANCE_X_OFFSET, BALANCE_Y_OFFSET, client, this);
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
            printsScreen = null;
            quotationScreen = null;
        }
        active = (StockButton) newActive;
        if (active != null) {
            handler.requestTransactions(active.item);
            handler.requestQuotation(active.item);
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
