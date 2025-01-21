package net.xelbayria.tarotboards.entity.data;

import net.xelbayria.tarotboards.util.ArrayHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import org.jetbrains.annotations.NotNull;

public class PCDataSerializers {

    public static final EntityDataSerializer<Byte[]> STACK = new EntityDataSerializer<>() {

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf, Byte @NotNull [] bytes) {
            friendlyByteBuf.writeByteArray(ArrayHelper.toPrimitive(bytes));
        }

        @Override
        public Byte @NotNull [] read(FriendlyByteBuf friendlyByteBuf) {
            return ArrayHelper.toObject(friendlyByteBuf.readByteArray());
        }

        @Override
        public Byte @NotNull [] copy(Byte @NotNull [] bytes) {
            return ArrayHelper.clone(bytes);
        }
    };
}
