package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.c2s.PacketMoveWaypointC2S;

import java.nio.ByteBuffer;

public class PacketMoveWaypointS2C extends PacketMoveWaypointC2S {
    public PacketMoveWaypointS2C(String server, PacketWaypointS2C from, PacketWaypointS2C to) {
        super(server, from, to);
    }

    public PacketMoveWaypointS2C(ByteBuffer buff) {
        super(buff);
    }

}
