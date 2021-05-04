package xyz.wagyourtail.voxelmapapi.accessor;

import com.mamiyaotaru.voxelmap.util.Waypoint;

import java.util.List;

public interface IWaypointManager {
    List<Waypoint> getDeletedWaypoints();
    void clearDeletedWaypoints();
    String getCurrentSubWorldName();
}
