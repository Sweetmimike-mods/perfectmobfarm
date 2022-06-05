package com.sweetmimike.perfectmobfarm.block.entity;

import com.mojang.logging.LogUtils;
import com.sweetmimike.perfectmobfarm.item.ItemManager;
import com.sweetmimike.perfectmobfarm.item.MobShard;
import com.sweetmimike.perfectmobfarm.screen.MobFarmMenu;
import com.sweetmimike.perfectmobfarm.utils.NbtTagsName;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Entity for the Iron Mob Farm block
 */
public class IronMobFarmEntity extends BlockEntity implements MenuProvider {

    private int cooldown;
    private int timer;
    private boolean isActive;
    private static final Logger LOGGER = LogUtils.getLogger();
    private Entity entityToDisplay;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            ItemStack stack = this.getStackInSlot(slot);
            if (stack.getItem() == ItemManager.MOB_SHARD.get() && stack.getTag() != null
                    && stack.getTag().getInt(NbtTagsName.KILLED_COUNT) == MobShard.KILL_NEEDED) {
                isActive = true;
            } else {
                isActive = false;
            }
            timer = 0;
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected IronMobFarmEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, int cooldown) {
        super(pType, pWorldPosition, pBlockState);
        this.cooldown = cooldown;
    }

    public IronMobFarmEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        this(BlockEntityManager.IRON_MOB_FARM_ENTITY.get(), pWorldPosition, pBlockState, 60);
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
        LOGGER.debug("CALL GENERATE DROP FROM " + this.getClass().getName());
        ItemStack mobShard = itemHandler.getStackInSlot(0);
        if (mobShard.getItem() == ItemManager.MOB_SHARD.get()) {
            CompoundTag nbtData = mobShard.getTag();
            if (nbtData != null && nbtData.getInt(NbtTagsName.KILLED_COUNT) == MobShard.KILL_NEEDED) {
                BlockEntity container = getNearbyContainer();
                if (container == null) {
                    LOGGER.debug("GENERATE-DROP ~ No container found");
                    return;
                }
                String resourcePath = nbtData.getString(NbtTagsName.RESOURCE_LOCATION);
                ResourceLocation loc = new ResourceLocation(resourcePath);

                LootTables ltManager = this.getLevel().getServer().getLootTables();
                LootTable lt = ltManager.get(loc);
                Vec3 position = new Vec3(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
                LootContext.Builder builder = (new LootContext.Builder((ServerLevel) this.level)).withParameter(LootContextParams.ORIGIN, position);

                LootContext ctx = builder.create(LootContextParamSets.EMPTY);
                List<ItemStack> generated = lt.getRandomItems(ctx);

                // Boolean value to determine if we can place at least one of the generated itemStacks
                AtomicBoolean canBePlaced = new AtomicBoolean(false);
                container.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
                    for (ItemStack itemStack : generated) {
                        ItemStack remainingStack = ItemHandlerHelper.insertItemStacked(iItemHandler, itemStack, false);

                        // If the counts are different, then at least one item as been placed
                        if (itemStack.getCount() != remainingStack.getCount()) {
                            canBePlaced.set(true);
                        }
                    }
                });

                // If no item can be placed, then stop damaging the mob shard
                if (canBePlaced.get()) {
                    boolean toDelete = mobShard.hurt(1, level.getRandom(), null);
                    if (toDelete) {
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
        BlockEntity blockEntity = getLevel().getBlockEntity(getBlockPos().below());
        if (blockEntity instanceof Container) {
            return blockEntity;
        }
        return null;
    }

    /**
     * Called when a mob farm block is removed.
     * Drop on the ground the mob shard.
     */
    public void drop() {
        ItemStack itemStack = itemHandler.getStackInSlot(0);
        Containers.dropItemStack(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), itemStack);
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Iron Mob Farm");
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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
}
