package net.xelbayria.tarotboards.entity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.xelbayria.tarotboards.TarotBoard;
import net.xelbayria.tarotboards.entity.data.PCDataSerializers;
import net.xelbayria.tarotboards.util.ArrayHelper;

public abstract class EntityStacked extends Entity {

    public static final int MAX_STACK_SIZE = TarotBoard.NUM_CARDS;
    protected static final EntityDataAccessor<Integer[]> STACK_IDS = SynchedEntityData.defineId(EntityStacked.class, PCDataSerializers.STACK);
    protected static final EntityDataAccessor<Boolean[]> STACK_COVERED = SynchedEntityData.defineId(EntityStacked.class, PCDataSerializers.BOOLEAN_ARRAY);

    public EntityStacked(EntityType<? extends EntityStacked> type, Level world) {
        super(type, world);
    }

    public EntityStacked(EntityType<? extends EntityStacked> type, Level world, Vec3 position) {
        this(type, world);
        setPos(position.x, position.y, position.z);
        setRot(0, 0);
    }

    /**
     * Returns number of cards in the stack
     */
    public int getStackAmount() {
        return this.entityData.get(STACK_IDS).length;
    }

    /**
     * Get card ID at index
     */
    public int getIDAt(int index) {
        Integer[] ids = this.entityData.get(STACK_IDS);
        if (index >= 0 && index < ids.length) {
            return ids[index];
        }
        return 0;
    }

    /**
     * Get covered flag at index
     */
    public boolean isCoveredAt(int index) {
        Boolean[] covered = this.entityData.get(STACK_COVERED);
        if (index >= 0 && index < covered.length) {
            return covered[index];
        }
        return true; // default covered = true
    }

    /**
     * Get the top card ID
     */
    public int getTopStackID() {
        int amount = getStackAmount();
        if (amount == 0) return 0;
        return getIDAt(amount - 1);
    }

    /**
     * Remove the top card from the stack
     */
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

    /**
     * Add a card to the top of the stack
     *
     * @param id      card ID to add
     * @param covered covered flag to add
     */
    public void addToTop(int id, boolean covered) {
        Integer[] oldIDs = this.entityData.get(STACK_IDS);
        Boolean[] oldCovered = this.entityData.get(STACK_COVERED);

        int newLength = oldIDs.length + 1;

        Integer[] newIDs = new Integer[newLength];
        Boolean[] newCovered = new Boolean[newLength];

        System.arraycopy(oldIDs, 0, newIDs, 0, oldIDs.length);
        System.arraycopy(oldCovered, 0, newCovered, 0, oldCovered.length);

        newIDs[newLength - 1] = id;
        newCovered[newLength - 1] = covered;

        this.entityData.set(STACK_IDS, newIDs);
        this.entityData.set(STACK_COVERED, newCovered);
    }

    /**
     * Initialize empty stack
     */
    public void createStack() {
        this.entityData.set(STACK_IDS, new Integer[0]);
        this.entityData.set(STACK_COVERED, new Boolean[0]);
    }

    /**
     * Shuffle stack keeping pairs aligned
     */
    public void shuffleStack() {
        Integer[] ids = this.entityData.get(STACK_IDS);
        Boolean[] covered = this.entityData.get(STACK_COVERED);

        int length = ids.length;
        CardStackEntry[] paired = new CardStackEntry[length];
        for (int i = 0; i < length; i++) {
            paired[i] = new CardStackEntry(ids[i], covered[i]);
        }

        // Fisher-Yates shuffle
        for (int i = length - 1; i > 0; i--) {
            int j = this.random.nextInt(i + 1);
            CardStackEntry temp = paired[i];
            paired[i] = paired[j];
            paired[j] = temp;
        }

        Integer[] newIDs = new Integer[length];
        Boolean[] newCovered = new Boolean[length];
        for (int i = 0; i < length; i++) {
            newIDs[i] = paired[i].cardID;
            newCovered[i] = paired[i].covered;
        }

        this.entityData.set(STACK_IDS, newIDs);
        this.entityData.set(STACK_COVERED, newCovered);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            noPhysics = false;
        } else {
            noPhysics = !level().noCollision(this);

            if (noPhysics) {
                setDeltaMovement(getDeltaMovement().add(0.0D, 0.02D, 0.0D));
            } else {
                setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }
        }

        move(MoverType.SELF, getDeltaMovement());

        Vec3 pos = position();
        double size = 0.2D;
        double addAmount = 0.0045D;

        setBoundingBox(new AABB(pos.x - size, pos.y, pos.z - size, pos.x + size, pos.y + 0.03D + (addAmount * getStackAmount()), pos.z + size));
    }

    public abstract void moreData();

    @Override
    protected void defineSynchedData() {
        this.entityData.define(STACK_IDS, new Integer[0]);
        this.entityData.define(STACK_COVERED, new Boolean[0]);
        moreData();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.entityData.set(STACK_IDS, ArrayHelper.toObject(compoundTag.getIntArray("StackIDs")));

        byte[] coveredBytes = compoundTag.getByteArray("StackCovered");
        Boolean[] coveredObjects = new Boolean[coveredBytes.length];
        for (int i = 0; i < coveredBytes.length; i++) {
            coveredObjects[i] = coveredBytes[i] != 0;
        }
        this.entityData.set(STACK_COVERED, coveredObjects);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putIntArray("StackIDs", ArrayHelper.toPrimitive(this.entityData.get(STACK_IDS)));

        Boolean[] covered = this.entityData.get(STACK_COVERED);
        byte[] coveredBytes = new byte[covered.length];
        for (int i = 0; i < covered.length; i++) {
            coveredBytes[i] = (byte) (covered[i] ? 1 : 0);
        }
        compoundTag.putByteArray("StackCovered", coveredBytes);
    }


    @Override
    public boolean isPickable() {
        return true;
    }

    public record CardStackEntry(int cardID, boolean covered) {}
}
