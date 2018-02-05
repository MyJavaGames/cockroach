package com.zhangyingwei.cockroach.utils;

import com.zhangyingwei.cockroach.CockroachApplication;
import com.zhangyingwei.cockroach.annotation.EnableAutoConfiguration;
import com.zhangyingwei.cockroach.annotation.HttpConfig;
import com.zhangyingwei.cockroach.annotation.Store;
import com.zhangyingwei.cockroach.annotation.ThreadConfig;
import com.zhangyingwei.cockroach.executer.task.Task;
import com.zhangyingwei.cockroach.queue.CockroachQueue;
import com.zhangyingwei.cockroach.queue.TaskQueue;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by zhangyw on 2017/12/12.
 */
@EnableAutoConfiguration
@Store(ImageStore.class)
@ThreadConfig(num = 1)
@HttpConfig(progress = true)
public class FileUtilsTest {
    public static void main(String[] args) throws Exception {
        CockroachQueue queue = TaskQueue.of();
        queue.push(new Task("http://mov.bn.netease.com/open-movie/nos/flv/2013/09/11/S97IU0TJ3_sd.flv"));
        CockroachApplication.run(FileUtilsTest.class,queue);
    }

    @Test
    public void getOrCreateTest() throws IOException {
        File file = FileUtils.openOrCreate("D://", "hello.txt");
        FileUtils.clearFile(file);
        for (int i = 0; i < 10; i++) {
            FileUtils.append(file,i+"\n");
        }
        FileUtils.closeWriters();
        FileUtils.delete(file);
    }
}