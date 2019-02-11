package com.ui.entity;

/**
 * 页面中的元素节点实体类
 * Created by cz on 2018-11-26.
 * @author cz
 */
public class ElementNode {
    /**
     *  pageUrl：        页面URL
     *  name：           元素名称
     *  resource_id：    元素id
     *  className：      元素class
     *  contentDesc：    元素desc
     *  bounds：         元素坐标
     *  xpath：          元素xpath路径
     *  nodeStatus：     元素执行状态
     *  action：         元素执行动作
     *  crawlerLoc：     元素xMind路径
     *  screenShotPath： 点击元素的截图路径
     */
    private String pageUrl;
    private String name;
    private String resource_id;
    private String className;
    private String contentDesc;
    private String bounds;
    private String xpath;
    private NodeStatus nodeStatus;
    private ActionEnum action;
    private String crawlerLoc;
    private String screenShotPath;
    private String xmindTopicId;

    public String getXmindTopicId() {
        return xmindTopicId;
    }

    public void setXmindTopicId(String xmindTopicId) {
        this.xmindTopicId = xmindTopicId;
    }

    public String getScreenShotPath() {
        return screenShotPath;
    }

    public void setScreenShotPath(String screenShotPath) {
        this.screenShotPath = screenShotPath;
    }

    public String getCrawlerLoc() {
        return crawlerLoc;
    }

    public void setCrawlerLoc(String crawlerLoc) {
        this.crawlerLoc = crawlerLoc;
    }

    public ActionEnum getAction() {
        return action;
    }

    public void setAction(ActionEnum action) {
        this.action = action;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResource_id() {
        return resource_id;
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getContentDesc() {
        return contentDesc;
    }

    public void setContentDesc(String contentDesc) {
        this.contentDesc = contentDesc;
    }

    public String getBounds() {
        return bounds;
    }

    public void setBounds(String bounds) {
        this.bounds = bounds;
    }

    public NodeStatus getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(NodeStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
    }
}
