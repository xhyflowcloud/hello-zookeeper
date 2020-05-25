package net.lovenn.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.lang.reflect.Field;

public class ZookeeperConfigObserver implements ConfigObserver {

    private final String path;

    private final Class<?> target;

    private ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 30000, null);

    public ZookeeperConfigObserver(String path, Class<?> target) throws IOException {
        this.path = path;
        this.target = target;
    }

    public String getPath() {
        return this.path;
    }

    public void update() {
        try {
            byte[] data = zk.getData(ZookeeperConfigSubject.getQualityPath(path), true, null);
            updateField(data);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateField(byte[] data) {
        try {
            Field f = target.getDeclaredField(path);
            f.setAccessible(true);
            String valueStr = new String(data);
            if (f.getType() == int.class || f.getType() == Integer.class) {
                f.set(target, Integer.parseInt(valueStr));
            }
            if (f.getType() == long.class || f.getType() == Long.class) {
                f.set(target, Long.parseLong(valueStr));
            }
            if (f.getType() == float.class || f.getType() == Float.class) {
                f.set(target, Float.parseFloat(valueStr));
            }
            if (f.getType() == double.class || f.getType() == Double.class) {
                f.set(target, Double.parseDouble(valueStr));
            }
            if (f.getType() == String.class) {
                f.set(target, valueStr);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
