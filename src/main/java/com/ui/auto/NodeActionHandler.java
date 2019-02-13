package com.ui.auto;

import com.ui.entity.ActionEnum;
import com.ui.entity.ComConstant;
import com.ui.entity.ElementNode;
import com.ui.entity.PageNode;
import com.ui.util.CommonUtil;
import com.ui.util.XmindUtil;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * 页面元素操作具体实现类
 */
public class NodeActionHandler extends NodeAction{
    private AppiumDriver driver;

    public NodeActionHandler(AppiumDriver driver){
        super(driver);
        this.driver = driver;
    }

    @Override
    protected String before(String pageUrl, ActionEnum actionEnum, ElementNode elementNode) {
        //截图
        return CommonUtil.captureScreenShot(driver,config);
    }

    @Override
    protected void triggerBefore(ActionEnum actionEnum, WebElement element, String inputText) {
        ExtentReportManager.createSuccessLog("特殊处理",element.getAttribute("xpath"),actionEnum.getDescription());
    }

    @Override
    protected void after(String pageUrl, ActionEnum actionEnum, ElementNode elementNode, String screenShotPath) {
        if (elementNode == null){
            ExtentReportManager.createSuccessLog(pageUrl,"",actionEnum.getDescription(),screenShotPath);
            return;
        }
        elementNode.setScreenShotPath(screenShotPath);
        String text = "".equals(elementNode.getName()) ? elementNode.getXpath():elementNode.getName();
        //报告
        ExtentReportManager.createSuccessLog(pageUrl,text,actionEnum.getDescription(),screenShotPath);
        //xmind轨迹
        addXmindNode(elementNode, ComConstant.XMIND_SUCCESS);
        //截图圈中
        new AsynTask().executeTask(screenShotPath,elementNode);
    }

    @Override
    protected void triggerAfter(ActionEnum actionEnum, WebElement element, String inputText) {

    }

    @Override
    protected void afterToThrowable(String pageUrl, String screenShotPath, ActionEnum actionEnum, ElementNode elementNode, Throwable e) {
        String text = "".equals(elementNode.getName()) ? elementNode.getXpath():elementNode.getName();
        if (e instanceof NoSuchElementException){
            ExtentReportManager.createFailLog(pageUrl, text,actionEnum.getDescription(), screenShotPath);
        }else {
            ExtentReportManager.createFailLog(pageUrl, text,actionEnum.getDescription(),screenShotPath, e);
        }
        addXmindNode(elementNode, ComConstant.XMIND_FAIL);
    }

    /**
     * 添加xmind节点
     * @param elementNode   当前操作元素节点
     * @param status        执行状态
     */
    private void addXmindNode(ElementNode elementNode, String status) {
        if (elementNode == null) {
            return;
        }
        PageNode currPageNode = Crawler.allPageNodeMaps.get(elementNode.getPageUrl());
        XmindUtil xmindUtil = XmindUtil.getInstance();
        String topicId;
        String xMindStr = StringUtils.isBlank(elementNode.getName()) ? elementNode.getXpath():elementNode.getName();
        if (currPageNode.getParentNode() == null){
            topicId = xmindUtil.createSonNode(xmindUtil.rootTopicId, xMindStr, status);
        } else {
            topicId = xmindUtil.createSonNode(currPageNode.getParentElement().getXmindTopicId(), xMindStr, status);
        }
        elementNode.setXmindTopicId(topicId);
    }
}
