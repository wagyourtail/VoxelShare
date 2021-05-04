package xyz.wagyourtail.voxelshare.mixin.xlPackets;

import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CustomPayloadC2SPacket.class)
public class MixinCustomPayloadC2SPacket {

    @ModifyConstant(method = "read", constant = @Constant(intValue = 32767))
    private int xlPackets(int old) {
        return 2000000000;
    }
}
