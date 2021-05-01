package xyz.wagyourtail.voxelshare.packets.c2s;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class PacketWaypointC2S extends Packet {
    public static final byte OPCODE = PacketOpcodes.Waypoint.opcode;
    public final String server, world, name;
    public final int x, y, z;
    public final long editTime;
    public final boolean enabled;
    public final int red, green, blue;
    public final String suffix, dimensions;

    public PacketWaypointC2S(String server, String world, String name, int x, int y, int z, long editTime, boolean enabled, int red, int green, int blue, String suffix, String dimension) {
        this.server = server;
        this.world = world;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.editTime = editTime;
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
        this.editTime = buff.getLong();
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
        ByteBuffer buff = ByteBuffer.allocate(Long.BYTES + Integer.BYTES * 10 + world.length + name.length + suffix.length + dimensions.length + 1);
        buff.putInt(world.length);
        buff.put(world);
        buff.putInt(name.length);
        buff.put(name);
        buff.putInt(x);
        buff.putInt(y);
        buff.putInt(z);
        buff.putLong(editTime);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PacketWaypointC2S)) return false;
        PacketWaypointC2S that = (PacketWaypointC2S) o;
        return x == that.x && y == that.y && z == that.z && enabled == that.enabled && red == that.red && green == that.green && blue == that.blue && server.equals(that.server) && world.equals(that.world) && name.equals(that.name) && suffix.equals(that.suffix) && dimensions.equals(that.dimensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, world, name, x, y, z, enabled, red, green, blue, suffix, dimensions);
    }

}
