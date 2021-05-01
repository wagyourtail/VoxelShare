package xyz.wagyourtail.voxelshare;

import xyz.wagyourtail.voxelshare.packets.Packet;

public abstract class Endpoint<T> {
    private int i = 0;
    public boolean sendWaypoints, sendRegions, sendPositions;
    public int waypointFrequency = Integer.MAX_VALUE, regionFrequency = Integer.MAX_VALUE, positionFrequency = Integer.MAX_VALUE;

    public void setConfig(boolean sendWaypoints, boolean sendRegions, boolean sendPositions, int waypointFrequency, int regionFrequency, int positionFrequency) {
        this.sendWaypoints = sendWaypoints && VoxelShare.config.sendWaypoint;
        this.sendRegions = sendRegions && VoxelShare.config.sendRegion;
        this.sendPositions = sendPositions && VoxelShare.config.sendRegion;
        this.waypointFrequency = Math.max(waypointFrequency, VoxelShare.config.waypointFrequency);
        this.regionFrequency = Math.max(regionFrequency, VoxelShare.config.regionFrequency);
        this.positionFrequency = Math.max(positionFrequency, VoxelShare.config.positionFrequency);
    }

    public void tick(T mc) {
        if (i % waypointFrequency == 0) doWaypoints(mc);
        if (i % regionFrequency == 0) doRegions(mc);
        if (i % positionFrequency == 0) doPositions(mc);
        ++i;
    }

    public abstract void doWaypoints(T mc);

    public abstract void doRegions(T mc);

    public abstract void doPositions(T mc);

    public abstract void sendPacket(T mc, Packet buff);
}
