package com.ui.entity;

/**
 * 页面元素节点遍历的状态
 * Created by cz on 2018-11-26.
 * @author cz
 */
public enum NodeStatus {
    SKIP("跳过遍历",1),
    EXECUTING("执行中",2),
    END("遍历完成",3) ,
    FAIL("执行失败",4);

    private String description;
    private int status;

    NodeStatus(String description, int status){
        this.description =description;
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
