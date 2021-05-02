package xyz.wagyourtail.voxelmapapi;

import com.mamiyaotaru.voxelmap.persistent.CachedRegion;
import com.mamiyaotaru.voxelmap.persistent.CompressibleMapData;
import com.mamiyaotaru.voxelmap.util.BlockStateParser;
import net.minecraft.block.BlockState;
import xyz.wagyourtail.voxelshare.RegionHelper;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Region {
    private static final IBlockStateParser bsp = (IBlockStateParser) new BlockStateParser();
    public final int x;
    public final int z;
    public final String world, dimension;
    private WeakReference<CachedRegion> actualRegion;

    public Region(int x, int z, String world, String dimension, CachedRegion region) {
        this.x = x;
        this.z = z;
        this.world = world;
        this.dimension = dimension;
        this.actualRegion = new WeakReference<>(region);
    }

    public long getTime() {
        CachedRegion region;
        if ((region = actualRegion.get()) == null) {
            //TODO: restore gc'd region

        }

        return ((ICachedRegion)region).getLastChangeTime();
    }

    public void combineData(RegionHelper.RegionData newData) {
        new Thread(() -> {
            CachedRegion region;
            if ((region = actualRegion.get()) == null) {
                //TODO: restore gc'd region

            }

            final CompressibleMapData data = region.getMapData();
            synchronized (data) {
                Map<BlockState, Integer> stateToInt = data.getStateToInt();
                Map<Integer, String> intToStateString = new HashMap<>();
                for (Map.Entry<BlockState, Integer> si : stateToInt.entrySet()) {
                    intToStateString.put(si.getValue(), si.getKey().toString());
                }
                RegionHelper.RegionData oldData = new RegionHelper.RegionData(data.getData(), intToStateString, ((ICachedRegion)region).getLastChangeTime());


                RegionHelper.RegionData zipped = RegionHelper.regionZipper(oldData, newData);


                System.arraycopy(zipped.data, 0, region.getMapData().getData(), 0, zipped.data.length);
                stateToInt.clear();
                for (Map.Entry<Integer, String> entry : zipped.key.entrySet()) {
                    stateToInt.put(bsp.doParseStateString(entry.getValue()), entry.getKey());
                }

                ((ICachedRegion)region).setLiveChunksUpdated();
                ((ICachedRegion)region).setLastChangeTime(zipped.editTime);
                if (((ICachedRegion) region).isClosed()) {
                    ((ICachedRegion) region).doSaveData(true);
                } else {
                    ((ICachedRegion)region).doFillImage();
                }
            }
        }).start();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region)) return false;
        Region region = (Region) o;
        return x == region.x && z == region.z && world.equals(region.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, world);
    }

}
