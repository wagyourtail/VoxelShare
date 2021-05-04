package xyz.wagyourtail.voxelmapapi.accessor;

import net.minecraft.block.BlockState;

public interface IBlockStateParser {
    BlockState doParseStateString(String stateString);
}
