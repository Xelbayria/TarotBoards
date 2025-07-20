package net.xelbayria.tarotboards.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.xelbayria.tarotboards.entity.EntityPokerChip;
import net.xelbayria.tarotboards.item.ItemPokerChip;
import net.xelbayria.tarotboards.util.CardHelper;

import java.util.Random;

public class RenderEntityPokerChip extends EntityRenderer<EntityPokerChip> {

    public RenderEntityPokerChip(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityPokerChip entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

        poseStack.pushPose();

        double zFightOffset = (entity.getId() % 1000) * 0.00005D;
        poseStack.translate(0, zFightOffset, 0);

        poseStack.translate(0, 0.01D, 0.07D);
        poseStack.scale(0.5F, 0.5F, 0.5F);

        int stackAmount = Math.min(entity.getStackAmount(), 64);

        for (int i = 0; i < stackAmount; i++) {
            int chipID = entity.getIDAt(i);
            if (chipID == 0) continue;

            poseStack.pushPose();

            Random randomX = new Random(i * 200000L);
            Random randomY = new Random(i * 100000L);

            poseStack.translate(
                    randomX.nextDouble() * 0.05D - 0.025D,
                    0,
                    randomY.nextDouble() * 0.05D - 0.025D
            );
            poseStack.mulPose(Axis.XN.rotationDegrees(90));

            CardHelper.renderItem(
                    new ItemStack(ItemPokerChip.getPokerChip(chipID)),
                    entity.level(),
                    0, 0, i * 0.032D,
                    poseStack, buffer, packedLight
            );

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityPokerChip entity) {
        return null; // Not used when rendering item stacks
    }
}
