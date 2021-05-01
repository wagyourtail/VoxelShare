package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.c2s.PacketWorldC2S;

import java.nio.ByteBuffer;

public class PacketWorldS2C extends PacketWorldC2S {
    public PacketWorldS2C(String world) {
        super(world);
    }

    public PacketWorldS2C(ByteBuffer buff) {
        super(buff);
    }

}
