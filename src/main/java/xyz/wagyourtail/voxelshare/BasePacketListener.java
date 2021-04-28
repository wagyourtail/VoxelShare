package xyz.wagyourtail.voxelshare;

import java.nio.ByteBuffer;

public abstract class BasePacketListener {


    public void throwWrongWay() {
        throw new RuntimeException("Opcode doesn't work in this direction");
    }

    public abstract void onPacket(ByteBuffer buff) throws UnsupportedOperationException;
}
