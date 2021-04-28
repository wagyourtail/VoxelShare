package xyz.wagyourtail.voxelshare.packets.c2s;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * [Byte: opCode, String server, PacketWaypointNoServer: point]
 */
public class PacketDeleteWaypointC2S extends Packet {
    public static final byte OPCODE = PacketOpcodes.DeleteWaypoint.opcode;

    public final String server;
    public final PacketWaypointC2S waypoint;

    public PacketDeleteWaypointC2S(String server, PacketWaypointC2S waypoint) {
        this.server = server;
        this.waypoint = waypoint;
    }

    public PacketDeleteWaypointC2S(ByteBuffer buff) {
        this.server = readString(buff);
        this.waypoint = new PacketWaypointC2S(server, buff);
    }

    @Override
    public ByteBuffer writePacket() {
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        ByteBuffer b1 = waypoint.writePacketNoServer();
        ByteBuffer buff = ByteBuffer.allocate(1 + Integer.BYTES + server.length + b1.capacity());
        buff.put(OPCODE);
        buff.putInt(server.length);
        buff.put(server);
        buff.put(b1);
        return buff;
    }

}
