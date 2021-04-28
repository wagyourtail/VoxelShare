package xyz.wagyourtail.voxelshare;

import com.google.gson.Gson;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.*;

public class VoxelShare implements ModInitializer {
    private static final Gson gson = new Gson();
    private static final File configFile = FabricLoader.getInstance().getConfigDir().resolve("VoxelShare.json").toFile();
    public static final Identifier packetId = new Identifier("voxelshare", "packet");
    public static ConfigOptions config;

    @Override
    public void onInitialize() {
        try {
            config = gson.fromJson(new FileReader(configFile), ConfigOptions.class);
        } catch (FileNotFoundException e) {
            gson.toJson(config = new ConfigOptions());
            e.printStackTrace();
        }
    }

    public static void saveConfig() throws IOException {
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(gson.toJson(config));
        }
    }

}
