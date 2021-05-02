package xyz.wagyourtail.voxelmapapi.mixin.position;

import com.mamiyaotaru.voxelmap.gui.overridden.PopupGuiScreen;
import com.mamiyaotaru.voxelmap.persistent.GuiPersistentMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.voxelshare.client.VoxelShareClient;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketPositionS2C;

import java.util.Map;
import java.util.UUID;

@Mixin(value = GuiPersistentMap.class)
public abstract class MixinGuiPersistentMap extends PopupGuiScreen {

    @Shadow(remap = false) private MinecraftClient mc;

    @Shadow(remap = false) private float scScale;

    @Shadow(remap = false) private float mapToGui;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mamiyaotaru/voxelmap/persistent/GuiPersistentMap;drawTexturedModalRect(FFFF)V", ordinal = 2, shift = At.Shift.AFTER))
    public void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (VoxelShareClient.clientPacketListener != null) {
            synchronized (VoxelShareClient.clientPacketListener.positions) {
                for (Map.Entry<UUID, PacketPositionS2C> pos : VoxelShareClient.clientPacketListener.positions.entrySet()) {
                    ClientPlayNetworkHandler handler;
                    if ((handler = mc.getNetworkHandler()) != null) {
                        PlayerListEntry player = handler.getPlayerListEntry(pos.getKey());
                        if (player != null) {
                            mc.getTextureManager().bindTexture(player.getSkinTexture());

                            int pX = (int) (-10.0F / this.scScale + pos.getValue().x * this.mapToGui);
                            int pZ = (int) (-10.0F / this.scScale + pos.getValue().z * this.mapToGui);

                            DrawableHelper.drawTexture(matrixStack, pX, pZ, (int) (20.0F / this.scScale), (int) (20.0F / this.scScale), 8.0F, 8.0F, 8, 8, 64, 64);
                            drawCenteredString(matrixStack, textRenderer, player.getProfile().getName(), (int) (pX + 10F / this.scScale), (int) (pZ + 20F / this.scScale), 0xAAAAAA);
                        }
                    }
                }
            }
        }
    }

}
