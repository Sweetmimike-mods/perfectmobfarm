package com.sweetmimike.perfectmobfarm.block;

import com.sweetmimike.perfectmobfarm.block.entity.EmeraldMobFarmEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Emerald Mob Farm block
 */
public class EmeraldMobFarm extends IronMobFarm {
    public EmeraldMobFarm(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EmeraldMobFarmEntity(pPos, pState);
    }
}
