package xyz.wagyourtail.voxelmapapi.mixin.events;

import com.mamiyaotaru.voxelmap.WaypointManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.voxelmapapi.events.SetWorldEvent;
import xyz.wagyourtail.voxelshare.server.VoxelShareServer;

@Mixin(value = WaypointManager.class, remap = false)
public class MixinWaypointManager {

    @Inject(method = "setSubworldName", at = @At("HEAD"))
    void onSetWorld(String name, boolean fromServer, CallbackInfo ci) {
        if (!fromServer)
            SetWorldEvent.EVENT.invoker().interact(name);
        else
            VoxelShareServer.logServerMessage("Set client world " + name);
    }
}
