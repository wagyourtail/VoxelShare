package xyz.wagyourtail.voxelshare;

import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;

public abstract class BasePacketListener {


    public void throwWrongWay() {
        throw new RuntimeException("Opcode doesn't work in this direction");
    }

    public abstract void onPacket(PacketOpcodes opcode, ByteBuffer buff) throws UnsupportedOperationException;
}
