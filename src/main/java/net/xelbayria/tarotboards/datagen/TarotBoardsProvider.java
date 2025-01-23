package net.xelbayria.tarotboards.datagen;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class TarotBoardsProvider {
    public static void init(IEventBus bus) {
        bus.addListener(TarotBoardsProvider::dataGather);
    }

    public static void dataGather(GatherDataEvent event) {
        var output = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(true, new TarotItemModelProvider(output, event.getExistingFileHelper()));
    }
}
