package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.c2s.PacketHaveRegionC2S;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketHaveRegionsC2S;

import java.nio.ByteBuffer;
import java.util.List;

public class PacketHaveRegionsS2C extends PacketHaveRegionsC2S {

    @SuppressWarnings({"unchecked","rawtypes"})
    public PacketHaveRegionsS2C(String server, String world, String dimension, List<PacketHaveRegionS2C> regions) {
        super(server, world, dimension, (List) regions);
    }

    public PacketHaveRegionsS2C(ByteBuffer buff) {
        super(buff);
    }

    @Override
    public PacketHaveRegionC2S createChild(ByteBuffer buff) {
        return new PacketHaveRegionS2C(server, world, dimension, buff);
    }

}
