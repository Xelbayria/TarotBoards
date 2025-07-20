package net.xelbayria.tarotboards.item;

import net.minecraftforge.registries.RegistryObject;
import net.xelbayria.tarotboards.entity.EntityPokerChip;
import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.item.base.ItemBase;
import net.xelbayria.tarotboards.util.StringHelper;
import net.minecraft.ChatFormatting;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemPokerChip extends ItemBase {

    private final int chipID;
    private final long value;  // Changed to long

    public ItemPokerChip(int chipID, long value) {  // Constructor updated
        super(new Properties());
        this.chipID = chipID;
        this.value = value;
    }

    public int getChipID() {
        return this.chipID;
    }

    public long getValue() {  // Getter for value
        return this.value;
    }

    public static Item getPokerChip(int pokerChipID) {
        RegistryObject<Item> item = InitItems.poker_chips.get(pokerChipID);
        if (item == null) throw new IllegalArgumentException("No poker chip for ID: " + pokerChipID);
        return item.get();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal(ChatFormatting.GRAY + "Value (1): " + ChatFormatting.GOLD + value));

        if (pStack.getCount() > 1) {
            long totalValue = value * (long) pStack.getCount();
            pTooltipComponents.add(Component.literal(ChatFormatting.GRAY + "Value (" + pStack.getCount() + "): " + ChatFormatting.GOLD + StringHelper.printCommas(totalValue)));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();

        if (player != null && !player.isCrouching()) {
            Level world = pContext.getLevel();

            EntityPokerChip chip = new EntityPokerChip(world, pContext.getClickLocation(), chipID);
            world.addFreshEntity(chip);
            pContext.getItemInHand().shrink(1);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
