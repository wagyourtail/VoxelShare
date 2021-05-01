package xyz.wagyourtail.voxelshare.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.voxelshare.events.server.PlayerJoinEvent;
import xyz.wagyourtail.voxelshare.events.server.PlayerLeaveEvent;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PlayerJoinEvent.EVENT.invoker().interact(player, server);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void onPlayerDisconnect(ServerPlayerEntity player, CallbackInfo ci) {
        PlayerLeaveEvent.EVENT.invoker().interact(player, server);
    }
}
