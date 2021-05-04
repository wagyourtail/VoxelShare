package xyz.wagyourtail.voxelmapapi.accessor;

import xyz.wagyourtail.voxelmapapi.events.Synchronization;

public interface ICachedRegion {
    boolean isClosed();

    void doSaveData(boolean newThread);

    void setLiveChunksUpdated();

    long getLastChangeTime();

    void setLastChangeTime(long time);

    Synchronization getSync();
}
