package com.sweetmimike.perfectmobfarm.block;

import com.sweetmimike.perfectmobfarm.PerfectMobFarm;
import com.sweetmimike.perfectmobfarm.item.ItemManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Manager for the mod blocks
 */
public class BlockManager {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PerfectMobFarm.MODID);

    public static final RegistryObject<Block> IRON_MOB_FARM = registerBlock("iron_mob_farm",
            () -> new IronMobFarm(BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops().strength(5f).noOcclusion()));

    public static final RegistryObject<Block> GOLD_MOB_FARM = registerBlock("gold_mob_farm",
            () -> new GoldMobFarm(BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops().strength(5f).noOcclusion()));

    public static final RegistryObject<Block> DIAMOND_MOB_FARM = registerBlock("diamond_mob_farm",
            () -> new DiamondMobFarm(BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops().strength(5f).noOcclusion()));

    public static final RegistryObject<Block> EMERALD_MOB_FARM = registerBlock("emerald_mob_farm",
            () -> new EmeraldMobFarm(BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops().strength(5f).noOcclusion()));

    public static final RegistryObject<Block> NETHERITE_MOB_FARM = registerBlock("netherite_mob_farm",
            () -> new NetheriteMobFarm(BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops().strength(5f).noOcclusion().explosionResistance(1200F)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, PerfectMobFarm.PERFECT_MOB_FARM_TAB);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab) {
        return ItemManager.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
