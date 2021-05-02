package xyz.wagyourtail.voxelmapapi.mixin.region;

import com.mamiyaotaru.voxelmap.persistent.CachedRegion;
import com.mamiyaotaru.voxelmap.persistent.CompressibleGLBufferedImage;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.voxelmapapi.ICachedRegion;

import java.util.Properties;

@Mixin(value = CachedRegion.class, remap = false)
public abstract class MixinCachedRegion implements ICachedRegion {
    @Shadow private boolean liveChunksUpdated;
    @Unique private long lastChangeTime = 0;

    @Shadow protected abstract void saveData(boolean newThread);

    @Shadow protected abstract void fillImage();

    @Shadow private CompressibleGLBufferedImage image;

    @Accessor
    public abstract boolean isClosed();

    @Override
    public void doSaveData(boolean newThread) {
        saveData(newThread);
    }

    @Override
    public void setLiveChunksUpdated() {
        liveChunksUpdated = true;
    }

    @Override
    public long getLastChangeTime() {
        return lastChangeTime;
    }

    @Inject(method = "doLoadChunkData", at = @At("TAIL"))
    public void onLoadChunkData(WorldChunk chunk, int chunkX, int chunkZ, CallbackInfo ci) {
        lastChangeTime = System.currentTimeMillis();
    }

    @Redirect(method = "loadCachedData", at = @At(value = "INVOKE", target = "Ljava/util/Properties;getProperty(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"))
    public String getProperty(Properties property, String key, String defVal) {
        lastChangeTime = Long.parseLong(property.getProperty("changetime", "0"));
        return property.getProperty(key, defVal);
    }

    @Redirect(method = "doSave", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuffer;append(Ljava/lang/String;)Ljava/lang/StringBuffer;", ordinal = 1))
    public StringBuffer append(StringBuffer buff, String line) {
        buff.append(line);
        buff.append("changetime:").append(lastChangeTime).append("\r\n");
        return buff;
    }

    @Override
    public void setLastChangeTime(long lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    @Override
    public void doFillImage() {
        synchronized (image) {
            fillImage();
        }
    }

}
