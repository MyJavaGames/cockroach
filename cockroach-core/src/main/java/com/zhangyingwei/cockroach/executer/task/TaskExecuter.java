package com.zhangyingwei.cockroach.executer.task;

import com.zhangyingwei.cockroach.executer.response.TaskErrorResponse;
import com.zhangyingwei.cockroach.executer.response.TaskResponse;
import com.zhangyingwei.cockroach.executer.response.filter.TaskResponseFilterBox;
import com.zhangyingwei.cockroach.http.HttpProxy;
import com.zhangyingwei.cockroach.http.ProxyTuple;
import com.zhangyingwei.cockroach.http.client.HttpClientProxy;
import com.zhangyingwei.cockroach.http.client.IHttpClient;
import com.zhangyingwei.cockroach.http.handler.ITaskErrorHandler;
import com.zhangyingwei.cockroach.queue.CockroachQueue;
import com.zhangyingwei.cockroach.store.IStore;
import com.zhangyingwei.cockroach.common.utils.NameUtils;
import org.apache.log4j.Logger;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyw on 2017/8/10.
 * 任务执行器，主要工作是从队列中取出任务然后执行任务
 */
public class TaskExecuter implements Runnable {
    private final ITaskErrorHandler errorHandlerBox;
    private final TaskResponseFilterBox filterBox;
    private Logger logger = Logger.getLogger(TaskExecuter.class);
    private CockroachQueue queue;
    private HttpClientProxy httpClient;
    private IStore store;
    private String id;
    private boolean autoClose;
    private int sleep;
    private boolean flag = true;

    public TaskExecuter(CockroachQueue queue, HttpClientProxy httpClient, IStore store, ITaskErrorHandler errorHandlerBox, int sleep, boolean autoClose, TaskResponseFilterBox filterBox) {
        this.queue = queue;
        this.httpClient = httpClient;
        this.store = store;
        this.id = NameUtils.name(TaskExecuter.class);
        this.errorHandlerBox = errorHandlerBox;
        this.autoClose = autoClose;
        this.sleep = sleep;
        this.filterBox = filterBox;
    }

    @Override
    public void run() {
        loop:while (flag) {
            TaskResponse response = null;
            try {
                Task task = null;
                if(autoClose){
                    task = this.queue.poll();
                    if(task == null){
                        flag = false;
                        break loop;
                    }
                }else{
                    task = this.queue.take();
                }
                TimeUnit.MILLISECONDS.sleep(sleep);
                logger.info(this.getId()+" GET - "+task);
                response = this.httpClient.doGet(task);
                response.setQueue(this.queue);
                if(response.isFalied()){
                    this.errorHandlerBox.error(new TaskErrorResponse(response));
                }else{
                    if (this.filterBox.accept(response)) {
                        this.store.store(response);
                    }
                }
            } catch (Exception e) {
                logger.error(this.getId()+" - "+ e.getLocalizedMessage());
            }
        }
        logger.info(id+" : over");
    }

    public void stop() {
        this.flag = false;
    }

    public String getId() {
        return id;
    }
}
