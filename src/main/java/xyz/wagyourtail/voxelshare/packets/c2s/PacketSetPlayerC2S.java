package xyz.wagyourtail.voxelshare.packets.c2s;

import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * [Byte: opcode, Long: lest, Long: most]
 */
public class PacketSetPlayerC2S extends Packet {
    public static final byte OPCODE = PacketOpcodes.Player.opcode;
    public final UUID playerUUID;

    public PacketSetPlayerC2S(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public PacketSetPlayerC2S(ByteBuffer buff) {
        long least = buff.getLong();
        long most = buff.getLong();
        playerUUID = new UUID(most, least);
    }

    @Override
    public ByteBuffer writePacket() {
        ByteBuffer buff = ByteBuffer.allocate(Long.BYTES * 2 + 1);
        buff.put(OPCODE);
        buff.putLong(playerUUID.getLeastSignificantBits());
        buff.putLong(playerUUID.getMostSignificantBits());
        return buff;
    }

}
