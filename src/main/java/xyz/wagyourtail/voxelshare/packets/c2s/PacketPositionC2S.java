package xyz.wagyourtail.voxelshare.packets.c2s;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * [Byte: opCode, String server, String world, int x, int z]
 */
public class PacketPositionC2S extends Packet {
    public static final byte OPCODE = PacketOpcodes.Position.opcode;
    public final String server, world;
    public final int x, z;

    public PacketPositionC2S(String server, String world, int x, int z) {
        this.server = server;
        this.world = world;

        this.x = x;
        this.z = z;
    }

    public PacketPositionC2S(ByteBuffer buff) {
        this.server = readString(buff);
        this.world = readString(buff);

        this.x = buff.getInt();
        this.z = buff.getInt();
    }

    @Override
    public ByteBuffer writePacket() {
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        byte[] world = this.world.getBytes(StandardCharsets.UTF_8);
        if (server != null && world != null) {
            ByteBuffer buff = ByteBuffer.allocate(world.length + server.length + Integer.BYTES * 4 + 1);
            buff.put(OPCODE);
            buff.putInt(server.length);
            buff.put(server);
            buff.putInt(world.length);
            buff.put(world);
            buff.putInt(x);
            buff.putInt(z);
            return buff;
        }
        return null;
    }

}
