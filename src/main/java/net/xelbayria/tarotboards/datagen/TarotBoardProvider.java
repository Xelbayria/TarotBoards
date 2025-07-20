package net.xelbayria.tarotboards.datagen;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class TarotBoardProvider {
    public static void init(IEventBus bus) {
        bus.addListener(TarotBoardProvider::dataGather);
    }

    public static void dataGather(GatherDataEvent event) {
        var output = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(true, new TarotItemModelProvider(output, event.getExistingFileHelper()));
        event.getGenerator().addProvider(true, new TarotEnglishLangProvider(output));
    }
}
