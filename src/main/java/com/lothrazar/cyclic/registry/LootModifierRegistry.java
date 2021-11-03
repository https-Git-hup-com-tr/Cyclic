package com.lothrazar.cyclic.registry;

import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.cyclic.enchant.EnchantAutoSmelt;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LootModifierRegistry {

  @SubscribeEvent
  public static void registerModifierSerializers(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
    IForgeRegistry<GlobalLootModifierSerializer<?>> r = event.getRegistry();
    if (EnchantAutoSmelt.CFG.get()) {
      r.register(new EnchantAutoSmelt.Serializer().setRegistryName(ModCyclic.MODID + ":" + EnchantAutoSmelt.ID));
    }
  }
}
