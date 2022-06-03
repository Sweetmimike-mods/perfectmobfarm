package com.sweetmimike.perfectmobfarm.block;

import com.sweetmimike.perfectmobfarm.block.entity.GoldMobFarmEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Gold Mob Farm block
 */
public class GoldMobFarm extends IronMobFarm {
    public GoldMobFarm(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new GoldMobFarmEntity(pPos, pState);
    }
}
