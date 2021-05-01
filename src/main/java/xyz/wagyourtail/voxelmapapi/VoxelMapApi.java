package xyz.wagyourtail.voxelmapapi;

import com.mamiyaotaru.voxelmap.VoxelMap;
import com.mamiyaotaru.voxelmap.util.Waypoint;

import java.util.List;

public class VoxelMapApi {
    public static String getCurrentServer() {
        return VoxelMap.getInstance().getWaypointManager().getCurrentWorldName();
    }

    public static String getCurrentWorld() {
        return VoxelMap.getInstance().getWaypointManager().getCurrentSubworldDescriptor(false);
    }

    public static void setCurrentWorld(String worldName) {
        VoxelMap.getInstance().newSubWorldName(worldName, true);
    }

    public static List<Waypoint> getWaypoints() {
        return VoxelMap.getInstance().getWaypointManager().getWaypoints();
    }

    public static List<Waypoint> getDeletedWaypoints() {
        return ((IWaypointManager)VoxelMap.getInstance().getWaypointManager()).getDeletedWaypoints();
    }

    public static void removeWaypoint(Waypoint point) {
        VoxelMap.getInstance().getWaypointManager().deleteWaypoint(point);
    }
    public static void addWaypoint(Waypoint point) {
        if (!getWaypoints().contains(point))
            VoxelMap.getInstance().getWaypointManager().addWaypoint(point);
    }

    public static void clearDeletedWaypoints() {
        ((IWaypointManager)VoxelMap.getInstance().getWaypointManager()).clearDeletedWaypoints();
    }

}
