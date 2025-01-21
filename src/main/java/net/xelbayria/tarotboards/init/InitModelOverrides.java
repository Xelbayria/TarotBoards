package net.xelbayria.tarotboards.init;

import net.xelbayria.tarotboards.util.ItemHelper;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class InitModelOverrides {

    public static void init() {
        ItemProperties.register(InitItems.CARD.get(), new ResourceLocation("value"), (stack, world, player, seed) -> stack.getDamageValue());
        ItemProperties.register(InitItems.CARD_COVERED.get(), new ResourceLocation("skin"), (stack, world, player, seed) -> ItemHelper.getNBT(stack).getByte("SkinID"));
        ItemProperties.register(InitItems.CARD_DECK.get(), new ResourceLocation("skin"), (stack, world, player, seed) -> ItemHelper.getNBT(stack).getByte("SkinID"));
    }
}
