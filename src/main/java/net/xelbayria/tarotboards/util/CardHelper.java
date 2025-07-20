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
import net.xelbayria.tarotboards.TarotBoard;

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
        Matcher matcher = TarotBoard.CARD_PATTERN.matcher(name);
        if (matcher.matches() && !TarotBoard.wilds.contains(name)) {
            // Process normal cards
            String value = matcher.group("value");
            String suit = matcher.group("suit");
            return Component.literal(value).append(" ").append(Component.literal("of").append(" ").append(Component.literal(suit))).withStyle(getStyle(suit));
        } else {
            return Component.literal(name).append("\n Wild").withStyle(ChatFormatting.WHITE);
        }
    }

    public static ChatFormatting getStyle(String suit) {
        if (INFERNAL_PACT_SUITS.contains(suit)) {
            return ChatFormatting.RED;
        } else if (AETHERIC_LOOM_SUITS.contains(suit)) {
            return ChatFormatting.AQUA;
        } else if (CELESTIAL_COURT_SUITS.contains(suit)) {
            return ChatFormatting.GOLD;
        } else if (VERDANT_CYCLE_SUITS.contains(suit)) {
            return ChatFormatting.GREEN;
        } else if (UMBRAL_DOMINION_SUITS.contains(suit)) {
            return ChatFormatting.LIGHT_PURPLE;
        } else {
            return ChatFormatting.WHITE;
        }
    }

    public static DyeColor getStyleDye(String suit) {
        if (INFERNAL_PACT_SUITS.contains(suit)) {
            return DyeColor.RED;
        } else if (AETHERIC_LOOM_SUITS.contains(suit)) {
            return DyeColor.BLUE;
        } else if (CELESTIAL_COURT_SUITS.contains(suit)) {
            return DyeColor.YELLOW;
        } else if (VERDANT_CYCLE_SUITS.contains(suit)) {
            return DyeColor.GREEN;
        } else if (UMBRAL_DOMINION_SUITS.contains(suit)) {
            return DyeColor.PURPLE;
        } else {
            return DyeColor.WHITE;
        }
    }

    // List of suits categorized by color
    public static final List<String> CELESTIAL_COURT_SUITS = List.of(
            "Stars", "Suns", "Crowns", "Quasars", "Crescents", "Sigils", "Comets", "Glyphs"
    );

    public static final List<String> UMBRAL_DOMINION_SUITS = List.of(
            "Veils", "Runes", "Hearts", "Spirals", "Eyes", "Omens", "Diamonds", "Orbs"
    );

    public static final List<String> INFERNAL_PACT_SUITS = List.of(
            "Arrows", "Flames", "Locks", "Arcs", "Swords", "Points", "Embers", "Gears"
    );

    public static final List<String> VERDANT_CYCLE_SUITS = List.of(
            "Flowers", "Leaves", "Mountains", "Shells", "Clovers", "Tridents", "Trees", "Waves"
    );

    public static final List<String> AETHERIC_LOOM_SUITS = List.of(
            "Clouds", "Crosses", "Shields", "Keys", "Spades", "Scrolls", "Looms", "Shards"
    );
}
