package net.xelbayria.tarotboards.entity;

import net.xelbayria.tarotboards.entity.base.EntityStacked;
import net.xelbayria.tarotboards.init.InitEntityTypes;
import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.item.ItemCardCovered;
import net.xelbayria.tarotboards.util.ChatHelper;
import net.xelbayria.tarotboards.util.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
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

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class EntityCard extends EntityStacked {

    private static final EntityDataAccessor<Float> ROTATION = SynchedEntityData.defineId(EntityCard.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> DECK_UUID = SynchedEntityData.defineId(EntityCard.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> COVERED = SynchedEntityData.defineId(EntityCard.class, EntityDataSerializers.BOOLEAN);

    public EntityCard(EntityType<? extends EntityCard> type, Level world) {
        super(type, world);
    }

    public EntityCard(Level world, Vec3 position, float rotation, UUID deckUUID, boolean covered, int firstCardID) {
        super(InitEntityTypes.CARD.get(), world, position);

        createStack();
        addToTop(firstCardID);
        this.entityData.set(ROTATION, rotation);
        this.entityData.set(DECK_UUID, Optional.of(deckUUID));
        this.entityData.set(COVERED, covered);
    }

    public float getRotation() {
        return this.entityData.get(ROTATION);
    }

    public UUID getDeckUUID() {
        return (this.entityData.get(DECK_UUID).isPresent()) ? this.entityData.get(DECK_UUID).get() : null;
    }

    public boolean isCover() {
        return this.entityData.get(COVERED);
    }

    private void takeCard(Player player) {
        ItemStack card = new ItemStack(InitItems.cards.get(getTopStackID()).get());
        if (this.entityData.get(COVERED)) card = new ItemStack(InitItems.CARD_COVERED.get());

        ItemHelper.getNBT(card).putUUID("UUID", getDeckUUID());
        ItemHelper.getNBT(card).putBoolean("Covered", this.entityData.get(COVERED));

        if (!level().isClientSide) {
            ItemHelper.spawnStackAtEntity(level(), player, card);
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

            BlockPos pos = blockPosition();

            List<EntityCardDeck> closeDecks = level().getEntitiesOfClass(EntityCardDeck.class, new AABB(pos.getX() - 20, pos.getY() - 20, pos.getZ() - 20, pos.getX() + 20, pos.getY() + 20, pos.getZ() + 20));

            boolean foundParentDeck = false;

            for (EntityCardDeck closeDeck : closeDecks) {

                if (getDeckUUID().equals(closeDeck.getUUID())) {
                    foundParentDeck = true;
                }
            }

            if (!foundParentDeck) discard();

            super.onRemovedFromWorld();
        }
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);

        if (stack.getItem() instanceof ItemCardCovered) {

            if (getStackAmount() < MAX_STACK_SIZE) {
                addToTop((byte) stack.getDamageValue());
                stack.shrink(1);
            }

            else {
                if (level().isClientSide) ChatHelper.printModMessage(ChatFormatting.RED, Component.translatable("message.stack_full"), pPlayer);
            }
        }

        else takeCard(pPlayer);

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        this.entityData.set(COVERED, !this.entityData.get(COVERED));
        return true;
    }

    @Override
    public void moreData() {
        this.entityData.define(ROTATION, 0F);
        this.entityData.define(DECK_UUID, Optional.empty());
        this.entityData.define(COVERED, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.entityData.set(ROTATION, compoundTag.getFloat("Rotation"));
        this.entityData.set(DECK_UUID, Optional.of(compoundTag.getUUID("DeckID")));
        this.entityData.set(COVERED, compoundTag.getBoolean("Covered"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putFloat("Rotation", this.entityData.get(ROTATION));
        compoundTag.putUUID("DeckID", getDeckUUID());
        compoundTag.putBoolean("Covered", this.entityData.get(COVERED));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
