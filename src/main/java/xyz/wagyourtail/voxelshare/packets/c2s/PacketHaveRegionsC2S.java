package xyz.wagyourtail.voxelshare.packets.c2s;

import com.google.common.collect.ImmutableList;
import xyz.wagyourtail.voxelshare.packets.PacketGroup;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class PacketHaveRegionsC2S extends PacketGroup<PacketHaveRegionC2S> {
    public static final byte OPCODE = PacketOpcodes.HaveRegions.opcode;
    public final String server, world;

    public PacketHaveRegionsC2S(String server, String world, List<PacketHaveRegionC2S> regions) {
        this.server = server;
        this.world = world;
        this.children = ImmutableList.copyOf(regions);
    }

    public PacketHaveRegionsC2S(ByteBuffer buff) {
        this.server = readString(buff);
        this.world = readString(buff);
        List<PacketHaveRegionC2S> regions = new LinkedList<>();
        while (buff.hasRemaining()) {
            regions.add(createChild(buff));
        }
        this.children = ImmutableList.copyOf(regions);
    }

    @Override
    public ByteBuffer writePacket() {
        byte[] server = this.server.getBytes(StandardCharsets.UTF_8);
        byte[] world = this.world.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(1 + Integer.BYTES * 2 + (Integer.BYTES * 2 + Long.BYTES) * children.size());
        buff.put(OPCODE);
        buff.putInt(server.length);
        buff.put(server);
        buff.putInt(world.length);
        buff.put(world);
        for (PacketHaveRegionC2S region : children) {
            buff.putLong(region.updateTime);
            buff.putInt(region.x);
            buff.putInt(region.z);
        }
        return buff;
    }

    @Override
    public PacketHaveRegionC2S createChild(ByteBuffer buff) {
        return new PacketHaveRegionC2S(server, world, buff);
    }

}
