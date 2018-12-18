package com.ui.auto;

import com.ui.entity.ActionEnum;
import com.ui.entity.ElementNode;
import com.ui.util.CommonUtil;
import io.appium.java_client.AppiumDriver;
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
    protected void before(ActionEnum actionEnum, ElementNode elementNode) {
        String screenShot = CommonUtil.captureScreenShot(driver,config);
        ExtentReportManager.createScreenShotLog(elementNode.getPageUrl()+" 页面的【" + elementNode.getXpath()+"】节点执行："+actionEnum.getDescription(),screenShot);
        new AsynTask().executeTask(screenShot,elementNode);
        elementNode.setScreenShotPath(screenShot);
    }

    @Override
    protected void triggerBefore(ActionEnum actionEnum, WebElement element, String inputText) {
        ExtentReportManager.createSuccessLog("特殊处理【" + element.getAttribute("xpath")+"】节点执行："+actionEnum.getDescription());
    }

    @Override
    protected void after(ActionEnum actionEnum, ElementNode elementNode) {

    }

    @Override
    protected void triggerAfter(ActionEnum actionEnum, WebElement element, String inputText) {

    }

    @Override
    protected void afterToThrowable(ActionEnum actionEnum, ElementNode elementNode, Throwable e) {
        // TODO 未找到元素 或 出现异常截图
        ExtentReportManager.createFailLog(elementNode.getPageUrl()+" 页面的【" + elementNode.getXpath()+"】节点执行："+actionEnum.getDescription()+" 发生错误, 继续弹出下一个节点任务!",e);
    }
}
