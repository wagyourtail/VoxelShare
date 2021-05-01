package xyz.wagyourtail.voxelshare.mixins.waypoint;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.mamiyaotaru.voxelmap.gui.GuiWaypoints;
import com.mamiyaotaru.voxelmap.textures.Sprite;
import com.mamiyaotaru.voxelmap.textures.TextureAtlas;
import com.mamiyaotaru.voxelmap.util.GLShim;
import com.mamiyaotaru.voxelmap.util.GLUtils;
import com.mamiyaotaru.voxelmap.util.Waypoint;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import xyz.wagyourtail.voxelshare.interfaces.waypoint.IIGuiWaypoints;

@Mixin(targets = "com.mamiyaotaru.voxelmap.gui.GuiSlotWaypoints.WaypointItem", remap = false)
public class MixinWaypointItem {
    
    @Shadow(remap = false)
    @Final
    private GuiWaypoints parentGui;
    
    @Shadow(remap = false)
    @Final
    private Waypoint waypoint;
    
    @Inject(at = @At("TAIL"), method = "method_25343",  remap = false)
    public void render(MatrixStack matrixStack, int slotIndex, int slotYPos, int leftEdge, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
        int x = leftEdge + 1;
        int y = slotYPos - 1;
        GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        TextureAtlas atlas = ((IIGuiWaypoints)this.parentGui).getWaypointManager().getTextureAtlas();
        GLUtils.disp(atlas.getGlId());
        Sprite icon = atlas.getAtlasSprite("voxelmap:images/waypoints/waypoint" + this.waypoint.imageSuffix + ".png");
        drawTexturedModalRect(x, y, icon, 16, 16);
    }
    
    @Unique
    public void drawTexturedModalRect(int xCoord, int yCoord, Sprite textureSprite, int widthIn, int heightIn) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, VertexFormats.POSITION_TEXTURE);
        vertexbuffer.vertex((double)(xCoord + 0), (double)(yCoord + heightIn), 1.0D).texture(textureSprite.getMinU(), textureSprite.getMaxV()).next();
        vertexbuffer.vertex((double)(xCoord + widthIn), (double)(yCoord + heightIn), 1.0D).texture(textureSprite.getMaxU(), textureSprite.getMaxV()).next();
        vertexbuffer.vertex((double)(xCoord + widthIn), (double)(yCoord + 0), 1.0D).texture(textureSprite.getMaxU(), textureSprite.getMinV()).next();
        vertexbuffer.vertex((double)(xCoord + 0), (double)(yCoord + 0), 1.0D).texture(textureSprite.getMinU(), textureSprite.getMinV()).next();
        tessellator.draw();
     }
}

