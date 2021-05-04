package xyz.wagyourtail.voxelmapapi.mixin.waypoints;

import com.mamiyaotaru.voxelmap.gui.GuiAddWaypoint;
import com.mamiyaotaru.voxelmap.gui.overridden.GuiScreenMinimap;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.voxelmapapi.accessor.IWaypoint;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mixin(value = GuiAddWaypoint.class, remap = false)
public class MixinGuiAddWaypoint extends GuiScreenMinimap {
    @Shadow protected Waypoint waypoint;

    @Inject(method = "acceptWaypoint", at = @At("HEAD"))
    void onSave(CallbackInfo ci) {
        ((IWaypoint)waypoint).setEditTime(System.currentTimeMillis());
    }

    @Inject(method = "render", at = @At("TAIL"))
    void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(((IWaypoint)waypoint).getEditTime()),ZoneId.systemDefault());

        drawStringWithShadow(matrixStack, textRenderer, "Last Edited: " + zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), 2, this.getHeight() - textRenderer.fontHeight, 0xAAAAAA);

    }
}
