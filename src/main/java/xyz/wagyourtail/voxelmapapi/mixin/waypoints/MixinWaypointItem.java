package xyz.wagyourtail.voxelmapapi.mixin.waypoints;

import com.mamiyaotaru.voxelmap.VoxelMap;
import com.mamiyaotaru.voxelmap.gui.GuiWaypoints;
import com.mamiyaotaru.voxelmap.gui.overridden.GuiSlotMinimap;
import com.mamiyaotaru.voxelmap.util.GLShim;
import com.mamiyaotaru.voxelmap.util.GLUtils;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.voxelmapapi.IWaypoint;

import java.lang.reflect.Field;

@Mixin(targets = "com.mamiyaotaru.voxelmap.gui.GuiSlotWaypoints$WaypointItem")
public class MixinWaypointItem {
    @Shadow @Final private Waypoint waypoint;

    @Shadow @Final private GuiWaypoints parentGui;
    @Unique GuiSlotMinimap parent = null;

    @Unique
    private GuiSlotMinimap getParent() {
        if (parent != null) return parent;
        try {
            Field f = this.getClass().getDeclaredField("this$0");
            parent = (GuiSlotMinimap) f.get(this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return parent;
    }

    @Inject(method = "mouseClicked", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onMouseClicked(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir, int leftEdge, byte padding,  int width) {
        System.out.println(leftEdge);
        System.out.println(width);
        System.out.println(mouseX);
        if (mouseX >= (double) (leftEdge + width - 16 - 20 - padding) && mouseX < (double) (leftEdge + width - 16 - padding)) {
            ((IWaypoint)waypoint).setSync(!((IWaypoint)waypoint).shouldSync());
            VoxelMap.getInstance().getWaypointManager().saveWaypoints();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void onRender(MatrixStack matrixStack, int slotIndex, int slotYPos, int leftEdge, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean mouseOver, float partialTicks, CallbackInfo ci) {
        GLShim.glColor4f(1,1,1,1);
        GLUtils.img("textures/gui/icons.png");
        int r = ((IWaypoint)waypoint).shouldSync() ? 0 : 5;
        DrawableHelper.drawTexture(matrixStack, leftEdge + 180, slotYPos - 1, 20,16, 0, 16 + r * 8,  10, 8, 256, 256);

    }
}
