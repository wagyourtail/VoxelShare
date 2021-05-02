package xyz.wagyourtail.voxelshare.packets.c2s;

import com.google.common.collect.ImmutableList;
import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketGroup;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * [Byte: opCode, String server, PacketWaypointNoServer[]: points]
 */
public class PacketWaypointsC2S extends PacketGroup<PacketWaypointC2S> {
    public static final byte OPCODE = PacketOpcodes.Waypoints.opcode;
    public final String server;

    public PacketWaypointsC2S(String server, List<PacketWaypointC2S> waypoints) {
        this.server = server;
        this.children = ImmutableList.copyOf(waypoints);
    }

    public PacketWaypointsC2S(ByteBuffer buff) {
        this.server = readString(buff);
        List<PacketWaypointC2S> waypoints = new LinkedList<>();
        while (buff.hasRemaining()) {
            waypoints.add(createChild(buff));
        }
        this.children = ImmutableList.copyOf(waypoints);
    }

    @Override
    public ByteBuffer writePacket() {
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        int size = 1 + Integer.BYTES + server.length;
        List<ByteBuffer> waypoints = new LinkedList<>();
        for (PacketWaypointC2S waypoint : this.children) {
            ByteBuffer wp = waypoint.writePacketNoServer();
            size += wp.capacity();
            waypoints.add(wp);
        }
        ByteBuffer buff = ByteBuffer.allocate(size);
        buff.put(OPCODE);
        buff.putInt(server.length);
        buff.put(server);
        waypoints.forEach(e -> buff.put((ByteBuffer) e.rewind()));
        return buff;
    }

    @Override
    public PacketWaypointC2S createChild(ByteBuffer buff) {
        return new PacketWaypointC2S(server, buff);
    }

}
