package com.sweetmimike.perfectmobfarm.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.sweetmimike.perfectmobfarm.block.IronMobFarm;
import com.sweetmimike.perfectmobfarm.block.entity.IronMobFarmEntity;
import com.sweetmimike.perfectmobfarm.item.ItemManager;
import com.sweetmimike.perfectmobfarm.item.MobShard;
import com.sweetmimike.perfectmobfarm.utils.NbtTagsName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.slf4j.Logger;

public class MobFarmRenderer implements BlockEntityRenderer<IronMobFarmEntity> {

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    private final BlockEntityRendererProvider.Context context;

    public MobFarmRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(IronMobFarmEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        final BlockRenderDispatcher dispatcher = this.context.getBlockRenderDispatcher();
//        pPoseStack.pushPose();
//        pPoseStack.translate(0.5f, 0.5f, 0.5f);
//        dispatcher.renderSingleBlock(Blocks.GLASS.defaultBlockState(), pPoseStack, pBufferSource, pPackedLight, pPackedOverlay,
//                EmptyModelData.INSTANCE);

//        pPoseStack.popPose();

        IItemHandler handler = pBlockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (handler == null) {
            return;
        }
        ItemStack itemHeld = handler.getStackInSlot(0);

        if (itemHeld.getItem() == ItemManager.MOB_SHARD.get()) {
            CompoundTag nbtTag = itemHeld.getTag();
            if (nbtTag != null && nbtTag.getInt(NbtTagsName.KILLED_COUNT) == MobShard.KILL_NEEDED) {
                EntityType type = EntityType.byString(nbtTag.getString(NbtTagsName.MOB_ID)).orElse(null);
                if (type == null) {
                    return;
                }
                renderGivenEntity(type, pBlockEntity, pPartialTick, pPoseStack, pBufferSource);
            }
        }
    }

    /**
     * Render the given entity type just above the Mob farm
     *
     * @param entityType
     * @param pBlockEntity
     * @param pPoseStack
     * @param pBufferSource
     */
    public void renderGivenEntity(EntityType entityType, IronMobFarmEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource) {
        final EntityRenderDispatcher entityDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        Entity entityToSpawn = pBlockEntity.getEntityToDisplay();
        if(entityToSpawn == null || entityToSpawn.getType() != entityType) {
            LOGGER.debug("NEED TO CREATE A NEW ENTITY");
            entityToSpawn = entityType.create(pBlockEntity.getLevel());
            pBlockEntity.setEntityToDisplay(entityToSpawn);
        }

        float rotation = pBlockEntity.getBlockState().getValue(IronMobFarm.FACING).toYRot();

        // Rotate the entity to match the mob farm direction
        entityToSpawn.setYHeadRot(rotation);
        entityToSpawn.setYBodyRot(rotation);

        float scale = 0.3f;
        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, 0.25f, 0.5f);
        pPoseStack.scale(scale, scale, scale);

        entityDispatcher.render(entityToSpawn, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, pPoseStack, pBufferSource, 15728880);
        pPoseStack.popPose();
    }
}
