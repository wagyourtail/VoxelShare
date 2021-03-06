package xyz.wagyourtail.voxelshare.packets.c2s;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 *  [Byte: opCode, String server, String world, Long: updateTime, int: x, int: z, byte[0x10000 * 18]: data, String keys]
 */
public class PacketRegionC2S extends Packet {
    public static final byte OPCODE = PacketOpcodes.RegionData.opcode;
    public final String server, world, dimension;
    public final long updateTime;
    public final int x, z;
    public final String keys;
    public final byte[] data;

    public PacketRegionC2S(String server, String world, String dimension, long updateTime, int x, int z, byte[] data, String keys) {
        this.server = server;
        this.world = world;
        this.dimension = dimension;
        this.updateTime = updateTime;
        this.x = x;
        this.z = z;
        this.keys = keys;
        this.data = data;
    }

    public PacketRegionC2S(ByteBuffer buff) {
        this.server = readString(buff);
        this.world = readString(buff);
        this.dimension = readString(buff);
        this.updateTime = buff.getLong();
        this.x = buff.getInt();
        this.z = buff.getInt();
        this.keys = readString(buff);
        this.data = new byte[0x10000 * 18];
        buff.get(this.data);
    }

    @Override
    public ByteBuffer writePacket() {
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        byte[] world = this.world.getBytes(StandardCharsets.UTF_8);
        byte[] dimension = this.dimension.getBytes(StandardCharsets.UTF_8);
        byte[] keys = this.keys.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(1 + server.length + world.length + keys.length + dimension.length + data.length + Long.BYTES + Integer.BYTES * 6);
        buff.put(OPCODE);
        buff.putInt(server.length);
        buff.put(server);
        buff.putInt(world.length);
        buff.put(world);
        buff.putInt(dimension.length);
        buff.put(dimension);
        buff.putLong(updateTime);
        buff.putInt(x);
        buff.putInt(z);
        buff.putInt(keys.length);
        buff.put(keys);
        buff.put(data);
        return buff;
    }

}
