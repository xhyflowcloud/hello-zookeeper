package net.lovenn.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZookeeperConfigSubject implements ConfigSubject {

    private static final String PRE_PATH = "/local/host/";

    /**
     * 观察者
     */
    private Map<String, ConfigObserver> observers = new ConcurrentHashMap<String, ConfigObserver>();

    private final ZooKeeper zk;

    public ZookeeperConfigSubject() throws IOException {
        this.zk = new ZooKeeper("127.0.0.1:2181", 30000, null);
    }

    public void attach(ConfigObserver configObserver) {
        observers.put(configObserver.getPath(), configObserver);
        try {
            zk.getData(getQualityPath(configObserver.getPath()), new InfiniteWatcher(configObserver.getPath()), new Stat());
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deattach(ConfigObserver configObserver) {
        observers.remove(configObserver.getPath());
        try {
            zk.removeWatches(getQualityPath(configObserver.getPath()), new EmptyWatcher(), Watcher.WatcherType.Data, true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public void notifyAllObserver() {
        for (ConfigObserver observer : observers.values()) {
            observer.update();
        }
    }

    public void notifyPath(String path) {
        ConfigObserver observer = observers.get(path);
        if (observer != null) {
            observer.update();
        }
    }

    private String getQualityPath(String path) {
        if (path == null) {
            return "";
        }
        if (!path.startsWith("/")) {
            return PRE_PATH + path;
        } else {
            return PRE_PATH + path.substring(1);
        }
    }

    public class InfiniteWatcher implements Watcher {

        private final String path;

        public InfiniteWatcher(String path) {
            this.path = path;
        }

        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.NodeDataChanged) {
                notifyPath(path);
                try {
                    zk.getData(getQualityPath(path), new InfiniteWatcher(path), new Stat());
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class EmptyWatcher implements Watcher{
        public void process(WatchedEvent event) {

        }
    }
}
