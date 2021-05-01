package xyz.wagyourtail.voxelshare.client;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.voxelshare.Endpoint;

public abstract class AbstractServerEndpoint extends Endpoint<MinecraftClient> {

    public abstract String getServerName();
}
