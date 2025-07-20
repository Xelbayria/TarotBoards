package net.xelbayria.tarotboards.entity.data;

import net.xelbayria.tarotboards.util.ArrayHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import org.jetbrains.annotations.NotNull;

public class PCDataSerializers {

    public static final EntityDataSerializer<Integer[]> STACK = new EntityDataSerializer<>() {

        @Override
        public void write(FriendlyByteBuf buf, Integer @NotNull [] integers) {
            buf.writeVarIntArray(ArrayHelper.toPrimitive(integers));
        }

        @Override
        public Integer @NotNull [] read(FriendlyByteBuf buf) {
            return ArrayHelper.toObject(buf.readVarIntArray());
        }

        @Override
        public Integer @NotNull [] copy(Integer @NotNull [] original) {
            return ArrayHelper.clone(original);
        }
    };

    public static final EntityDataSerializer<Boolean[]> BOOLEAN_ARRAY = new EntityDataSerializer<>() {

        @Override
        public void write(FriendlyByteBuf buf, Boolean @NotNull [] booleans) {
            buf.writeVarInt(booleans.length);
            for (boolean b : booleans) {
                buf.writeBoolean(b);
            }
        }

        @Override
        public Boolean @NotNull [] read(FriendlyByteBuf buf) {
            int length = buf.readVarInt();
            Boolean[] booleans = new Boolean[length];
            for (int i = 0; i < length; i++) {
                booleans[i] = buf.readBoolean();
            }
            return booleans;
        }

        @Override
        public Boolean @NotNull [] copy(Boolean @NotNull [] original) {
            return original.clone();
        }
    };
}
