package com.sweetmimike.perfectmobfarm.event;

import com.sweetmimike.perfectmobfarm.PerfectMobFarm;
import com.sweetmimike.perfectmobfarm.block.entity.BlockEntityManager;
import com.sweetmimike.perfectmobfarm.renderer.MobFarmRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = PerfectMobFarm.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        for (RegistryObject<BlockEntityType> o : BlockEntityManager.MOB_FARM_ENTITIES_LIST) {
            event.registerBlockEntityRenderer(o.get(), MobFarmRenderer::new);
        }

//        event.registerBlockEntityRenderer(BlockEntityManager.IRON_MOB_FARM_ENTITY.get(), MobFarmRenderer::new);
//        event.registerBlockEntityRenderer(BlockEntityManager.GOLD_MOB_FARM_ENTITY.get(), MobFarmRenderer::new);
//        event.registerBlockEntityRenderer(BlockEntityManager.DIAMOND_MOB_FARM_ENTITY.get(), MobFarmRenderer::new);
//        event.registerBlockEntityRenderer(BlockEntityManager.EMERALD_MOB_FARM_ENTITY.get(), MobFarmRenderer::new);
//        event.registerBlockEntityRenderer(BlockEntityManager.DIAMOND_MOB_FARM_ENTITY.get(), MobFarmRenderer::new);
    }

}
