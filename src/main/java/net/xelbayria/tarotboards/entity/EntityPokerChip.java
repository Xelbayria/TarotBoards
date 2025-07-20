package net.xelbayria.tarotboards.entity;

import net.xelbayria.tarotboards.entity.base.EntityStacked;
import net.xelbayria.tarotboards.util.ChatHelper;
import net.xelbayria.tarotboards.util.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.xelbayria.tarotboards.init.InitEntityTypes;
import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.item.ItemPokerChip;

public class EntityPokerChip extends EntityStacked {

    public EntityPokerChip(EntityType<? extends EntityPokerChip> type, Level world) {
        super(type, world);
    }

    public EntityPokerChip(Level world, Vec3 position, int firstChipID) {
        super(InitEntityTypes.POKER_CHIP.get(), world, position);

        createStack();
        addToTop(firstChipID, false);
    }

    private void takeChip(Player player) {
        int chipID = getTopStackID();

        if (!level().isClientSide) {
            spawnChip(player, ItemPokerChip.getPokerChip(chipID), 1);
        }

        removeFromTop();

        if (getStackAmount() <= 0) {
            discard();
        }
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);

        if (stack.getItem() instanceof ItemPokerChip) {

            if (pPlayer.isCrouching()) {
                while (true) {
                    if (getStackAmount() < MAX_STACK_SIZE && stack.getCount() > 0) {
                        ItemPokerChip chip = (ItemPokerChip) stack.getItem();
                        addToTop(chip.getChipID(), false);
                        stack.shrink(1);
                    } else break;
                }
            } else {
                if (getStackAmount() < MAX_STACK_SIZE) {
                    ItemPokerChip chip = (ItemPokerChip) stack.getItem();
                    addToTop(chip.getChipID(), false);
                    stack.shrink(1);
                } else {
                    if (level().isClientSide)
                        ChatHelper.printModMessage(ChatFormatting.RED, Component.translatable("message.stack_full"), pPlayer);
                }
            }

        } else {
            takeChip(pPlayer);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    private void spawnChip(Player player, Item item, int amount) {
        if (!level().isClientSide) {
            ItemStack chip = new ItemStack(item, amount);
            ItemHelper.spawnStackAtEntity(level(), player, chip);
        }
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 pos = position();
        double size = 0.1D;
        double addAmount = 0.01575D;

        setBoundingBox(new AABB(pos.x - size, pos.y, pos.z - size,
                pos.x + size, pos.y + 0.02D + (addAmount * getStackAmount()), pos.z + size));
    }

    @Override
    public void moreData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        // No owner data to load
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        // No owner data to save
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
