package com.ui.auto;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.ui.entity.Config;
import com.ui.entity.ComConstant;
import com.ui.entity.ElementNode;
import com.ui.entity.PageNode;
import com.ui.entity.ActionEnum;
import com.ui.entity.NodeStatus;
import com.ui.util.CommonUtil;
import io.appium.java_client.AppiumDriver;
import org.dom4j.Element;
import org.dom4j.Node;
import org.openqa.selenium.WebElement;

import java.util.*;

/**
 * 节点生成类
 * Created by Administrator on 2018-06-29.
 */
public class NodeFactory {
    private AppiumDriver<WebElement> driver;
    private Config config  = InitConfig.getInstance().config;

    public NodeFactory(AppiumDriver<WebElement> driver){
        this.driver = driver;
    }

    /**
     * 创建页面节点
     * @param rootElement    当前页面xml
     * @param pageUrl       当前页面URL
     * @param parentNode    父页面
     * @param parentElement 父页面执行元素
     * @return
     */
    public PageNode createPageNode(Element rootElement,String pageUrl, PageNode parentNode, ElementNode parentElement) {
        int pageDepth = parentNode == null? 1 : parentNode.getDepth()+1;
        //  当前页面深度大于配置深度则不解析页面节点
        if (pageDepth > config.getMaxDepth()){
            return null;
        }
        //  获取当前页面xml失败，需重新获取
        if (rootElement == null){
            rootElement = CommonUtil.refreshPageDocument(driver);
        }
        //加载页面元素信息
        List<ElementNode> elementNodes = getPageElements(pageUrl,rootElement);
        Stack<ElementNode> taskStack = new Stack<ElementNode>();
        taskStack.addAll(elementNodes);
        //创建页面节点
        PageNode pageNode = new PageNode();
        pageNode.setUrl(pageUrl);
        pageNode.setNodeStatus(elementNodes.isEmpty()?NodeStatus.SKIP: NodeStatus.EXECUTING);
        pageNode.setParentNode(parentNode);
        pageNode.setParentElement(parentElement);
        pageNode.setDepth(pageDepth);
        pageNode.setAllElementNodes(elementNodes);
        pageNode.setStackElementNodes(taskStack);
        //  TODO 父节点暂时不放置，将当前页面节点加入父页面对象中
        if (parentNode != null){
            parentNode.addSonPage(pageNode);
            pageNode.setCrawlerLoc("/"+parentNode.getUrl()+"_"+ parentElement.getXpath());
        }
        return pageNode;
    }


    /**
     * 解析当前页面可执行的元素节点
     * @param pageUrl       当前页面url
     * @param rootElement    当前页面xml
     * @return
     */
    public List<ElementNode> getPageElements(String pageUrl, Element rootElement) {
        Map<String, Element> selectElements, blackElements, firstElements, lastElements;
        List<ElementNode> crawlerNodes = new ArrayList<>();
        List<Map<String, Element>> elements = new ArrayList<>();

        // 获取所有遍历元素
        selectElements = getNodesFromXPath(rootElement, config.getSelectedList());
        // 获取黑名单元素
        blackElements = getNodesFromXPath(rootElement, config.getBlackList());
        // 过滤黑名单元素
        selectElements = intersect(selectElements, blackElements);
        // 获取优先遍历元素
        firstElements = getNodesFromXPath(rootElement,  config.getFirstList());
        // 过滤优先遍历元素
        selectElements = intersect(selectElements, firstElements);
        // 获取最后遍历元素
        lastElements = getNodesFromXPath(rootElement, config.getLastList());
        // 过滤最后遍历元素
        selectElements = intersect(selectElements, lastElements);
        // 当前页面需要遍历元素总集合
        elements.add(firstElements);
        elements.add(selectElements);
        elements.add(lastElements);
        //判断页面没有可执行节点，直接退出
        if (elements.isEmpty()){
            return crawlerNodes;
        }
        //创建页面元素对象
        for (Map<String, Element> m : elements) {
            for (Map.Entry<String, Element> entry : m.entrySet()) {
                crawlerNodes.add(createElementNode(entry.getValue(), pageUrl));
            }
        }
        // List集合顺序反转
        Collections.reverse(crawlerNodes);
        return crawlerNodes;
    }

    /**
     * 根据xpath获取对应的Dom节点集合
     *
     * @param rootElement Dom根节点
     * @param nodeList 查询节点集合
     * @return
     */
    private Map<String, Element> getNodesFromXPath(Element rootElement, List<String> nodeList) {
        ImmutableMap.Builder<String, Element> builder = new ImmutableMap.Builder<String, Element>();
        if (nodeList != null && nodeList.size() > 0) {
            for (String s : nodeList) {
                // 存在xpath错误时会出错，后期优化
                List<Element> elements = rootElement.selectNodes(s);
                for (Element e : elements) {
                    builder.put(e.getUniquePath(), e);
                }
            }
        }
        return builder.build();
    }

    /**
     * 创建页面元素节点
     * @param element         dom的节点
     * @param pageUrl   元素所在页面唯一标识url
     * @return
     */
    private ElementNode createElementNode(Element element, String pageUrl) {
        String xpath = element.getUniquePath();
        String clazz = element.attributeValue(ComConstant.CLASS);
        ElementNode eLementNode = new ElementNode();
        eLementNode.setPageUrl(pageUrl);
        eLementNode.setName(element.attributeValue(ComConstant.TEXT));
        eLementNode.setClassName(clazz);
        eLementNode.setContentDesc(element.attributeValue(ComConstant.CONTENT_DESC));
        eLementNode.setResource_id(element.attributeValue(ComConstant.RESOURCE_ID));
        eLementNode.setBounds(element.attributeValue(ComConstant.BOUNDS));
        eLementNode.setXpath(xpath);
        eLementNode.setAction(ComConstant.EDIT_TEXT.equals(clazz) ? ActionEnum.INPUT : ActionEnum.CLICK);
        eLementNode.setCrawlerLoc("/"+pageUrl+"_"+ xpath);
        return eLementNode;
    }

//    /**
//     * 获取节点(当前节点没有则查找子节点)的名称
//     *
//     * @param element
//     * @return 返回节点的名称
//     */
//    private String getElementName(Element element) {
//        String nodeText = "";
//        String text = element.attributeValue(ComConstant.TEXT);
//        if (text != null && !"".equals(text)) {
//            return nodeText = text;
//        }
//        for (int i = 0; i < element.nodeCount(); i++) {
//            Node node = element.node(i);
//            if (node instanceof Element && "".equals(nodeText) || null == nodeText) {
//                text = ((Element) node).attributeValue("text");
//                if (text != null && !"".equals(text)) {
//                    return nodeText = text;
//                }
//                nodeText = getElementName((Element) node);
//            }
//        }
//        return nodeText;
//    }

//    /**
//     * 获取元素的xpath，获取当前节点的xpath，由于移动端与文档解析不一致
//     * 父节点的下标仍未处理，后面优化
//     * @param context
//     * @return
//     */
//    private String getElementXPath(Element context) {
//        Element parent = context.getParent();
//        StringBuffer buffer = new StringBuffer();
//        buffer.append(context.getPath());
//        List<?> mySiblings = parent.elements(context.getQName());
//        int index = mySiblings.indexOf(context) + 1;
//        buffer.append("[");
//        buffer.append(Integer.toString(index));
//        buffer.append("]");
//        return buffer.toString();
//    }

    /**
     * 根据键值(节点的xpath路径)相同或包含去重
     * @param sourceMap     源集合
     * @param repeatMap     过滤集合
     * @return
     */
    private Map<String, Element> intersect(Map<String, Element> sourceMap, Map<String, Element> repeatMap) {
        Map<String, Element> intersectMap = Maps.newHashMap();
        for (String source : sourceMap.keySet()) {
            for (String remove : repeatMap.keySet()) {
                if (remove.equals(source) || remove.contains(source)) {
                    intersectMap.put(source, sourceMap.get(source));
                }
            }
        }
        MapDifference<String, Element> diffHadle = Maps.difference(sourceMap, intersectMap);
        // 返回左边集合相对右边集合特有的元素
        return diffHadle.entriesOnlyOnLeft();
    }
}
