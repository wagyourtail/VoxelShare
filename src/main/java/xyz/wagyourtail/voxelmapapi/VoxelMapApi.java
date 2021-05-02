package xyz.wagyourtail.voxelmapapi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.ibm.icu.impl.locale.XCldrStub;
import com.mamiyaotaru.voxelmap.VoxelMap;
import com.mamiyaotaru.voxelmap.persistent.CachedRegion;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class VoxelMapApi {
    private static Map<String, Map<String, Set<Region>>> regions = new HashMap<>();
    private static boolean firstGet = true;
    public static String getCurrentServer() {
        return VoxelMap.getInstance().getWaypointManager().getCurrentWorldName();
    }

    public static String getCurrentWorld() {
        return ((IWaypointManager)VoxelMap.getInstance().getWaypointManager()).getCurrentSubWorldName();
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

    @ApiStatus.Internal
    public static synchronized void addRegion(String world, String dimension, CachedRegion region) {
        Region n = new Region(region.getX(), region.getZ(), world, dimension, region);
        Set<Region> reg = regions.computeIfAbsent(world, e -> new HashMap<>()).computeIfAbsent(dimension, d -> new LinkedHashSet<>());
        reg.remove(n);
        reg.add(n);
    }

    public static synchronized Map<String, Map<String, Set<Region>>> getRegions() {
        if (firstGet) {
            //TODO: load not yet loaded regions (threadsafely)
            firstGet = false;
        }

        Map<String, Map<String, Set<Region>>> copy = new HashMap<>();
        Map<String, Set<Region>> innerCopy;
        for (Map.Entry<String, Map<String, Set<Region>>> entry : regions.entrySet()) {
            innerCopy = new HashMap<>();
            for (Map.Entry<String, Set<Region>> innerEntry : entry.getValue().entrySet()) {
                innerCopy.put(innerEntry.getKey(), ImmutableSet.copyOf(innerEntry.getValue()));
            }
            copy.put(entry.getKey(), ImmutableMap.copyOf(innerCopy));
        }
        return ImmutableMap.copyOf(copy);
    }
}
