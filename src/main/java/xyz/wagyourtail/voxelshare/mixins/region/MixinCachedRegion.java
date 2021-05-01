package xyz.wagyourtail.voxelshare.mixins.region;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mamiyaotaru.voxelmap.persistent.CachedRegion;
import com.mamiyaotaru.voxelmap.persistent.CompressibleMapData;

import net.minecraft.client.world.ClientWorld;
import xyz.wagyourtail.voxelshare.interfaces.region.ICachedRegion;

@Mixin(value = CachedRegion.class, remap = false)
public class MixinCachedRegion implements ICachedRegion {
    
    @Shadow(remap = false)
    private String subworldNamePathPart;
    
    @Shadow(remap = false)
    private String worldNamePathPart;
    
    @Shadow(remap = false)
    private String dimensionNamePathPart;
    
    @Shadow(remap = false)
    private int x;
    
    @Shadow(remap = false)
    private int z;
    
    @Shadow(remap = false)
    private CompressibleMapData data;
    
    @Shadow(remap = false)
    private ClientWorld world;
    
    @Inject(at = @At(value = "INVOKE", target = "Ljava/io/FileOutputStream;close()V", shift = At.Shift.AFTER), method = "doSave", remap = false)
    public void onSave(CallbackInfo ci) {
        
    }

    @Override
    public ClientWorld getClientWorld() {
        return world;
    }

    @Override
    public String getServer() {
        return worldNamePathPart;
    }

    @Override
    public String getWorld() {
        return subworldNamePathPart + dimensionNamePathPart;
    }
    
}
