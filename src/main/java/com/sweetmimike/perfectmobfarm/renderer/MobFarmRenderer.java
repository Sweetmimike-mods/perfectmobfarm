package com.sweetmimike.perfectmobfarm.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import com.sweetmimike.perfectmobfarm.block.IronMobFarm;
import com.sweetmimike.perfectmobfarm.block.entity.IronMobFarmEntity;
import com.sweetmimike.perfectmobfarm.config.CommonConfigs;
import com.sweetmimike.perfectmobfarm.item.ItemManager;
import com.sweetmimike.perfectmobfarm.item.MobShard;
import com.sweetmimike.perfectmobfarm.utils.NbtTagsName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class MobFarmRenderer implements BlockEntityRenderer<IronMobFarmEntity> {

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    private final BlockEntityRendererProvider.Context context;

    public MobFarmRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(IronMobFarmEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        IItemHandler handler = pBlockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (handler == null) {
            return;
        }
        ItemStack itemHeld = handler.getStackInSlot(0);

        if (itemHeld.getItem() == ItemManager.MOB_SHARD.get()) {
            CompoundTag nbtTag = itemHeld.getTag();
            if (nbtTag != null && nbtTag.getInt(NbtTagsName.KILLED_COUNT) == CommonConfigs.MOB_SHARD_KILL_NEEDED.get()) {
                EntityType type = EntityType.byString(nbtTag.getString(NbtTagsName.MOB_ID)).orElse(null);
                if (type == null) {
                    return;
                }
                renderGivenEntity(type, pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight);
            }
        }
    }

    /**
     * Render the given entity type just above the Mob farm
     *
     * @param entityType
     * @param pBlockEntity
     * @param pPartialTick
     * @param pPoseStack
     * @param pBufferSource
     * @param pPackedLight
     */
    public void renderGivenEntity(EntityType entityType, IronMobFarmEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight) {
        pPoseStack.pushPose();
        Entity entityToSpawn = pBlockEntity.getEntityToDisplay();
        if (entityToSpawn == null || entityToSpawn.getType() != entityType) {
            LOGGER.debug("NEED TO CREATE A NEW ENTITY");
            entityToSpawn = entityType.create(pBlockEntity.getLevel());
            pBlockEntity.setEntityToDisplay(entityToSpawn);
        }

        float rotation = pBlockEntity.getBlockState().getValue(IronMobFarm.FACING).toYRot();

        // Set head rotation to 0 to avoid head shaking
        entityToSpawn.setYHeadRot(0.0F);

        float scale = 0.3f;

        pPoseStack.translate(0.5f, 0.25f, 0.5f);
        pPoseStack.scale(scale, scale, scale);
        // Rotate to match block direction
        pPoseStack.mulPose(Vector3f.YP.rotationDegrees(-rotation));

        Minecraft.getInstance().getEntityRenderDispatcher().render(entityToSpawn, 0.0D, 0.0D, 0.0D, 0.0F,
                pPartialTick, pPoseStack, pBufferSource, pPackedLight);
        pPoseStack.popPose();
    }
}
