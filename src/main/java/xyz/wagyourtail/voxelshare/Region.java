package xyz.wagyourtail.voxelshare;

import java.util.Objects;

public class Region {
    public final int x, z;
    public long updateTime;

    public Region(int x, int z, long updateTime) {
        this.x = x;
        this.z = z;
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return x == region.x && z == region.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

}
