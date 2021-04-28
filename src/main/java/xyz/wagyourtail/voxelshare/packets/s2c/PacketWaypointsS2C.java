package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.c2s.PacketWaypointC2S;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketWaypointsC2S;

import java.nio.ByteBuffer;
import java.util.List;

public class PacketWaypointsS2C extends PacketWaypointsC2S {

    @SuppressWarnings({"unchecked","rawtypes"})
    public PacketWaypointsS2C(String server, List<PacketWaypointS2C> waypoints) {
        super(server, (List)waypoints);
    }

    public PacketWaypointsS2C(ByteBuffer buff) {
        super(buff);
    }

    @Override
    public PacketWaypointC2S createChild(ByteBuffer buff) {
        return new PacketWaypointS2C(server, buff);
    }

}
