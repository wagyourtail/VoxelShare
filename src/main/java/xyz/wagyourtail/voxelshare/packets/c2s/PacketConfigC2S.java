package xyz.wagyourtail.voxelshare.packets.c2s;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;

public class PacketFrequencyC2S extends Packet {
    public static final byte OPCODE = PacketOpcodes.ConfigSync.opcode;
    public final int waypointFrequency, regionFrequency, positionFrequency;

    public PacketFrequencyC2S(int waypointFrequency, int regionFrequency, int positionFrequency) {
        this.waypointFrequency = waypointFrequency;
        this.regionFrequency = regionFrequency;
        this.positionFrequency = positionFrequency;
    }

    public PacketFrequencyC2S(ByteBuffer buff) {
        this.waypointFrequency = buff.getInt();
        this.regionFrequency = buff.getInt();
        this.positionFrequency = buff.getInt();
    }

    @Override
    public ByteBuffer writePacket() {
        ByteBuffer buff = ByteBuffer.allocate(1 + Integer.BYTES * 3);
        buff.put(OPCODE);
        buff.putInt(waypointFrequency);
        buff.putInt(regionFrequency);
        buff.putInt(positionFrequency);
        return buff;
    }

}
