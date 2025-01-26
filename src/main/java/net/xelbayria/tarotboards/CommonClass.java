package net.xelbayria.tarotboards;

import net.xelbayria.tarotboards.init.InitEntityTypes;
import net.xelbayria.tarotboards.init.InitItems;
import net.xelbayria.tarotboards.init.InitRecipes;
import net.xelbayria.tarotboards.init.InitTileEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;

public class CommonClass {

    public static void init(IEventBus modEventBus) {
        InitItems.init(modEventBus);
        InitEntityTypes.init(modEventBus);
        InitTileEntityTypes.init(modEventBus);
        InitRecipes.init(modEventBus);
    }

    public static ResourceLocation customLocation(String name) {
        return new ResourceLocation(TBConstants.MOD_ID, name);
    }
}
