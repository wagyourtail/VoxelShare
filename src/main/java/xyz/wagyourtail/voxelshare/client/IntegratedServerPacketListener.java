package xyz.wagyourtail.voxelshare.client;

import xyz.wagyourtail.voxelshare.server.DedicatedServerPacketListener;

import java.util.UUID;

public class IntegratedServerPacketListener extends DedicatedServerPacketListener {
    public IntegratedServerPacketListener(UUID player) {
        super(player);
    }

}
