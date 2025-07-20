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
import net.xelbayria.tarotboards.TarotBoard;
import net.xelbayria.tarotboards.entity.base.EntityStacked;
import net.xelbayria.tarotboards.init.InitEntityTypes;
import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.util.ChatHelper;
import net.xelbayria.tarotboards.util.ItemHelper;

public class EntityCardDeck extends EntityStacked {

    private static final EntityDataAccessor<Float> ROTATION = SynchedEntityData.defineId(EntityCardDeck.class, EntityDataSerializers.FLOAT);

    public EntityCardDeck(EntityType<? extends EntityCardDeck> type, Level world) {
        super(type, world);
    }

    public EntityCardDeck(Level world, Vec3 position, float rotation) {
        super(InitEntityTypes.CARD_DECK.get(), world, position);
        createAndFillDeck();
        shuffleStack();
        this.entityData.set(ROTATION, rotation);
    }

    @Override
    public void moreData() {
        this.entityData.define(ROTATION, 0F);
    }

    public float getRotation() {
        return this.entityData.get(ROTATION);
    }

    private void createAndFillDeck() {
        Integer[] newStackIDs = new Integer[TarotBoard.NUM_CARDS];
        Boolean[] newStackCovered = new Boolean[TarotBoard.NUM_CARDS];

        for (int i = 0; i < TarotBoard.NUM_CARDS; i++) {
            newStackIDs[i] = i;
            newStackCovered[i] = true;
        }

        this.entityData.set(STACK_IDS, newStackIDs);
        this.entityData.set(STACK_COVERED, newStackCovered);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            int stackAmount = getStackAmount();
            if (stackAmount > 0) {
                int topCardID = getTopStackID();
                boolean topCovered = isCoveredAt(stackAmount - 1);

                ItemStack cardStack = new ItemStack(InitItems.CARD_COVERED.get());
                cardStack.setDamageValue(topCardID);

                CompoundTag nbt = ItemHelper.getNBT(cardStack);
                nbt.putUUID("UUID", getUUID());
                nbt.putBoolean("Covered", topCovered);

                if (!level().isClientSide) {
                    ItemHelper.spawnStackAtEntity(level(), player, cardStack);
                    removeFromTop();
                }

                return player.getMainHandItem().isEmpty() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            } else if (level().isClientSide) {
                ChatHelper.printModMessage(ChatFormatting.RED, Component.translatable("message.stack_empty"), player);
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof Player player) {
            if (player.isCrouching()) {
                ItemStack deckItem = new ItemStack(InitItems.CARD_DECK.get());
                ItemHelper.spawnStackAtEntity(level(), player, deckItem);
                discard();
            } else {
                shuffleStack();
                if (level().isClientSide) {
                    ChatHelper.printModMessage(ChatFormatting.GREEN, Component.translatable("message.stack_shuffled"), player);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.entityData.set(ROTATION, compoundTag.getFloat("Rotation"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putFloat("Rotation", this.entityData.get(ROTATION));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
