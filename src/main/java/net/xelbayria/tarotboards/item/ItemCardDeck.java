package net.xelbayria.tarotboards.item;

import net.xelbayria.tarotboards.entity.EntityCardDeck;
import net.xelbayria.tarotboards.item.base.ItemBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemCardDeck extends ItemBase {

    public ItemCardDeck() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("lore.cover").append(" ").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level world = pContext.getLevel();
        if (!world.isClientSide) {
            EntityCardDeck cardDeck = new EntityCardDeck(world, pContext.getClickLocation(), pContext.getRotation());
            world.addFreshEntity(cardDeck);
            pContext.getItemInHand().shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
}
