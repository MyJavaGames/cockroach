package com.zhangyingwei.cockroach.http;

import com.zhangyingwei.cockroach.executer.task.Task;
import com.zhangyingwei.cockroach.executer.response.TaskResponse;
import com.zhangyingwei.cockroach.http.client.okhttp.COkHttpClient;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by zhangyw on 2017/8/10.
 */
public class COkIHttpClientTest {
    @Test
    public void doGet() throws Exception {
        COkHttpClient client = new COkHttpClient();
        TaskResponse resp = (TaskResponse) client.doGet(new Task("https://luolei.org"));
        Assert.assertNotNull(resp.select("a"));
    }
}