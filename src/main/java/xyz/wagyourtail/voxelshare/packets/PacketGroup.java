package xyz.wagyourtail.voxelshare.packets;

import java.nio.ByteBuffer;
import java.util.List;

public abstract class PacketGroup<T extends Packet> extends Packet {
    public List<T> children;

    public abstract T createChild(ByteBuffer buff);
}
