package com.sweetmimike.perfectmobfarm.block.entity;

import com.sweetmimike.perfectmobfarm.config.ServerConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Entity for the Diamond Mob Farm block
 */
public class DiamondMobFarmEntity extends IronMobFarmEntity {

    public DiamondMobFarmEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityManager.DIAMOND_MOB_FARM_ENTITY.get(), pWorldPosition, pBlockState, ServerConfigs.DIAMOND_MOB_FARM_COOLDOWN.get());
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Diamond Mob Farm");
    }
}
