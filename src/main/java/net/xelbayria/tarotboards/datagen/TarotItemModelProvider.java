package net.xelbayria.tarotboards.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.xelbayria.tarotboards.TBConstants;
import net.xelbayria.tarotboards.init.InitItems;

public class TarotItemModelProvider extends ItemModelProvider {
    public TarotItemModelProvider(PackOutput generator, ExistingFileHelper existingFileHelper) {
        super(generator, TBConstants.MOD_ID, existingFileHelper);
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
        withExistingParent(item.getId().getPath(), new ResourceLocation(TBConstants.MOD_ID, "card" ));
    }

    private void withParentChip(RegistryObject<Item> item) {
        withExistingParent(item.getId().getPath(), new ResourceLocation(TBConstants.MOD_ID, "poker_chip" ));
    }
}
