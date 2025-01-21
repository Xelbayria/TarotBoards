package net.xelbayria.tarotboards.event;

import net.xelbayria.tarotboards.item.ItemCardCovered;
import net.xelbayria.tarotboards.network.ModNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CardInteractEvent {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onLeftClick(InputEvent.MouseButton event) {

        Minecraft mc = Minecraft.getInstance();

        if (mc.screen == null) {

            if (event.getAction() == 1 && event.getButton() == 0) {

                Player player = mc.player;

                if (mc.level != null && player != null) {

                    ItemStack heldStack = player.getMainHandItem();

                    if (heldStack.getItem() instanceof ItemCardCovered card) {
                        card.flipCard(heldStack, player);

                        ModNetworking.cardInteract("flipinv");
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
