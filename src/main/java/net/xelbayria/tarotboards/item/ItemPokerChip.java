package net.xelbayria.tarotboards.item;

import net.xelbayria.tarotboards.entity.EntityPokerChip;
import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.item.base.ItemBase;
import net.xelbayria.tarotboards.tileentity.TileEntityPokerTable;
import net.xelbayria.tarotboards.util.ItemHelper;
import net.xelbayria.tarotboards.util.Location;
import net.xelbayria.tarotboards.util.StringHelper;
import net.xelbayria.tarotboards.util.UnitChatMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ItemPokerChip extends ItemBase {

    private final int chipID;
    private final int value;

    public ItemPokerChip(int chipID, int value) {
        super(new Properties());
        this.chipID = chipID;
        this.value = value;
    }

    private UnitChatMessage getUnitMessage(Player... players) {
        return new UnitChatMessage("poker_chip", players);
    }

    public int getChipID() {
        return this.chipID;
    }

    public static Item getPokerChip(int pokerChipID) {
        return InitItems.poker_chips.get(pokerChipID).get();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        CompoundTag nbt = ItemHelper.getNBT(pStack);

        if (nbt.hasUUID("OwnerID")) {
            pTooltipComponents.add(Component.literal(ChatFormatting.GRAY + "Owner: " + ChatFormatting.GOLD + nbt.getString("OwnerName")));
        }

        else pTooltipComponents.add(Component.literal(ChatFormatting.GRAY + "Owner: " + ChatFormatting.GOLD + "Not set"));

        pTooltipComponents.add(Component.literal(ChatFormatting.GRAY + "Value (1): " + ChatFormatting.GOLD + value));

        if (pStack.getCount() > 1) {
            pTooltipComponents.add(Component.literal(ChatFormatting.GRAY + "Value (" + pStack.getCount() + "): " + ChatFormatting.GOLD + StringHelper.printCommas(value * pStack.getCount())));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack heldItem = pPlayer.getItemInHand(pUsedHand);

        if (pPlayer.isCrouching()) {

            UnitChatMessage unitMessage = getUnitMessage(pPlayer);
            CompoundTag nbt = ItemHelper.getNBT(heldItem);

            if (!nbt.hasUUID("OwnerID")) {

                nbt.putUUID("OwnerID", pPlayer.getUUID());
                nbt.putString("OwnerName", pPlayer.getDisplayName().getString());

                if (pLevel.isClientSide) unitMessage.printMessage(ChatFormatting.GREEN, Component.translatable("message.poker_chip_owner_set"));
            }

            else if (pLevel.isClientSide) unitMessage.printMessage(ChatFormatting.RED, Component.translatable("message.poker_chip_owner_error"));

            return InteractionResultHolder.success(heldItem);
        }

        return InteractionResultHolder.fail(heldItem);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {

        Player player = pContext.getPlayer();

        if (player != null) {

            if (!player.isCrouching()) {

                Level world = pContext.getLevel();
                Location location = new Location(world, pContext.getClickedPos());

                UnitChatMessage unitMessage = getUnitMessage(player);
                CompoundTag nbt = ItemHelper.getNBT(pContext.getItemInHand());

                if (nbt.hasUUID("OwnerID")) {

                    UUID ownerID = nbt.getUUID("OwnerID");
                    String ownerName = nbt.getString("OwnerName");

                    if (location.getBlockState().hasBlockEntity()) {

                        BlockEntity tileEntity = location.getTileEntity();

                        if (tileEntity instanceof TileEntityPokerTable pokerTable) {

                            if (!ownerID.equals(pokerTable.getOwnerID())) {

                                if (world.isClientSide) unitMessage.printMessage(ChatFormatting.RED, Component.translatable("message.poker_chip_table_error"));
                                return InteractionResult.PASS;
                            }
                        }
                    }

                    EntityPokerChip chip = new EntityPokerChip(world, pContext.getClickLocation(), ownerID, ownerName, chipID);
                    world.addFreshEntity(chip);
                    pContext.getItemInHand().shrink(1);
                }

                else if (world.isClientSide) unitMessage.printMessage(ChatFormatting.RED, Component.translatable("message.poker_chip_owner_missing"));

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }
}
