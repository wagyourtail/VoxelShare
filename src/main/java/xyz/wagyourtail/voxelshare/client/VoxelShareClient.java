package xyz.wagyourtail.voxelshare.client;

import com.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
import com.mamiyaotaru.voxelmap.util.DimensionContainer;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.world.dimension.DimensionType;
import xyz.wagyourtail.voxelmapapi.IWaypoint;
import xyz.wagyourtail.voxelmapapi.VoxelMapApi;
import xyz.wagyourtail.voxelmapapi.events.SetWorldEvent;
import xyz.wagyourtail.voxelshare.VoxelShare;
import xyz.wagyourtail.voxelshare.client.endpoints.DedicatedServerEndpoint;
import xyz.wagyourtail.voxelshare.client.endpoints.IntegratedServerEndpoint;
import xyz.wagyourtail.voxelshare.client.server.IntegratedServerPacketListener;
import xyz.wagyourtail.voxelshare.events.client.LeaveServerEvent;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketConfigC2S;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketWaypointC2S;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketWorldC2S;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketPingS2C;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketWaypointS2C;
import xyz.wagyourtail.voxelshare.server.VoxelShareServer;

import java.nio.ByteBuffer;
import java.util.TreeSet;

@Environment(EnvType.CLIENT)
public class VoxelShareClient extends VoxelShareServer implements ClientModInitializer {
    //TODO: ad-hoc server stuff with 2 port (full duplex) TCP hole punching
    public static AbstractClientPacketListener clientPacketListener;

    @Override
    public void onInitializeClient() {
        registerEvents();
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    @Override
    public void registerEvents() {
        super.registerEvents();
        ClientSidePacketRegistry.INSTANCE.register(VoxelShare.packetId, this::onServerPacket);
        LeaveServerEvent.EVENT.register(this::onDisconnect);
        SetWorldEvent.EVENT.register(this::onWorldChange);
    }

    protected void onTick(MinecraftClient mc) {
        if (mc.isIntegratedServerRunning()) {
            super.onTick(mc.getServer());
        }

        if (clientPacketListener != null) {
            clientPacketListener.server.tick(mc);
        }

    }

    public static void logToChat(String message) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText("[VoxelShare] " + message).styled(s -> s.withColor(Formatting.GRAY).withItalic(true)));
    }

    protected void onDisconnect() {
        clientPacketListener = null;
        VoxelShareServer.logServerMessage("closing server.");
        VoxelMapApi.clearDeletedWaypoints();
    }

    protected void onWorldChange(String world) {
        if (clientPacketListener != null) {
            clientPacketListener.server.sendPacket(MinecraftClient.getInstance(), new PacketWorldC2S(VoxelMapApi.getCurrentServer(), world));
        }
    }

    @Override
    protected void onPlayerJoin(PlayerEntity player, MinecraftServer mc) {
        serverPacketListners.computeIfAbsent(player.getUuid(),uuid -> new IntegratedServerPacketListener(uuid, mc)).player.sendPacket(mc, new PacketPingS2C());
    }

    private void onServerPacket(PacketContext context, PacketByteBuf buffer) {
        if (clientPacketListener == null) {
            MinecraftClient mc = MinecraftClient.getInstance();
            setClientPacketListener(context, mc, VoxelMapApi.getCurrentServer());
        } else if (!(clientPacketListener.server instanceof DedicatedServerEndpoint) && !clientPacketListener.server.getServerName().equals(VoxelMapApi.getCurrentServer())) {
            MinecraftClient mc = MinecraftClient.getInstance();
            setClientPacketListener(context, mc, VoxelMapApi.getCurrentServer());
        }

        ByteBuffer buff = buffer.nioBuffer();
        clientPacketListener.onPacket(PacketOpcodes.getByOpcode(buff.get()), buff);
    }

    private void setClientPacketListener(PacketContext context, MinecraftClient mc, String server) {
        if (mc.isIntegratedServerRunning()) {
            clientPacketListener = new ClientPacketListener(new IntegratedServerEndpoint(server), mc);
        } else {
            clientPacketListener = new ClientPacketListener(new DedicatedServerEndpoint(server), mc);
        }

        dedicatedServerAuthPackets(context, mc);
        logToChat("Server-side component detected.");
    }

    private void dedicatedServerAuthPackets(PacketContext context, MinecraftClient mc) {
        clientPacketListener.server.sendPacket(context, new PacketWorldC2S(VoxelMapApi.getCurrentServer(), VoxelMapApi.getCurrentWorld()));
        clientPacketListener.server.sendPacket(context, new PacketConfigC2S(VoxelShare.config));
    }

    @Override
    protected void onClientPacket(PacketContext context, PacketByteBuf buffer) {
        ByteBuffer buff = buffer.nioBuffer();

        serverPacketListners.computeIfAbsent(context.getPlayer().getUuid(), uuid -> new IntegratedServerPacketListener(uuid, MinecraftClient.getInstance().getServer()))
            .onPacket(PacketOpcodes.getByOpcode(buff.get()), buff);
    }

    public static PacketWaypointS2C WpToPacket(Waypoint wp) {
        StringBuilder dims = new StringBuilder();
        for (DimensionContainer dim : wp.dimensions) {
            dims.append(dim.getStorageName()).append("#");
        }
        return new PacketWaypointS2C(VoxelMapApi.getCurrentServer(), wp.world, wp.name, wp.x, wp.y, wp.z, ((IWaypoint)wp).getEditTime(), wp.enabled, (int) (wp.red * 255), (int) (wp.green * 255), (int) (wp.blue * 255), wp.imageSuffix, dims.toString());
    }

    public static Waypoint PacketToWp(PacketWaypointC2S wp) {
        TreeSet<DimensionContainer> dims = new TreeSet<>();
        String[] dimensionStrings = wp.dimensions.split("#");

        for (String dimensionString : dimensionStrings) {
            dims.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByIdentifier(dimensionString));
        }

        if (dims.size() == 0) {
            dims.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByResourceLocation(DimensionType.OVERWORLD_REGISTRY_KEY.getValue()));
        }

        Waypoint point = new Waypoint(wp.name, wp.x, wp.z, wp.y, wp.enabled, wp.red / 255F, wp.green / 255F, wp.blue / 255F, wp.suffix, wp.world, dims);
        ((IWaypoint)point).setEditTime(wp.editTime);
        return point;
    }

}
