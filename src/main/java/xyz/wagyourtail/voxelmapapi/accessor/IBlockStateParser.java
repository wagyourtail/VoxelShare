package xyz.wagyourtail.voxelmapapi;

import net.minecraft.block.BlockState;

public interface IBlockStateParser {
    BlockState doParseStateString(String stateString);
}
