package net.lovenn.zookeeper;

import java.io.IOException;
import java.lang.reflect.Field;

public class ConfigSubjectService {

    private String classFilePath;

    private ZookeeperConfigSubject configSubject = new ZookeeperConfigSubject();

    public ConfigSubjectService(String classFilePath) throws IOException, ClassNotFoundException {
        this.classFilePath = classFilePath;
        Class<?> contants = Class.forName(classFilePath);
        parseClassFieldAndAttach(contants);

    }

    public void parseClassFieldAndAttach(Class<?> contants) {

        Field[] toCreateFields = contants.getDeclaredFields();

        if (toCreateFields.length > 0) {
            for (Field f : toCreateFields) {
                f.setAccessible(true);

                String value = null;
                try {
                    if (f.getType() == String.class) {
                        value = (String) f.get(contants);
                    }
                    if (f.getType() == Integer.class || f.getType() == int.class) {
                        value = String.valueOf(f.get(contants));
                    }
                    if (f.getType() == Long.class || f.getType() == long.class) {
                        value = String.valueOf(f.get(contants));
                    }
                    if (f.getType() == Float.class || f.getType() == float.class) {
                        value = String.valueOf(f.get(contants));
                    }
                    if (f.getType() == Double.class || f.getType() == double.class) {
                        value = String.valueOf(f.get(contants));
                    }
                    if(value != null) {
                        configSubject.attach(new ZookeeperConfigObserver(value, contants));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
