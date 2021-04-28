package xyz.wagyourtail.voxelshare.packets.c2s;

import com.google.common.collect.ImmutableList;
import xyz.wagyourtail.voxelshare.packets.PacketGroup;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;


/**
 * [Byte: opCode, String server, String world, region[] regions]
 *
 * region: [int x,int z]
 */
public class PacketRequestRegionsC2S extends PacketGroup<PacketRequestRegionC2S> {
    public static final byte OPCODE = PacketOpcodes.RequestRegions.opcode;
    public final String server, world;

    public PacketRequestRegionsC2S(String server, String world, List<PacketRequestRegionC2S> regions) {
        this.server = server;
        this.world = world;
        this.children = ImmutableList.copyOf(regions);
    }

    public PacketRequestRegionsC2S(ByteBuffer buff) {
        this.server = readString(buff);
        this.world = readString(buff);
        List<PacketRequestRegionC2S> regions = new LinkedList<>();
        while (buff.hasRemaining()) {
            regions.add(createChild(buff));
        }
        this.children = ImmutableList.copyOf(regions);
    }

    @Override
    public ByteBuffer writePacket() {
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        byte[] world = this.world.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(1 + Integer.BYTES * (1 + children.size()) * 2);
        buff.put(OPCODE);
        buff.putInt(server.length);
        buff.put(server);
        buff.putInt(world.length);
        buff.put(world);
        for (PacketRequestRegionC2S region : children) {
            buff.putInt(region.x);
            buff.putInt(region.z);
        }
        return buff;
    }

    @Override
    public PacketRequestRegionC2S createChild(ByteBuffer buff) {
        return new PacketRequestRegionC2S(server, world, buff);
    }

}
