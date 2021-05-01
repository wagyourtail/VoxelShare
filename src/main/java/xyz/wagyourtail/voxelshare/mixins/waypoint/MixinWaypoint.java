package xyz.wagyourtail.voxelshare.mixins.waypoint;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.TreeSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
import com.mamiyaotaru.voxelmap.util.DimensionContainer;
import com.mamiyaotaru.voxelmap.util.TextUtils;
import com.mamiyaotaru.voxelmap.util.Waypoint;

import net.minecraft.world.dimension.DimensionType;
import xyz.wagyourtail.voxelshare.interfaces.waypoint.IWaypoint;

@Mixin(value = Waypoint.class, remap = false)
public class MixinWaypoint implements IWaypoint {
    
    @Shadow(remap = false)
    public TreeSet<DimensionContainer> dimensions;
    
    @Shadow(remap = false)
    public String name;
    
    @Shadow(remap = false)
    public boolean enabled;
    
    @Shadow(remap = false)
    public String imageSuffix;
    
    @Shadow(remap = false)
    public String world;
    
    @Shadow(remap = false)
    public int x;
    
    @Shadow(remap = false)
    public int y;
    
    @Shadow(remap = false)
    public int z;
    
    @Shadow(remap = false)
    public float red;
    
    @Shadow(remap = false)
    public float green;
    
    @Shadow(remap = false)
    public float blue;
    
    @Unique
    private Waypoint syncedWaypoint;

    @Override
    public Waypoint getSyncedWaypoint() {
        return syncedWaypoint;
    }
    
    @Override
    public void updateSyncedWaypoint() {
        syncedWaypoint = (Waypoint)(Object)this;
    }

    @Override
    public SyncState getSyncState() {
        if (syncedWaypoint == null) return SyncState.NotSynced;
        if (syncedWaypoint.equals(this)) return SyncState.Synced;
        return SyncState.OutOfDate;
    }

    @Override
    public byte[] toBytes() {
        String dimensions = "";
        for (DimensionContainer dim : this.dimensions) {
            dimensions += dim.getStorageName() + "#";
        }
        if (dimensions.equals("")) {
            dimensions = AbstractVoxelMap.getInstance().getDimensionManager()
                .getDimensionContainerByResourceLocation(DimensionType.OVERWORLD_REGISTRY_KEY.getValue())
                .getStorageName();
        }
        byte[] name = TextUtils.scrubName(this.name).getBytes(StandardCharsets.UTF_8);
        byte enabled = (byte) (this.enabled ? 1 : 0);
        byte[] suffix = this.imageSuffix.getBytes(StandardCharsets.UTF_8);
        byte[] world = (TextUtils.scrubName(this.world)).getBytes(StandardCharsets.UTF_8);
        byte[] dim = dimensions.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer
            .allocate(name.length + suffix.length + world.length + dim.length + Integer.BYTES * 10 + 1);
        buff.putInt(name.length);
        buff.put(name);
        buff.putInt(this.x);
        buff.putInt(this.y);
        buff.putInt(this.z);
        buff.put(enabled);
        buff.putInt((int) (this.red * 255));
        buff.putInt((int) (this.green * 255));
        buff.putInt((int) (this.blue * 255));
        buff.putInt(suffix.length);
        buff.put(suffix);
        buff.putInt(world.length);
        buff.put(world);
        buff.putInt(dim.length);
        buff.put(dim);
        return buff.array();
    }
    
    @Override
    public Waypoint copy() {
        TreeSet<DimensionContainer> dimCopy = new TreeSet<>();
        for (DimensionContainer dim : dimensions) dimCopy.add(dim);
        return new Waypoint(name, x, y, z, enabled, red, green, blue, imageSuffix, world, dimCopy);
    }
    
    @Overwrite(remap = false)
    public int getUnifiedColor() {
        switch(this.getSyncState()) {
            case OutOfDate:
                return 0xFF8000;
            case Synced:
                return 0x00FF00;
            default:
            case NotSynced:
                return 0xFF0000;
        }
    }
    
}
