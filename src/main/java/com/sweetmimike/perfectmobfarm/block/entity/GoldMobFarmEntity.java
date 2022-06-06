package com.sweetmimike.perfectmobfarm.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Entity for the Gold Mob Farm block
 */
public class GoldMobFarmEntity extends IronMobFarmEntity {

    public GoldMobFarmEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityManager.GOLD_MOB_FARM_ENTITY.get(), pWorldPosition, pBlockState, 400);
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Gold Mob Farm");
    }
}
