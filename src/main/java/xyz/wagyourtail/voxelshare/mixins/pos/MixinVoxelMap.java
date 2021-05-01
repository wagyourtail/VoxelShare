package xyz.wagyourtail.voxelshare.mixins.pos;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mamiyaotaru.voxelmap.VoxelMap;
import com.neovisionaries.ws.client.WebSocketException;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.voxelshare.PlayerData;
import xyz.wagyourtail.voxelshare.VoxelShare;

@Mixin(value = VoxelMap.class, remap = false)
public class MixinVoxelMap {
    
    @Unique
    int ticks = 10;
    
    
    @Inject(at = @At("TAIL"), method = "onTick", remap = false)
    public void onTick(MinecraftClient mc, CallbackInfo info) {
        
        if (ticks > 10 && mc.player != null && mc.world != null && mc.world.isClient) {
            if (VoxelShare.INSTANCE != null && VoxelShare.INSTANCE.c == null) {
                if (VoxelShare.INSTANCE.settings != null)
                try {
                    VoxelShare.INSTANCE.connect();
                } catch (IOException | WebSocketException e) {
                    e.printStackTrace();
                }
            }
            if (VoxelShare.INSTANCE != null && VoxelShare.INSTANCE.c != null && VoxelShare.INSTANCE.c.getWebSocket().isOpen()) {
                if (!mc.getSession().getUuid().equals(VoxelShare.INSTANCE.c.setUUID)) {
                    VoxelShare.INSTANCE.c.setPlayer(mc.getSession().getUuid());
                }
                try {
                    VoxelShare.INSTANCE.c.sendPos((int)mc.player.getX(), (int)mc.player.getZ());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            
            ticks = 0;
            
            synchronized (PlayerData.players) {
                List<UUID> removal = new LinkedList<>();
                long currentTime = System.currentTimeMillis();
                //clearoldpos
                for (Entry<UUID, PlayerData> en : PlayerData.players.entrySet()) {
                    if (currentTime - en.getValue().updateTime > 2000) {
                        removal.add(en.getKey());
                    }
                }
                for (UUID key : removal) {
                    PlayerData.players.remove(key);
                }
            }
        } else {
            ++ticks;
        }
        
    }
    
}
