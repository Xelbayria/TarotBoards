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
import net.xelbayria.tarotboards.TarotBoards;
import net.xelbayria.tarotboards.entity.data.PCDataSerializers;
import net.xelbayria.tarotboards.util.ArrayHelper;

public abstract class EntityStacked extends Entity {

    public static final int MAX_STACK_SIZE = TarotBoards.NUM_CARDS;

    protected static final EntityDataAccessor<Integer[]> STACK = SynchedEntityData.defineId(EntityStacked.class, PCDataSerializers.STACK);

    public EntityStacked(EntityType<? extends EntityStacked> type, Level world) {
        super(type, world);
    }

    public EntityStacked(EntityType<? extends EntityStacked> type, Level world, Vec3 position) {
        this(type, world);
        setPos(position.x, position.y, position.z);
        setRot(0, 0);
    }

    public int getStackAmount() {
        return this.entityData.get(STACK).length;
    }

    public int getTopStackID() {
        return getIDAt(getStackAmount() - 1);
    }

    public int getIDAt(int index) {

        if (index >= 0 && index < getStackAmount()) {
            return this.entityData.get(STACK)[index];
        }

        return 0;
    }

    public void removeFromTop() {
        Integer[] newStack = new Integer[getStackAmount() - 1];

        for (int index = 0; index < newStack.length; index++) {
            newStack[index] = this.entityData.get(STACK)[index];
        }

        this.entityData.set(STACK, newStack);
    }

    public void addToTop(int id) {

        Integer[] newStack = new Integer[getStackAmount() + 1];

        for (int index = 0; index < getStackAmount(); index++) {
            newStack[index] = this.entityData.get(STACK)[index];
        }

        newStack[newStack.length - 1] = id;

        this.entityData.set(STACK, newStack);
    }

    public void createStack() {
        Integer[] newStack = new Integer[0];
        this.entityData.set(STACK, newStack);
    }

    public void shuffleStack() {

        Integer[] newStack = new Integer[getStackAmount()];

        for (int index = 0; index < getStackAmount(); index++) {
            newStack[index] = this.entityData.get(STACK)[index];
        }

        ArrayHelper.shuffle(newStack);

        this.entityData.set(STACK, newStack);
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
        this.entityData.define(STACK, new Integer[0]);
        moreData();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.entityData.set(STACK, ArrayHelper.toObject(compoundTag.getIntArray("Stack")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putIntArray("Stack", ArrayHelper.toPrimitive(this.entityData.get(STACK)));
    }

    @Override
    public boolean isPickable() {
        return true;
    }
}
