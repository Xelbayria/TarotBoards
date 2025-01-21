package net.xelbayria.tarotboards.event;

import net.xelbayria.tarotboards.util.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.xelbayria.tarotboards.item.ItemCardDeck;

public class CraftingEvent {

    @SubscribeEvent
    public void onCrafted(PlayerEvent.ItemCraftedEvent event) {

        if (event.getCrafting().getItem() instanceof ItemCardDeck) {

            if (event.getInventory().getItem(4).getItem() == Items.RED_DYE) {
                CompoundTag nbt = ItemHelper.getNBT(event.getCrafting());
                nbt.putByte("SkinID", (byte)1);
            }
        }
    }
}
