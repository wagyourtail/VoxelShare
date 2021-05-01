package xyz.wagyourtail.voxelshare.client.server;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import xyz.wagyourtail.voxelmapapi.IWaypoint;
import xyz.wagyourtail.voxelmapapi.VoxelMapApi;
import xyz.wagyourtail.voxelshare.client.VoxelShareClient;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketDeleteWaypointC2S;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketEditWaypointC2S;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketWaypointC2S;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketWaypointsC2S;
import xyz.wagyourtail.voxelshare.server.DedicatedClientEndpoint;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class IntegratedClientEndpoint extends DedicatedClientEndpoint {
    public IntegratedClientEndpoint(UUID player) {
        super(player);
    }

    @Override
    public void doRegions(MinecraftServer mc) {
        assert MinecraftClient.getInstance().player != null;
        if (MinecraftClient.getInstance().player.getUuid().equals(player)) return;


        //TODO:
    }

    @Override
    public void doWaypoints(MinecraftServer mc) {
        assert MinecraftClient.getInstance().player != null;
        if (MinecraftClient.getInstance().player.getUuid().equals(player)) return;

        if (waypointSendTime == 0) {
            List<PacketWaypointC2S> wp = VoxelMapApi.getWaypoints().stream().filter(e -> ((IWaypoint) e).shouldSync()).map(VoxelShareClient::WpToPacket).collect(Collectors.toList());
            sendPacket(mc, new PacketWaypointsC2S(VoxelMapApi.getCurrentServer(), wp));
        } else {
            String server = VoxelMapApi.getCurrentServer();
            List<PacketWaypointC2S> newWp = new LinkedList<>();
            List<PacketDeleteWaypointC2S> deleted = VoxelMapApi.getDeletedWaypoints().stream().map(e -> new PacketDeleteWaypointC2S(server, VoxelShareClient.WpToPacket(e))).collect(Collectors.toList());
            List<PacketEditWaypointC2S> edited = new LinkedList<>();
            VoxelMapApi.getWaypoints().stream().filter(e -> ((IWaypoint) e).getEditTime() > waypointSendTime && ((IWaypoint) e).shouldSync()).forEach(e -> {
                IWaypoint wp = (IWaypoint) e;
                if (wp.getOld() != null) {
                    edited.add(new PacketEditWaypointC2S(server, VoxelShareClient.WpToPacket(wp.getOld()), VoxelShareClient.WpToPacket(e)));
                } else {
                    newWp.add(VoxelShareClient.WpToPacket(e));
                }
            });
            sendPacket(mc, new PacketWaypointsC2S(server, newWp));
            deleted.forEach(e -> sendPacket(mc, e));
            edited.forEach(e -> sendPacket(mc, e));
        }
    }

}
