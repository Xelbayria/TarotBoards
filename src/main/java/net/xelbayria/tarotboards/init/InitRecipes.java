package net.xelbayria.tarotboards.init;

import net.xelbayria.tarotboards.TBConstants;
import net.xelbayria.tarotboards.recipes.CardDeckRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TBConstants.MOD_ID);

    public static final RegistryObject<RecipeSerializer<CardDeckRecipe>> DECK = RECIPES.register("deck", () -> new SimpleCraftingRecipeSerializer<>(CardDeckRecipe::new));

    public static void init(IEventBus modEventBus) {
        RECIPES.register(modEventBus);
    }
}
