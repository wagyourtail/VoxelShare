package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.c2s.PacketWaypointC2S;

import java.nio.ByteBuffer;

public class PacketWaypointS2C extends PacketWaypointC2S {
    public PacketWaypointS2C(String server, String world, String name, int x, int y, int z, boolean enabled, int red, int green, int blue, String suffix, String dimension) {
        super(server, world, name, x, y, z, enabled, red, green, blue, suffix, dimension);
    }

    public PacketWaypointS2C(ByteBuffer buff) {
        super(buff);
    }

    public PacketWaypointS2C(String server, ByteBuffer buff) {
        super(server, buff);
    }

}
