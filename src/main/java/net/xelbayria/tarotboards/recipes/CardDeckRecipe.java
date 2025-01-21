package net.xelbayria.tarotboards.recipes;

import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.init.InitRecipes;
import net.xelbayria.tarotboards.util.ItemHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CardDeckRecipe extends CustomRecipe {
    public CardDeckRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {

        boolean matches = true;

        for (int i = 0; i < pContainer.getContainerSize(); ++i) {

            ItemStack stackInSlot = pContainer.getItem(i);

            if (i != 4) {

                if (stackInSlot.getItem() != Items.PAPER) {
                    matches = false;
                }
            }
        }

        ItemStack middleSlot = pContainer.getItem(4);

        if (middleSlot.getItem() != Items.BLUE_DYE && middleSlot.getItem() != Items.RED_DYE && middleSlot.getItem() != Items.BLACK_DYE && middleSlot.getItem() != Items.PINK_DYE) {
            matches = false;
        }

        return matches;
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {

        ItemStack result = new ItemStack(InitItems.CARD_DECK.get());
        CompoundTag nbt = ItemHelper.getNBT(result);

        ItemStack middleSlot = pContainer.getItem(4);

        if (middleSlot.getItem() == Items.RED_DYE) {
            nbt.putByte("SkinID", (byte)1);
        }

        else if (middleSlot.getItem() == Items.BLACK_DYE) {
            nbt.putByte("SkinID", (byte)2);
        }

        else if (middleSlot.getItem() == Items.PINK_DYE) {
            nbt.putByte("SkinID", (byte)3);
        }

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 9;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return InitRecipes.DECK.get();
    }
}
