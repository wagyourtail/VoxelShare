package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.c2s.PacketRequestRegionC2S;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketRequestRegionsC2S;

import java.nio.ByteBuffer;
import java.util.List;

public class PacketRequestRegionsS2C extends PacketRequestRegionsC2S {

    public PacketRequestRegionsS2C(String server, String world, String dimension, List<PacketRequestRegionC2S> regions) {
        super(server, world, dimension, regions);
    }

    public PacketRequestRegionsS2C(ByteBuffer buff) {
        super(buff);
    }

    @Override
    public PacketRequestRegionC2S createChild(ByteBuffer buff) {
        return new PacketRequestRegionS2C(server, world, dimension, buff);
    }

}
