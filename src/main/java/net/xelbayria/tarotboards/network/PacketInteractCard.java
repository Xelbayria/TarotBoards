package net.xelbayria.tarotboards.network;

import net.minecraft.network.FriendlyByteBuf;
import net.xelbayria.tarotboards.item.ItemCardCovered;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketInteractCard {
    private final String command;

    public PacketInteractCard (String command) {
        this.command = command;
    }

    public PacketInteractCard (FriendlyByteBuf buf) {
        command = buf.readUtf(11).trim();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(command, 11);
    }

    public static void handle(PacketInteractCard packet, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                if (packet.command.equalsIgnoreCase("flipinv")) {

                    Item item = player.getMainHandItem().getItem();

                    if (item instanceof ItemCardCovered) {
                        ItemCardCovered card = (ItemCardCovered)player.getMainHandItem().getItem();
                        card.flipCard(player.getMainHandItem(), player);
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
