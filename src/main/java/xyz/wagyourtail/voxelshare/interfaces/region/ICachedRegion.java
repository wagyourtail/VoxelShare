package xyz.wagyourtail.voxelshare.interfaces.region;

import net.minecraft.client.world.ClientWorld;

public interface ICachedRegion {
    
    public ClientWorld getClientWorld();
    
    public String getServer();
    
    public String getWorld();
    
}
