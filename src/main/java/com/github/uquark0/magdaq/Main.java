package com.github.uquark0.magdaq;

import com.github.uquark0.magdaq.block.BlockManager;
import com.github.uquark0.magdaq.block.entity.BlockEntityTypeManager;
import com.github.uquark0.magdaq.economy.Broker;
import com.github.uquark0.magdaq.economy.Market;
import com.github.uquark0.magdaq.economy.MoneyAmount;
import com.github.uquark0.magdaq.economy.order.BuyLimitOrder;
import com.github.uquark0.magdaq.economy.order.SellMarketOrder;
import com.github.uquark0.magdaq.gui.ScreenHandlerTypeManager;
import com.github.uquark0.magdaq.gui.TradingTerminalScreenHandler;
import com.github.uquark0.magdaq.util.Registrable;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements ModInitializer {
    public static final String MODID = "magdaq";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Market MARKET = new Market();

    @Override
    public void onInitialize() {
        Registrable[] toRegister = new Registrable[] {
                (Registrable) BlockManager.TRADING_TERMINAL_BLOCK
        };

        for (Registrable r : toRegister)
            r.register(LOGGER);

        BlockEntityTypeManager.registerAll();
        ScreenHandlerTypeManager.registerAll();
        TradingTerminalScreenHandler.registerC2SPackets();

        Broker fake = new Broker() {
            @Override
            public void reduceMoney(MoneyAmount amount) {

            }

            @Override
            public void increaseMoney(MoneyAmount amount) {

            }

            @Override
            public void reduceStock(Item stock, int amount) {

            }

            @Override
            public void increaseStock(Item stock, int amount) {

            }
        };

        MARKET.addStock(Items.DIAMOND);
        MARKET.getMarketMaker(Items.DIAMOND).putOrder(new BuyLimitOrder(10, new MoneyAmount(5, 11), fake, Items.DIAMOND));
        MARKET.getMarketMaker(Items.DIAMOND).putOrder(new BuyLimitOrder(15, new MoneyAmount(5, 22), fake, Items.DIAMOND));
        MARKET.getMarketMaker(Items.DIAMOND).putOrder(new BuyLimitOrder(5, new MoneyAmount(5, 33), fake, Items.DIAMOND));
        MARKET.getMarketMaker(Items.DIAMOND).putOrder(new BuyLimitOrder(4, new MoneyAmount(5, 0), fake, Items.DIAMOND));
        MARKET.getMarketMaker(Items.DIAMOND).putOrder(new BuyLimitOrder(12, new MoneyAmount(5, 22), fake, Items.DIAMOND));
        MARKET.getMarketMaker(Items.DIAMOND).putOrder(new BuyLimitOrder(5, new MoneyAmount(5, 44), fake, Items.DIAMOND));
        MARKET.getMarketMaker(Items.DIAMOND).putOrder(new BuyLimitOrder(11, new MoneyAmount(5, 11), fake, Items.DIAMOND));
        MARKET.getMarketMaker(Items.DIAMOND).putOrder(new SellMarketOrder(128, fake, Items.DIAMOND));
        MARKET.addStock(Items.IRON_INGOT);
        MARKET.getMarketMaker(Items.IRON_INGOT).putOrder(new BuyLimitOrder(5, new MoneyAmount(5, 11), fake, Items.IRON_INGOT));
        MARKET.getMarketMaker(Items.IRON_INGOT).putOrder(new SellMarketOrder(10, fake, Items.IRON_INGOT));
        MARKET.addStock(Items.GOLD_INGOT);
        MARKET.getMarketMaker(Items.GOLD_INGOT).putOrder(new BuyLimitOrder(1, new MoneyAmount(5, 11), fake, Items.GOLD_INGOT));
        MARKET.getMarketMaker(Items.GOLD_INGOT).putOrder(new SellMarketOrder(10, fake, Items.GOLD_INGOT));
    }
}
