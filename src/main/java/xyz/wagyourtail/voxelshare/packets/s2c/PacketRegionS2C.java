package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.c2s.PacketRegionC2S;

import java.nio.ByteBuffer;

public class PacketRegionS2C extends PacketRegionC2S {

    public PacketRegionS2C(String server, String world, String dimension, long updateTime, int x, int z, byte[] data, String keys) {
        super(server, world, dimension, updateTime, x, z, data, keys);
    }

    public PacketRegionS2C(ByteBuffer buff) {
        super(buff);
    }

}
