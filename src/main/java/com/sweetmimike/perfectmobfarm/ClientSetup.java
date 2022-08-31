package com.sweetmimike.perfectmobfarm;

import com.sweetmimike.perfectmobfarm.block.BlockManager;
import com.sweetmimike.perfectmobfarm.config.ServerConfigs;
import com.sweetmimike.perfectmobfarm.item.ItemManager;
import com.sweetmimike.perfectmobfarm.screen.MenuManager;
import com.sweetmimike.perfectmobfarm.screen.MobFarmScreen;
import com.sweetmimike.perfectmobfarm.utils.NbtTagsName;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Helper class used to setup the client side
 */
public class ClientSetup {

    /**
     * Static tool : no need to instanciate
     */
    private ClientSetup() {
        // Empty
    }

    /**
     * Method that do all the logic to init the client side
     *
     * @param event
     */
    public static void setup(FMLClientSetupEvent event) {
        MenuScreens.register(MenuManager.MOB_FARM_MENU.get(), MobFarmScreen::new);

        event.enqueueWork(() -> {
            ItemProperties.register(ItemManager.MOB_SHARD.get(), new ResourceLocation(PerfectMobFarm.MODID, "completed"), ((pStack, pLevel, pEntity, pSeed) -> {
                CompoundTag nbtData = pStack.getTag();
                if (nbtData != null && nbtData.contains(NbtTagsName.KILLED_COUNT)) {
                    return nbtData.getInt(NbtTagsName.KILLED_COUNT) == ServerConfigs.MOB_SHARD_KILL_NEEDED.get() ? 1.0F : 0.0F;
                }
                return 0.0F;
            }));

            ItemProperties.register(ItemManager.ADVANCED_MOB_SHARD.get(), new ResourceLocation(PerfectMobFarm.MODID, "completed"), ((pStack, pLevel, pEntity, pSeed) -> {
                CompoundTag nbtData = pStack.getTag();
                if (nbtData != null && nbtData.contains(NbtTagsName.KILLED_COUNT)) {
                    return nbtData.getInt(NbtTagsName.KILLED_COUNT) == ServerConfigs.MOB_SHARD_KILL_NEEDED.get() ? 1.0F : 0.0F;
                }
                return 0.0F;
            }));
        });
    }
}
