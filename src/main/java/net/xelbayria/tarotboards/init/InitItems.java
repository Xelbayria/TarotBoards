package net.xelbayria.tarotboards.init;

import net.xelbayria.tarotboards.TarotBoards;
import net.xelbayria.tarotboards.block.BlockBarStool;
import net.xelbayria.tarotboards.block.BlockPokerTable;
import net.xelbayria.tarotboards.block.base.BlockItemBase;
import net.xelbayria.tarotboards.item.ItemCard;
import net.xelbayria.tarotboards.item.ItemCardCovered;
import net.xelbayria.tarotboards.item.ItemCardDeck;
import net.xelbayria.tarotboards.item.ItemPokerChip;
import net.xelbayria.tarotboards.PCReference;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InitItems {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PCReference.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PCReference.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PCReference.MOD_ID);

    //----- BLOCKS ------\\

    public static final RegistryObject<BlockPokerTable> POKER_TABLE = BLOCKS.register("poker_table", BlockPokerTable::new);
    public static final RegistryObject<Item> POKER_TABLE_ITEM = ITEMS.register("poker_table", () -> new BlockItemBase(POKER_TABLE.get()));

    public static final RegistryObject<BlockBarStool> BAR_STOOL = BLOCKS.register("bar_stool", BlockBarStool::new);
    public static final RegistryObject<Item> BAR_STOOL_ITEM = ITEMS.register("bar_stool", () -> new BlockItemBase(BAR_STOOL.get()));

    //public static final RegistryObject<Block> CASINO_CARPET_SPACE = BLOCKS.register("casino_carpet_space", BlockCasinoCarpet::new);
    //public static final RegistryObject<Item> CASINO_CARPET_SPACE_ITEM = ITEMS.register("casino_carpet_space", () -> new BlockItemBase(CASINO_CARPET_SPACE.get()));

    //----- ITEMS ------\\

    public static final RegistryObject<ItemCardDeck> CARD_DECK = ITEMS.register("card_deck", ItemCardDeck::new);
    public static final RegistryObject<ItemCardCovered> CARD_COVERED = ITEMS.register("card_covered", ItemCardCovered::new);

    public static final List<RegistryObject<Item>> cards = new ArrayList<>();

    public static void CARDS() {
        for (String wildName : TarotBoards.wilds) {
            cards.add(ITEMS.register(wildName.toLowerCase(Locale.ROOT), () -> new ItemCard(wildName)));
        }

        for (String suit : TarotBoards.suits) {
            for (String value : TarotBoards.values) {
                cards.add(ITEMS.register((value + "_of_" + suit).toLowerCase(Locale.ROOT), () -> new ItemCard(value + " of " + suit)));
            }
        }
    }

    public static final RegistryObject<Item> POKER_CHIP = ITEMS.register("poker_chip", () -> new ItemPokerChip(0, 0));

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register(PCReference.MOD_ID, () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(cards.get(0).get()))
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        ITEMS.getEntries().stream().filter(object -> !(object.get() instanceof ItemCardCovered)).forEach((registryObject) -> {
                            output.accept(new ItemStack(registryObject.get()));
                        });
                    }).title(Component.translatable("itemGroup." + PCReference.MOD_ID + ".tab"))
            .build());

    //public static final RegistryObject<Item> DICE_WHITE = ITEMS.register("dice_white", ItemDice::new);

    public static void init (IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CARDS();
        TABS.register(modEventBus);
    }
}
