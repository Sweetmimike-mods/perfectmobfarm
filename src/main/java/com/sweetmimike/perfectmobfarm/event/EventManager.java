package com.sweetmimike.perfectmobfarm.event;

import com.sweetmimike.perfectmobfarm.PerfectMobFarm;
import com.sweetmimike.perfectmobfarm.item.MobShard;
import com.sweetmimike.perfectmobfarm.utils.NbtTagsName;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
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

@Mod.EventBusSubscriber(modid = PerfectMobFarm.MODID)
public class EventManager {

    /**
     * Called when a player right click on a mob with a mob shard
     *
     * @param event
     */
    @SubscribeEvent
    public static void clickMobShard(PlayerInteractEvent.EntityInteract event) {
        Player pPlayer = event.getPlayer();
        if (pPlayer.getLevel().isClientSide()) {
            if (event.getItemStack().getItem() instanceof MobShard && event.getTarget() instanceof Mob mob) {
                if (mob instanceof WitherBoss || mob instanceof EnderDragon || mob instanceof ElderGuardian) {
                    pPlayer.sendMessage(new TextComponent("You can't capture this mob"), pPlayer.getUUID());
                    return;
                }
                ItemStack shardStack = event.getItemStack();
                CompoundTag nbtTag = null;
                if (!(shardStack.hasTag())) {
                    nbtTag = new CompoundTag();
                    CompoundTag nbtMobTag = new CompoundTag();
                    mob.save(nbtMobTag);

                    nbtTag.putString(NbtTagsName.MOB, mob.getName().getString());
                    nbtTag.putInt(NbtTagsName.KILLED_COUNT, 0);
                    nbtTag.putString(NbtTagsName.RESOURCE_LOCATION, mob.getLootTable().toString());
                    shardStack.setTag(nbtTag);
                    pPlayer.sendMessage(new TextComponent(mob.getDisplayName().getString() + " captured !"), pPlayer.getUUID());
                } else {
                    nbtTag = shardStack.getTag();
                }
            }

        }
    }

    /**
     * Called when a player kill a mob. It will check if the player has a mob shard
     * and increment the killed count
     *
     * @param event
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
                            if (killed_count < MobShard.KILL_NEEDED) {
                                killed_count = killed_count + 1;
                                nbtData.putInt(NbtTagsName.KILLED_COUNT, killed_count);
                                is.setTag(nbtData);
                            }
                        }
                    }
                }
            }
        }
    }
}
