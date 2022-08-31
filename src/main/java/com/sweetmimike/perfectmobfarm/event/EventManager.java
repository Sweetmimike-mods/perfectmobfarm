package com.sweetmimike.perfectmobfarm.event;

import com.mojang.logging.LogUtils;
import com.sweetmimike.perfectmobfarm.PerfectMobFarm;
import com.sweetmimike.perfectmobfarm.config.ServerConfigs;
import com.sweetmimike.perfectmobfarm.item.MobShard;
import com.sweetmimike.perfectmobfarm.utils.NbtTagsName;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Manager for the mod events
 */
@Mod.EventBusSubscriber(modid = PerfectMobFarm.MODID)
public class EventManager {
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Called when a player right click on a mob with a mob shard
     *
     * @param event Player interact event
     */
    @SubscribeEvent
    public static void clickMobShard(PlayerInteractEvent.EntityInteract event) {
        Player pPlayer = event.getEntity();
        if (!pPlayer.getLevel().isClientSide()) {
            if (event.getItemStack().getItem() instanceof MobShard && event.getTarget() instanceof Mob mob) {
                if (mob instanceof WitherBoss || mob instanceof EnderDragon || mob instanceof ElderGuardian) {
                    //pPlayer.sendMessage(Component.literal("You can't capture this mob"), pPlayer.getUUID());
                    pPlayer.displayClientMessage(Component.literal("You can't capture this mob"), false);
                    return;
                }
                ItemStack shardStack = event.getItemStack();
                CompoundTag nbtTag;
                if (!shardStack.hasTag() || shardStack.getTag() == null || shardStack.getTag().get(NbtTagsName.MOB) == null) {
                    nbtTag = new CompoundTag();
//                    CompoundTag nbtMobTag = new CompoundTag();
//                    mob.save(nbtMobTag);

                    nbtTag.putString(NbtTagsName.MOB, mob.getName().getString());
                    nbtTag.putInt(NbtTagsName.KILLED_COUNT, 0);
                    nbtTag.putString(NbtTagsName.RESOURCE_LOCATION, mob.getLootTable().toString());
                    nbtTag.putString(NbtTagsName.MOB_ID, mob.getEncodeId());
                    shardStack.setTag(nbtTag);
                    pPlayer.displayClientMessage(Component.literal(mob.getDisplayName().getString() + " captured !"), false);
                    //pPlayer.sendMessage(new TextComponent(mob.getDisplayName().getString() + " captured !"), pPlayer.getUUID());
                } else {
                    String mobName = shardStack.getTag().getString(NbtTagsName.MOB);
                    //pPlayer.sendMessage(new TextComponent("You already captured a " + mobName), pPlayer.getUUID());
                    pPlayer.displayClientMessage(Component.literal("You already captured a " + mobName), false);

                }
            }

        }
    }

    /**
     * Called when a player kill a mob. It will check if the player has a mob shard
     * and increment the killed count
     *
     * @param event LivingDeathEvent
     */
    @SubscribeEvent
    public static void onPlayerKillMob(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            for (ItemStack is : player.getInventory().items) {
                if (is.getItem() instanceof MobShard) {
                    CompoundTag nbtData = is.getTag();
                    if (nbtData != null && nbtData.contains(NbtTagsName.MOB)) {
                        String mobName = nbtData.getString(NbtTagsName.MOB);
                        if (mobName.equals(event.getEntity().getName().getString())) {
                            int killed_count = nbtData.getInt(NbtTagsName.KILLED_COUNT);
                            if (killed_count < ServerConfigs.MOB_SHARD_KILL_NEEDED.get()) {
                                killed_count = killed_count + 1;
                                nbtData.putInt(NbtTagsName.KILLED_COUNT, killed_count);
                                is.setTag(nbtData);

                                // Break when one mob shard has been incremented to avoid incrementation of every mob shard
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
