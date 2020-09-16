package com.github.uquark0.magdaq;

import com.github.uquark0.magdaq.block.BlockManager;
import com.github.uquark0.magdaq.block.entity.BlockEntityTypeManager;
import com.github.uquark0.magdaq.economy.Broker;
import com.github.uquark0.magdaq.economy.Market;
import com.github.uquark0.magdaq.economy.MoneyAmount;
import com.github.uquark0.magdaq.economy.order.BuyLimitOrder;
import com.github.uquark0.magdaq.economy.order.BuyMarketOrder;
import com.github.uquark0.magdaq.economy.order.Order;
import com.github.uquark0.magdaq.economy.order.SellLimitOrder;
import com.github.uquark0.magdaq.gui.container.TradingTerminalScreenHandler;
import com.github.uquark0.magdaq.util.Registrable;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class Main implements ModInitializer {
    public static final String MODID = "magdaq";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Market MARKET = new Market();
    public static final Random RANDOM = new Random();

    @Override
    public void onInitialize() {
        Registrable[] toRegister = new Registrable[] {
                (Registrable) BlockManager.TRADING_TERMINAL_BLOCK
        };

        for (Registrable r : toRegister)
            r.register(LOGGER);

        BlockEntityTypeManager.registerAll();
        TradingTerminalScreenHandler.register();
        TradingTerminalScreenHandler.registerC2SPackets();
        MARKET.addStock(Items.DIAMOND);
        MARKET.addStock(Items.GOLD_INGOT);
        MARKET.addStock(Items.IRON_INGOT);
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

        new Thread(() -> {
            while (true) {
                Order order;
                int amount = Math.abs(RANDOM.nextInt()) % 32;
                MoneyAmount price = new MoneyAmount(6, 50 + Math.abs(RANDOM.nextInt()) % 20);
                if (RANDOM.nextBoolean())
                    order = new BuyLimitOrder(amount, price, fake, Items.DIAMOND);
                else
                    order = new SellLimitOrder(amount, price, fake, Items.DIAMOND);
                MARKET.getMarketMaker(Items.DIAMOND).putOrder(order);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
