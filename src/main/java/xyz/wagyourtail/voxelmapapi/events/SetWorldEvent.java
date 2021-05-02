package xyz.wagyourtail.voxelmapapi.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface SetWorldEvent {
    Event<SetWorldEvent> EVENT = EventFactory.createArrayBacked(SetWorldEvent.class, listeners -> world -> {
        for (SetWorldEvent listener : listeners) {
            listener.interact(world);
        }
    });

    void interact(String world);
}
