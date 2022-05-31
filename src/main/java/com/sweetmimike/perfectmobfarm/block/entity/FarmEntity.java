package com.sweetmimike.perfectmobfarm.block.entity;

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
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
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

import java.util.List;

public class FarmEntity extends BlockEntity implements MenuProvider {

    private int cooldown;
    private int timer;
    private boolean isActive;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            System.out.println("CONTENT CHANGED IN SLOT : " + slot);
            if (this.getStackInSlot(slot).getItem() == ItemManager.MOB_SHARD.get()) {
                isActive = true;
            } else {
                isActive = false;
            }
            System.out.println("ISACTIVE = " + isActive);
            timer = 0;
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public FarmEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityManager.FARM_ENTITY.get(), pWorldPosition, pBlockState);
        this.cooldown = 60;
        timer = 0;
    }

    public void tick() {
        if (isActive) {
            timer++;
            if (timer >= cooldown) {
                generateDrop();
                timer = 0;
            }
        }
    }

    public void generateDrop() {
        System.out.println("CALL GENERATE DROP2");
        ItemStack mobShard = itemHandler.getStackInSlot(0);
        if (mobShard.getItem() == ItemManager.MOB_SHARD.get()) {
            CompoundTag nbtData = mobShard.getTag();
            if (nbtData != null && nbtData.getInt(NbtTagsName.KILLED_COUNT) == MobShard.KILL_NEEDED) {
                BlockEntity container = getNearbyContainer();
                if (container != null) {
                    String resourcePath = nbtData.getString(NbtTagsName.RESOURCE_LOCATION);
                    ResourceLocation loc = new ResourceLocation(resourcePath);

                    LootTables ltManager = this.getLevel().getServer().getLootTables();
                    LootTable lt = ltManager.get(loc);
                    Vec3 position = new Vec3(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
                    LootContext.Builder builder = (new LootContext.Builder((ServerLevel) this.level))
                            .withParameter(LootContextParams.ORIGIN, position);

                    LootContext ctx = builder.create(LootContextParamSets.EMPTY);
                    List<ItemStack> generated = lt.getRandomItems(ctx);
                    container.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
                        for (ItemStack itemStack : generated) {
                            ItemHandlerHelper.insertItemStacked(iItemHandler, itemStack, false);
                        }
                    });
                }
            }
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

    @Override
    public Component getDisplayName() {
        return new TextComponent("Mob Farm");
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
}