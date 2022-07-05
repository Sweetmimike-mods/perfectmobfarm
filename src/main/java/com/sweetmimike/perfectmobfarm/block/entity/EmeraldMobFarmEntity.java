package com.sweetmimike.perfectmobfarm.block.entity;

import com.sweetmimike.perfectmobfarm.config.ServerConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

/**
 * Entity for the Emerald Mob Farm block
 */
public class EmeraldMobFarmEntity extends IronMobFarmEntity {

    public EmeraldMobFarmEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityManager.EMERALD_MOB_FARM_ENTITY.get(), pWorldPosition, pBlockState, ServerConfigs.EMERALD_MOB_FARM_COOLDOWN.get());
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Emerald Mob Farm");
    }

    @Override
    protected LootContext.Builder constructContextBuilder(Mob mob) {
        Vec3 position = new Vec3(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
        Player fakePlayer = (Player) getOrCreateFakePlayer();

        ItemStack swordLooting = new ItemStack(Items.DIAMOND_SWORD);
        swordLooting.enchant(Enchantments.MOB_LOOTING, 1);
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, swordLooting);

        LootContext.Builder builder = (new LootContext.Builder((ServerLevel) this.level))
                .withParameter(LootContextParams.ORIGIN, position)
                .withParameter(LootContextParams.THIS_ENTITY, mob)
                .withParameter(LootContextParams.DAMAGE_SOURCE, DamageSource.playerAttack(fakePlayer))
                .withParameter(LootContextParams.KILLER_ENTITY, fakePlayer)
                .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, fakePlayer);

        return builder;
    }
}
