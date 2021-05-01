package xyz.wagyourtail.voxelshare.packets.c2s;

import xyz.wagyourtail.voxelshare.ConfigOptions;
import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;

public class PacketConfigC2S extends Packet {
    public static final byte OPCODE = PacketOpcodes.ConfigSync.opcode;
    public final boolean sendWaypoint, sendRegion, sendPosition;
    public final int waypointFrequency, regionFrequency, positionFrequency;

    public PacketConfigC2S(ConfigOptions config) {
        this.sendWaypoint = config.sendWaypoint;
        this.sendRegion = config.sendRegion;
        this.sendPosition = config.sendPosition;
        this.waypointFrequency = config.waypointFrequency;
        this.regionFrequency = config.regionFrequency;
        this.positionFrequency = config.positionFrequency;
    }

    public PacketConfigC2S(boolean sendWaypoint, boolean sendRegion, boolean sendPosition, int waypointFrequency, int regionFrequency, int positionFrequency) {
        this.sendWaypoint = sendWaypoint;
        this.sendRegion = sendRegion;
        this.sendPosition = sendPosition;
        this.waypointFrequency = waypointFrequency;
        this.regionFrequency = regionFrequency;
        this.positionFrequency = positionFrequency;
    }

    public PacketConfigC2S(ByteBuffer buff) {
        this.sendWaypoint = buff.get() != 0;
        this.sendRegion = buff.get() != 0;
        this.sendPosition = buff.get() != 0;
        this.waypointFrequency = buff.getInt();
        this.regionFrequency = buff.getInt();
        this.positionFrequency = buff.getInt();
    }

    @Override
    public ByteBuffer writePacket() {
        ByteBuffer buff = ByteBuffer.allocate(4 + Integer.BYTES * 3);
        buff.put(OPCODE);
        buff.put((byte) (sendWaypoint ? 1 : 0));
        buff.put((byte) (sendRegion ? 1 : 0));
        buff.put((byte) (sendPosition ? 1 : 0));
        buff.putInt(waypointFrequency);
        buff.putInt(regionFrequency);
        buff.putInt(positionFrequency);
        return buff;
    }

}
