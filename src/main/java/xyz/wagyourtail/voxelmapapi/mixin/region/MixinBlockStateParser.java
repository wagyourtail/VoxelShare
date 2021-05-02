package xyz.wagyourtail.voxelmapapi.mixin.region;

import com.mamiyaotaru.voxelmap.util.BlockStateParser;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.voxelmapapi.IBlockStateParser;

@Mixin(BlockStateParser.class)
public abstract class MixinBlockStateParser implements IBlockStateParser {


    @Shadow
    private static BlockState parseStateString(String stateString) {
        return null;
    }

    @Override
    public BlockState doParseStateString(String stateString) {
        return parseStateString(stateString);
    }

}
