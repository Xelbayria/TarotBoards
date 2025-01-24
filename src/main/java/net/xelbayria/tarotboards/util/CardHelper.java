package net.xelbayria.tarotboards.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.xelbayria.tarotboards.TarotBoards;

import java.util.List;
import java.util.regex.Matcher;

public class CardHelper {

    public static void renderItem(ItemStack stack, Level level, double offsetX, double offsetY, double offsetZ, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight) {
        matrixStack.pushPose();
        matrixStack.translate(offsetX, offsetY, offsetZ);
        var renderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model = renderer.getModel(stack, level, null, 0);
        renderer.render(stack, ItemDisplayContext.GROUND, false, matrixStack, buffer, combinedLight, OverlayTexture.NO_OVERLAY, model);
        matrixStack.popPose();
    }

    public static MutableComponent getCardName(String name) {
        Matcher matcher = TarotBoards.CARD_PATTERN.matcher(name);
        if (matcher.matches() && !TarotBoards.wilds.contains(name)) {
            // Process normal cards
            String value = matcher.group("value");
            String suit = matcher.group("suit");
            return Component.literal(value).append(" ").append(Component.literal("of").append(" ").append(Component.literal(suit))).withStyle(getStyle(suit));
        } else {
            return Component.literal(name).append("\n Wild").withStyle(ChatFormatting.WHITE);
        }
    }

    public static ChatFormatting getStyle(String suit) {
        if (RED_SUITS.contains(suit)) {
            return ChatFormatting.RED;
        } else if (BLUE_SUITS.contains(suit)) {
            return ChatFormatting.AQUA;
        } else if (YELLOW_SUITS.contains(suit)) {
            return ChatFormatting.YELLOW;
        } else if (GREEN_SUITS.contains(suit)) {
            return ChatFormatting.GREEN;
        } else if (PURPLE_SUITS.contains(suit)) {
            return ChatFormatting.LIGHT_PURPLE;
        } else {
            return ChatFormatting.WHITE;
        }
    }

    public static DyeColor getStyleDye(String suit) {
        if (RED_SUITS.contains(suit)) {
            return DyeColor.RED;
        } else if (BLUE_SUITS.contains(suit)) {
            return DyeColor.BLUE;
        } else if (YELLOW_SUITS.contains(suit)) {
            return DyeColor.YELLOW;
        } else if (GREEN_SUITS.contains(suit)) {
            return DyeColor.GREEN;
        } else if (PURPLE_SUITS.contains(suit)) {
            return DyeColor.PURPLE;
        } else {
            return DyeColor.WHITE;
        }
    }

    // List of suits categorized by color
    public static final List<String> BLUE_SUITS = List.of(
            "Arcs", "Spades", "Clouds", "Clovers", "Comets", "Crescents", "Crosses", "Crowns"
    );

    public static final List<String> RED_SUITS = List.of(
            "Diamonds", "Embers", "Eyes", "Gears", "Glyphs", "Flames", "Flowers", "Hearts"
    );

    public static final List<String> YELLOW_SUITS = List.of(
            "Arrows", "Keys", "Locks", "Leaves", "Mountains", "Points", "Scrolls", "Shells"
    );

    public static final List<String> GREEN_SUITS = List.of(
            "Shields", "Spirals", "Stars", "Suns", "Swords", "Tridents", "Trees", "Waves"
    );

    public static final List<String> PURPLE_SUITS = List.of(
            "Quasars", "Runes", "Omens", "Sigils", "Orbs", "Veils", "Looms", "Shards"
    );
}
