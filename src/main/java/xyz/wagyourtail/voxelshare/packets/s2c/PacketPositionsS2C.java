package xyz.wagyourtail.voxelshare.packets.s2c;

import com.google.common.collect.ImmutableList;
import xyz.wagyourtail.voxelshare.packets.PacketGroup;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class PacketPositionsS2C extends PacketGroup<PacketPositionS2C> {
    public static final byte OPCODE = PacketOpcodes.Positions.opcode;
    public final String server, world, dimension;

    public PacketPositionsS2C(String server, String world, String dimension, List<PacketPositionS2C> positions) {
        this.server = server;
        this.world = world;
        this.dimension = dimension;
        this.children = ImmutableList.copyOf(positions);
    }

    public PacketPositionsS2C(ByteBuffer buff) {
        this.server = readString(buff);
        this.world = readString(buff);
        this.dimension = readString(buff);
        List<PacketPositionS2C> positions = new LinkedList<>();
        while (buff.hasRemaining()) {
            positions.add(createChild(buff));
        }
        this.children = ImmutableList.copyOf(positions);
    }

    @Override
    public ByteBuffer writePacket() {
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        byte[] world = this.world.getBytes(StandardCharsets.UTF_8);
        byte[] dimension = this.dimension.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(1 + (Integer.BYTES * 2 + Long.BYTES * 2) * children.size() + Integer.BYTES * 3 + server.length + world.length + dimension.length);
        buff.put(OPCODE);
        buff.putInt(server.length);
        buff.put(server);
        buff.putInt(world.length);
        buff.put(world);
        buff.putInt(dimension.length);
        buff.put(dimension);
        for (PacketPositionS2C pos : children) {
            buff.putLong(pos.player.getLeastSignificantBits());
            buff.putLong(pos.player.getMostSignificantBits());
            buff.putInt(pos.x);
            buff.putInt(pos.z);
        }
        return buff;
    }

    @Override
    public PacketPositionS2C createChild(ByteBuffer buff) {
        return new PacketPositionS2C(server, world, dimension, buff);
    }

}
