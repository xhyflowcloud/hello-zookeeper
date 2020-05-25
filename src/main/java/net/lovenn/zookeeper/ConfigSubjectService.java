package net.lovenn.zookeeper;

import java.io.IOException;

public class ConfigSubjectService {

    private String classFilePath;

    private ZookeeperConfigSubject configSubject = new ZookeeperConfigSubject();

    public ConfigSubjectService(String classFilePath) throws IOException {
        this.classFilePath = classFilePath;

    }
}
