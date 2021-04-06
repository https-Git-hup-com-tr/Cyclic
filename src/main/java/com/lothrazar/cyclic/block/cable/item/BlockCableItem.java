package com.lothrazar.cyclic.block.cable.item;

import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.cyclic.block.cable.CableBase;
import com.lothrazar.cyclic.block.cable.EnumConnectType;
import com.lothrazar.cyclic.block.cable.ShapeCache;
import com.lothrazar.cyclic.util.UtilItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockCableItem extends CableBase {

  public BlockCableItem(Properties properties) {
    super(properties.hardnessAndResistance(0.5F));
  }

  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    if (world.isRemote) {
      TileEntity ent = world.getTileEntity(pos);
      for (Direction d : Direction.values()) {
        IItemHandler handlerHere = ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, d).orElse(null);
        //show current
        if (handlerHere != null) {
          ItemStack current = handlerHere.getStackInSlot(0);
          if (!current.isEmpty()) {
            player.sendMessage(new TranslationTextComponent(d.toString() + " " + current.getDisplayName().getString()), player.getUniqueID());
          }
        }
      }
    }
    return super.onBlockActivated(state, world, pos, player, hand, hit);
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return ShapeCache.getOrCreate(state, CableBase::createShape);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock()) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      for (Direction d : Direction.values()) {
        IItemHandler items = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, d).orElse(null);
        UtilItemStack.dropAll(items, worldIn, pos);
      }
      worldIn.updateComparatorOutputLevel(pos, this);
      super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new TileCableItem();
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState stateIn, LivingEntity placer, ItemStack stack) {
    for (Direction d : Direction.values()) {
      TileEntity facingTile = worldIn.getTileEntity(pos.offset(d));
      IItemHandler cap = facingTile == null ? null : facingTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, d.getOpposite()).orElse(null);
      if (cap != null) {
        stateIn = stateIn.with(FACING_TO_PROPERTY_MAP.get(d), EnumConnectType.INVENTORY);
      }
    }
    worldIn.setBlockState(pos, stateIn);
  }

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    EnumProperty<EnumConnectType> property = FACING_TO_PROPERTY_MAP.get(facing);
    EnumConnectType oldProp = stateIn.get(property);
    if (oldProp.isBlocked() || oldProp.isExtraction()) {
      return stateIn;
    }
    if (isItem(stateIn, facing, facingState, world, currentPos, facingPos)) {
      ModCyclic.LOGGER.info(stateIn.get(property) + " to  inventory");
      return stateIn.with(property, EnumConnectType.INVENTORY);
    }
    else {
      ModCyclic.LOGGER.info(stateIn.get(property) + " to  none ");
      return stateIn.with(property, EnumConnectType.NONE);
    }
  }
}
