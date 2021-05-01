package xyz.wagyourtail.voxelshare.packets.c2s;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;

public class PacketPingC2S extends Packet {
    public static final byte OPCODE = PacketOpcodes.PING.opcode;

    @Override
    public ByteBuffer writePacket() {
        return ByteBuffer.allocate(1).put(OPCODE);
    }

}
