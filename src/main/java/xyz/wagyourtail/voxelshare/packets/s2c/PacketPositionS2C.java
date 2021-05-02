package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 *
 * [Byte: opCode, String server, String world, Long lestUUID, Long mostUUID, int x, int z]
 */
public class PacketPositionS2C extends Packet {
    public static final byte OPCODE = PacketOpcodes.Position.opcode;
    public final String server, world, dimension;
    public final UUID player;
    public final int x, z;

    public PacketPositionS2C(String server, String world, String dimension, UUID player, int x, int z) {
        this.server = server;
        this.world = world;
        this.dimension = dimension;
        this.player = player;
        this.x = x;
        this.z = z;
    }

    public PacketPositionS2C(ByteBuffer buff) {
        this(readString(buff), readString(buff), readString(buff), buff);
    }

    PacketPositionS2C(String server, String world, String dimension, ByteBuffer buff) {
        this.server = server;
        this.world = world;
        this.dimension =dimension;
        long lest = buff.getLong();
        long most = buff.getLong();
        this.player = new UUID(most, lest);
        this.x = buff.getInt();
        this.z = buff.getInt();
    }

    @Override
    public ByteBuffer writePacket() {
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        byte[] world = this.world.getBytes(StandardCharsets.UTF_8);
        byte[] dimension = this.dimension.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(1 + Integer.BYTES * 5 + Long.BYTES * 2 + server.length + world.length + dimension.length);
        buff.put(OPCODE);
        buff.putInt(server.length);
        buff.put(server);
        buff.putInt(world.length);
        buff.put(world);
        buff.putInt(dimension.length);
        buff.put(dimension);
        buff.putLong(player.getLeastSignificantBits());
        buff.putLong(player.getMostSignificantBits());
        buff.putInt(x);
        buff.putInt(z);
        return buff;
    }
}
