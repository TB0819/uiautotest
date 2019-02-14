package com.ui.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 页面节点实体类
 * Created by cz on 2018-11-26.
 * @author cz
 */
public class PageInfo {
    /**
     * url：                 页面唯一标识(URL)
     * depth：               页面深度
     * nodeStatus：          页面遍历状态
     * allElementInfos：     页面所有可执行元素
     * stackElementInfos：   页面待执行元素任务栈
     * crawlerLoc：          页面初次进入路径xMind
     * parentNode：          父节点：首次进入该页面的前一页面
     * parentElement：       父节点页面点击的元素
     * sonPages：            当前页面进入的子页面集合
     * parentList：          父节点路径
     */
    private String url;
    private int depth;
    private NodeStatus nodeStatus;
    private List<ElementInfo> allElementInfos;
    private Stack<ElementInfo> stackElementInfos;
    private String crawlerLoc;
    private PageInfo parentNode;
    private ElementInfo parentElement;
    private List<PageInfo> sonPages;
    private List<Map<PageInfo, ElementInfo>> parentList;

    public void addSonPage(PageInfo sonPage){
        if (this.sonPages == null){
            this.sonPages = new ArrayList<PageInfo>();
        }
        this.sonPages.add(sonPage);
    }

    public List<PageInfo> getSonPages() {
        return this.sonPages;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ElementInfo getParentElement() {
        return parentElement;
    }

    public void setParentElement(ElementInfo parentElement) {
        this.parentElement = parentElement;
    }

    public List<ElementInfo> getAllElementInfos() {
        return allElementInfos;
    }

    public void setAllElementInfos(List<ElementInfo> allElementInfos) {
        this.allElementInfos = allElementInfos;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public NodeStatus getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(NodeStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public Stack<ElementInfo> getStackElementInfos() {
        return stackElementInfos;
    }

    public void setStackElementInfos(Stack<ElementInfo> stackElementInfos) {
        this.stackElementInfos = stackElementInfos;
    }

    public String getCrawlerLoc() {
        return crawlerLoc;
    }

    public void setCrawlerLoc(String crawlerLoc) {
        this.crawlerLoc = crawlerLoc;
    }

    public PageInfo getParentNode() {
        return parentNode;
    }

    public void setParentNode(PageInfo parentNode) {
        this.parentNode = parentNode;
    }
}
