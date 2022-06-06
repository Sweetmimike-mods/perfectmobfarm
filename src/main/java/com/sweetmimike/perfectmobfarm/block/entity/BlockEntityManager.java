package com.sweetmimike.perfectmobfarm.block.entity;

import com.sweetmimike.perfectmobfarm.PerfectMobFarm;
import com.sweetmimike.perfectmobfarm.block.BlockManager;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Manager for the mod block entities
 */
public class BlockEntityManager {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, PerfectMobFarm.MODID);

    public static final RegistryObject<BlockEntityType<IronMobFarmEntity>> IRON_MOB_FARM_ENTITY = BLOCK_ENTITIES.register("iron_mob_farm_entity",
            () -> Builder.of(IronMobFarmEntity::new, BlockManager.IRON_MOB_FARM.get()).build(null));

    public static final RegistryObject<BlockEntityType<GoldMobFarmEntity>> GOLD_MOB_FARM_ENTITY = BLOCK_ENTITIES.register("gold_mob_farm_entity",
            () -> Builder.of(GoldMobFarmEntity::new, BlockManager.GOLD_MOB_FARM.get()).build(null));

    public static final RegistryObject<BlockEntityType<DiamondMobFarmEntity>> DIAMOND_MOB_FARM_ENTITY = BLOCK_ENTITIES.register("diamond_mob_farm_entity",
            () -> Builder.of(DiamondMobFarmEntity::new, BlockManager.DIAMOND_MOB_FARM.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
