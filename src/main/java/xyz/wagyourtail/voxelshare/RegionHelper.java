package xyz.wagyourtail.voxelshare;

import com.google.common.collect.Lists;
import xyz.wagyourtail.voxelmapapi.RegionContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RegionHelper {
    public static class RegionData {
        public final long editTime;
        public final byte[] data;
        public final Map<Integer, String> key;

        public RegionData(byte[] data, Map<Integer, String> key, long editTime) {
            this.data = data;
            this.key = key;
            this.editTime = editTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RegionData)) return false;
            RegionData that = (RegionData) o;
            return editTime == that.editTime && Arrays.equals(data, that.data);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(editTime);
            result = 31 * result + Arrays.hashCode(data);
            return result;
        }
    }

    public static Map<String, Map<String, List<File>>> getFiles(File server) throws IOException {
        Map<String, Map<String, List<File>>> fmap = new HashMap<>();
        Files.walk(server.toPath()).filter(e -> e.toFile().isFile() && e.getFileName().toString().matches("-?\\d+,-?\\d+\\.zip")).forEach(e -> {
            List<String> parents = new LinkedList<>();
            int i = server.toPath().getNameCount();
            for (; i < e.getNameCount(); ++i) {
                parents.add(e.getName(i).toString());
            }
            String world;
            String dimension;
            if (parents.size() == 1) {
                world = "";
                dimension = "";
            } else if (parents.size() == 2) {
                world = "";
                dimension = parents.get(0);
            } else {
                world = parents.get(0);
                dimension = parents.get(1);
            }
            fmap.computeIfAbsent(world, w -> new HashMap<>()).computeIfAbsent(dimension, d -> new LinkedList<>()).add(e.toFile());
        });
        return fmap;
    }

    public static RegionData regionZipper(RegionData ra, RegionData rb) {
        if (ra.editTime < rb.editTime) {
            return regionZipper(ra, rb, rb.editTime);
        } else {
            return regionZipper(rb, ra, ra.editTime);
        }
    }

    public static RegionData regionZipper(RegionData oldR, RegionData newR, long newTime) {
        byte[] out = new byte[0x10000 * 18];
        List<String> blocks = Lists.newArrayList((String)null);
        for (int blk = 0; blk < 0x10000; ++blk) {
            //void on old
            if (oldR.data[blk] == 0 || newR.data[blk] != 0) {
                combineBlockIds(newR, out, blocks, blk);
            } else {
                combineBlockIds(oldR, out, blocks, blk);
            }
        }

        Map<Integer, String> blockIds = new HashMap<>();
        for (int i = 1; i < blocks.size(); ++i) {
            blockIds.put(i, blocks.get(i));
        }
        return new RegionData(out, blockIds, newTime);
    }

    private static void combineBlockIds(RegionData oldR, byte[] out, List<String> blocks, int blk) {
        String[] block = getBlocks(blk, oldR);
        int[] blockID = new int[4];
        for (int i = 0; i < 4; ++i) {
            if (blocks.contains(block[i])) {
                blockID[i] = blocks.indexOf(block[i]);
            } else {
                blockID[i] = blocks.size();
                blocks.add(block[i]);
            }
        }
        writeBlockToNew(blk, oldR.data, blockID, out);
    }

    private static String[] getBlocks(int blk, RegionData region) {
        String[] blocks = new String[4];
        blocks[0] = region.key.get(getInt(region.data, blk + 0x10000));
        blocks[1] = region.key.get(getInt(region.data, blk + 0x50000));
        blocks[2] = region.key.get(getInt(region.data, blk + 0x90000));
        blocks[3] = region.key.get(getInt(region.data, blk + 0xD0000));
        return blocks;
    }

    private static int getInt(byte[] from, int pos) {
        return from[pos] << 8 + from[pos + 0x10000];
    }

    private static void writeBlockToNew(int blk, byte[] from, int[] blocks, byte[] to) {
        to[blk] = from[blk];
        to[blk + 0x10000] = (byte) (blocks[0] >> 8);
        to[blk + 0x20000] = (byte) blocks[0];
        to[blk + 0x30000] = from[blk + 0x30000];
        to[blk + 0x40000] = from[blk + 0x40000];
        to[blk + 0x50000] = (byte) (blocks[1] >> 8);
        to[blk + 0x60000] = (byte) blocks[1];
        to[blk + 0x70000] = from[blk + 0x70000];
        to[blk + 0x80000] = from[blk + 0x80000];
        to[blk + 0x90000] = (byte) (blocks[2] >> 8);
        to[blk + 0xA0000] = (byte) blocks[2];
        to[blk + 0xB0000] = from[blk + 0xB0000];
        to[blk + 0xC0000] = from[blk + 0xC0000];
        to[blk + 0xD0000] = (byte) (blocks[3] >> 8);
        to[blk + 0xE0000] = (byte) blocks[3];
        to[blk + 0xF0000] = from[blk + 0xF0000];
        to[blk + 0x100000] = from[blk + 0x100000];
        to[blk + 0x110000] = from[blk + 0x110000];
    }
}
