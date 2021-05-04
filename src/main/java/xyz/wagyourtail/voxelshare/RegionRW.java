package xyz.wagyourtail.voxelshare;

import java.io.IOException;
import java.util.Map;

public interface RegionRW<T> {

    void setParent(T parent);

    byte[] getDataBytes() throws IOException;

    Map<Integer, String> getKey() throws IOException;

    void setKey(Map<Integer, String> key);

    long getLastChangeTime() throws IOException;

    void setLastChangeTime(long time) throws IOException;

    void doSaveData() throws IOException;
}
