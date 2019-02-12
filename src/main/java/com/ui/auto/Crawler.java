package com.ui.auto;

import com.ui.entity.*;
import com.ui.entity.ActionEnum;
import com.ui.entity.NodeStatus;
import com.ui.util.CommonUtil;
import com.ui.util.Log;
import com.ui.util.XmindUtil;
import io.appium.java_client.AppiumDriver;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.openqa.selenium.WebElement;

import java.util.*;

/**
 * TODO 1、crash 重启服务并定位出现节点重跑
 * TODO 2、生成遍历路径(xMind)
 * TODO 3、一页显示不下，滑屏处理
 * TODO 4、列表页面相同控件处理
 * 遍历实现类
 */
public class Crawler {
    //当前窗口页面的唯一标识
    private String currentPageUrl;
    //当前页面节点
    private PageNode currentPageNode;
    //当前执行元素节点
    private ElementNode currentElementNode;
    //页面任务栈
    private Stack<PageNode> taskPageStack = new Stack<PageNode>();
    //访问过的页面集合
    public static Map<String,PageNode> allPageNodeMaps = new HashMap<String,PageNode>();
    private Config config  = InitConfig.getInstance().config;
    private AppiumDriver driver;
    private NodeActionHandler nodeActionHandler;

    public Crawler(AppiumDriver driver) {
        this.driver = driver;
        nodeActionHandler = new NodeActionHandler(driver);
    }

    /**
     * 遍历入口
     */
    public void crawl(){
        long startTime = System.currentTimeMillis();
        while(!taskPageStack.isEmpty()){
            long endTime = System.currentTimeMillis();
            long time = endTime - startTime;
            if (isExit() || time>300000){
                CommonUtil.exitCrawler(driver);
                return;
            }

            /*
             *  特殊处理
             *  如：点击弹框弹框按钮
            */
            execTrigger();
            /* ====================== 页面遍历 ====================== */
            Element currentRootElement = CommonUtil.refreshPageDocument(driver);
            currentPageUrl = CommonUtil.getPageUrl(driver,currentRootElement);
            PageNode existPage = allPageNodeMaps.get(currentPageUrl);
            if(existPage != null){
                // TODO 判断页面结果是否发生变化,目前很慢，后续优化
//                if (num != 1){
//                    PageNode newPage = new NodeFactory(driver).createPageNode(currentRootElement,currentPageUrl,currentPageNode,currentElementNode);
//                    refreshStructure(newPage,existPage);
//                }
                NodeStatus status = existPage.getNodeStatus();
                //情况2：存在访问过页面，判断是否遍历完成
                switch (status){
                    case END:
                        //情况2.1：遍历完成,判断是否有子节点，现只考虑一层子节点
                        //TODO 如子节点的子节点没处理完
                        PageNode sonNode = getSonNode(existPage);
                        if (sonNode == null){
                            //情况2.1.1：没有子节点，触发返回操作
                            nodeActionHandler.runAction(ActionEnum.BACK,null);
                            continue;
                        }
                        //情况2.1.2：子节点遍历完成，触发返回操作
                        NodeStatus sonStatus = sonNode.getNodeStatus();
                        if (sonStatus == NodeStatus.END){
                            nodeActionHandler.runAction(ActionEnum.BACK,null);
                            continue;
                        }
                        //TODO 情况2.1.3：子节点还未遍历完成，继续遍历该页面下的子节点
                        break;
                    case EXECUTING:
                        //情况2.2.1：未遍历完成,非当前窗口，更新任务栈(先移除再添加)
                        if (!existPage.getUrl().equals(currentPageNode.getUrl())){
                            refreshTaskStack(existPage);
                        }
                        //情况2.2.2：当前页面，不做任何处理
                        break;
                    case SKIP:
                        //情况2.3：跳过遍历，触发返回操作
                        nodeActionHandler.runAction(ActionEnum.BACK,null);
                        continue;
                    default:
                        Log.logFlow("节点遍历情况类型错误");
                }
            }else {
                //情况1：当前页面是新页面，并加载页面对象
                PageNode newPageNode = new NodeFactory(driver).createPageNode(currentRootElement,currentPageUrl,currentPageNode,currentElementNode);
                //情况1.1：当前页面深度大于配置的深度，点击返回，不创建新页面的节点（点击返回不会出现新的页面，如有则会出现问题）
                if (newPageNode == null){
                    nodeActionHandler.runAction(ActionEnum.BACK,null);
                    continue;
                }
                allPageNodeMaps.put(newPageNode.getUrl(),newPageNode);
                //情况1.2：页面没有可执行元素，跳过遍历，不加入任务栈中，并触发返回操作
                if (newPageNode.getNodeStatus() == NodeStatus.SKIP){
                    nodeActionHandler.runAction(ActionEnum.BACK,null);
                    continue;
                }
                //情况1.3：页面有可执行元素，将页面对象添加到任务栈，更新成第一个出栈
                taskPageStack.push(newPageNode);
            }

            /* ====================== 执行页面元素节点操作 ====================== */
            currentPageNode = taskPageStack.pop();
            Stack<ElementNode> currentElementStack = currentPageNode.getStackElementNodes();
            if (currentElementStack.isEmpty()){
                //页面没有可执行元素，更新页面节点遍历状态
                currentPageNode.setNodeStatus(NodeStatus.END);
                continue;
            }
            //获取元素节点，并执行操作
            currentElementNode = currentElementStack.pop();
            boolean flag = nodeActionHandler.runAction(currentElementNode.getAction(),currentElementNode);
            //设置元素遍历状态
            currentElementNode.setNodeStatus(flag ? NodeStatus.END : NodeStatus.FAIL);
            //重新将当前窗口页面节点更新至任务栈中判断后续操作
            taskPageStack.push(currentPageNode);
        }
    }

    /**
     * 更新任务栈的节点顺序
     * @param pageNode  页面节点
     */
    private void refreshTaskStack(PageNode pageNode){
        //页面结果变化检测
        if (taskPageStack.search(pageNode) != -1){
            taskPageStack.remove(pageNode);
            taskPageStack.push(pageNode);
        }else {
            taskPageStack.push(pageNode);
        }
    }

    /**
     * 更新页面结构
     * @param newPage      新页面节点
     * @param existPage    原页面节点
     */
    private  void refreshStructure(PageNode newPage, PageNode existPage){
        List<ElementNode> oldPageAllNode = new ArrayList<ElementNode>();
        List<ElementNode> newPageAllNode = newPage.getAllElementNodes();
        oldPageAllNode.addAll(existPage.getAllElementNodes());
        int nodeLength = newPageAllNode.size();
        for (int i = 0; i < nodeLength; i++){
            for (int j = 0; j< oldPageAllNode.size(); j++){
                if (newPageAllNode.get(i).getXpath().equals(oldPageAllNode.get(j).getXpath())){
                    break;
                }
                if ( j >=(oldPageAllNode.size()-1) && !newPageAllNode.get(i).getXpath().equals(oldPageAllNode.get(j).getXpath())){
                    existPage.getStackElementNodes().push(newPageAllNode.get(i));
                    existPage.getAllElementNodes().add(newPageAllNode.get(i));
                }
            }
        }
    }
    /**
     * 获取当前页面节点未完成的页面子节点
     * @param node  页面节点
     * @return
     */
    private PageNode getSonNode(PageNode node){
        List<PageNode> sonPageList = node.getSonPages();
        for (PageNode pageNode : sonPageList){
            if (pageNode.getNodeStatus() == NodeStatus.EXECUTING){
                return pageNode;
            }
        }
        return null;
    }

    /**
     * 初始化进入遍历步骤
     */
    public void initStartPage(){
        /*  ====================== 执行进入被测首页面 ====================== */
        ExtentReportManager.createSuccessLog("开始进入被测页面");
        List<Trigger> triggers = config.getStartPageAndroidStep();
        triggers.stream().forEach(p -> {
            WebElement element = driver.findElementByXPath(p.getXpath());
            nodeActionHandler.runTriggerAction(p.getActionEnum(),element,null);
        });
        /*  ====================== 首次进入加载当前页面为第一个节点 ====================== */
        ExtentReportManager.createSuccessLog("加载首页面为第一个节点");
        Element currentRootElement = CommonUtil.refreshPageDocument(driver);
        this.currentPageUrl = CommonUtil.getPageUrl(driver,currentRootElement);
        PageNode firstPageNode = new NodeFactory(driver).createPageNode(currentRootElement,currentPageUrl,null,null);
        this.taskPageStack.push(firstPageNode);
        this.allPageNodeMaps.put(currentPageUrl,firstPageNode);
        this.currentPageNode = firstPageNode;
        XmindUtil.getInstance().createWorkBook(firstPageNode.getUrl());
        ExtentReportManager.createSuccessLog("开始遍历");
    }

    /**
     * 特殊处理操作
     */
    public void execTrigger(){
        List<Trigger> triggers = config.getTriggerList();
        if (triggers.isEmpty()){
            return;
        }
        String pageSource = driver.getPageSource();
        triggers.stream().forEach(p ->{
            try {
                Element element = CommonUtil.getElementByXpath(pageSource,p.getXpath());
                if (element != null){
                    WebElement webElement = driver.findElementByXPath(p.getXpath());
                    nodeActionHandler.runTriggerAction(p.getActionEnum(),webElement,null);
                }
            } catch (DocumentException e) {
                Log.logError("特殊处理操作失败!",e);
            }
        });
    }

    /**
     * 检查退出遍历条件
     * @return
     */
    private boolean isExit(){
        String currentPackage = getCurrentAppName(driver.getPageSource());

        boolean flag = true;
        // 判断是否是当前app
        if (currentPackage.contains(config.getAppPackage())){
            return false;
        }
        // 判断包名是否有效
        for(String packageName: config.getAndroidValidPackageList()){
            if (currentPackage != null && currentPackage.contains(packageName)){
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 获取当前app包的名称
     * @param xml
     * @return
     */
    private String getCurrentAppName(String xml){
        String name = null;
        try {
            Element appPackage = CommonUtil.getElementByXpath(xml,ComConstant.ANDROID_APPNAME_XPATH);
            name = appPackage.attributeValue(ComConstant.APP_PACKAGE);
        } catch (DocumentException e) {
            Log.logError("获取当前APP包名失败",e);
        }
        return name;
    }
}
