package xyz.wagyourtail.voxelshare.packets.c2s;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class PacketHaveRegionC2S extends Packet {
    public static final byte OPCODE = PacketOpcodes.HaveRegion.opcode;
    public final String server, world;
    public final long updateTime;
    public final int x, z;

    public PacketHaveRegionC2S(String server, String world, long updateTime, int x, int z) {
        this.server = server;
        this.world = world;
        this.updateTime = updateTime;
        this.x = x;
        this.z = z;
    }

    public PacketHaveRegionC2S(ByteBuffer buff) {
        this.server = readString(buff);
        this.world = readString(buff);
        this.updateTime = buff.getLong();
        this.x = buff.getInt();
        this.z = buff.getInt();
    }

    public PacketHaveRegionC2S(String server, String world, ByteBuffer buff) {
        this.server = server;
        this.world = world;
        this.updateTime = buff.getLong();
        this.x = buff.getInt();
        this.z = buff.getInt();
    }

    @Override
    public ByteBuffer writePacket() {
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        byte[] world = this.world.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(1 + Integer.BYTES * 4 + Long.BYTES + server.length + world.length);
        buff.put(OPCODE);
        buff.putInt(server.length);
        buff.put(server);
        buff.putInt(world.length);
        buff.put(world);
        buff.putLong(updateTime);
        buff.putInt(x);
        buff.putInt(z);
        return buff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PacketHaveRegionC2S)) return false;
        PacketHaveRegionC2S that = (PacketHaveRegionC2S) o;
        return updateTime == that.updateTime && x == that.x && z == that.z && server.equals(that.server) && world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, world, updateTime, x, z);
    }

}
