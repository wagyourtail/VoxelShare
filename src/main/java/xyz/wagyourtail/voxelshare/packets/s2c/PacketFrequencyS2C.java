package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.c2s.PacketFrequencyC2S;

import java.nio.ByteBuffer;

public class PacketFrequencyS2C extends PacketFrequencyC2S {
    public PacketFrequencyS2C(int waypointFrequency, int regionFrequency, int positionFrequency) {
        super(waypointFrequency, regionFrequency, positionFrequency);
    }

    public PacketFrequencyS2C(ByteBuffer buff) {
        super(buff);
    }

}
