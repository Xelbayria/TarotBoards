package net.xelbayria.tarotboards.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.xelbayria.tarotboards.init.InitItems;

public class TarotItemModelProvider extends ItemModelProvider {
    public TarotItemModelProvider(PackOutput generator, ExistingFileHelper existingFileHelper) {
        super(generator, "tarotboards", existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for(var card : InitItems.cards) {
            withParentCard(card);
        }

        for(var chip : InitItems.poker_chips) {
            withParentChip(chip);
        }
    }

    private void withParentCard(RegistryObject<Item> item) {
        withExistingParent(item.getId().getPath(), new ResourceLocation("tarotboards", "card" ));
    }

    private void withParentChip(RegistryObject<Item> item) {
        withExistingParent(item.getId().getPath(), new ResourceLocation("tarotboards", "poker_chip" ));
    }
}
