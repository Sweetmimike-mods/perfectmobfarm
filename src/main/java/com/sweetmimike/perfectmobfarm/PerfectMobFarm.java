package com.sweetmimike.perfectmobfarm;

import com.mojang.logging.LogUtils;
import com.sweetmimike.perfectmobfarm.block.BlockManager;
import com.sweetmimike.perfectmobfarm.block.entity.BlockEntityManager;
import com.sweetmimike.perfectmobfarm.config.ServerConfigs;
import com.sweetmimike.perfectmobfarm.item.ItemManager;
import com.sweetmimike.perfectmobfarm.screen.MenuManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PerfectMobFarm.MODID)
public class PerfectMobFarm {

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "perfectmobfarm";

    /**
     * Add a new creative mode tab for the mod
     */
    public static final CreativeModeTab PERFECT_MOB_FARM_TAB = new CreativeModeTab("perfectmobfarmtab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemManager.MOB_SHARD.get());
        }
    };

    public PerfectMobFarm() {

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPECS, "perfectmobfarm-server.toml");

        ItemManager.register(eventBus);
        BlockManager.register(eventBus);
        BlockEntityManager.register(eventBus);
        MenuManager.register(eventBus);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ClientSetup.setup(event);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getName());
    }
}
