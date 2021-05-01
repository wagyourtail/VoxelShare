package xyz.wagyourtail.voxelmapapi.mixin;

import com.mamiyaotaru.voxelmap.WaypointManager;
import com.mamiyaotaru.voxelmap.util.DimensionContainer;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.voxelmapapi.IWaypoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

@Mixin(value = WaypointManager.class, remap = false)
public class MixinWaypointManager {
    @Unique long editTime;
    @Unique boolean sync;

    //LOAD
    @Inject(method = "loadWaypointsExtensible", at = @At(value = "INVOKE", target = "Ljava/util/TreeSet;<init>()V"))
    public void onLoadWaypoints(String worldNameStandard, CallbackInfoReturnable<Boolean> cir) {
        editTime = 0;
        sync = false;
    }

    @Inject(method = "loadWaypointsExtensible", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z", ordinal = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onLoadWaypoints(String worldNameStandard, CallbackInfoReturnable<Boolean> cir, File f, File g, int x, int y, BufferedReader in, String sCurrentLine, String[] pairs, String name, int z, boolean enabled, float red, float green, float blue, String suffix, String world, TreeSet dimensions, int t, int splitIndex, String key, String value) {
        if (key.equals("editTime")) {
            editTime = Long.parseLong(value);
        } else if (key.equals("sync")) {
            sync = Boolean.parseBoolean(value);
        }
    }

    @Inject(method = "loadWaypoint", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;contains(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onLoadWaypoint(String name, int x, int z, int y, boolean enabled, float red, float green, float blue, String suffix, String world, TreeSet<DimensionContainer> dimensions, CallbackInfo ci, Waypoint newWaypoint) {
        ((IWaypoint)newWaypoint).setEditTime(editTime);
        ((IWaypoint)newWaypoint).setSync(sync);
    }

    //SAVE
    @Inject(method = "saveWaypoints", at = @At(value = "INVOKE", target = "Ljava/io/PrintWriter;println(Ljava/lang/String;)V", ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onSave(CallbackInfo ci, String worldNameSave, File f, PrintWriter out, String knownSub, String seeds, String oldNorth, String oldNorthName, Map.Entry entry, Iterator var16, Waypoint pt) {
        long editTime = ((IWaypoint) pt).getEditTime();
        boolean sync = ((IWaypoint) pt).shouldSync();
        out.print("editTime:" + editTime + ",Sync:" + sync + ",");
    }
}
