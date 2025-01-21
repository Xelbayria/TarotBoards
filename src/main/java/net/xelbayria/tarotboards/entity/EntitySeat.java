package net.xelbayria.tarotboards.entity;

import net.xelbayria.tarotboards.init.InitEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class EntitySeat extends Entity {

    private BlockPos sourceBlock;

    public EntitySeat(EntityType<? extends EntitySeat> type, Level world) {
        super(type, world);
    }

    public EntitySeat(Level world, BlockPos sourceBlock) {
        this(InitEntityTypes.SEAT.get(), world);
        this.sourceBlock = sourceBlock;
        setPos(sourceBlock.getX() + 0.5F, sourceBlock.getY() + 0.3F, sourceBlock.getZ() + 0.5F);
    }

    private BlockPos getSourceBlock() {
        return sourceBlock;
    }

    public static void createSeat(Level world, BlockPos pos, Player player) {

        if (!world.isClientSide) {

            List<EntitySeat> seats = world.getEntitiesOfClass(EntitySeat.class, new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));

            if (seats.isEmpty()) {

                EntitySeat seat = new EntitySeat(world, pos);
                world.addFreshEntity(seat);
                player.startRiding(seat);
            }
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        super.tick();

        if (sourceBlock == null) {
            sourceBlock = this.blockPosition();
        }

        if (!level().isClientSide) {

            if (getPassengers().isEmpty() || this.level().getBlockState(sourceBlock).isAir()) {
                discard();
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.0D;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
