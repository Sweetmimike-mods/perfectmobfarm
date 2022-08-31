package com.sweetmimike.perfectmobfarm.block.entity;

import com.mojang.logging.LogUtils;
import com.sweetmimike.perfectmobfarm.config.ServerConfigs;
import com.sweetmimike.perfectmobfarm.item.MobShard;
import com.sweetmimike.perfectmobfarm.screen.MobFarmMenu;
import com.sweetmimike.perfectmobfarm.utils.NbtTagsName;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Entity for the Iron Mob Farm block
 */
public class IronMobFarmEntity extends BlockEntity implements MenuProvider {

    private static final Logger LOGGER = LogUtils.getLogger();
    /**
     * Time needed by the farm to generate drops
     */
    private int cooldown;
    /**
     * Timer that counts ticks
     */
    private int timer;
    /**
     * Boolean value that determines if the farm is currently generating loots or not
     */
    private boolean isActive;
    /**
     * ItemStackHandler
     */
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            ItemStack stack = this.getStackInSlot(slot);
            isActive = stack.getItem() instanceof MobShard && stack.getTag() != null
                    && stack.getTag().getInt(NbtTagsName.KILLED_COUNT) == ServerConfigs.MOB_SHARD_KILL_NEEDED.get();
            timer = 0;
            setChanged();
        }
    };
    /**
     * Fake player used to generate loot
     */
    private LivingEntity fakePlayer;
    /**
     * Entity to display inside the farm
     */
    private Entity entityToDisplay;
    /**
     * Mob used to create loots
     */
    private Mob mobToLoot;
    /**
     * LazyItemHandler
     */
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected IronMobFarmEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, int cooldown) {
        super(pType, pWorldPosition, pBlockState);
        this.cooldown = cooldown;
    }

    public IronMobFarmEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        this(BlockEntityManager.IRON_MOB_FARM_ENTITY.get(), pWorldPosition, pBlockState, ServerConfigs.IRON_MOB_FARM_COOLDOWN.get());
    }

    /**
     * Function called each tick.
     * If the mob farm is active, then call generate drop.
     */
    public void tick() {
        if (isActive) {
            timer++;
            if (timer >= cooldown) {
                generateDrop();
                timer = 0;
            }
        }
    }

    /**
     * Generate drops from the mob type captured by the mob shard in the mob farm.
     */
    public void generateDrop() {
        ItemStack mobShard = itemHandler.getStackInSlot(0);
        if (mobShard.getItem() instanceof MobShard) {
            CompoundTag nbtData = mobShard.hasTag() ? mobShard.getTag() : null;
            if (nbtData != null && nbtData.getInt(NbtTagsName.KILLED_COUNT) == ServerConfigs.MOB_SHARD_KILL_NEEDED.get()) {
                BlockEntity container = getNearbyContainer();
                if (container == null) {
                    LOGGER.debug("GENERATE-DROP ~ No container found");
                    return;
                }
                String resourcePath = nbtData.getString(NbtTagsName.RESOURCE_LOCATION);
                ResourceLocation loc = new ResourceLocation(resourcePath);

                LootTables ltManager = this.getLevel().getServer().getLootTables();
                LootTable lt = ltManager.get(loc);

                Optional<EntityType<?>> optEntityType = EntityType.byString(nbtData.getString(NbtTagsName.MOB_ID));
                if (optEntityType.isEmpty()) {
                    LOGGER.error("In generateDrop(): Unable to get entity type.");
                    return;
                }

                EntityType<?> entityType = optEntityType.get();
                Mob mob = getOrCreateMobToLoot(entityType);

                LootContext.Builder builder = constructContextBuilder(mob);
                LootContext ctx = builder.create(LootContextParamSets.ENTITY);
                List<ItemStack> generated = lt.getRandomItems(ctx);
                LOGGER.debug("ITEMS GENERATED ~~ " + generated);

                // Boolean value to determine if we can place at least one of the generated itemStacks
                AtomicBoolean canBePlaced = new AtomicBoolean(false);
                container.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
                    for (ItemStack itemStack : generated) {

                        // Air can be generated from loot table and needs to be count as an item
                        // If it only generates Air, then mob shard should be damaged
                        if (itemStack.getItem() == Items.AIR) {
                            canBePlaced.set(true);
                            continue;
                        }
                        ItemStack remainingStack = ItemHandlerHelper.insertItemStacked(iItemHandler, itemStack, false);

                        // If the counts are different, then at least one item as been placed
                        if (itemStack.getCount() != remainingStack.getCount()) {
                            canBePlaced.set(true);
                        }
                    }
                });

                // If no item can be placed, then stop damaging the mob shard
                if (canBePlaced.get()) {
                    mobShard.setDamageValue(mobShard.getDamageValue() + 1);
                    if (mobShard.getMaxDamage() <= mobShard.getDamageValue()) {
                        mobShard.setCount(0);
                    }
                }
            }
        } else {
            // If the itemstack in slot is not a mob shard, then turn mob farm off
            isActive = false;
        }
    }

    /**
     * Get the block entity below and return it
     *
     * @return The block entity below
     */
    public BlockEntity getNearbyContainer() {
        BlockEntity blockEntity = Objects.requireNonNull(getLevel()).getBlockEntity(getBlockPos().below());
        if (blockEntity instanceof Container) {
            return blockEntity;
        }
        return null;
    }

    /**
     * Construct a LootContext Builder.
     *
     * @param mob Mob from which drops come
     * @return A LootContext Builder
     */
    protected LootContext.Builder constructContextBuilder(Mob mob) {
        Vec3 position = new Vec3(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
        Player fakePlayer = (Player) getOrCreateFakePlayer();

        if (this.level == null) {
            return null;
        }

        return (new LootContext.Builder((ServerLevel) this.level))
                .withParameter(LootContextParams.ORIGIN, position)
                .withParameter(LootContextParams.THIS_ENTITY, mob)
                .withParameter(LootContextParams.DAMAGE_SOURCE, DamageSource.playerAttack(fakePlayer))
                .withParameter(LootContextParams.KILLER_ENTITY, fakePlayer)
                .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, fakePlayer);
    }

    /**
     * Called when a mob farm block is removed.
     * Drop on the ground the mob shard.
     */
    public void drop() {
        ItemStack itemStack = itemHandler.getStackInSlot(0);
        if (level != null)
            Containers.dropItemStack(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), itemStack);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Iron Mob Farm");
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new MobFarmMenu(pContainerId, pInventory, this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        isActive = pTag.getBoolean("isActive");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putBoolean("isActive", isActive);
        super.saveAdditional(pTag);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public Entity getEntityToDisplay() {
        return entityToDisplay;
    }

    public void setEntityToDisplay(Entity entityToDisplay) {
        this.entityToDisplay = entityToDisplay;
    }

    /**
     * FakePlayer getter.
     *
     * @return If fakePlayer is null returns a new one, otherwise fakePlayer.
     */
    protected LivingEntity getOrCreateFakePlayer() {
        if (fakePlayer == null) {
            fakePlayer = FakePlayerFactory.getMinecraft((ServerLevel) this.level);
        }
        return fakePlayer;
    }

    /**
     * Check current mobToLoot and create a new one or return the current one depending on the state.
     *
     * @param entityType Entity type to compare
     * @return If mobToLoot is null, create a new one. If its type is different from the passed one, create a new one. Otherwise return the current.
     */
    private Mob getOrCreateMobToLoot(EntityType entityType) {
        if (level == null) return mobToLoot;

        if (mobToLoot == null || mobToLoot.getType() != entityType) {
            mobToLoot = (Mob) entityType.create(this.level);
        }
        return mobToLoot;
    }
}
