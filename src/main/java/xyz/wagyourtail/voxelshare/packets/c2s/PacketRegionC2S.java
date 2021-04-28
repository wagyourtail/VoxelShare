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
    public final String server, world;
    public final long updateTime;
    public final int x, z;
    public final byte[] data;
    public final String keys;

    public PacketRegionC2S(String server, String world, long updateTime, int x, int z, byte[] data, String keys) {
        this.server = server;
        this.world = world;
        this.updateTime = updateTime;
        this.x = x;
        this.z = z;
        this.data = data;
        this.keys = keys;
    }

    public PacketRegionC2S(ByteBuffer buff) {
        this.server = readString(buff);
        this.world = readString(buff);
        this.updateTime = buff.getLong();
        this.x = buff.getInt();
        this.z = buff.getInt();
        this.data = new byte[0x10000 * 18];
        buff.get(this.data);
        this.keys = readString(buff);
    }

    @Override
    public ByteBuffer writePacket() {
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        byte[] world = this.world.getBytes(StandardCharsets.UTF_8);
        byte[] keys = this.keys.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(1 + server.length + world.length + keys.length + data.length + Long.BYTES + Integer.BYTES * 4);
        buff.put(OPCODE);
        buff.putInt(server.length);
        buff.put(server);
        buff.putInt(world.length);
        buff.put(world);
        buff.putLong(updateTime);
        buff.putInt(x);
        buff.putInt(z);
        buff.put(data);
        buff.putInt(keys.length);
        buff.put(keys);
        return buff;
    }

}
