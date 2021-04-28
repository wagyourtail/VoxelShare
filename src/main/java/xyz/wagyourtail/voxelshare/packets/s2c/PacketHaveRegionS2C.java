package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.c2s.PacketHaveRegionC2S;

import java.nio.ByteBuffer;

public class PacketHaveRegionS2C extends PacketHaveRegionC2S {


    public PacketHaveRegionS2C(String server, String world, long updateTime, int x, int z) {
        super(server, world, updateTime, x, z);
    }

    public PacketHaveRegionS2C(ByteBuffer buff) {
        super(buff);
    }

    public PacketHaveRegionS2C(String server, String world, ByteBuffer buff) {
        super(server, world, buff);
    }

}
