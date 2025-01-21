package net.xelbayria.tarotboards.entity.data;

import net.xelbayria.tarotboards.util.ArrayHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import org.jetbrains.annotations.NotNull;

public class PCDataSerializers {

    public static final EntityDataSerializer<Integer[]> STACK = new EntityDataSerializer<>() {

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf, Integer @NotNull [] integers) {
            friendlyByteBuf.writeVarIntArray(ArrayHelper.toPrimitive(integers));
        }

        @Override
        public Integer @NotNull [] read(FriendlyByteBuf friendlyByteBuf) {
            return ArrayHelper.toObject(friendlyByteBuf.readVarIntArray());
        }

        @Override
        public Integer @NotNull [] copy(Integer @NotNull [] bytes) {
            return ArrayHelper.clone(bytes);
        }
    };
}
