package xyz.wagyourtail.voxelshare.events.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface LeaveServerEvent {
    Event<LeaveServerEvent> EVENT = EventFactory.createArrayBacked(LeaveServerEvent.class, listeners -> () -> {
        for (LeaveServerEvent listener : listeners) {
            listener.interact();
        }
    });

    void interact();
}
