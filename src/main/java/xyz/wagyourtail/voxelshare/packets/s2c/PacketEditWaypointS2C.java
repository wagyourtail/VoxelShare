package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.c2s.PacketEditWaypointC2S;

import java.nio.ByteBuffer;

public class PacketEditWaypointS2C extends PacketEditWaypointC2S {
    public PacketEditWaypointS2C(String server, PacketWaypointS2C from, PacketWaypointS2C to) {
        super(server, from, to);
    }

    public PacketEditWaypointS2C(ByteBuffer buff) {
        super(buff);
    }

}
