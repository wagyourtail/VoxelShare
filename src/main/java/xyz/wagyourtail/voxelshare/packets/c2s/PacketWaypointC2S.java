package xyz.wagyourtail.voxelshare.packets.c2s;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * [byte OpCode, String server, String world, String:name, int: x, int: y, int: z, byte: enabled, int:
 * red, int: green, int: blue, String: suffix, String: dimensions]
 */
public class PacketWaypointC2S extends Packet {
    public static final byte OPCODE = PacketOpcodes.Waypoint.opcode;
    public final String server, world, name;
    public final int x, y, z;
    public final boolean enabled;
    public final int red, green, blue;
    public final String suffix, dimensions;

    public PacketWaypointC2S(String server, String world, String name, int x, int y, int z, boolean enabled, int red, int green, int blue, String suffix, String dimension) {
        this.server = server;
        this.world = world;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.enabled = enabled;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.suffix = suffix;
        this.dimensions = dimension;
    }

    public PacketWaypointC2S(ByteBuffer buff) {
        this(readString(buff), buff);
    }

    public PacketWaypointC2S(String server, ByteBuffer buff) {
        this.server = server;
        this.world = readString(buff);
        this.name = readString(buff);
        this.x = buff.getInt();
        this.y = buff.getInt();
        this.z = buff.getInt();
        this.enabled = buff.get() != 0;
        this.red = buff.getInt();
        this.green = buff.getInt();
        this.blue = buff.getInt();
        this.suffix = readString(buff);
        this.dimensions = readString(buff);
    }

    @Override
    public ByteBuffer writePacket() {
        ByteBuffer b1 = writePacketNoServer();
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(1 + b1.capacity() + Integer.BYTES + server.length);
        buff.put(OPCODE);
        buff.putInt(server.length);
        buff.put(server);
        buff.put(b1);
        return buff;
    }

    public ByteBuffer writePacketNoServer() {
        byte[] world = this.world.getBytes(StandardCharsets.UTF_8);
        byte[] name = this.name.getBytes(StandardCharsets.UTF_8);
        byte[] suffix = this.suffix.getBytes(StandardCharsets.UTF_8);
        byte[] dimensions = this.dimensions.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(Integer.BYTES * 10 + world.length + name.length + suffix.length + dimensions.length + 1);
        buff.putInt(world.length);
        buff.put(world);
        buff.putInt(name.length);
        buff.put(name);
        buff.putInt(x);
        buff.putInt(y);
        buff.putInt(z);
        buff.put((byte) (enabled ? 1 : 0));
        buff.putInt(red);
        buff.putInt(green);
        buff.putInt(blue);
        buff.putInt(suffix.length);
        buff.put(suffix);
        buff.putInt(dimensions.length);
        buff.put(dimensions);
        return buff;
    }
}
