package xyz.wagyourtail.voxelmapapi.accessor;

import com.mamiyaotaru.voxelmap.util.Waypoint;

public interface IWaypoint {
    boolean shouldSync();
    void setSync(boolean sync);
    long getEditTime();
    void setEditTime(long time);
    void setOld(Waypoint point);
    void clearOld();
    Waypoint getOld();
    Waypoint clone();
}
