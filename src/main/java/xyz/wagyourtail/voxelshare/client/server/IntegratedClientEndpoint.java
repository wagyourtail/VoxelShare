package xyz.wagyourtail.voxelshare.client.server;

import net.minecraft.server.MinecraftServer;
import xyz.wagyourtail.voxelshare.server.DedicatedServerClient;

import java.util.UUID;

public class IntegratedServerClient extends DedicatedServerClient {
    public IntegratedServerClient(UUID player) {
        super(player);
    }

    @Override
    public void doRegions(MinecraftServer mc) {
        super.doRegions(mc);
    }

    @Override
    public void doWaypoints(MinecraftServer mc) {
        super.doWaypoints(mc);
    }

}
