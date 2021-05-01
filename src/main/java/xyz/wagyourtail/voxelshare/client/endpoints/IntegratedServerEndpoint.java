package xyz.wagyourtail.voxelshare.client;

import net.minecraft.client.MinecraftClient;

public class IntegratedServerEndpoint extends DedicatedServerEndpoint {
    public IntegratedServerEndpoint(String sName) {
        super(sName);
    }

    @Override
    public void doWaypoints(MinecraftClient mc) {
        //integrated server already has everything
    }

    @Override
    public void doPositions(MinecraftClient mc) {
        //integrated server already has everything
    }

    @Override
    public void doRegions(MinecraftClient mc) {
        //integrated server already has everything
    }

}
