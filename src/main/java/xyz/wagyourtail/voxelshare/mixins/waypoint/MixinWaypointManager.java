package xyz.wagyourtail.voxelshare.mixins.waypoint;

import java.util.LinkedHashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mamiyaotaru.voxelmap.WaypointManager;
import com.mamiyaotaru.voxelmap.util.Waypoint;

import xyz.wagyourtail.voxelshare.interfaces.waypoint.IIWaypointManager;

@Mixin(value = WaypointManager.class, remap = false)
public class MixinWaypointManager implements IIWaypointManager {

    @Unique
    private Set<Waypoint> deletedWaypoints = new LinkedHashSet<>();
    
    @Inject(at = @At("HEAD"), method = "addWaypoint", remap = false)
    public void deleteWaypoint(Waypoint newPt, CallbackInfo info) {
        deletedWaypoints.add(newPt);
    }

    @Override
    public Set<Waypoint> getDeletedPoints() {
        return deletedWaypoints;
    }
    
}
