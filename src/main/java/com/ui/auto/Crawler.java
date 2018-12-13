package com.ui.auto;

import com.ui.entity.*;
import com.ui.entity.ActionEnum;
import com.ui.entity.NodeStatus;
import com.ui.util.CommonUtil;
import com.ui.util.Log;
import com.ui.util.MindUtil;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * TODO 1、crash 重启服务并定位出现节点重跑
 * TODO 2、生成遍历路径(xMind)
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
    private List<PageNode>  AllPageNodes = new ArrayList<PageNode>();
    //当前页面元素任务栈
    private Stack<ElementNode> currentElementStack;
    private Config config  = InitConfig.getInstance().config;
    private AppiumDriver<WebElement> driver;
    MindUtil mindUtil = MindUtil.getInstance();

    public Crawler(AppiumDriver<WebElement> driver) {
        this.driver = driver;
    }

    /**
     * 遍历入口
     */
    public void crawl(){
        /*  ====================== 首次进入加载当前页面为第一个节点 ====================== */
        Element currentRootElement = CommonUtil.refreshPageDocument(driver);
        currentPageUrl = CommonUtil.getPageUrl(driver,currentRootElement);
        PageNode firstPageNode = new NodeFactory(driver).createPageNode(currentRootElement,currentPageUrl,null,null);
        taskPageStack.push(firstPageNode);

        while(!taskPageStack.isEmpty()){
            //TODO 判断是否退出循环
            if (isExit()){ return; }

            /*
             *  特殊处理
             *  如：点击弹框弹框按钮
            */
            execTrigger();
            /* ====================== 页面遍历 ====================== */
            currentRootElement = CommonUtil.refreshPageDocument(driver);
            currentPageUrl = CommonUtil.getPageUrl(driver,currentRootElement);
            PageNode existPage = getExistPageForAllPageNodes(currentPageUrl);
            if(existPage != null){
                // TODO 判断页面结果是否发生变化,目前很慢，后续优化
                PageNode newPage = new NodeFactory(driver).createPageNode(currentRootElement,currentPageUrl,currentPageNode,currentElementNode);
                refreshStructure(newPage,existPage);

                NodeStatus status = existPage.getNodeStatus();
                //情况2：存在访问过页面，判断是否遍历完成
                switch (status){
                    case END:
                        //情况2.1：遍历完成,判断是否有子节点，现只考虑一层子节点
                        //TODO 如子节点的子节点没处理完
                        PageNode sonNode = getSonNode(existPage);
                        if (sonNode == null){
                            //情况2.1.1：没有子节点，触发返回操作
                            executeBack();
                            continue;
                        }
                        //情况2.1.2：子节点遍历完成，触发返回操作
                        NodeStatus sonStatus = sonNode.getNodeStatus();
                        if (sonStatus == NodeStatus.END){
                            executeBack();
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
                        executeBack();
                        continue;
                    default:
                        Log.logFlow("节点遍历情况类型错误");
                }
            }else {
                //情况1：当前页面是新页面，并加载页面对象
                PageNode newPageNode = new NodeFactory(driver).createPageNode(currentRootElement,currentPageUrl,currentPageNode,currentElementNode);
                //情况1.1：当前页面深度大于配置的深度，点击返回，不创建新页面的节点（点击返回不会出现新的页面，如有则会出现问题）
                if (newPageNode == null){
                    executeBack();
                    continue;
                }
                AllPageNodes.add(newPageNode);
                //情况1.2：页面没有可执行元素，跳过遍历，不加入任务栈中，并触发返回操作
                if (newPageNode.getNodeStatus() == NodeStatus.SKIP){
                    executeBack();
                    continue;
                }
                //情况1.3：页面有可执行元素，将页面对象添加到任务栈，更新成第一个出栈
                taskPageStack.push(newPageNode);
            }

            /* ====================== 执行页面元素节点操作 ====================== */
            currentPageNode = taskPageStack.pop();
            currentElementStack = currentPageNode.getStackElementNodes();
            if (currentElementStack.isEmpty()){
                //页面没有可执行元素，更新页面节点遍历状态
                currentPageNode.setNodeStatus(NodeStatus.END);
                continue;
            }
            //获取元素节点，并执行操作
            currentElementNode = currentElementStack.pop();
            boolean flag = executeAction(currentElementNode);
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
            for (ElementNode existElementNode : oldPageAllNode){
                if (newPageAllNode.get(i).getXpath().equals(existElementNode.getXpath())){
                    break;
                }
                if ( i >=(nodeLength-1) && !newPageAllNode.get(i).getXpath().equals(existElementNode.getXpath())){
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
     * 获取当前窗口在历史集合中的信息
     * @param pageUrl   当前页面URL
     * @return
     */
    private PageNode getExistPageForAllPageNodes(String pageUrl){
        for (PageNode pageNode: AllPageNodes){
            if (pageNode.getUrl().equals(pageUrl)){
                return pageNode;
            }
        }
        return null;
    }

    /**
     * 初始化进入遍历步骤
     */
    public void initStartPage(){
        List<Trigger> triggers = config.getStartPageAndroidStep();
        triggers.stream().forEach(p -> {
            WebElement element = driver.findElementByXPath(p.getXpath());
            switch (p.getAction()){
                case "click":
                    element.click();
                    break;
                case "input":
                    element.clear();
                    element.sendKeys(p.getValue());
                    break;
                default:
                    Log.logError("操作类型错误!",null);
            }
        });
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
        CommonUtil.waitSleep(2);
        triggers.stream().forEach(p ->{
            try {
                Element element = CommonUtil.getElementByXpath(pageSource,p.getXpath());
                if (element != null){
                    Log.logInfo("触发器预处理——>" + p.getXpath());
                    driver.findElementByXPath(p.getXpath()).click();
                }
            } catch (DocumentException e) {
                Log.logError("特殊处理操作失败!",e);
            }
        });
    }

    /**
     * 点击返回键操作
     */
    public void executeBack(){
        AndroidDriver androidDriver = (AndroidDriver) driver;
        androidDriver.pressKey(new KeyEvent().withKey(AndroidKey.BACK));
    }

    /**
     * 执行元素操作，判断当前窗口任务处理完毕,并返回当前窗口
     * @param elementNode   元素节点
     */
    public boolean executeAction(ElementNode elementNode){
        boolean flag = false;
        try {
            ActionEnum actionEnum = elementNode.getAction();
            // 统一根据xpth查找元素
            WebElement element = driver.findElementByXPath(elementNode.getXpath());
            //截图并异步处理高亮
            String screenShot = CommonUtil.captureScreenShot(driver,config);
            if (element != null){
                new AsynTask().executeTask(screenShot,elementNode);
            }
            elementNode.setScreenShotPath(screenShot);
            switch (actionEnum) {
                case CLICK:
                    element.click();
                    break;
                case INPUT:
                    element.clear();
                    //  随机取配置文件inputList中内容输入，无内容则默认输入"test"
                    List<String> inputList = config.getInputTestList();
                    if (inputList.isEmpty()){
                        element.sendKeys("test");
                    }else {
                        Random random = new Random();
                        int n = random.nextInt(inputList.size());
                        element.sendKeys(inputList.get(n));
                    }
                    break;
            }
            flag = true;
        }catch (NoSuchElementException e1){
            Log.logError("节点任务 -> [info = " + elementNode.getXpath() + "], NoSuchElementException, 弹出下一个节点任务", e1);
        }catch (Exception e){
            Log.logError("节点任务 -> [info = " + elementNode.getXpath() + "], 发生未知异常, 弹出下一个节点任务", e);
        }
        return flag;
    }

    /**
     * 检查退出遍历条件
     * @return
     */
    private boolean isExit(){
        String currentPackage = getCurrentAppName(driver.getPageSource());
        return isValidPackage(currentPackage)?false:true;
    }

    private boolean isValidPackage(String currentPackage){
        boolean flag = false;
        // 判断是否是当前app
        if (currentPackage.contains(config.getAppPackage())){
            flag = true;
            return flag;
        }
        // 判断包名是否有效
        for(String packageName: config.getAndroidValidPackageList()){
            if (currentPackage != null && currentPackage.contains(packageName)){
                flag = true;
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

    public void drawXmind(){
        AllPageNodes.stream().filter(p -> p.getDepth() == 1).forEach(p -> createXmindNode(p));
        mindUtil.closeMind();
    }

    public void createXmindNode(PageNode node){
        for (ElementNode elementNode : node.getAllElementNodes()){
            if (elementNode.getNodeStatus() == NodeStatus.FAIL)    {
                mindUtil.createFailNode("//*[@TEXT='Start']",elementNode.getPageUrl()+"_"+elementNode.getXpath());
            }else {
                mindUtil.createSuccessNode("//*[@TEXT='Start']",elementNode.getPageUrl()+"_"+elementNode.getXpath());
            }
        }
    }
}
