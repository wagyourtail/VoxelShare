package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.util.UUID;

public class PacketPlayerLeaveS2C extends Packet {
    public static final byte OPCODE = PacketOpcodes.PlayerLeave.opcode;
    public final UUID player;

    public PacketPlayerLeaveS2C(UUID player) {
        this.player = player;
    }

    public PacketPlayerLeaveS2C(ByteBuffer buff) {
        long least = buff.getLong();
        long most = buff.getLong();
        player = new UUID(most, least);
    }

    @Override
    public ByteBuffer writePacket() {
        ByteBuffer buff = ByteBuffer.allocate(1 + Long.BYTES * 2);
        buff.put(OPCODE);
        buff.putLong(player.getLeastSignificantBits());
        buff.putLong(player.getMostSignificantBits());
        return buff;
    }

}
