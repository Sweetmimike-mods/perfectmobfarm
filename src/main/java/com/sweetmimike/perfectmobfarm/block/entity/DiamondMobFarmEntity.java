package com.sweetmimike.perfectmobfarm.block.entity;

import com.sweetmimike.perfectmobfarm.config.CommonConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Entity for the Diamond Mob Farm block
 */
public class DiamondMobFarmEntity extends IronMobFarmEntity {

    public DiamondMobFarmEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityManager.DIAMOND_MOB_FARM_ENTITY.get(), pWorldPosition, pBlockState, CommonConfigs.DIAMOND_MOB_FARM_COOLDOWN.get());
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Diamond Mob Farm");
    }
}
