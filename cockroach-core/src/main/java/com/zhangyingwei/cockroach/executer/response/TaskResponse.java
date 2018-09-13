package com.zhangyingwei.cockroach.executer.response;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import cn.wanghaomiao.xpath.model.JXDocument;
import com.zhangyingwei.cockroach.common.exception.HttpException;
import com.zhangyingwei.cockroach.executer.task.Task;
import com.zhangyingwei.cockroach.http.client.IHttpClient;
import com.zhangyingwei.cockroach.queue.CockroachQueue;
import com.zhangyingwei.cockroach.common.utils.CockroachUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zhangyw on 2017/8/10.
 * 请求返回结构
 */
public class TaskResponse implements ICockroachResponse {
    private Map<String, List<String>> headers;
    private Task task;
    private JXDocument xdocument;
    private CockroachQueue queue;
    private ResponseContent content;
    private boolean failed = false;
    private IHttpClient httpClient;

    public TaskResponse(byte[] contentBytes, Map<String, List<String>> headers, int code, Task task) throws IOException, HttpException {
        this.content = new ResponseContent();
        this.content.setContentBytes(contentBytes);
        this.task = task;
        this.headers = headers;
        if (!CockroachUtils.validHttpCode(code)) {
            throw new HttpException(this.getContent().string(),code);
        }
    }

    public TaskResponse() {
        this.content = new ResponseContent();
    }

    @Override
    public ResponseContent getContent() throws IOException {
        return this.content;
    }

    private JXDocument parseJXDocument() throws IOException {
        if (this.xdocument == null) {
            Document doc = this.content.toDocument();
            this.xdocument = new JXDocument(doc);
        }
        return this.xdocument;
    }

    @Override
    public Task getTask() {
        return task;
    }

    public TaskResponse setTask(Task task) {
        this.task = task;
        return this;
    }

    public TaskResponse charset(String charset) {
        this.content.charset(charset);
        return this;
    }

    public Elements select(String cssSelect) throws IOException {
        return this.content.toDocument().select(cssSelect);
    }

    public Elements xpath(String xpath) throws IOException, XpathSyntaxErrorException {
        List<Element> elements = this.parseJXDocument().sel(xpath).stream().map(obj -> {
            return (Element) obj;
        }).collect(Collectors.toList());
        return new Elements(Optional.of(elements).orElse(new ArrayList<Element>()));
    }

    @Override
    public boolean isGroup(String group){
        return task.getGroup().equals(group);
    }

    @Override
    public boolean isGroupStartWith(String groupPrefix) {
        return task.getGroup().startsWith(groupPrefix);
    }

    @Override
    public boolean isGroupEndWith(String end) {
        return task.getGroup().endsWith(end);
    }

    @Override
    public boolean isGroupContains(String str) {
        return task.getGroup().contains(str);
    }

    public void setQueue(CockroachQueue queue) {
        this.queue = queue;
    }

    @Override
    public CockroachQueue getQueue() {
        return queue;
    }

    @Override
    public List<String> header(String key) {
        if (this.headers.containsKey(key)) {
            return this.headers.get(key);
        }
        return null;
    }

    public boolean isFalied() {
        return this.failed;
    }

    public TaskResponse falied(String message) {
        this.failed = true;
        this.content.setContentBytes(message.getBytes());
        return this;
    }

    public IHttpClient getHttpClient() {
        return httpClient;
    }

    public TaskResponse setHttpClient(IHttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }
}