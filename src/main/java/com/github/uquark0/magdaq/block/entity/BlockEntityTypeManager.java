package com.github.uquark0.magdaq.block.entity;

import com.github.uquark0.magdaq.block.BlockManager;
import com.github.uquark0.magdaq.block.TradingTerminalBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class BlockEntityTypeManager {
    public static BlockEntityType<TradingTerminalBlockEntity> TRADING_TERMINAL_BLOCK_ENTITY_TYPE;

    public static void registerAll() {
        TRADING_TERMINAL_BLOCK_ENTITY_TYPE = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                TradingTerminalBlock.ID,
                BlockEntityType.Builder.create(TradingTerminalBlockEntity::new, BlockManager.TRADING_TERMINAL_BLOCK).build(null)
        );
    }
}
