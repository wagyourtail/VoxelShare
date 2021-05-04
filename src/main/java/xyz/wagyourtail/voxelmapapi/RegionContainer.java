package xyz.wagyourtail.voxelmapapi;

import com.google.common.collect.ImmutableMap;
import com.mamiyaotaru.voxelmap.VoxelMap;
import com.mamiyaotaru.voxelmap.persistent.CachedRegion;
import com.mamiyaotaru.voxelmap.persistent.EmptyCachedRegion;
import xyz.wagyourtail.voxelmapapi.accessor.ICachedRegion;
import xyz.wagyourtail.voxelmapapi.mixin.region.MixinPersistentMap;
import xyz.wagyourtail.voxelshare.RegionHelper;
import xyz.wagyourtail.voxelshare.RegionRW;
import xyz.wagyourtail.voxelshare.RegionReadWriter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RegionContainer {
    public final String world, dimension;
    public final int x, z;

    public RegionContainer(String world, String dimension, int x, int z) {
        this.world = world;
        this.dimension = dimension;
        this.x = x;
        this.z = z;
    }

    public long getTime() {
        AtomicLong time = new AtomicLong();
        applyRegionData((reg) -> {
            try {
                time.set(reg.getLastChangeTime());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> {
            time.set(0);
            return false;
        });
        return time.get();
    }

    public void combineData(RegionHelper.RegionData newData) {
        new Thread(() -> applyRegionData((reg) -> {
            try {
                combineDataInternal(reg, newData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> true)).start();
    }

    public RegionHelper.RegionData getData() {
        AtomicReference<RegionHelper.RegionData> data = new AtomicReference<>(null);
        applyRegionData(e -> {
            try {
                synchronized (e) {
                    byte[] dataCopy = new byte[0x10000 * 18];
                    System.arraycopy(e.getDataBytes(), 0, dataCopy, 0, dataCopy.length);
                    data.set(new RegionHelper.RegionData(dataCopy, ImmutableMap.copyOf(e.getKey()), e.getLastChangeTime()));
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }, () -> true);
        return data.get();
    }

    /**
     * @param applyRegionStuff if not empty this runs
     * @param isEmpty return if should delete and continue
     */
    private void applyRegionData(Consumer<RegionRW<?>> applyRegionStuff, Supplier<Boolean> isEmpty) {
        MixinPersistentMap pMap = (MixinPersistentMap) VoxelMap.getInstance().getPersistentMap();
        synchronized (pMap.getCachedRegions()) {
            synchronized (pMap.getCachedRegionsPool()) {
                CachedRegion region = pMap.getCachedRegions().get(x + "," + z);
                if (region != null) {
                    if (region instanceof EmptyCachedRegion) {
                        if (isEmpty.get()) {
                            pMap.getCachedRegions().remove(x + "," + z);
                            pMap.getCachedRegionsPool().remove(region);
                        } else {
                            return;
                        }
                    } else {
                        try {
                            ((ICachedRegion) region).getSync().waitFor();
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        RegionRW<CachedRegion> mapData = (RegionRW<CachedRegion>) region.getMapData();
                        mapData.setParent(region);
                        applyRegionStuff.accept(mapData);
                        return;
                    }
                }
            }
            RegionReadWriter region = new RegionReadWriter(VoxelMapApi.getBaseFolder(), world, dimension, x, z);
            applyRegionStuff.accept(region);
        }
    }

    private void combineDataInternal(RegionRW<?> region, RegionHelper.RegionData newData) throws IOException {
        synchronized (region) {
            RegionHelper.RegionData oldData = new RegionHelper.RegionData(region.getDataBytes(), region.getKey(), region.getLastChangeTime());

            RegionHelper.RegionData zipped = RegionHelper.regionZipper(oldData, newData);

            System.arraycopy(zipped.data, 0, region.getDataBytes(), 0, zipped.data.length);
            region.setKey(zipped.key);

            region.setLastChangeTime(zipped.editTime);
            region.doSaveData();
        }
    }
}
