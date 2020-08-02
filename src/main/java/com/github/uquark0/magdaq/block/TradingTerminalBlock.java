package com.github.uquark0.magdaq.block;

import com.github.uquark0.magdaq.Main;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public class TradingTerminalBlock extends AbstractHorizontalFacingBlock {
    public static final Identifier ID = new Identifier(Main.MODID, "trading_terminal");
    public static final Block.Settings BLOCK_SETTINGS = Block.Settings
            .of(Material.STONE)
            .nonOpaque()
            .strength(1.5f, 6)
            .lightLevel(blockState -> 7);
    public static final Item.Settings ITEM_SETTINGS = new Item.Settings()
            .maxCount(1)
            .group(ItemGroup.REDSTONE);

    public TradingTerminalBlock() {
        super(ID, BLOCK_SETTINGS, ITEM_SETTINGS);
    }
}
