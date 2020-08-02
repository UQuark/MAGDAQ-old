package com.github.uquark0.magdaq.block;

import com.github.uquark0.magdaq.util.Registrable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Logger;

public abstract class AbstractHorizontalFacingBlock extends HorizontalFacingBlock implements Registrable {
    private final BlockItem item;
    private final Identifier id;

    protected AbstractHorizontalFacingBlock(Identifier id, Block.Settings blockSettings, Item.Settings itemSettings) {
        super(blockSettings);
        this.id = id;
        item = new BlockItem(this, itemSettings);
    }

    @Override
    public void register(Logger logger) {
        Registry.register(Registry.BLOCK, id, this);
        Registry.register(Registry.ITEM, id, item);
        logger.debug("Registered " + id.toString() + " and its BlockItem");
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }
}
