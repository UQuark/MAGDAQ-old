package com.github.uquark0.magdaq.block.entity;

import com.github.uquark0.magdaq.gui.ScreenHandlerTypeManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TradingTerminalBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    public TradingTerminalBlockEntity() {
        super(BlockEntityTypeManager.TRADING_TERMINAL_BLOCK_ENTITY_TYPE);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("gui.trading_terminal.title");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return ScreenHandlerTypeManager.TRADING_TERMINAL_SCREEN_HANDLER_TYPE.create(syncId, inv);
    }
}
