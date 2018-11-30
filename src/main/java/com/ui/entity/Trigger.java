package com.ui.entity;

/**
 * 特殊处理实体类
 * Created by cz on 2018-11-26.
 * @author cz
 */
public class Trigger {
    private String action;
    private String xpath;
    private String page;
    private String value;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
