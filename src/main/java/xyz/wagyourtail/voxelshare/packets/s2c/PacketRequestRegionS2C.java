package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.c2s.PacketRequestRegionC2S;

import java.nio.ByteBuffer;

public class PacketRequestRegionS2C extends PacketRequestRegionC2S {

    public PacketRequestRegionS2C(String server, String world, int x, int z) {
        super(server, world, x, z);
    }

    public PacketRequestRegionS2C(ByteBuffer buff) {
        super(buff);
    }

    public PacketRequestRegionS2C(String server, String world, ByteBuffer buff) {
        super(server, world, buff);
    }

}
