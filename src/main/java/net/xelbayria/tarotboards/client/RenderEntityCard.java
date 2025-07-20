package net.xelbayria.tarotboards.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.xelbayria.tarotboards.TarotBoard;
import net.xelbayria.tarotboards.entity.EntityCard;
import net.xelbayria.tarotboards.entity.base.EntityStacked;
import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.util.CardHelper;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        if (topEntry.covered()) {
            card = new ItemStack(InitItems.CARD_COVERED.get());
        } else {
            String descriptionId = card.getDescriptionId();
            List<String> lineList = getCardLines(descriptionId);

            boolean isWild = TarotBoard.wilds.contains(lineList.get(0));
            boolean hasSuit = lineList.size() > 2;
            String suit = hasSuit ? lineList.get(2) : null;

            int count = pEntity.getStackAmount() >= 52 ? 52 : pEntity.getStackAmount();

            for (int i = 0; i < count; i++) {
                double yOffset = i * 0.003D;

                this.translateSignText(pPoseStack, pEntity, zFightOffset, yOffset);

                if (!isWild && hasSuit) {
                    for (String line : lineList) {
                        float f = (float) (-this.getFont().width(line) / 2);
                        pPoseStack.translate(0, 12, 0);
                        this.getFont().drawInBatch(
                                line,
                                f,
                                -25,
                                CardHelper.getStyle(suit).getColor(),
                                false,
                                pPoseStack.last().pose(),
                                pBuffer,
                                Font.DisplayMode.NORMAL,
                                0,
                                pPackedLight
                        );
                    }
                } else {
                    // Wild or no suit — render name only
                    String name = String.join(" ", lineList);
                    float f = (float) (-this.getFont().width(name) / 2);
                    this.getFont().drawInBatch(
                            name,
                            f,
                            0,
                            DyeColor.WHITE.getTextColor(),
                            false,
                            pPoseStack.last().pose(),
                            pBuffer,
                            Font.DisplayMode.NORMAL,
                            0,
                            pPackedLight
                    );
                }
            }
        }

        pPoseStack.popPose();

        // Render the card item stack(s)
        pPoseStack.pushPose();
        pPoseStack.translate(0, zFightOffset, 0);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(-pEntity.getRotation() + 180));
        pPoseStack.scale(1.5F, 1.5F, 1.5F);

        int renderCount = pEntity.getStackAmount() >= 52 ? 52 : pEntity.getStackAmount();
        for (int i = 0; i < renderCount; i++) {
            CardHelper.renderItem(
                    card,
                    pEntity.level(),
                    0,
                    i * 0.003D,
                    0,
                    pPoseStack,
                    pBuffer,
                    pPackedLight
            );
        }

        pPoseStack.popPose();
    }

    private List<String> getCardLines(String descriptionId) {
        // Clean up
        String name = WordUtils.capitalizeFully(
                descriptionId.replace("item.tarotboard.", "").replace("_", " ")
        );
        String[] lines = name.split(" ");
        ArrayList<String> lineList = new ArrayList<>(Arrays.asList(lines));

        // If normal card: add mapped index
        if (lines.length > 1) {
            String value = lines[0];
            int rawIndex = TarotBoard.values.indexOf(value);
            int mappedIndex = rawIndex - ((TarotBoard.values.size() - 1) / 2);
            lineList.add("(" + mappedIndex + ")");
        }
        return lineList;
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
