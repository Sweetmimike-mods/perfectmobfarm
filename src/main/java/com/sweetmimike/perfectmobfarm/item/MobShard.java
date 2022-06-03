package com.sweetmimike.perfectmobfarm.item;

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

    public static final int KILL_NEEDED = 2;

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
                        "Killed : " + killed_count + "/" + KILL_NEEDED));
            }
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
