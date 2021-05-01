package xyz.wagyourtail.voxelshare.events.server;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

public interface PlayerJoinEvent {
    Event<PlayerJoinEvent> EVENT = EventFactory.createArrayBacked(PlayerJoinEvent.class, listeners -> (player, mc) -> {
        for (PlayerJoinEvent listener : listeners) {
            listener.interact(player, mc);
        }
    });

    void interact(PlayerEntity player, MinecraftServer mc);
}
