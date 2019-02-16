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
 * TODO 1、crash 重启服务并定位出现节点重跑(跳转到指定activity)
 * TODO 2、一页显示不下，滑屏处理(判断listView控件scrollable为true进行滑动操作)
 * TODO 3、列表页面相同控件处理(通过listView控件筛选，根据实际情况首页面不做筛选)
 * TODO 4、页面结构发生变化(如弹框浮层，浮层内的元素无法获取等)
 * 遍历实现类
 */
public class Crawler {
    /*
        currentPageUrl:     当前窗口页面的唯一标识
        currentPageInfo:    当前页面信息
        currentElementInfo: 当前执行元素信息
        taskPageStack:      页面节点任务栈
        allPageNodeMaps:    访问过的页面集合
     */
    private String currentPageUrl;
    private PageInfo currentPageInfo;
    private ElementInfo currentElementInfo;
    private Stack<PageInfo> taskPageStack = new Stack<PageInfo>();
    public static Map<String, PageInfo> allPageNodeMaps = new HashMap<String, PageInfo>();
    private Config config  = InitializeConfiguration.getInstance().config;

    private AppiumDriver driver;
    private NodeActionHandler nodeActionHandler;

    public Crawler(AppiumDriver driver) {
        this.driver = driver;
        nodeActionHandler = new NodeActionHandler(driver);
    }

    /**
     * 执行遍历
     */
    public void run(){
        initializeStartPage();
        crawlRun();
    }

    /**
     * 遍历入口
     */
    public void crawlRun(){
        long startTime = System.currentTimeMillis();
        while(!taskPageStack.isEmpty()){
            if (isExit(startTime)){
                return;
            }
            /* ====================== 特殊处理 ====================== */
            executeTrigger();
            /* ====================== 页面遍历 ====================== */
            Element currentRootElement = CommonUtil.refreshPageDocument(driver);
            currentPageUrl = CommonUtil.getPageUrl(driver,currentRootElement);
            PageInfo existPage = allPageNodeMaps.get(currentPageUrl);
            boolean executeStatus;
            if(existPage != null){
                // TODO 判断页面结构是否发生变化,目前很慢，后续优化
//                if (num != 1){
//                    PageInfo newPage = new NodeFactory(driver).createPageNode(currentRootElement,currentPageUrl,currentPageInfo,currentElementInfo);
//                    refreshStructure(newPage,existPage);
//                }
                executeStatus = executeExistPage(existPage);
            }else {
                executeStatus = executeNewPage(currentRootElement, driver);
            }
            if (executeStatus) {
                continue;
            }
            /* ====================== 执行页面元素节点操作 ====================== */
            executeElementAction();
        }
    }

    /**
     * 执行元素操作
     */
    private void executeElementAction(){
        currentPageInfo = taskPageStack.pop();
        Stack<ElementInfo> currentElementStack = currentPageInfo.getStackElementInfos();
        if (currentElementStack.isEmpty()){
            //页面没有可执行元素，更新页面节点遍历状态
            currentPageInfo.setNodeStatus(NodeStatus.END);
            return;
        }
        //获取元素节点，并执行操作
        currentElementInfo = currentElementStack.pop();
        boolean flag = nodeActionHandler.runAction(currentPageUrl, currentElementInfo.getAction(), currentElementInfo);
        //设置元素遍历状态
        currentElementInfo.setNodeStatus(flag ? NodeStatus.END : NodeStatus.FAIL);
        //重新将当前窗口页面节点更新至任务栈中判断后续操作
        taskPageStack.push(currentPageInfo);
    }
    /**
     * 执行已经存在的页面
     */
    private boolean executeExistPage(PageInfo existPage){
        NodeStatus status = existPage.getNodeStatus();
        //情况2：存在访问过页面，判断是否遍历完成
        switch (status){
            case END:
                //情况2.1：遍历完成,判断是否有子节点，现只考虑一层子节点
                //TODO 如子节点的子节点没处理完
                PageInfo sonNode = getSonNode(existPage);
                if (sonNode == null){
                    //情况2.1.1：没有子节点，触发返回操作
                    nodeActionHandler.runAction(currentPageUrl, ActionEnum.BACK,null);
                    return true;
                }
                //情况2.1.2：子节点遍历完成，触发返回操作
                NodeStatus sonStatus = sonNode.getNodeStatus();
                if (sonStatus == NodeStatus.END){
                    nodeActionHandler.runAction(currentPageUrl, ActionEnum.BACK,null);
                    return true;
                }
                //TODO 情况2.1.3：子节点还未遍历完成，继续遍历该页面下的子节点
                break;
            case EXECUTING:
                //情况2.2.1：未遍历完成,非当前窗口，更新任务栈(先移除再添加)
                if (!existPage.getUrl().equals(currentPageInfo.getUrl())){
                    refreshTaskStack(existPage);
                }
                //情况2.2.2：当前页面，不做任何处理
                break;
            case SKIP:
                //情况2.3：跳过遍历，触发返回操作
                nodeActionHandler.runAction(currentPageUrl, ActionEnum.BACK,null);
                return true;
            default:
                Log.logFlow("节点遍历情况类型错误");
        }
        return false;
    }

    /**
     * 执行新页面
     * @param currentRootElement
     * @param driver
     * @return
     */
    private boolean executeNewPage(Element currentRootElement, AppiumDriver driver){
        //情况1：当前页面是新页面，并加载页面对象
        PageInfo newPageInfo = new NodeFactory(driver).createPageNode(currentRootElement,currentPageUrl, currentPageInfo, currentElementInfo);
        allPageNodeMaps.put(newPageInfo.getUrl(), newPageInfo);
        /*
            情况1.1：当前页面深度大于配置的深度，点击返回，不创建新页面的节点（点击返回不会出现新的页面，如有则会出现问题）
            情况1.2：页面没有可执行元素，跳过遍历，不加入任务栈中，并触发返回操作
         */
        if (newPageInfo.getNodeStatus() == NodeStatus.SKIP) {
            nodeActionHandler.runAction(currentPageUrl, ActionEnum.BACK,null);
            return true;
        }
        //情况1.3：页面有可执行元素，将页面对象添加到任务栈，更新成第一个出栈
        taskPageStack.push(newPageInfo);
        return false;
    }

    /**
     * 更新任务栈的节点顺序
     * @param pageInfo  页面节点
     */
    private void refreshTaskStack(PageInfo pageInfo){
        //页面结果变化检测
        if (taskPageStack.search(pageInfo) != -1){
            taskPageStack.remove(pageInfo);
            taskPageStack.push(pageInfo);
        }else {
            taskPageStack.push(pageInfo);
        }
    }

    /**
     * 更新页面结构
     * @param newPage      新页面节点
     * @param existPage    原页面节点
     */
    private  void refreshStructure(PageInfo newPage, PageInfo existPage){
        List<ElementInfo> oldPageAllNode = new ArrayList<ElementInfo>();
        List<ElementInfo> newPageAllNode = newPage.getAllElementInfos();
        oldPageAllNode.addAll(existPage.getAllElementInfos());
        int nodeLength = newPageAllNode.size();
        for (int i = 0; i < nodeLength; i++){
            for (int j = 0; j< oldPageAllNode.size(); j++){
                if (newPageAllNode.get(i).getXpath().equals(oldPageAllNode.get(j).getXpath())){
                    break;
                }
                if ( j >=(oldPageAllNode.size()-1) && !newPageAllNode.get(i).getXpath().equals(oldPageAllNode.get(j).getXpath())){
                    existPage.getStackElementInfos().push(newPageAllNode.get(i));
                    existPage.getAllElementInfos().add(newPageAllNode.get(i));
                }
            }
        }
    }
    /**
     * 获取当前页面节点未完成的页面子节点
     * @param node  页面节点
     * @return
     */
    private PageInfo getSonNode(PageInfo node){
        List<PageInfo> sonPageList = node.getSonPages();
        for (PageInfo pageInfo : sonPageList){
            if (pageInfo.getNodeStatus() == NodeStatus.EXECUTING){
                return pageInfo;
            }
        }
        return null;
    }

    /**
     * 初始化进入遍历首页
     */
    public void initializeStartPage(){
        /*  ====================== 执行进入被测首页面 ====================== */
        ExtentReportManager.createSuccessLog("初始化被测页面");
        List<Trigger> triggers = config.getStartPageAndroidStep();
        triggers.stream().forEach(p -> {
            WebElement element = driver.findElementByXPath(p.getXpath());
            nodeActionHandler.runTriggerAction(p.getActionEnum(),element,null);
        });
        /*  ====================== 首次进入加载当前页面为第一个节点 ====================== */
        Element currentRootElement = CommonUtil.refreshPageDocument(driver);
        currentPageUrl = CommonUtil.getPageUrl(driver,currentRootElement);
        PageInfo firstPageInfo = new NodeFactory(driver).createPageNode(currentRootElement,currentPageUrl,null,null);
        taskPageStack.push(firstPageInfo);
        allPageNodeMaps.put(currentPageUrl, firstPageInfo);
        currentPageInfo = firstPageInfo;
        XmindUtil.getInstance().createWorkBook(firstPageInfo.getUrl());
        ExtentReportManager.createSuccessLog(String.format("%s为首页面，并开始遍历...", currentPageUrl));
    }

    /**
     * 特殊处理操作,如：点击弹框弹框按钮
     */
    private void executeTrigger(){
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
    private boolean isExit(long startTime){
        String currentPackage = getCurrentAppName(driver.getPageSource());

        boolean flag = overTimeCheck(startTime);
        if (flag) {
            return true;
        }
        // 判断是否是当前app
        if (currentPackage.contains(config.getAppPackage())){
            return false;
        }
        // 判断包名是否有效
        for(String packageName: config.getAndroidValidPackageList()){
            if (currentPackage != null && currentPackage.contains(packageName)){
                return false;
            }
        }
        return false;
    }

    /**
     * 遍历时间check
     * @param startTime
     * @return
     */
    private boolean overTimeCheck(long startTime){
        long endTime = System.currentTimeMillis();
        long actDuration = endTime - startTime;
        long expDuration = config.getDuration() * 60000;
        if (actDuration > expDuration) {
            return true;
        }
        return false;
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
