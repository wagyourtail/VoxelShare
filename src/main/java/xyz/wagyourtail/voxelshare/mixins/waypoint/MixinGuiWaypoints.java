package xyz.wagyourtail.voxelshare.mixins.waypoint;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mamiyaotaru.voxelmap.gui.GuiWaypoints;
import com.mamiyaotaru.voxelmap.interfaces.IWaypointManager;

import xyz.wagyourtail.voxelshare.interfaces.waypoint.IIGuiWaypoints;

@Mixin(GuiWaypoints.class)
public class MixinGuiWaypoints implements IIGuiWaypoints {
    @Shadow(remap = false)
    @Final
    protected IWaypointManager waypointManager;

    @Override
    public IWaypointManager getWaypointManager() {
        return waypointManager;
    }

    
}
