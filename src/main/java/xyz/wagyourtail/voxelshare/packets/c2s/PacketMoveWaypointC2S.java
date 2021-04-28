package xyz.wagyourtail.voxelshare.packets.c2s;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PacketMoveWaypointC2S extends Packet {
    public static final byte OPCODE = PacketOpcodes.MoveWaypoint.opcode;
    public final String server;
    public final PacketWaypointC2S from;
    public final PacketWaypointC2S to;

    public PacketMoveWaypointC2S(String server, PacketWaypointC2S from, PacketWaypointC2S to) {
        this.server = server;
        this.from = from;
        this.to = to;
    }

    public PacketMoveWaypointC2S(ByteBuffer buff) {
        this.server = readString(buff);
        this.from = new PacketWaypointC2S(server, buff);
        this.to = new PacketWaypointC2S(server, buff);
    }

    @Override
    public ByteBuffer writePacket() {
        ByteBuffer from = this.from.writePacketNoServer();
        ByteBuffer to = this.to.writePacketNoServer();
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(1 + from.capacity() + to.capacity() + server.length + Integer.BYTES);
        buff.put(OPCODE);
        buff.putInt(server.length);
        buff.put(server);
        buff.put(from);
        buff.put(to);
        return buff;
    }

}
