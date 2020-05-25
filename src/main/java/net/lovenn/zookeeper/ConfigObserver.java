package net.lovenn.zookeeper;

public interface ConfigObserver {

    String getPath();

    /**
     * 更新
     */
    void update();
}
