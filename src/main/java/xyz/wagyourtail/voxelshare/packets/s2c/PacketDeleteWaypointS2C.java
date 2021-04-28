package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.c2s.PacketDeleteWaypointC2S;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketWaypointC2S;

import java.nio.ByteBuffer;

public class PacketDeleteWaypointS2C extends PacketDeleteWaypointC2S {

    public PacketDeleteWaypointS2C(String server, PacketWaypointC2S waypoint) {
        super(server, waypoint);
    }

    public PacketDeleteWaypointS2C(ByteBuffer buff) {
        super(buff);
    }

}
