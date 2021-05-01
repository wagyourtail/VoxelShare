package xyz.wagyourtail.voxelshare.events.server;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

public interface PlayerLeaveEvent {
    Event<PlayerLeaveEvent> EVENT = EventFactory.createArrayBacked(PlayerLeaveEvent.class, listeners -> (player, mc) -> {
        for (PlayerLeaveEvent listener : listeners) {
            listener.interact(player, mc);
        }
    });

    void interact(PlayerEntity player, MinecraftServer mc);
}
