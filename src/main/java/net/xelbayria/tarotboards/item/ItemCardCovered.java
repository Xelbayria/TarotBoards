package net.xelbayria.tarotboards.item;

import net.xelbayria.tarotboards.entity.EntityCard;
import net.xelbayria.tarotboards.entity.EntityCardDeck;
import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.item.base.ItemBase;
import net.xelbayria.tarotboards.util.CardHelper;
import net.xelbayria.tarotboards.util.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ItemCardCovered extends ItemBase {

    public ItemCardCovered() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        CompoundTag nbt = ItemHelper.getNBT(pStack);
        pTooltipComponents.add(Component.translatable("lore.cover").append(" ").withStyle(ChatFormatting.GRAY).append(Component.translatable(CardHelper.CARD_SKIN_NAMES[nbt.getByte("SkinID")]).withStyle(ChatFormatting.AQUA)));
    }

    public void flipCard(ItemStack heldItem, LivingEntity entity) {

        if (entity instanceof Player player) {

            if (heldItem.getItem() instanceof ItemCardCovered) {
                CompoundTag heldNBT = ItemHelper.getNBT(heldItem);

                Item nextCard = InitItems.CARD.get();
                if (!heldNBT.getBoolean("Covered")) nextCard = InitItems.CARD_COVERED.get();

                ItemStack newCard = new ItemStack(nextCard);
                newCard.setDamageValue(heldItem.getDamageValue());

                ItemHelper.getNBT(newCard).putUUID("UUID", heldNBT.getUUID("UUID"));
                ItemHelper.getNBT(newCard).putByte("SkinID", heldNBT.getByte("SkinID"));
                ItemHelper.getNBT(newCard).putBoolean("Covered", !heldNBT.getBoolean("Covered"));

                player.setItemInHand(InteractionHand.MAIN_HAND, newCard);
            }
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pLevel.getGameTime() % 60 == 0) {

            if (pEntity instanceof Player player) {
                BlockPos pos = player.blockPosition();

                CompoundTag nbt = ItemHelper.getNBT(pStack);

                if (nbt.hasUUID("UUID")) {
                    UUID id = ItemHelper.getNBT(pStack).getUUID("UUID");

                    if (id.getLeastSignificantBits() == 0) {
                        return;
                    }

                    List<EntityCardDeck> closeDecks = pLevel.getEntitiesOfClass(EntityCardDeck.class, new AABB(pos.getX() - 20, pos.getY() - 20, pos.getZ() - 20, pos.getX() + 20, pos.getY() + 20, pos.getZ() + 20));

                    boolean found = false;

                    for (EntityCardDeck closeDeck : closeDecks) {

                        if (closeDeck.getUUID().equals(id)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        player.getInventory().getItem(pSlotId).shrink(1);
                    }
                }
            }
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();

        if (player != null) {

            if (!player.isCrouching()) {

                BlockPos pos = pContext.getClickedPos();
                List<EntityCardDeck> closeDecks = pContext.getLevel().getEntitiesOfClass(EntityCardDeck.class, new AABB(pos.getX() - 8, pos.getY() - 8, pos.getZ() - 8, pos.getX() + 8, pos.getY() + 8, pos.getZ() + 8));

                CompoundTag nbt = ItemHelper.getNBT(pContext.getItemInHand());

                UUID deckID = nbt.getUUID("UUID");

                for (EntityCardDeck closeDeck : closeDecks) {

                    if (closeDeck.getUUID().equals(deckID)) {

                        Level world = pContext.getLevel();
                        EntityCard cardDeck = new EntityCard(world, pContext.getClickLocation(), pContext.getRotation(), nbt.getByte("SkinID"), deckID, nbt.getBoolean("Covered"), (byte) pContext.getItemInHand().getDamageValue());
                        world.addFreshEntity(cardDeck);
                        pContext.getItemInHand().shrink(1);

                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }
}
