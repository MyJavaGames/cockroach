package com.zhangyingwei.cockroach;

import com.zhangyingwei.cockroach.config.CockroachConfig;
import com.zhangyingwei.cockroach.executer.task.Task;
import com.zhangyingwei.cockroach.queue.TaskQueue;
import com.zhangyingwei.cockroach.store.SelecterTestStore;
import org.junit.Test;

/**
 * Created by zhangyw on 2017/8/10.
 */
public class CockroachContextTest {
    @Test
    public void test() {}
    public static void main(String[] args) throws Exception {
        CockroachConfig config = new CockroachConfig()
                .setAppName("haha")
                .setThread(1)
                .setAutoClose(true)
//                .setProxys("121.232.145.21:9000")
//                .setHttpClient(COkHttpClient.class)
                .setStore(SelecterTestStore.class);
        CockroachContext context = new CockroachContext(config);
        TaskQueue queue = TaskQueue.of();
        queue.push(new Task("https://www.cnblogs.com/wanghaomiao/p/4899355.html"));
        context.start(queue);

//        new Thread(() -> {
//            int i = 1;
//            while(true){
//                i++;
//                try {
//                    Thread.sleep(1);
//                    String url = "http://op.5068.com/qb/118368_" + i + ".html";
//                    System.out.println(url);
//                    queue.push(new Task(url));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (i > 9) {
//                    break;
//                }
//            }
//        }).start();


    }
}