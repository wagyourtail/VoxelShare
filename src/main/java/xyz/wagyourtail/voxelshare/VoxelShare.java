package xyz.wagyourtail.voxelshare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
import com.mamiyaotaru.voxelmap.util.TextUtils;
import com.neovisionaries.ws.client.WebSocketException;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public class VoxelShare implements ClientModInitializer {
    public static VoxelShare INSTANCE;
    public VSSettings settings;
    public ClientConnection c = null;
    
    @Override
    public void onInitializeClient() {
        Gson gson = new Gson();
        File f = new File(FabricLoader.getInstance().getConfigDir().toFile(), "VoxelShare.json");
        try {
            settings = gson.fromJson(new FileReader(f), VSSettings.class);
        } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
            e.printStackTrace();
            settings = new VSSettings();
            try (FileWriter fi = new FileWriter(f)) {
                fi.write(gson.toJson(settings));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        INSTANCE = this;
    }

    public void setServer(String s) {
        this.settings.server = s;
    }
    
    public void connect() throws IOException, WebSocketException {
        if (c != null && c.getWebSocket().isOpen()) {
            c.disconnect();
        }
        c = new ClientConnection(settings.server);
        c.connect();
    }
    
    public static String getServer() {
        try {
            return TextUtils.scrubNameFile(AbstractVoxelMap.getInstance().getWaypointManager().getCurrentWorldName());
        } catch (NullPointerException e) {
            return null;
        }
    }
    
    public static String getWorld() {
        try {
            String subworld = AbstractVoxelMap.getInstance().getWaypointManager().getCurrentSubworldDescriptor(false);
            if (subworld != "") {
                return TextUtils.scrubNameFile(subworld) + "/" + getDimension();
            }
            return "" + getDimension();
        } catch (NullPointerException e) {
            return null;
        }
    }
    
    @SuppressWarnings("resource")
    private static String getDimension() {
        try {
            return TextUtils.scrubNameFile(AbstractVoxelMap.getInstance().getDimensionManager()
                .getDimensionContainerByWorld(MinecraftClient.getInstance().world).getStorageName());
        } catch (NullPointerException e) {
            return null;
        }
    }
}
