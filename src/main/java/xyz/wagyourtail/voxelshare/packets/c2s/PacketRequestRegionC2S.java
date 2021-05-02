package xyz.wagyourtail.voxelshare.packets.c2s;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * [Byte: opCode, String server, String world, int x, int z]
 */
public class PacketRequestRegionC2S extends Packet {
    public static final byte OPCODE = PacketOpcodes.RequestRegion.opcode;
    public final String server, world, dimension;
    public final int x, z;

    public PacketRequestRegionC2S(String server, String world, String dimension, int x, int z) {
        this.server = server;
        this.world = world;
        this.dimension = dimension;
        this.x = x;
        this.z = z;
    }

    public PacketRequestRegionC2S(ByteBuffer buff) {
        this.server = readString(buff);
        this.world = readString(buff);
        this.dimension = readString(buff);
        this.x = buff.getInt();
        this.z = buff.getInt();
    }

    public PacketRequestRegionC2S(String server, String world, String dimension, ByteBuffer buff) {
        this.server = server;
        this.world = world;
        this.dimension = dimension;
        this.x = buff.getInt();
        this.z = buff.getInt();
    }

    @Override
    public ByteBuffer writePacket() {
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        byte[] world = this.world.getBytes(StandardCharsets.UTF_8);
        byte[] dimension = this.dimension.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(1 + Integer.BYTES * 4 + server.length + world.length);
        buff.put(OPCODE);
        buff.putInt(server.length);
        buff.put(server);
        buff.putInt(world.length);
        buff.put(world);
        buff.putInt(dimension.length);
        buff.put(dimension);
        buff.putInt(x);
        buff.putInt(z);
        return buff;
    }
}
