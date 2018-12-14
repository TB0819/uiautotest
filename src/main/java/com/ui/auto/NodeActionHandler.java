package com.ui.auto;

import com.ui.entity.ActionEnum;
import com.ui.entity.ElementNode;
import com.ui.util.CommonUtil;
import com.ui.util.Log;
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
        WebElement element = driver.findElementByXPath(elementNode.getXpath());
        String screenShot = CommonUtil.captureScreenShot(driver,config);
        if (element != null){
            new AsynTask().executeTask(screenShot,elementNode);
        }
        elementNode.setScreenShotPath(screenShot);
    }

    @Override
    protected void after(ActionEnum actionEnum, ElementNode elementNode) {

    }

    @Override
    protected void afterToThrowable(ActionEnum actionEnum, ElementNode elementNode, Throwable e) {
        Log.logError("节点任务 -> [info = " + elementNode.getXpath() + "], 执行发生错误, 继续弹出下一个节点任务!", e);
    }
}
