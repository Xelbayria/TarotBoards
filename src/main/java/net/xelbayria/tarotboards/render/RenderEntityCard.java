package net.xelbayria.tarotboards.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.xelbayria.tarotboards.entity.EntityCard;
import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.item.ItemCardCovered;
import net.xelbayria.tarotboards.util.CardHelper;
import net.xelbayria.tarotboards.util.ItemHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RenderEntityCard extends EntityRenderer<EntityCard> {

    public RenderEntityCard(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(EntityCard pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);

        ItemStack card;

        if (pEntity.getTags().contains("CardID")) {
            card = new ItemStack(InitItems.cards.get(pEntity.getTopStackID()).get());
        } else {
            card = new ItemStack(InitItems.cards.get(0).get());
        }

        if (pEntity.isCover()) {
            card = new ItemStack(InitItems.CARD_COVERED.get());
        }

        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(-pEntity.getRotation() + 180));
        pPoseStack.scale(1.5F, 1.5F, 1.5F);

        for (byte i = 0; i < 52; i++) {
            CardHelper.renderItem(card, pEntity.level(), 0, i * 0.003D, 0, pPoseStack, pBuffer, pPackedLight);
        }

        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCard pEntity) {
        return null;
    }
}
