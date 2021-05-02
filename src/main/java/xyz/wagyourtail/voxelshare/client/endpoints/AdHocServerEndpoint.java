package xyz.wagyourtail.voxelshare.client.endpoints;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import xyz.wagyourtail.voxelmapapi.VoxelMapApi;
import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketPositionC2S;

//TODO: adhoc
public class AdHocServerEndpoint extends AbstractServerEndpoint {

    public AdHocServerEndpoint(String sName) {
        super(sName);
    }

    @Override
    public void doWaypoints(MinecraftClient mc) {
        //TODO
    }

    @Override
    public void doRegions(MinecraftClient mc) {
        //TODO
    }

    @Override
    public void doPositions(MinecraftClient mc) {
        assert mc.player != null;
        BlockPos pos = mc.player.getBlockPos();
        sendPacket(mc, new PacketPositionC2S(VoxelMapApi.getCurrentServer(), VoxelMapApi.getCurrentWorld(), mc.player.world.getRegistryKey().getValue().getPath(), pos.getX(), pos.getZ()));
    }

    @Override
    public void sendPacket(MinecraftClient mc, Packet buff) {
        //TODO
    }

    @Override
    public void sendPacket(PacketContext ctx, Packet buff) {
        //TODO
    }

}
