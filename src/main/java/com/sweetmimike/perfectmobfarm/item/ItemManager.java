package com.sweetmimike.perfectmobfarm.item;

import com.sweetmimike.perfectmobfarm.PerfectMobFarm;
import com.sweetmimike.perfectmobfarm.config.CommonConfigs;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Manager for the mod items
 */
public class ItemManager {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PerfectMobFarm.MODID);

    public static final RegistryObject<Item> MOB_SHARD = ITEMS.register("mob_shard",
            () -> new MobShard(new Item.Properties().tab(PerfectMobFarm.PERFECT_MOB_FARM_TAB).durability(256)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
