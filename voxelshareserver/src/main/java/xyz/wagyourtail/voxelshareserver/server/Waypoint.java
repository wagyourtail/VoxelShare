package xyz.wagyourtail.voxelshareserver.server;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Waypoint {
    
    String name;
    int x;
    int y;
    int z;
    boolean enabled;
    float r;
    float g;
    float b;
    String suffix;
    String world;
    String dimensions;
    
    public Waypoint(String name, int x, int y, int z, boolean enabled, float r, float g, float b, String suffix, String world, String dimensions) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.enabled = enabled;
        this.r = r;
        this.g = g;
        this.b = b;
        this.suffix = suffix;
        this.world = world;
        this.dimensions = dimensions;
    }
    
    
    public boolean equals(Waypoint w) {
        return x == w.x && y == w.y && z == w.z;
    }
    
    public boolean equals(int x, int y, int z) {
        return this.x == x && this.y == y && this.z == z;
    }
    
    public byte[] toBytes() {
        byte[] name = this.name.getBytes(StandardCharsets.UTF_8);
        byte enabled = (byte) (this.enabled ? 1 : 0);
        byte[] suffix = this.suffix.getBytes(StandardCharsets.UTF_8);
        byte[] world = this.world.getBytes(StandardCharsets.UTF_8);
        byte[] dim = dimensions.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer
            .allocate(name.length + suffix.length + world.length + dim.length + Integer.BYTES * 10 + 1);
        buff.putInt(name.length);
        buff.put(name);
        buff.putInt(x);
        buff.putInt(y);
        buff.putInt(z);
        buff.put(enabled);
        buff.putInt((int) (r * 255));
        buff.putInt((int) (g * 255));
        buff.putInt((int) (b * 255));
        buff.putInt(suffix.length);
        buff.put(suffix);
        buff.putInt(world.length);
        buff.put(world);
        buff.putInt(dim.length);
        buff.put(dim);
        
        return buff.array();
    }
    
    public static Waypoint fromBytes(ByteBuffer buff) {
        byte[] name = new byte[buff.getInt()];
        buff.get(name);
        int x = buff.getInt();
        int y = buff.getInt();
        int z = buff.getInt();
        boolean enabled = buff.get() != 0;
        float red = buff.getInt() / 255F;
        float green = buff.getInt() / 255F;
        float blue = buff.getInt() / 255F;
        byte[] suffix = new byte[buff.getInt()];
        buff.get(suffix);
        byte[] world = new byte[buff.getInt()];
        buff.get(world);
        byte[] dimensions = new byte[buff.getInt()];
        buff.get(dimensions);
        return new Waypoint(new String(name, StandardCharsets.UTF_8), x, y, z, enabled, red, green, blue, new String(suffix, StandardCharsets.UTF_8), new String(world, StandardCharsets.UTF_8), new String(dimensions, StandardCharsets.UTF_8));
    }
}
