package xyz.wagyourtail.voxelshare.interfaces.waypoint;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;

import com.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
import com.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
import com.mamiyaotaru.voxelmap.util.DimensionContainer;
import com.mamiyaotaru.voxelmap.util.TextUtils;
import com.mamiyaotaru.voxelmap.util.Waypoint;

import net.minecraft.world.dimension.DimensionType;

public interface IWaypoint {

    public SyncState getSyncState();
    
    public Waypoint getSyncedWaypoint();
    
    public void updateSyncedWaypoint();
    
    public Waypoint copy();
    
    /*
     * pointData Structure:
     * [int:nameLength, String:name, int: x, int: y, int: z, byte: enabled, int:
     * red, int: green, int: blue, int: suffixLength, String: suffix, int:
     * worldLength, String: world, int: dimensionsLength, String: dimensions]
     */
    public static Waypoint fromBytes(IWaypointManager man, ByteBuffer buff) {
        Set<String> knownSubWorlds = man.getKnownSubworldNames();
        byte[] name = new byte[buff.getInt()];
        buff.get(name);
        int x = buff.getInt();
        int y = buff.getInt();
        int z = buff.getInt();
        boolean enabled = buff.get() != 0;
        float red = buff.getInt() / 255F;
        float green = buff.getInt() / 255F;
        float blue = buff.getInt() / 255F;
        byte[] suffix = new byte[buff.getInt()];
        buff.get(suffix);
        byte[] world = new byte[buff.getInt()];
        buff.get(world);
        byte[] dimensions = new byte[buff.getInt()];
        buff.get(dimensions);
        Set<DimensionContainer> dimSet = new TreeSet<>();
        for (String dim : new String(dimensions, StandardCharsets.UTF_8).split("#")) {
            dimSet.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByIdentifier(dim));
        }
        if (dimSet.size() == 0) {
            dimSet.add(AbstractVoxelMap.getInstance().getDimensionManager()
                .getDimensionContainerByResourceLocation(DimensionType.OVERWORLD_REGISTRY_KEY.getValue()));
        }
        if (!new String(world, StandardCharsets.UTF_8).equals("")) {
            knownSubWorlds.add(TextUtils.descrubName(new String(world, StandardCharsets.UTF_8)));
        }
        return new Waypoint(new String(name, StandardCharsets.UTF_8), x, y, z, enabled, red, green, blue, new String(suffix, StandardCharsets.UTF_8),
            new String(world, StandardCharsets.UTF_8), (TreeSet<DimensionContainer>) dimSet);
    }
    
    
    public byte[] toBytes();
    
    public static enum SyncState {
        Synced, OutOfDate, NotSynced;
    }

}
