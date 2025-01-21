package net.xelbayria.tarotboards.entity;

import net.minecraft.ChatFormatting;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.xelbayria.tarotboards.entity.base.EntityStacked;
import net.xelbayria.tarotboards.init.InitEntityTypes;
import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.util.ChatHelper;
import net.xelbayria.tarotboards.util.ItemHelper;

public class EntityCardDeck extends EntityStacked {

    private static final EntityDataAccessor<Float> ROTATION = SynchedEntityData.defineId(EntityCardDeck.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Byte> SKIN_ID = SynchedEntityData.defineId(EntityCardDeck.class, EntityDataSerializers.BYTE);

    public EntityCardDeck(EntityType<? extends EntityCardDeck> type, Level world) {
        super(type, world);
    }

    public EntityCardDeck(Level world, Vec3 position, float rotation, byte skinID) {
        super(InitEntityTypes.CARD_DECK.get(), world, position);

        createAndFillDeck();
        shuffleStack();

        this.entityData.set(ROTATION, rotation);
        this.entityData.set(SKIN_ID, skinID);
    }

    public float getRotation() {
        return this.entityData.get(ROTATION);
    }

    public byte getSkinID() {
        return this.entityData.get(SKIN_ID);
    }

    private void createAndFillDeck() {

        Byte[] newStack = new Byte[52];

        for (byte index = 0; index < 52; index++) {
            newStack[index] = index;
        }

        this.entityData.set(STACK, newStack);
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        if (pHand == InteractionHand.MAIN_HAND) {

            if (getStackAmount() > 0) {

                int cardID = getTopStackID();

                ItemStack card = new ItemStack(InitItems.CARD_COVERED.get());

                card.setDamageValue(cardID);
                ItemHelper.getNBT(card).putUUID("UUID", getUUID());
                ItemHelper.getNBT(card).putByte("SkinID", this.entityData.get(SKIN_ID));
                ItemHelper.getNBT(card).putBoolean("Covered", true);

                if (!level().isClientSide) {
                    ItemHelper.spawnStackAtEntity(level(), pPlayer, card);
                }

                removeFromTop();

                return pPlayer.getMainHandItem().isEmpty() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }

            else if (level().isClientSide) ChatHelper.printModMessage(ChatFormatting.RED, Component.translatable("message.stack_empty"), pPlayer);
        }

        return InteractionResult.FAIL;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getDirectEntity() instanceof Player player) {

            if (player.isCrouching()) {
                ItemStack deck = new ItemStack(InitItems.CARD_DECK.get());
                ItemHelper.getNBT(deck).putByte("SkinID", this.entityData.get(SKIN_ID));

                ItemHelper.spawnStackAtEntity(level(), player, deck);
                discard();
            } else {
                shuffleStack();
                if (level().isClientSide) ChatHelper.printModMessage(ChatFormatting.GREEN, Component.translatable("message.stack_shuffled"), player);
            }

            return true;
        }

        return false;
    }

    @Override
    public void moreData() {
        this.entityData.define(ROTATION, 0F);
        this.entityData.define(SKIN_ID, (byte) 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.entityData.set(ROTATION, compoundTag.getFloat("Rotation"));
        this.entityData.set(SKIN_ID, compoundTag.getByte("SkinID"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putFloat("Rotation", this.entityData.get(ROTATION));
        compoundTag.putByte("SkinID", this.entityData.get(SKIN_ID));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
