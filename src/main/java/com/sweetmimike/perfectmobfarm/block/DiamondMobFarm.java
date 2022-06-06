package com.sweetmimike.perfectmobfarm.block;

import com.sweetmimike.perfectmobfarm.block.entity.DiamondMobFarmEntity;
import com.sweetmimike.perfectmobfarm.block.entity.GoldMobFarmEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Gold Mob Farm block
 */
public class DiamondMobFarm extends IronMobFarm {
    public DiamondMobFarm(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DiamondMobFarmEntity(pPos, pState);
    }
}
