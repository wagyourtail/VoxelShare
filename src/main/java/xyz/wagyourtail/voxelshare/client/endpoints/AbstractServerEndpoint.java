package xyz.wagyourtail.voxelshare.client.endpoints;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.voxelshare.Endpoint;
import xyz.wagyourtail.voxelshare.Region;

import java.util.List;

public abstract class AbstractServerEndpoint extends Endpoint<MinecraftClient> {
    public final String serverName;
    public List<Region> serverRegions = null;
    public long waypointSendTime = 0;
    public long regionSendTime = 0;

    public AbstractServerEndpoint(String sName) {
        this.serverName = sName;
    }

    public String getServerName() {
        return serverName;
    }
}
