package xyz.wagyourtail.voxelmapapi.mixin.region;

import com.google.common.collect.BiMap;
import com.mamiyaotaru.voxelmap.persistent.CachedRegion;
import com.mamiyaotaru.voxelmap.persistent.CompressibleMapData;
import com.mamiyaotaru.voxelmap.util.BlockStateParser;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import xyz.wagyourtail.voxelmapapi.accessor.IBlockStateParser;
import xyz.wagyourtail.voxelmapapi.accessor.ICachedRegion;
import xyz.wagyourtail.voxelshare.RegionRW;

import java.util.HashMap;
import java.util.Map;

@Mixin(CompressibleMapData.class)
public abstract class MixinCompressibleMapData implements RegionRW<CachedRegion> {
    @Unique
    private static final IBlockStateParser bsp = (IBlockStateParser) new BlockStateParser();
    @Shadow public abstract byte[] getData();

    @Shadow private BiMap<BlockState, Integer> stateToInt;

    @Shadow public abstract BiMap<BlockState, Integer> getStateToInt();

    @Unique
    CachedRegion parent;

    @Override
    public void setParent(CachedRegion parent) {
        this.parent = parent;
    }

    @Override
    public byte[] getDataBytes() {
        return getData();
    }

    @Override
    public synchronized Map<Integer, String> getKey() {
        Map<BlockState, Integer> stateToInt = getStateToInt();
        Map<Integer, String> intToStateString = new HashMap<>();
        for (Map.Entry<BlockState, Integer> si : stateToInt.entrySet()) {
            intToStateString.put(si.getValue(), si.getKey().toString());
        }
        return intToStateString;
    }

    @Override
    public synchronized void setKey(Map<Integer, String> key) {
        stateToInt.clear();
        for (Map.Entry<Integer, String> entry : key.entrySet()) {
            stateToInt.put(bsp.doParseStateString(entry.getValue()), entry.getKey());
        }
    }

    @Override
    public long getLastChangeTime() {
        return ((ICachedRegion)parent).getLastChangeTime();
    }

    @Override
    public void setLastChangeTime(long time) {
        ((ICachedRegion)parent).setLastChangeTime(time);
    }

    @Override
    public void doSaveData() {
        ((ICachedRegion) parent).setLiveChunksUpdated();
        if (((ICachedRegion) parent).isClosed()) {
            ((ICachedRegion) parent).doSaveData(true);
        } else {
            parent.refresh(true);
        }
    }

}
