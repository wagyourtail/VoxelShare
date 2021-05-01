package xyz.wagyourtail.voxelmapapi.mixin.waypoints;

import com.mamiyaotaru.voxelmap.gui.GuiWaypoints;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.voxelmapapi.IWaypoint;

@Mixin(value = GuiWaypoints.class, remap = false)
public class MixinGuiWaypoints {

    @Inject(method = "editWaypoint", at = @At("HEAD"))
    public void onEditWaypoint(Waypoint waypoint, CallbackInfo ci) {
        IWaypoint wp = (IWaypoint) waypoint;
        wp.setOld(wp.clone());
    }
}
