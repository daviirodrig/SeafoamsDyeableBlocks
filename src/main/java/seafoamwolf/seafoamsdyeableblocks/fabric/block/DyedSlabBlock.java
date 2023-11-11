package seafoamwolf.seafoamsdyeableblocks.fabric.block;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.BlockView;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class DyedSlabBlock extends SlabBlock implements DyedBlockInterface {
    public DyedSlabBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public DyedSlabBlock(Block baseBlock) {
		super(FabricBlockSettings.copyOf(baseBlock));
	}

    public void onPlaced(World world, BlockPos pos, BlockState blockState, LivingEntity entity, ItemStack item) {
        dyedOnPlaced(world, pos, blockState, entity, item);
		super.onPlaced(world, pos, blockState, entity, item);
	}
    
	public static void dropStacks(BlockState state, World world, BlockPos pos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack2) {
        if (world instanceof ServerWorld) {
            DyedBlockEntity dyeableBlockEntity = (DyedBlockEntity)blockEntity;
            Block droppedBlock = dyeableBlockEntity.getOriginalBlock();
            ItemStack droppedStack;

            if (droppedBlock != null) {
                droppedStack = new ItemStack(droppedBlock);
            } else {
                droppedStack = new ItemStack(state.getBlock());
                droppedStack = dyeableBlockEntity.ApplyNBT(droppedStack);
            }

            dropStack(world, pos, droppedStack);
            state.onStacksDropped((ServerWorld)world, pos, stack2, false);
        }
    }

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        dyedAfterBreak(world, player, pos, state, blockEntity, stack);

        // Drops

        ItemStack droppedStack = getItemStack(state, world, pos, blockEntity, player, stack);
        
        dropStack(world, pos, droppedStack);
        state.onStacksDropped((ServerWorld)world, pos, stack, false);
    }

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return dyedGetPickStack(world, pos, state);
    }

	@Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return dyedCreateBlockEntity(pos, state);
    }

	@Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return dyedOnUse(state, world, pos, player, hand, hit);
    }
}