package net.xelbayria.tarotboards.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.DyeColor;
import net.xelbayria.tarotboards.TarotBoard;
import net.xelbayria.tarotboards.entity.EntityCard;
import net.xelbayria.tarotboards.entity.base.EntityStacked;
import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.util.CardHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.text.WordUtils;
import org.joml.Random;

public class RenderEntityCard extends EntityRenderer<EntityCard> {

    public RenderEntityCard(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(EntityCard pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);

        double zFightOffset = (pEntity.getId() % 1000) * 0.00015D;

        pPoseStack.pushPose();
        ItemStack card = new ItemStack(InitItems.cards.get(pEntity.getTopStackID()).get());

        EntityStacked.CardStackEntry topEntry = pEntity.getTopEntry();

        if ((topEntry.covered())) {
            card = new ItemStack(InitItems.CARD_COVERED.get());
        } else {
            String name = card.getDescriptionId();
            name = WordUtils.capitalizeFully(name.replace("item.tarotboard.", "").replace("_", " "));

            String[] lines = name.split(" ");

            if (pEntity.getStackAmount() >= 52) {
                for (int i = 0; i < 52; i++) {
                    double yOffset = i * 0.003D;
                    if (!TarotBoard.wilds.contains(name) && lines.length > 1) {
                        // Process normal cards
                        String value = lines[0];
                        String suit = lines[2];
                        this.translateSignText(pPoseStack, pEntity, zFightOffset, yOffset);
                        for (String line : lines) {
                            float f = (float) (-this.getFont().width(line) / 2);
                            pPoseStack.translate(0, 12, 0);
                            this.getFont().drawInBatch(line, f, -25, CardHelper.getStyle(suit).getColor(), false, pPoseStack.last().pose(), pBuffer, Font.DisplayMode.NORMAL, 0, pPackedLight);
                        }
                    } else {
                        this.translateSignText(pPoseStack, pEntity, zFightOffset, yOffset);
                        float f = (float) (-this.getFont().width(name) / 2);
                        this.getFont().drawInBatch(name, f, 0, DyeColor.WHITE.getTextColor(), false, pPoseStack.last().pose(), pBuffer, Font.DisplayMode.NORMAL, 0, pPackedLight);
                    }
                }
            } else {
                for (int i = 0; i < pEntity.getStackAmount(); i++) {
                    double yOffset = i * 0.003D;
                    if (!TarotBoard.wilds.contains(name) && lines.length > 1) {
                        // Process normal cards
                        String value = lines[0];
                        String suit = lines[2];
                        this.translateSignText(pPoseStack, pEntity, zFightOffset, yOffset);
                        for (String line : lines) {
                            float f = (float) (-this.getFont().width(line) / 2);
                            pPoseStack.translate(0, 12, 0);
                            this.getFont().drawInBatch(line, f, -25, CardHelper.getStyle(suit).getColor(), false, pPoseStack.last().pose(), pBuffer, Font.DisplayMode.NORMAL, 0, pPackedLight);
                        }
                    } else {
                        this.translateSignText(pPoseStack, pEntity, zFightOffset, yOffset);
                        float f = (float) (-this.getFont().width(name) / 2);
                        this.getFont().drawInBatch(name, f, 0, DyeColor.WHITE.getTextColor(), false, pPoseStack.last().pose(), pBuffer, Font.DisplayMode.NORMAL, 0, pPackedLight);
                    }
                }
            }
        }

        pPoseStack.popPose();

        pPoseStack.pushPose();
        zFightOffset = (pEntity.getId() % 1000) * 0.00015D;
        pPoseStack.translate(0, zFightOffset, 0);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(-pEntity.getRotation() + 180));
        pPoseStack.scale(1.5F, 1.5F, 1.5F);

        if (pEntity.getStackAmount() >= 52) {
            for (int i = 0; i < 52; i++) {
                CardHelper.renderItem(card, pEntity.level(), 0, i * 0.003D, 0, pPoseStack, pBuffer, pPackedLight);
            }
        } else {
            for (int i = 0; i < pEntity.getStackAmount(); i++) {
                CardHelper.renderItem(card, pEntity.level(), 0, i * 0.003D, 0, pPoseStack, pBuffer, pPackedLight);
            }
        }

        pPoseStack.popPose();
    }

    private void translateSignText(PoseStack pPoseStack, EntityCard pEntity, double zFightOffset, double yOffset) {
        pPoseStack.mulPose(Axis.YP.rotationDegrees(-pEntity.getRotation() + 180));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
        pPoseStack.translate(0, 0, -(0.025 + zFightOffset) + yOffset);
        pPoseStack.scale(0.005F, 0.005F, 0.005F);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCard pEntity) {
        return null;
    }
}
