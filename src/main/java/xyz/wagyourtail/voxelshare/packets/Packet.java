package xyz.wagyourtail.voxelshare.packets;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class Packet {

    public abstract ByteBuffer writePacket();

    /**
     * [int strLength, byte[strLength] string]
     * @param buff buffer to read from
     *
     * @return read string value.
     */
    protected static String readString(ByteBuffer buff) {
        int length = buff.getInt();
        byte[] string = new byte[length];
        buff.get(string);
        return new String(string, StandardCharsets.UTF_8);
    }
}
