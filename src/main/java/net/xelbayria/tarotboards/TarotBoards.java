package net.xelbayria.tarotboards;

import net.xelbayria.tarotboards.datagen.TarotBoardsProvider;
import net.xelbayria.tarotboards.entity.data.PCDataSerializers;
import net.xelbayria.tarotboards.event.CardInteractEvent;
import net.xelbayria.tarotboards.init.InitEntityTypes;
import net.xelbayria.tarotboards.network.ModNetworking;
import net.xelbayria.tarotboards.render.*;
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

@Mod(PCReference.MOD_ID)
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
        EntityRenderers.register(InitEntityTypes.DICE.get(), RenderEntityDice::new);
        EntityRenderers.register(InitEntityTypes.SEAT.get(), RenderEntitySeat::new);
    }
}