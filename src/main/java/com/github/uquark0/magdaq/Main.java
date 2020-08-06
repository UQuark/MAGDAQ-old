package com.github.uquark0.magdaq;

import com.github.uquark0.magdaq.block.BlockManager;
import com.github.uquark0.magdaq.block.entity.BlockEntityTypeManager;
import com.github.uquark0.magdaq.economy.Broker;
import com.github.uquark0.magdaq.economy.Market;
import com.github.uquark0.magdaq.economy.MoneyAmount;
import com.github.uquark0.magdaq.economy.order.BuyMarketOrder;
import com.github.uquark0.magdaq.economy.order.SellLimitOrder;
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
        MARKET.getMarketMaker(Items.DIAMOND).putOrder(new SellLimitOrder(15, new MoneyAmount(5, 1), fake, Items.DIAMOND));
        MARKET.getMarketMaker(Items.DIAMOND).putOrder(new SellLimitOrder(7, new MoneyAmount(5, 2), fake, Items.DIAMOND));
        MARKET.getMarketMaker(Items.DIAMOND).putOrder(new SellLimitOrder(21, new MoneyAmount(5, 0), fake, Items.DIAMOND));
        MARKET.getMarketMaker(Items.DIAMOND).putOrder(new BuyMarketOrder(64, fake, Items.DIAMOND));
    }
}
