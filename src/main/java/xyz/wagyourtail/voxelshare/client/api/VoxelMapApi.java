package xyz.wagyourtail.voxelshare.client.api;

import org.jetbrains.annotations.NotNull;

public interface VoxelMapApi {
    //TODO: implement
    @NotNull VoxelMapApi INSTANCE = new VoxelMapApiImpl();

    String getCurrentServer();
}
