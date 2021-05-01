package xyz.wagyourtail.voxelshare.client;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import xyz.wagyourtail.voxelshare.VoxelShare;
import xyz.wagyourtail.voxelshare.packets.Packet;

import java.nio.ByteBuffer;

public class DedicatedServerEndpoint extends AbstractServerEndpoint {
    public final String serverName;

    public DedicatedServerEndpoint(String sName) {
        this.serverName = sName;
    }

    public String getServerName() {
        return serverName;
    }

    public boolean isDedicated() {
        return true;
    }

    @Override
    public void sendPacket(MinecraftClient mc, Packet pkt) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeBytes((ByteBuffer) pkt.writePacket().rewind());
        ClientSidePacketRegistry.INSTANCE.sendToServer(VoxelShare.packetId, packet);
    }

    @Override
    public void doWaypoints(MinecraftClient mc) {

    }

    @Override
    public void doRegions(MinecraftClient mc) {

    }

    @Override
    public void doPositions(MinecraftClient mc) {
        //server will just get this from entities
    }

}
