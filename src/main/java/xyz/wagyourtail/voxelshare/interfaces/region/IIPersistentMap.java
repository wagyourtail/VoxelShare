package xyz.wagyourtail.voxelshare.interfaces.region;

import com.mamiyaotaru.voxelmap.persistent.CachedRegion;

public interface IIPersistentMap {
    
    public CachedRegion getRegion(int x, int z);
    
    public void putIfAbsent(CachedRegion reg);
    
}
