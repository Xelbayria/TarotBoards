package net.xelbayria.tarotboards;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.xelbayria.tarotboards.datagen.TarotBoardsProvider;
import net.xelbayria.tarotboards.entity.data.PCDataSerializers;
import net.xelbayria.tarotboards.event.CardInteractEvent;
import net.xelbayria.tarotboards.init.InitEntityTypes;
import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.network.ModNetworking;
import net.xelbayria.tarotboards.client.*;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;
import java.util.regex.Pattern;

@Mod(TBConstants.MOD_ID)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class TarotBoards {

    public TarotBoards() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CommonClass.init(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        EntityDataSerializers.registerSerializer(PCDataSerializers.STACK);
        TarotBoardsProvider.init(modEventBus);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        ModNetworking.registerPackets();
        MinecraftForge.EVENT_BUS.register(new CardInteractEvent());
    }

    public static final Pattern CARD_PATTERN = Pattern.compile("^(?<value>[\\d,a-z,A-Z]+) of (?<suit>[a-z,A-Z]+)$");

    public static final List<String> wilds = List.of(
            "Joker", "Soul", "Light", "Dark", "Judgement", "Chorus", "Life", "Death", "Wrath",
            "Pride", "Greed", "Lust", "Envy", "Gluttony", "Sloth", "Chasity", "Temperance", "Charity",
            "Diligence", "Kindness", "Patience", "Humility", "Voice", "Voices", "Mother", "Father", "Brother",
            "Sister", "Duality", "Accord", "Husband", "Wife", "Progeny", "Corridor", "Field", "Intellect", "Brawn",
            "Despair", "Past", "Present", "Future", "Gate", "Sign", "Ruin", "Snow", "Rain", "Tempest", "Lovers",
            "Discord", "Concord", "Harmony", "Dissonance", "Earth", "Fire", "Water", "Air", "Spirit",
            "Oblivion", "Obscurity", "Purgatory", "Nether", "Underworld", "Aether", "Overworld", "Limbo", "Chaos",
            "Balance", "Doom", "Peace", "Evil", "Good", "Neutral", "Hope"
    );

    public static final List<String> suits = List.of(
            "Arcs", "Arrows", "Clouds", "Clovers", "Comets", "Crescents", "Crosses",
            "Crowns", "Diamonds", "Embers", "Eyes", "Gears", "Glyphs", "Flames", "Flowers",
            "Hearts", "Keys", "Locks", "Leaves", "Mountains", "Points", "Scrolls", "Shells",
            "Shields", "Spades", "Spirals", "Stars", "Suns", "Swords", "Tridents", "Trees", "Waves",
            "Quasars", "Runes", "Omens", "Sigils", "Orbs", "Veils", "Looms", "Shards"
    );

    public static final List<String> values = List.of(
            //Negative Cards
            "Shadow", "Specter", "Phantom", "Void", "Wraith",
            "Ghoul", "Banshee", "Reverent", "Eidolon", "Shade",
            "Doppelganger", "Hollow", "Abyss", "Chimera", "Poltergeist",
            "Wight", "Apparition", "Nightmare", "Succubus", "Incubus",
            "Necromancer", "Fury", "Grim", "Harbinger", "Spectacle",
            "Lich", "Gorgon", "Drake", "Demon", "Frost",
            "Golem", "Hydra", "Inferno", "Juggernaut", "Kraken", "Reaper",
            "Leviathan", "Manticore", "Naga", "Blight", "Serpent",

            //Neutral Card
            "Hold",

            //Positive Cards
            "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "Jack", "Queen", "King", "Nomad", "Prince",
            "Rune", "Fable", "Sorceress", "Utopia", "Wizard",
            "Titan", "Baron", "Illusionist", "Oracle", "Magician",
            "Luminary", "Eclipse", "Celestial", "Duke", "Genesis",
            "Zephyr", "Vesper", "Umbra", "Valkyrie",
            "Warden", "Zenith", "Yggdrasil", "Zodiac", "Phoenix", "Raven", "Cipher"
    );

    public static final int NUM_CARDS = (suits.size() * values.size()) + wilds.size();

    private void onClientSetup(final FMLClientSetupEvent event) {
        EntityRenderers.register(InitEntityTypes.CARD.get(), RenderEntityCard::new);
        EntityRenderers.register(InitEntityTypes.CARD_DECK.get(), RenderEntityCardDeck::new);
        EntityRenderers.register(InitEntityTypes.POKER_CHIP.get(), RenderEntityPokerChip::new);
        EntityRenderers.register(InitEntityTypes.SEAT.get(), RenderEntitySeat::new);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerItemColor(RegisterColorHandlersEvent.Item event) {
        ItemColor WHITE = (arg, i) -> 0xf9fffe;
        ItemColor ORANGE = (arg, i) -> 0xf9801d;
        ItemColor MAGENTA = (arg, i) -> 0xc74ebd;
        ItemColor LIGHT_BLUE = (arg, i) -> 0x3ab3da;
        ItemColor YELLOW = (arg, i) -> 0xfed83d;
        ItemColor LIME = (arg, i) -> 0x80c71f;
        ItemColor PINK = (arg, i) -> 0xf38baa;
        ItemColor GRAY = (arg, i) -> 0x474f52;
        ItemColor LIGHT_GRAY = (arg, i) -> 0x9d9d97;
        ItemColor CYAN = (arg, i) -> 0x169c9c;
        ItemColor PURPLE = (arg, i) -> 0x8932b8;
        ItemColor BLUE = (arg, i) -> 0x3c44aa;
        ItemColor BROWN = (arg, i) -> 0x835432;
        ItemColor GREEN = (arg, i) -> 0x5e7c16;
        ItemColor RED = (arg, i) -> 0xb02e26;
        ItemColor BLACK = (arg, i) -> 0x1d1d21;

        event.register(WHITE, InitItems.poker_chips.get(DyeColor.WHITE.getId()).get());
        event.register(ORANGE, InitItems.poker_chips.get(DyeColor.ORANGE.getId()).get());
        event.register(MAGENTA, InitItems.poker_chips.get(DyeColor.MAGENTA.getId()).get());
        event.register(LIGHT_BLUE, InitItems.poker_chips.get(DyeColor.LIGHT_BLUE.getId()).get());
        event.register(YELLOW, InitItems.poker_chips.get(DyeColor.YELLOW.getId()).get());
        event.register(LIME, InitItems.poker_chips.get(DyeColor.LIME.getId()).get());
        event.register(PINK, InitItems.poker_chips.get(DyeColor.PINK.getId()).get());
        event.register(GRAY, InitItems.poker_chips.get(DyeColor.GRAY.getId()).get());
        event.register(LIGHT_GRAY, InitItems.poker_chips.get(DyeColor.LIGHT_GRAY.getId()).get());
        event.register(CYAN, InitItems.poker_chips.get(DyeColor.CYAN.getId()).get());
        event.register(PURPLE, InitItems.poker_chips.get(DyeColor.PURPLE.getId()).get());
        event.register(BLUE, InitItems.poker_chips.get(DyeColor.BLUE.getId()).get());
        event.register(BROWN, InitItems.poker_chips.get(DyeColor.BROWN.getId()).get());
        event.register(GREEN, InitItems.poker_chips.get(DyeColor.GREEN.getId()).get());
        event.register(RED, InitItems.poker_chips.get(DyeColor.RED.getId()).get());
        event.register(BLACK, InitItems.poker_chips.get(DyeColor.BLACK.getId()).get());
    }
}