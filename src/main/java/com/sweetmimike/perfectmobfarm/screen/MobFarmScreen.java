package com.sweetmimike.perfectmobfarm.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sweetmimike.perfectmobfarm.PerfectMobFarm;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Mob farm screen
 */
public class MobFarmScreen extends AbstractContainerScreen<MobFarmMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(PerfectMobFarm.MODID, "textures/gui/mobfarm-gui-2.png");

    public MobFarmScreen(MobFarmMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        // Change to fit to the actual gui texture
        this.imageHeight = 133;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        this.renderProgressBar(pPoseStack, x, y);
    }

    public void renderProgressBar(PoseStack pPoseStack, int x, int y) {
        int progressed = menu.getProgress();
        this.blit(pPoseStack, x + 152, y + 47 - progressed, 177, 47 - progressed, 4, 41);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pPoseStack, pMouseX, pMouseY);
    }
}
