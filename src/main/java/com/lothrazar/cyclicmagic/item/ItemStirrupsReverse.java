package com.lothrazar.cyclicmagic.item;
import com.lothrazar.cyclicmagic.IHasRecipe;
import com.lothrazar.cyclicmagic.item.base.BaseTool;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemStirrupsReverse extends BaseTool implements IHasRecipe {
  public ItemStirrupsReverse() {
    super(100);
  }
  @Override
  public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
    World world = entity.getEntityWorld();
    if (player.isRidingOrBeingRiddenBy(entity) == false) {
      entity.startRiding(player, true);
      player.getPassengers().add(entity);
    }
    else {
      player.removePassengers();
    }
    super.onUse(stack, player, world, hand);
    return true;
  }
  @Override
  public IRecipe addRecipe() {
    return RecipeRegistry.addShapedRecipe(new ItemStack(this),
        "sl ",
        " ls",
        " ii",
        'l', Items.LEAD,
        'i', "ingotIron",
        's', "leather");
  }
}
