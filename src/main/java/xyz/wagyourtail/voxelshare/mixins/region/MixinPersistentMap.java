package xyz.wagyourtail.voxelshare.mixins.region;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mamiyaotaru.voxelmap.persistent.CachedRegion;
import com.mamiyaotaru.voxelmap.persistent.PersistentMap;

import xyz.wagyourtail.voxelshare.interfaces.region.IIPersistentMap;

@Mixin(value = PersistentMap.class, remap = false)
public class MixinPersistentMap implements IIPersistentMap {

    @Shadow(remap = false)
    protected ConcurrentHashMap<String, CachedRegion> cachedRegions;

    @Shadow(remap = false)
    protected List<CachedRegion> cachedRegionsPool;

    @Override
    public CachedRegion getRegion(int x, int z) {
        synchronized (cachedRegions) {
            return cachedRegions.get(String.format("%d,%d", x, z));
        }
    }

    @Override
    public void putIfAbsent(CachedRegion reg) {
        String key = String.format("%d,%d", reg.getX(), reg.getZ());
        synchronized (this.cachedRegions) {
            if (cachedRegions.containsKey(key)) {
                this.cachedRegions.put(key, reg);
                synchronized (this.cachedRegionsPool) {
                    this.cachedRegionsPool.add(reg);
                }
            }
        }
        reg.refresh(true);
    }

}
