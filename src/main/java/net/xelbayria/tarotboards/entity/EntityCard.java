package net.xelbayria.tarotboards.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.xelbayria.tarotboards.entity.base.EntityStacked;
import net.xelbayria.tarotboards.init.InitEntityTypes;
import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.util.ArrayHelper;
import net.xelbayria.tarotboards.util.ChatHelper;
import net.xelbayria.tarotboards.util.ItemHelper;

import java.util.Optional;
import java.util.UUID;

public class EntityCard extends EntityStacked {

    private static final EntityDataAccessor<Optional<UUID>> DECK_UUID = SynchedEntityData.defineId(EntityCard.class, net.minecraft.network.syncher.EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Float> ROTATION = SynchedEntityData.defineId(EntityCard.class, net.minecraft.network.syncher.EntityDataSerializers.FLOAT);

    public EntityCard(EntityType<? extends EntityCard> type, Level world) {
        super(type, world);
    }

    public EntityCard(Level world, Vec3 position, float rotation, UUID deckUUID, int firstCardID, boolean covered) {
        super(InitEntityTypes.CARD.get(), world, position);
        createStack();
        addToTop(firstCardID, covered);
        this.entityData.set(ROTATION, rotation);
        this.entityData.set(DECK_UUID, Optional.ofNullable(deckUUID));
    }

    @Override
    public void moreData() {
        this.entityData.define(DECK_UUID, Optional.empty());
        this.entityData.define(ROTATION, 0.0f);
    }

    @Override
    public int getStackAmount() {
        return this.entityData.get(STACK_IDS).length;
    }

    public int getCardIDAt(int index) {
        Integer[] ids = this.entityData.get(STACK_IDS);
        if (index >= 0 && index < ids.length) {
            return ids[index];
        }
        return 0;
    }

    public boolean isCoveredAt(int index) {
        Boolean[] covered = this.entityData.get(STACK_COVERED);
        if (index >= 0 && index < covered.length) {
            return covered[index];
        }
        return true;
    }

    public CardStackEntry getTopEntry() {
        int amount = getStackAmount();
        if (amount == 0) return null;
        int topIndex = amount - 1;
        return new CardStackEntry(getCardIDAt(topIndex), isCoveredAt(topIndex));
    }

    @Override
    public int getTopStackID() {
        CardStackEntry top = getTopEntry();
        return top == null ? 0 : top.cardID();
    }

    @Override
    public void removeFromTop() {
        Integer[] oldIDs = this.entityData.get(STACK_IDS);
        Boolean[] oldCovered = this.entityData.get(STACK_COVERED);

        int newLength = oldIDs.length - 1;
        if (newLength < 0) newLength = 0;

        Integer[] newIDs = new Integer[newLength];
        Boolean[] newCovered = new Boolean[newLength];

        System.arraycopy(oldIDs, 0, newIDs, 0, newLength);
        System.arraycopy(oldCovered, 0, newCovered, 0, newLength);

        this.entityData.set(STACK_IDS, newIDs);
        this.entityData.set(STACK_COVERED, newCovered);
    }

    public void addToTop(int cardID, boolean covered) {
        Integer[] oldIDs = this.entityData.get(STACK_IDS);
        Boolean[] oldCovered = this.entityData.get(STACK_COVERED);

        int newLength = oldIDs.length + 1;

        Integer[] newIDs = new Integer[newLength];
        Boolean[] newCovered = new Boolean[newLength];

        System.arraycopy(oldIDs, 0, newIDs, 0, oldIDs.length);
        System.arraycopy(oldCovered, 0, newCovered, 0, oldCovered.length);

        newIDs[newLength - 1] = cardID;
        newCovered[newLength - 1] = covered;

        this.entityData.set(STACK_IDS, newIDs);
        this.entityData.set(STACK_COVERED, newCovered);
    }

    @Override
    public void createStack() {
        this.entityData.set(STACK_IDS, new Integer[0]);
        this.entityData.set(STACK_COVERED, new Boolean[0]);
    }

    @Override
    public void shuffleStack() {
        Integer[] ids = this.entityData.get(STACK_IDS);
        Boolean[] covered = this.entityData.get(STACK_COVERED);

        int length = ids.length;
        CardStackEntry[] paired = new CardStackEntry[length];
        for (int i = 0; i < length; i++) {
            paired[i] = new CardStackEntry(ids[i], covered[i]);
        }

        for (int i = length - 1; i > 0; i--) {
            int j = this.random.nextInt(i + 1);
            CardStackEntry temp = paired[i];
            paired[i] = paired[j];
            paired[j] = temp;
        }

        Integer[] newIDs = new Integer[length];
        Boolean[] newCovered = new Boolean[length];
        for (int i = 0; i < length; i++) {
            newIDs[i] = paired[i].cardID();
            newCovered[i] = paired[i].covered();
        }

        this.entityData.set(STACK_IDS, newIDs);
        this.entityData.set(STACK_COVERED, newCovered);
    }

    private void takeCard(Player player) {
        CardStackEntry entry = getTopEntry();
        if (entry == null) return;

        ItemStack cardStack;
        if (entry.covered()) {
            cardStack = new ItemStack(InitItems.CARD_COVERED.get());
        } else {
            cardStack = new ItemStack(InitItems.cards.get(entry.cardID()).get());
        }

        CompoundTag nbt = ItemHelper.getNBT(cardStack);
        nbt.putInt("CardID", entry.cardID());
        nbt.putUUID("UUID", getDeckUUID());
        nbt.putBoolean("Covered", entry.covered());

        if (!level().isClientSide) {
            ItemHelper.spawnStackAtEntity(level(), player, cardStack);
        }

        removeFromTop();

        if (getStackAmount() <= 0) {
            discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level().getGameTime() % 20 == 0) {
            Vec3 pos = position();
            Level level = level();

            UUID deckUUID = getDeckUUID();
            if (deckUUID != null) {
                boolean foundParentDeck = false;
                for (EntityCardDeck deck : level.getEntitiesOfClass(EntityCardDeck.class,
                        new AABB(pos.x - 20, pos.y - 20, pos.z - 20, pos.x + 20, pos.y + 20, pos.z + 20))) {
                    if (deckUUID.equals(deck.getUUID())) {
                        foundParentDeck = true;
                        break;
                    }
                }
                if (!foundParentDeck) discard();
            }
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        boolean isCardItem = InitItems.cards.stream().anyMatch(item -> held.getItem() == item.get());

        if (isCardItem || held.getItem() == InitItems.CARD_COVERED.get()) {
            CompoundTag tag = held.getTag();
            if (tag != null && tag.contains("CardID")) {
                int cardID = tag.getInt("CardID");

                if (cardID >= 0 && cardID < InitItems.cards.size()) {
                    boolean covered = tag.getBoolean("Covered");

                    if (!level().isClientSide) {
                        addToTop(cardID, covered);

                        if (!player.isCreative()) {
                            held.shrink(1);
                        }
                    }
                    return InteractionResult.SUCCESS;
                } else {
                    if (level().isClientSide) {
                        ChatHelper.printModMessage(ChatFormatting.RED, Component.translatable("message.invalid_card"), player);
                    }
                    return InteractionResult.FAIL;
                }
            }
        }

        if (getStackAmount() <= 0) {
            if (level().isClientSide) {
                ChatHelper.printModMessage(ChatFormatting.RED, Component.translatable("message.stack_empty"), player);
            }
            return InteractionResult.FAIL;
        }

        if (!level().isClientSide) {
            takeCard(player);
        }

        return InteractionResult.SUCCESS;
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
        CardStackEntry top = getTopEntry();
        if (top != null) {
            int index = getStackAmount() - 1;
            Boolean[] covered = this.entityData.get(STACK_COVERED);
            covered[index] = !covered[index];
            this.entityData.set(STACK_COVERED, covered);
            return true;
        }
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putFloat("Rotation", getRotation());

        UUID deckUuid = getDeckUUID();
        if (deckUuid != null) {
            tag.putUUID("DeckUUID", deckUuid);
        }

        tag.putIntArray("StackIDs", ArrayHelper.toPrimitive(this.entityData.get(STACK_IDS)));

        Boolean[] covered = this.entityData.get(STACK_COVERED);
        byte[] coveredBytes = new byte[covered.length];
        for (int i = 0; i < covered.length; i++) {
            coveredBytes[i] = (byte)(covered[i] ? 1 : 0);
        }
        tag.putByteArray("StackCovered", coveredBytes);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        this.entityData.set(ROTATION, tag.getFloat("Rotation"));
        this.entityData.set(DECK_UUID, tag.hasUUID("DeckUUID") ? Optional.of(tag.getUUID("DeckUUID")) : Optional.empty());

        this.entityData.set(STACK_IDS, ArrayHelper.toObject(tag.getIntArray("StackIDs")));

        byte[] coveredBytes = tag.getByteArray("StackCovered");
        Boolean[] coveredObjects = new Boolean[coveredBytes.length];
        for (int i = 0; i < coveredBytes.length; i++) {
            coveredObjects[i] = coveredBytes[i] != 0;
        }
        this.entityData.set(STACK_COVERED, coveredObjects);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public float getRotation() {
        return this.entityData.get(ROTATION);
    }

    public UUID getDeckUUID() {
        return this.entityData.get(DECK_UUID).orElse(null);
    }
}
