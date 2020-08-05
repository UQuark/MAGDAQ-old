package com.github.uquark0.magdaq.block;

import com.github.uquark0.magdaq.Main;
import com.github.uquark0.magdaq.block.entity.TradingTerminalBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TradingTerminalBlock extends AbstractHorizontalFacingBlock implements BlockEntityProvider {
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

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new TradingTerminalBlockEntity();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            player.openHandledScreen((TradingTerminalBlockEntity) world.getBlockEntity(pos));
            return ActionResult.CONSUME;
        }
    }
}
