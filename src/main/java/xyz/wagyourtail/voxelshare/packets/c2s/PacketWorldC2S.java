package xyz.wagyourtail.voxelshare.packets.c2s;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PacketWorldC2S extends Packet {
    public static final byte OPCODE = PacketOpcodes.World.opcode;
    public final String server;
    public final String world;

    public PacketWorldC2S(String server, String world) {
        this.server = server;
        this.world = world;
    }

    public PacketWorldC2S(ByteBuffer buff) {
        this.server = readString(buff);
        this.world = readString(buff);
    }

    @Override
    public ByteBuffer writePacket() {
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        byte[] world = this.world.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(1 + Integer.BYTES * 2 + world.length + server.length);
        buff.put(OPCODE);
        buff.putInt(server.length);
        buff.put(server);
        buff.putInt(world.length);
        buff.put(world);
        return buff;
    }

}
