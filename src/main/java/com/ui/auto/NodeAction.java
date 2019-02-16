package com.ui.auto;

import com.ui.entity.ActionEnum;
import com.ui.entity.Config;
import com.ui.entity.ElementInfo;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;

/**
 * 页面元素操作抽象类
 */
public abstract class NodeAction {
    private AppiumDriver driver;
    protected Config config  = InitializeConfiguration.getInstance().config;

    public NodeAction(AppiumDriver driver) {
        this.driver = driver;
    }

    /**
     * 元素节点操作之前处理
     * @param pageUrl       当前所在页面
     * @param actionEnum    操作类型
     * @param elementInfo   元素节点
     */
    protected abstract String before(String pageUrl, ActionEnum actionEnum, ElementInfo elementInfo);

    /**
     * 执行特殊处理或进入测试步骤之前处理
     * @param actionEnum
     * @param element
     * @param inputText
     */
    protected abstract void triggerBefore(ActionEnum actionEnum,WebElement element, String inputText);

    /**
     * 元素节点操作之后处理
     * @param pageUrl       当前所在页面
     * @param actionEnum    操作类型
     * @param elementInfo   元素节点
     * @param screenShotPath 截图路径
     */
    protected abstract void after(String pageUrl, ActionEnum actionEnum, ElementInfo elementInfo, String screenShotPath);

    /**
     * 执行特殊处理或进入测试步骤之后处理
     * @param actionEnum
     * @param element
     * @param inputText
     */
    protected abstract void triggerAfter(ActionEnum actionEnum,WebElement element, String inputText);

    /**
     * 元素节点操作出现异常之后处理
     * @param pageUrl       当前所在页面
     * @param screenShotPath 截图路径
     * @param actionEnum    操作类型
     * @param elementInfo   元素节点
     * @param e             异常
     */
    protected abstract void afterToThrowable(String pageUrl, String screenShotPath, ActionEnum actionEnum, ElementInfo elementInfo, Throwable e);

    /**
     * 执行页面元素节点操作
     * @param pageUrl       当前所在页面
     * @param actionEnum    操作类型
     * @param elementInfo   元素节点
     * @return
     */
    public boolean runAction(String pageUrl, ActionEnum actionEnum, ElementInfo elementInfo){
        boolean flag = false;
        WebElement element;
        String screenShotPath ="";
        try {
            screenShotPath = this.before(pageUrl, actionEnum, elementInfo);
            switch (actionEnum){
                case BACK:
                    AndroidDriver androidDriver = (AndroidDriver) driver;
                    androidDriver.pressKey(new KeyEvent().withKey(AndroidKey.BACK));
                    break;
                case CLICK:
                    element = driver.findElementByXPath(elementInfo.getXpath());
                    element.click();
                    break;
                case INPUT:
                    element = driver.findElementByXPath(elementInfo.getXpath());
                    element.clear();
                    String inputText = getInputText();
                    element.sendKeys(inputText);
                    break;
                default: break;
            }
            flag = true;
            this.after(pageUrl, actionEnum, elementInfo,screenShotPath);
        }catch (Exception e){
            this.afterToThrowable(pageUrl,screenShotPath,actionEnum, elementInfo,e);
        }
        return flag;
    }

    /**
     * 执行特殊处理或进入测试步骤
     * @param actionEnum
     * @param element
     * @param inputText
     */
    public void runTriggerAction(ActionEnum actionEnum,WebElement element, String inputText){
        switch (actionEnum){
            case BACK:
                AndroidDriver androidDriver = (AndroidDriver) driver;
                androidDriver.pressKey(new KeyEvent().withKey(AndroidKey.BACK));
                break;
            case CLICK:
                element.click();
                break;
            case INPUT:
                element.clear();
                element.sendKeys(inputText);
                break;
            default: break;
        }
    }

    /**
     * 获取输入框输入的内容：随机取配置文件inputList中内容输入，无内容则默认输入"test"
     * @return
     */
    private String getInputText(){
        List<String> inputList = config.getInputTestList();
        if (inputList.isEmpty()){
            return "test";
        }
        Random random = new Random();
        int n = random.nextInt(inputList.size());
        return inputList.get(n);
    }
}
