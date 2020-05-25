package net.lovenn;

import net.lovenn.zookeeper.ConfigSubjectService;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Test {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        ConfigSubjectService configSubjectService = new ConfigSubjectService("net.lovenn.zookeeper.Contants");
        CountDownLatch countDownLatch = new CountDownLatch(2);
        countDownLatch.await();
    }
}
