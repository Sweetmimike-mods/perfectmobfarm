package com.sweetmimike.perfectmobfarm.item;

import com.sweetmimike.perfectmobfarm.config.ServerConfigs;
import net.minecraft.world.item.ItemStack;

/**
 * Advanced Mob Shard
 */
public class AdvancedMobShard extends MobShard {
    public AdvancedMobShard(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ServerConfigs.ADVANCED_MOB_SHARD_DURABILITY.get();
    }
}
