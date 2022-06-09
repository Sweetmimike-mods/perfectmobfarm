package com.sweetmimike.perfectmobfarm.block.entity;

import com.sweetmimike.perfectmobfarm.PerfectMobFarm;
import com.sweetmimike.perfectmobfarm.block.BlockManager;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Manager for the mod block entities
 */
public class BlockEntityManager {

    public static final List<RegistryObject> MOB_FARM_ENTITIES_LIST = new ArrayList<>();

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, PerfectMobFarm.MODID);

    public static final RegistryObject<BlockEntityType<IronMobFarmEntity>> IRON_MOB_FARM_ENTITY = BLOCK_ENTITIES.register("iron_mob_farm_entity",
            () -> Builder.of(IronMobFarmEntity::new, BlockManager.IRON_MOB_FARM.get()).build(null));

    public static final RegistryObject<BlockEntityType<GoldMobFarmEntity>> GOLD_MOB_FARM_ENTITY = BLOCK_ENTITIES.register("gold_mob_farm_entity",
            () -> Builder.of(GoldMobFarmEntity::new, BlockManager.GOLD_MOB_FARM.get()).build(null));

    public static final RegistryObject<BlockEntityType<DiamondMobFarmEntity>> DIAMOND_MOB_FARM_ENTITY = BLOCK_ENTITIES.register("diamond_mob_farm_entity",
            () -> Builder.of(DiamondMobFarmEntity::new, BlockManager.DIAMOND_MOB_FARM.get()).build(null));

    public static final RegistryObject<BlockEntityType<EmeraldMobFarmEntity>> EMERALD_MOB_FARM_ENTITY = BLOCK_ENTITIES.register("emerald_mob_farm_entity",
            () -> Builder.of(EmeraldMobFarmEntity::new, BlockManager.EMERALD_MOB_FARM.get()).build(null));

    public static final RegistryObject<BlockEntityType<NetheriteMobFarmEntity>> NETHERITE_MOB_FARM_ENTITY = BLOCK_ENTITIES.register("netherite_mob_farm_entity",
            () -> Builder.of(NetheriteMobFarmEntity::new, BlockManager.NETHERITE_MOB_FARM.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    static {
        Collections.addAll(MOB_FARM_ENTITIES_LIST, IRON_MOB_FARM_ENTITY, GOLD_MOB_FARM_ENTITY, DIAMOND_MOB_FARM_ENTITY, EMERALD_MOB_FARM_ENTITY, NETHERITE_MOB_FARM_ENTITY);
    }
}
