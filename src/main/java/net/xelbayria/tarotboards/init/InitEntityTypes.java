package net.xelbayria.tarotboards.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.xelbayria.tarotboards.entity.*;
import net.xelbayria.tarotboards.CommonClass;
import net.xelbayria.tarotboards.PCReference;

public class InitEntityTypes {

    //TODO: CHANGE THIS TO USE TAROT CARDS
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PCReference.MOD_ID);

    public static final RegistryObject<EntityType<EntityCard>> CARD = ENTITY_TYPES.register("card", () -> EntityType.Builder.<EntityCard>of(EntityCard::new, MobCategory.MISC).sized(0.5F, 0.5F).build(CommonClass.customLocation("card").toString()));
    public static final RegistryObject<EntityType<EntityCardDeck>> CARD_DECK = ENTITY_TYPES.register("card_deck", () -> EntityType.Builder.<EntityCardDeck>of(EntityCardDeck::new, MobCategory.MISC).sized(0.5F, 0.5F).build(CommonClass.customLocation("card_deck").toString()));
    public static final RegistryObject<EntityType<EntityPokerChip>> POKER_CHIP = ENTITY_TYPES.register("poker_chip", () -> EntityType.Builder.<EntityPokerChip>of(EntityPokerChip::new, MobCategory.MISC).sized(0.3F, 0.3F).build(CommonClass.customLocation("poker_chip").toString()));
    public static final RegistryObject<EntityType<EntityDice>> DICE = ENTITY_TYPES.register("dice", () -> EntityType.Builder.<EntityDice>of(EntityDice::new, MobCategory.MISC).sized(0.3F, 0.3F).build(CommonClass.customLocation("dice").toString()));
    public static final RegistryObject<EntityType<EntitySeat>> SEAT = ENTITY_TYPES.register("seat", () -> EntityType.Builder.<EntitySeat>of(EntitySeat::new, MobCategory.MISC).sized(0, 0).build(CommonClass.customLocation("seat").toString()));

    public static void init(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }


}
