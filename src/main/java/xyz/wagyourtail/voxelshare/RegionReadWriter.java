package xyz.wagyourtail.voxelshare;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class RegionReadWriter implements RegionRW<Object> {
    public final File file;
    public final String world, dimension;
    public final int x, z;

    private boolean loadedData = false, loadedKey = false, loadedControl = false;

    private final byte[] data = new byte[0x10000 * 18];
    private final Map<Integer, String> key = new HashMap<>();
    private final Properties controlProperties = new Properties();

    public RegionReadWriter(File directory, String world, String dimension, int x, int z) {
        this.file = new File(directory, world + "/" + dimension + "/" + x + "," + z + ".zip");
        this.world = world;
        this.dimension = dimension;
        this.x = x;
        this.z = z;
    }

    @Override
    public void setParent(Object parent) {
        //IGNORE in this class.
    }

    private synchronized void loadData() throws IOException {
        if (loadedData) return;
        if (file.exists()) {
            try (ZipFile zf = new ZipFile(file)) {
                ZipEntry ze = zf.getEntry("data");
                try (InputStream is = zf.getInputStream(ze)) {
                    int count, total = 0;
                    for (byte[] byteData = new byte[2048]; (count = is.read(byteData, 0, 2048)) != -1 && count + total <= this.data.length; total += count) {
                        System.arraycopy(byteData, 0, data, total, count);
                    }
                }
            }
        }
        loadedData = true;
    }

    private synchronized  void loadKey() throws IOException {
        if (loadedKey) return;
        if (file.exists()) {
            try (ZipFile zf = new ZipFile(file)) {
                ZipEntry ze = zf.getEntry("key");
                try (InputStream is = zf.getInputStream(ze)) {
                    String[] content = IOUtils.toString(is, StandardCharsets.UTF_8).split("\r?\n");
                    for (String s : content) {
                        String[] parts = s.split("\\s", 2);
                        key.put(Integer.parseInt(parts[0]), parts[1]);
                    }
                }
            }
        }
        loadedKey = true;
    }

    private synchronized void loadControl() throws IOException {
        if (loadedControl) return;
        if (file.exists()) {
            try (ZipFile zf = new ZipFile(file)) {
                ZipEntry ze = zf.getEntry("control");
                try (InputStream is = zf.getInputStream(ze)) {
                    controlProperties.load(is);
                }
            }
            if (Integer.parseInt(controlProperties.getProperty("version", "1")) < 2) {
                throw new IllegalStateException("Cannot work with version 1 map data");
            }
        }
        loadedControl = true;
    }

    @Override
    public synchronized byte[] getDataBytes() throws IOException {
        loadData();
        return data;
    }

    @Override
    public synchronized Map<Integer, String> getKey() throws IOException {
        loadKey();
        return key;
    }

    @Override
    public synchronized void setKey(Map<Integer, String> key) {
        synchronized (this) {
            loadedKey = true;
        }
        this.key.clear();
        this.key.putAll(key);
    }

    @Override
    public synchronized long getLastChangeTime() throws IOException {
        loadControl();
        return Long.parseLong(controlProperties.getProperty("changetime", "0"));
    }

    @Override
    public synchronized void setLastChangeTime(long time) throws IOException {
        loadControl();
        controlProperties.setProperty("changetime", String.valueOf(time));
    }

    @Override
    public synchronized void doSaveData() throws IOException {
        loadData();
        loadKey();
        loadControl();
        try (FileOutputStream fos = new FileOutputStream(file); ZipOutputStream zos = new ZipOutputStream(fos)) {
            ZipEntry ze = new ZipEntry("data");
            ze.setSize(data.length);
            zos.putNextEntry(ze);
            zos.write(data);
            zos.closeEntry();

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Integer, String> k : key.entrySet()) {
                sb.append(k.getKey()).append(' ').append(k.getValue()).append("\r\n");
            }
            byte[] key = sb.toString().getBytes(StandardCharsets.UTF_8);
            ze = new ZipEntry("key");
            ze.setSize(key.length);
            zos.putNextEntry(ze);
            zos.write(key);
            zos.closeEntry();

            byte[] control = ("version:2\r\n" + "changetime:" + getLastChangeTime() + "\r\n").getBytes(StandardCharsets.UTF_8);
            ze = new ZipEntry("control");
            ze.setSize(control.length);
            zos.putNextEntry(ze);
            zos.write(control);
            zos.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
