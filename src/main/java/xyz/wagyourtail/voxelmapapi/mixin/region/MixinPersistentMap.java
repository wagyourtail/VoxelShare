package xyz.wagyourtail.voxelmapapi.mixin.region;

import com.mamiyaotaru.voxelmap.persistent.CachedRegion;
import com.mamiyaotaru.voxelmap.persistent.PersistentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(PersistentMap.class)
public interface MixinPersistentMap {
    @Accessor
    ConcurrentHashMap<String, CachedRegion> getCachedRegions();

    @Accessor
    List<CachedRegion> getCachedRegionsPool();
}
