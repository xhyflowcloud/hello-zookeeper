package net.lovenn.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class ZookeeperServer implements Watcher {
    private static final String HOST = "127.0.0.1";

    private static final int PORT = 2181;

    private ZooKeeper zk;

    private static final String BASE_PATH = "/localhost/config/";

    public ZookeeperServer() throws IOException, KeeperException, InterruptedException {
        zk = new ZooKeeper("127.0.0.1:2181", 30000, this);
//        while (zk.getState() != ZooKeeper.States.CONNECTED) {
//            System.out.println("NOT CONNECTION");
//        }
        if(zk.exists("/localhost", null) == null) {
            zk.create("/localhost", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        if(zk.exists("/localhost/config", null) == null) {
            zk.create("/localhost/config", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        zk.getData("/localhost", null, null);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void process(WatchedEvent event) {
//        if(event.getState() == Event.KeeperState.SyncConnected) {
//            System.out.println("zk connection success");
//        }
//        else {
//            if(zk != null) {
//                try {
//                    zk.close();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println("zk connection failed");
//        }
        if(event.getType() == Event.EventType.NodeCreated) {

        }
    }

    public void create(String path, String value) {
        if(value != null && !value.equals("null")) {
            try {
                if(zk.exists(path, null) == null) {
                    zk.create(path, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String sub(String path) {

        try {
            byte[] data = zk.getData(path, new UpdateWatcher(path), new Stat());
            return new String(data);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class UpdateWatcher implements Watcher {

        private String path;

        private Field field;

        public UpdateWatcher(String path) {
            this.path = path;
            for(Field f: Contants.class.getDeclaredFields()) {
                if(f.getName().equals(path.substring(path.lastIndexOf('/') + 1, path.length()))) {
                    field = f;
                    break;
                }
            }
        }

        public void process(WatchedEvent event) {
            if(event.getType() == Event.EventType.NodeDataChanged) {
                try {
                    byte[] data = zk.getData(path, new UpdateWatcher(path), new Stat());
                    if(field != null && field.getType() == String.class) {
                        field.setAccessible(true);
                        try {
                            field.set(Contants.class, new String(data));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void registerNodes(Class<?> contants) {

        if(contants != null) {
            Field[] toCreateFields = contants.getDeclaredFields();

            if(toCreateFields.length > 0) {
               for (Field f: toCreateFields) {
                   f.setAccessible(true);

                   String value = null;
                   try {
                       if(f.getType() == String.class) {
                            value = (String) f.get(contants);
                       }
                       if(f.getType() == Integer.class || f.getType() == int.class) {
                            value = String.valueOf(f.get(contants));
                       }
                       if(f.getType() == Long.class || f.getType() == long.class) {
                           value = String.valueOf(f.get(contants));
                       }
                       if(f.getType() == Float.class || f.getType() == float.class) {
                           value = String.valueOf(f.get(contants));
                       }
                       if(f.getType() == Double.class || f.getType() == double.class) {
                           value = String.valueOf(f.get(contants));
                       }
                       create(BASE_PATH + f.getName(), value);
                   } catch (IllegalAccessException e) {
                       e.printStackTrace();
                   }
               }
            }
        }
    }

    public void registerNodesEvent(Class<?> contants) {

        if(contants != null) {
            Field[] toCreateFields = contants.getDeclaredFields();

            if(toCreateFields.length > 0) {
                for (Field f: toCreateFields) {
                    f.setAccessible(true);

                    String value = null;
                    if(f.getType() == String.class) {
                        sub(BASE_PATH + f.getName());
                    }
                }
            }
        }
    }

    public void close() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zk = null;
    }
}
