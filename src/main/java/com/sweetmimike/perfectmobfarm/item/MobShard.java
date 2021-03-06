package com.sweetmimike.perfectmobfarm.item;

import com.sweetmimike.perfectmobfarm.config.ServerConfigs;
import com.sweetmimike.perfectmobfarm.utils.NbtTagsName;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Mob Shard item
 */
public class MobShard extends Item {

    public MobShard(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        CompoundTag nbtTag = pStack.getTag();
        if (nbtTag != null) {
            if (nbtTag.contains(NbtTagsName.MOB) && nbtTag.contains(NbtTagsName.KILLED_COUNT)) {

                String mobName = nbtTag.getString(NbtTagsName.MOB);
                int killed_count = nbtTag.getInt(NbtTagsName.KILLED_COUNT);
                int maxDamage = pStack.getMaxDamage();
                int currentDurability = maxDamage - pStack.getDamageValue();
                pTooltipComponents.add(new TextComponent(ChatFormatting.GRAY + "" + ChatFormatting.ITALIC + mobName));
                pTooltipComponents.add(new TextComponent(ChatFormatting.GRAY + "" + ChatFormatting.ITALIC +
                        "Killed : " + killed_count + "/" + ServerConfigs.MOB_SHARD_KILL_NEEDED.get()));
            }
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ServerConfigs.MOB_SHARD_DURABILITY.get();
    }

    @Override
    public boolean canBeDepleted() {
        return true;
    }
}
