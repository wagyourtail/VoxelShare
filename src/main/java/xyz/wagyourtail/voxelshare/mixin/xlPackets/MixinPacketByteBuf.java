package xyz.wagyourtail.voxelshare.mixin.xlPackets;

import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PacketByteBuf.class)
public class MixinPacketByteBuf {

    @ModifyConstant(method = "readCompoundTag", constant = @Constant(longValue = 2097152L))
    private long xlPackets(long old) {
        return 2000000000L;
    }

}
