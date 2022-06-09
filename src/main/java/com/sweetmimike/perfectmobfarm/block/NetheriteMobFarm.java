package com.sweetmimike.perfectmobfarm.block;

import com.sweetmimike.perfectmobfarm.block.entity.NetheriteMobFarmEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Netherite Mob Farm block
 */
public class NetheriteMobFarm extends IronMobFarm {
    public NetheriteMobFarm(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NetheriteMobFarmEntity(pPos, pState);
    }
}
