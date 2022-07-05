package com.sweetmimike.perfectmobfarm.block.entity;

import com.sweetmimike.perfectmobfarm.config.ServerConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Entity for the Gold Mob Farm block
 */
public class GoldMobFarmEntity extends IronMobFarmEntity {

    public GoldMobFarmEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityManager.GOLD_MOB_FARM_ENTITY.get(), pWorldPosition, pBlockState, ServerConfigs.GOLD_MOB_FARM_COOLDOWN.get());
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Gold Mob Farm");
    }
}
