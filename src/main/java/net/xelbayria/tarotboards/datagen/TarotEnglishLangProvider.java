package net.xelbayria.tarotboards.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import net.xelbayria.tarotboards.init.InitEntityTypes;
import net.xelbayria.tarotboards.init.InitItems;
import org.apache.commons.lang3.text.WordUtils;

public class TarotEnglishLangProvider extends LanguageProvider {
    public TarotEnglishLangProvider(PackOutput generator) {
        super(generator, "tarotboards", "en_us");
    }

    @Override
    protected void addTranslations() {
        for(var card : InitItems.cards) {
            addItem(card);
        }

        for(var chip : InitItems.poker_chips) {
            addItem(chip);
        }

        addEntityType(InitEntityTypes.CARD, "Card");
        addEntityType(InitEntityTypes.CARD_DECK, "Card Deck");
        addEntityType(InitEntityTypes.POKER_CHIP, "Poker Chip");

        addBlock(InitItems.POKER_TABLE);
        addBlock(InitItems.BAR_STOOL);
        addItem(InitItems.CARD_COVERED);
        addItem(InitItems.CARD_DECK);
        add(InitItems.TAB, "TarotBoards");
    }

    private void addItem(RegistryObject<Item> registryObject) {
        addItem(registryObject, WordUtils.capitalizeFully(registryObject.getId().getPath().replace("_", " ")));
    }

    private <T extends Block> void addBlock(RegistryObject<T> registryObject) {
        addBlock(registryObject, WordUtils.capitalizeFully(registryObject.getId().getPath().replace("_", " ")));
    }

    private void add(RegistryObject<CreativeModeTab> tab, String entry) {
        this.add("itemGroup." + tab.getId().toLanguageKey(), entry);
    }
}
