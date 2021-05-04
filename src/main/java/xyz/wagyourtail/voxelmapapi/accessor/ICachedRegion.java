package xyz.wagyourtail.voxelmapapi;

public interface ICachedRegion {
    boolean isClosed();

    void doSaveData(boolean newThread);

    void setLiveChunksUpdated();

    long getLastChangeTime();

    void setLastChangeTime(long time);
}
