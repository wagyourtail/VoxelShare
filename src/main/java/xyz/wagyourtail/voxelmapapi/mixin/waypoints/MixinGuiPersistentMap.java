package xyz.wagyourtail.voxelmapapi.mixin.waypoints;

import com.mamiyaotaru.voxelmap.gui.overridden.Popup;
import com.mamiyaotaru.voxelmap.persistent.GuiPersistentMap;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.voxelmapapi.accessor.IWaypoint;

@Mixin(value = GuiPersistentMap.class, remap = false)
public class MixinGuiPersistentMap {
    @Shadow private Waypoint selectedWaypoint;

    @Inject(method = "popupAction", at = @At(value = "INVOKE", target = "Lcom/mamiyaotaru/voxelmap/gui/GuiAddWaypoint;<init>(Lcom/mamiyaotaru/voxelmap/gui/IGuiWaypoints;Lcom/mamiyaotaru/voxelmap/interfaces/IVoxelMap;Lcom/mamiyaotaru/voxelmap/util/Waypoint;Z)V", ordinal = 1))
    public void onEditWaypoint(Popup popup, int action, CallbackInfo ci) {
        IWaypoint wp = (IWaypoint) selectedWaypoint;
        wp.setOld(wp.clone());
    }
}
