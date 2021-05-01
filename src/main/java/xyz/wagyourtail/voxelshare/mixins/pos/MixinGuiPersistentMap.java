package xyz.wagyourtail.voxelshare.mixins.pos;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mamiyaotaru.voxelmap.persistent.GuiPersistentMap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import xyz.wagyourtail.voxelshare.PlayerData;

@Mixin(value = GuiPersistentMap.class, remap = false)
public abstract class MixinGuiPersistentMap {

    @Shadow(remap = false)
    private MinecraftClient mc;
    
    @Shadow(remap = false)
    private float scScale;

    @Shadow(remap = false)
    private float mapToGui;

    @Shadow(remap = false)
    public abstract void drawTexturedModalRect(float x, float y, float width, float height);
    
    @Unique
    public Map<UUID, Integer> boundTextures = new HashMap<>();

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mamiyaotaru/voxelmap/persistent/GuiPersistentMap;drawTexturedModalRect(FFFF)V", ordinal = 2, shift = At.Shift.AFTER), method = "method_25394", remap = false)
    public void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
        synchronized (PlayerData.players) {
            for (Entry<UUID, PlayerData> e : PlayerData.players.entrySet()) {
                
                mc.getTextureManager().bindTexture(e.getValue().entry.getSkinTexture());

                int playerX = e.getValue().x;
                int playerZ = e.getValue().z;
                
                DrawableHelper.drawTexture(matrixStack, (int)(-10.0F / this.scScale + playerX * this.mapToGui), (int)(-10.0F / this.scScale + playerZ * this.mapToGui), (int)(20.0F / this.scScale), (int)(20.0F / this.scScale), 8.0F,  8.0F, 8, 8, 64, 64);
                
                //drawTexturedModalRect(-10.0F / this.scScale + playerX * this.mapToGui,
                //    -10.0F / this.scScale + playerZ * this.mapToGui, 20.0F / this.scScale, 20.0F / this.scScale);
            }
        }
    }

}
