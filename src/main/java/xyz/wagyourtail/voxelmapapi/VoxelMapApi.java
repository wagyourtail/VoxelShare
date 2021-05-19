package xyz.wagyourtail.voxelmapapi;

import com.mamiyaotaru.voxelmap.VoxelMap;
import com.mamiyaotaru.voxelmap.WaypointManager;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.ApiStatus;
import xyz.wagyourtail.voxelmapapi.accessor.IWaypointManager;
import xyz.wagyourtail.voxelmapapi.mixin.region.MixinPersistentMap;
import xyz.wagyourtail.voxelshare.RegionHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VoxelMapApi {
    /**
     * world, dimension, regionKey, region
     */
    private static final Map<String, Map<String, Map<String, RegionContainer>>> regions = new HashMap<>();
    private static boolean firstGet = true;

    public static void clearRegions() {
        regions.clear();
    }

    public static String getCurrentServer() {
        String mapName;
        WaypointManager man = ((WaypointManager)VoxelMap.getInstance().getWaypointManager());
        if (MinecraftClient.getInstance().isIntegratedServerRunning()) {
            mapName = MinecraftClient.getInstance().getServer().getSavePath(WorldSavePath.ROOT).normalize().toFile().getName();
        } else {
            mapName = man.getServerName();
            if (mapName != null) {
                mapName = mapName.toLowerCase();
            }
        }
        return mapName;
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
    public static synchronized RegionContainer addRegion(String world, String dimension, String key, int x, int z) {
        RegionContainer n = new RegionContainer(world, dimension, x, z);
        Map<String, RegionContainer> reg = regions.computeIfAbsent(world, e -> new HashMap<>()).computeIfAbsent(dimension, d -> new LinkedHashMap<>());
        reg.put(key, n);
        return n;
    }

    public static synchronized Map<String, Map<String, Map<String, RegionContainer>>> getRegions() {
        if (firstGet) {
            MixinPersistentMap pMap = (MixinPersistentMap) VoxelMap.getInstance().getPersistentMap();
            synchronized (pMap.getCachedRegions()) {
                try {
                    Map<String, Map<String, List<File>>> files = RegionHelper.getFiles(getBaseFolder());
                    for (Map.Entry<String, Map<String, List<File>>> wd : files.entrySet()) {
                        String world = wd.getKey();
                        for (Map.Entry<String, List<File>> dim : wd.getValue().entrySet()) {
                            String dimension = dim.getKey();
                            for (File file : dim.getValue()) {
                                String key = file.getName().replace(".zip", "");
                                regions.computeIfAbsent(world, w -> new HashMap<>()).computeIfAbsent(dimension, d ->
                                    new LinkedHashMap<>()).computeIfAbsent(key, k -> {
                                        String[] kp = k.split(",");
                                        return new RegionContainer(world, dimension, Integer.parseInt(kp[0]), Integer.parseInt(kp[1]));
                                });
                            }
                        }
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            firstGet = false;
        }

        return regions;
    }

    public static synchronized RegionContainer getOrCreateRegion(String world, String dimension, int x, int z) {
        return getRegions().computeIfAbsent(world, w -> new HashMap<>())
            .computeIfAbsent(dimension, d -> new LinkedHashMap<>())
            .computeIfAbsent(x + "," + z, k -> new RegionContainer(world, dimension, x, z));
    }

    public static synchronized File getBaseFolder() {
        return new File(FabricLoader.getInstance().getGameDirectory(), "/voxelmap/cache/" + getCurrentServer() + "/");
    }

    public static synchronized void addNewRegionData(String world, String dimension, int x, int z, RegionHelper.RegionData region) {
        RegionContainer match = getOrCreateRegion(world, dimension, x, z);
        match.combineData(region);
    }
}
