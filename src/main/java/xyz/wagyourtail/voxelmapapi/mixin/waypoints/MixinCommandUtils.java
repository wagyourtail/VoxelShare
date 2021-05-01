package xyz.wagyourtail.voxelmapapi.mixin.waypoints;

import com.mamiyaotaru.voxelmap.util.CommandUtils;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.voxelmapapi.IWaypoint;

import java.util.Iterator;

@Mixin(CommandUtils.class)
public class MixinCommandUtils {

    @Inject(method = "waypointClicked", at = @At(value = "INVOKE", target = "Lcom/mamiyaotaru/voxelmap/gui/GuiAddWaypoint;<init>(Lcom/mamiyaotaru/voxelmap/gui/IGuiWaypoints;Lcom/mamiyaotaru/voxelmap/interfaces/IVoxelMap;Lcom/mamiyaotaru/voxelmap/util/Waypoint;Z)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onClickWaypoint(String command, CallbackInfo ci, boolean contron, String details, Waypoint newWaypoint, Iterator var4, Waypoint existingWaypoint) {
        IWaypoint wp = (IWaypoint) existingWaypoint;
        wp.setOld(wp.clone());
    }
}
