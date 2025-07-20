package net.xelbayria.tarotboards;

import net.xelbayria.tarotboards.init.InitEntityTypes;
import net.xelbayria.tarotboards.init.InitItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;

public class CommonClass {

    public static void init(IEventBus modEventBus) {
        InitItems.init(modEventBus);
        InitEntityTypes.init(modEventBus);
    }

    public static ResourceLocation customLocation(String name) {
        return ResourceLocation.fromNamespaceAndPath(TBConstants.MOD_ID, name);
    }
}
