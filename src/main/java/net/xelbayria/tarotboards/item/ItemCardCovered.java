package net.xelbayria.tarotboards.item;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.xelbayria.tarotboards.TarotBoards;
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

import java.util.*;

public class ItemCardCovered extends ItemBase {

    public ItemCardCovered() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("lore.cover").append(" ").withStyle(ChatFormatting.GRAY));
    }

    private Set<Integer> usedCardIDs = new HashSet<>();

    private int generateValidCardID() {
        int cardID;
        Random random = new Random();
        do {
            cardID = random.nextInt(TarotBoards.NUM_CARDS); // Generate random ID
        } while (usedCardIDs.contains(cardID)); // Keep generating if ID is already used

        usedCardIDs.add(cardID); // Mark this ID as used
        return cardID;
    }

    public void flipCard(ItemStack heldItem, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (heldItem.getItem() instanceof ItemCardCovered) {
                CompoundTag heldNBT = ItemHelper.getNBT(heldItem);

                // Initialize flip history if it doesn't exist
                if (!heldNBT.contains("FlipHistory")) {
                    heldNBT.put("FlipHistory", new ListTag());
                }

                ListTag flipHistory = heldNBT.getList("FlipHistory", Tag.TAG_INT);

                int cardID;

                if (heldNBT.getBoolean("Covered")) {
                    // If uncovering: Get the last card from history or generate a new one
                    if(!ItemHelper.getNBT(heldItem).contains("CardID")) {
                        if (!flipHistory.isEmpty()) {
                            cardID = heldNBT.getInt("CardID");
                        } else {
                            cardID = generateValidCardID();
                            flipHistory.add(IntTag.valueOf(cardID));
                            ItemHelper.getNBT(heldItem).putInt("CardID", cardID);
                        }
                    } else {
                        if (!flipHistory.isEmpty()) {
                            cardID = heldNBT.getInt("CardID");
                            flipHistory.remove(flipHistory.size() - 1);
                        } else {
                            cardID = heldNBT.getInt("CardID");
                        }
                    }
                } else {
                    cardID = heldNBT.getInt("CardID");
                }

                // Use the cardID to fetch the specific card
                Item nextCard = InitItems.cards.get(cardID).get();

                ItemStack newCard = new ItemStack(nextCard);
                ItemHelper.getNBT(newCard).putInt("CardID", cardID);
                ItemHelper.getNBT(newCard).putUUID("UUID", heldNBT.getUUID("UUID"));
                ItemHelper.getNBT(newCard).putBoolean("Covered", !heldNBT.getBoolean("Covered"));
                ItemHelper.getNBT(newCard).put("FlipHistory", flipHistory); // Save updated history

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
                        EntityCard cardDeck = new EntityCard(world, pContext.getClickLocation(), pContext.getRotation(), deckID, nbt.getBoolean("Covered"), nbt.getInt("CardID"));
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
