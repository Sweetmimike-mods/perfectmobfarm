package com.sweetmimike.perfectmobfarm.screen.slot;

import com.sweetmimike.perfectmobfarm.item.MobShard;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class MobShardSlot extends SlotItemHandler {
    public MobShardSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.getItem() instanceof MobShard;
    }
}
