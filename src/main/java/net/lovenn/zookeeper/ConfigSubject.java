package net.lovenn.zookeeper;

public interface ConfigSubject {

    /**
     * 绑定观察者
     */
    void attach(ConfigObserver configObserver);

    /**
     * 解除观察者
     */
    void deattach(ConfigObserver configObserver);

    /**
     * 通知观察者
     */
    void notifyAllObserver();

    /**
     * 通知路径
     */
    void notifyPath(String path);
}
