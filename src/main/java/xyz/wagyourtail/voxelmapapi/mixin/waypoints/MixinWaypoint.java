package xyz.wagyourtail.voxelmapapi.mixin.waypoints;

import com.mamiyaotaru.voxelmap.util.DimensionContainer;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import xyz.wagyourtail.voxelmapapi.IWaypoint;

import java.util.TreeSet;

@Mixin(value = Waypoint.class, remap = false)
public class MixinWaypoint implements IWaypoint {
    @Shadow public String name;
    @Shadow public int x;
    @Shadow public int z;
    @Shadow public int y;
    @Shadow public boolean enabled;
    @Shadow public float red;
    @Shadow public float green;
    @Shadow public float blue;
    @Shadow public String imageSuffix;
    @Shadow public String world;
    @Shadow public TreeSet<DimensionContainer> dimensions;
    @Unique Waypoint old = null;
    @Unique boolean sync = false;
    @Unique long editTime = 0;

    @Override
    public boolean shouldSync() {
        return sync;
    }

    @Override
    public void setSync(boolean sync) {
        this.sync = sync;
    }

    @Override
    public long getEditTime() {
        return editTime;
    }

    @Override
    public void setEditTime(long time) {
        this.editTime = time;
    }

    @Override
    public void setOld(Waypoint point) {
        old = point;
    }

    @Override
    public void clearOld() {
        old = null;
    }

    @Override
    public Waypoint getOld() {
        return old;
    }

    @Override
    public Waypoint clone() {
        Waypoint clonePoint = new Waypoint(name, x, z, y, enabled, red, green, blue, imageSuffix, world, (TreeSet<DimensionContainer>) dimensions.clone());
        ((IWaypoint)clonePoint).setEditTime(editTime);
        return clonePoint;
    }

}
