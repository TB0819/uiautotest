package com.ui.auto;

import com.ui.entity.ActionEnum;
import com.ui.entity.Config;
import com.ui.entity.ElementNode;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;

/**
 * TODO 后续改成接口动态代理方式提供可修改
 * 页面元素操作抽象类
 */
public abstract class NodeAction {
    private AppiumDriver driver;
    protected Config config  = InitConfig.getInstance().config;

    public NodeAction(AppiumDriver driver) {
        this.driver = driver;
    }

    /**
     * 元素节点操作之前处理
     * @param actionEnum    操作类型
     * @param elementNode   元素节点
     */
    protected abstract void before(ActionEnum actionEnum, ElementNode elementNode);

    /**
     * 执行特殊处理或进入测试步骤之前处理
     * @param actionEnum
     * @param element
     * @param inputText
     */
    protected abstract void triggerBefore(ActionEnum actionEnum,WebElement element, String inputText);

    /**
     * 元素节点操作之后处理
     * @param actionEnum    操作类型
     * @param elementNode   元素节点
     */
    protected abstract void after(ActionEnum actionEnum, ElementNode elementNode);

    /**
     * 执行特殊处理或进入测试步骤之后处理
     * @param actionEnum
     * @param element
     * @param inputText
     */
    protected abstract void triggerAfter(ActionEnum actionEnum,WebElement element, String inputText);

    /**
     * 元素节点操作出现异常之后处理
     * @param actionEnum    操作类型
     * @param elementNode   元素节点
     * @param e             异常
     */
    protected abstract void afterToThrowable(ActionEnum actionEnum, ElementNode elementNode, Throwable e);

    /**
     * 执行页面元素节点操作
     * @param actionEnum    操作类型
     * @param elementNode   元素节点
     * @return
     */
    public boolean runAction(ActionEnum actionEnum, ElementNode elementNode){
        boolean flag = false;
        WebElement element = null;
        try {
            if (elementNode != null){
                element = driver.findElementByXPath(elementNode.getXpath());
                //  执行前处理
                this.before(actionEnum,elementNode);
            }
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
                    String inputText = getInputText();
                    element.sendKeys(inputText);
                    break;
                default: break;
            }
            flag = true;
        }catch (NoSuchElementException e1){
            this.afterToThrowable(actionEnum,elementNode,e1);
        }catch (Exception e){
            this.afterToThrowable(actionEnum,elementNode,e);
        }
        //  执行后处理
        this.after(actionEnum,elementNode);
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
