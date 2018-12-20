package com.ui.util;

import com.ui.auto.ExtentReportManager;
import com.ui.auto.InitConfig;
import com.ui.entity.ComConstant;
import com.ui.entity.Config;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * 工具类
 * @author cz
 */
public class CommonUtil {
    private static final String DataFormat = "yyyy-MM-dd_HH_mm_ss";
    private static Config config  = InitConfig.getInstance().config;

    /**
     * 关闭appium服务以及关闭报告
     * @param driver
     */
    public static void exitCrawler(AppiumDriver driver){
        ExtentReportManager.getExtentReports().flush();
        driver.quit();
    }

    /**
     * 获取当前页面XML
     * @param driver    Appium服务驱动
     * @return
     */
    public static Element refreshPageDocument(AppiumDriver driver){
        Element rootElement = null;
        // 获取手机当前页面XML信息
        CommonUtil.waitSleep(config.getWaitSec());
        String pageDom = driver.getPageSource();
        for (int i=0;i<3;i++){
            try {
                Document document = DocumentHelper.parseText(pageDom);
                rootElement = document.getRootElement();
            } catch (DocumentException e) {
                if (i < 2){
                    CommonUtil.waitSleep(2);
                    Log.logInfo(String.format("第【%d】次解析失败",i));
                    continue;
                }
            }
        }
        return rootElement;
    }

    /**
     * 根据xPath 查找单个元素
     * @param xml       当前页面xml
     * @param xPath     指定查找Xpath
     * @return          返回查找的元素
     * @throws DocumentException
     */
    public static Element getElementByXpath(String xml,String xPath) throws DocumentException {
        CommonUtil.waitSleep(2);
        Document document = DocumentHelper.parseText(xml);
        Element rootElement = document.getRootElement();
        Element singleNode = (Element) rootElement.selectSingleNode(xPath);
        return singleNode;
    }

    /**
     * 加载当前窗口页面的URL
     * @param driver        Appium服务驱动
     * @param rootElement   当前页面xml
     * @return  以字符串形式返回页面URL
     */
    public static String getPageUrl(AppiumDriver driver,Element rootElement){
        String pageUrl = "";
        AndroidDriver androidDriver = (AndroidDriver) driver;
        try {
            Element titleNode = (Element) rootElement.selectSingleNode(ComConstant.Message_Url);
            if (titleNode == null) {
                titleNode = (Element) rootElement.selectSingleNode(ComConstant.T_Title_Url);
            }
            if (titleNode == null) {
                titleNode = (Element) rootElement.selectSingleNode(ComConstant.T_Title_Help);
            }
            if (titleNode != null) {
                pageUrl = androidDriver.currentActivity() + titleNode.attributeValue(ComConstant.TEXT);
            } else {
                pageUrl = androidDriver.currentActivity();
            }
        }catch (Exception e){
            pageUrl = androidDriver.currentActivity();
        }
        return pageUrl;
    }

    /**
     * 获取当前时间戳，格式：字符串
     * @return  返回当前时间戳
     */
    public static String getCurrentTime() {
        Calendar now=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat(DataFormat);
        return sdf.format(now.getTimeInMillis());
    }

    /**
     * 时间等待
     * @param seconds   指定时长(秒)
     */
    public static void waitSleep(long seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Log.logError(String.format("线程等待【%d】秒失败",seconds),e);
        }
    }

    /**
     * 截图
     * @param driver    Appium服务驱动
     * @return  返回截图路径
     */
    public static String captureScreenShot(AppiumDriver driver,Config config){
        String capturedScreen;
        String screenPath = config.getScreenshotPath();
        if (!screenPath.isEmpty() && screenPath != null){
            capturedScreen = screenPath + ComConstant.DEFAULT_SCREENSHOT_ANDROID_PATH + "/"+ getCurrentTime()+ ".png";
        }else {
            capturedScreen = ComConstant.DEFAULT_SCREENSHOT_PATH + ComConstant.DEFAULT_SCREENSHOT_ANDROID_PATH + getCurrentTime()+ ".png";
        }
        try {
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(capturedScreen));
        } catch (IOException e) {
            Log.logError("截图失败——>",e);
        }
        return capturedScreen;
    }
}