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
public class PageNode {
    /**
     * url：                 页面唯一标识(URL)
     * depth：               页面深度
     * nodeStatus：          页面遍历状态
     * allElementNodes：     页面所有可执行元素
     * stackElementNodes：   页面待执行元素任务栈
     * crawlerLoc：          页面初次进入路径xMind
     * parentNode：          父节点：首次进入该页面的前一页面
     * parentElement：       父节点页面点击的元素
     * sonPages：            当前页面进入的子页面集合
     * parentList：          父节点路径
     */
    private String url;
    private int depth;
    private NodeStatus nodeStatus;
    private List<ElementNode> allElementNodes;
    private Stack<ElementNode> stackElementNodes;
    private String crawlerLoc;
    private PageNode parentNode;
    private ElementNode parentElement;
    private List<PageNode> sonPages;
    private List<Map<PageNode,ElementNode>> parentList;

    public void addSonPage(PageNode sonPage){
        if (this.sonPages == null){
            this.sonPages = new ArrayList<PageNode>();
        }
        this.sonPages.add(sonPage);
    }

    public List<PageNode> getSonPages() {
        return this.sonPages;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ElementNode getParentElement() {
        return parentElement;
    }

    public void setParentElement(ElementNode parentElement) {
        this.parentElement = parentElement;
    }

    public List<ElementNode> getAllElementNodes() {
        return allElementNodes;
    }

    public void setAllElementNodes(List<ElementNode> allElementNodes) {
        this.allElementNodes = allElementNodes;
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

    public Stack<ElementNode> getStackElementNodes() {
        return stackElementNodes;
    }

    public void setStackElementNodes(Stack<ElementNode> stackElementNodes) {
        this.stackElementNodes = stackElementNodes;
    }

    public String getCrawlerLoc() {
        return crawlerLoc;
    }

    public void setCrawlerLoc(String crawlerLoc) {
        this.crawlerLoc = crawlerLoc;
    }

    public PageNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(PageNode parentNode) {
        this.parentNode = parentNode;
    }
}
