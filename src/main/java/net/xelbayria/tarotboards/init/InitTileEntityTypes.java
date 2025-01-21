package net.xelbayria.tarotboards.init;

import net.xelbayria.tarotboards.PCReference;
import net.xelbayria.tarotboards.tileentity.TileEntityPokerTable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitTileEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PCReference.MOD_ID);

    public static final RegistryObject<BlockEntityType<TileEntityPokerTable>> POKER_TABLE = TILE_ENTITY_TYPES.register("poker_table", () -> BlockEntityType.Builder.of(TileEntityPokerTable::new, InitItems.POKER_TABLE.get()).build(null));

    public static void init(IEventBus modEventBus) {
        TILE_ENTITY_TYPES.register(modEventBus);
    }
}
