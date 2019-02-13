package com.ui.auto;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.gherkin.model.Feature;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.ui.entity.ComConstant;
import com.ui.util.Log;

import java.io.IOException;

/**
 * 报告管理
 */
public class ExtentReportManager {
    private static ExtentReports extentReports;
    public static ExtentTest parentsNode;

    public static ExtentReports getInstance() {
        if (extentReports == null)
            createInstance(ComConstant.REPORT_PATH);
        return extentReports;
    }

    public static ExtentReports getExtentReports(){
        return extentReports;
    }

    public static void createSuccessLog(String message){
        parentsNode.log(Status.PASS,message);
    }

    public static void createSuccessLog(String pageName,String elementText, String action ,String screenPath){
        try {
            parentsNode.log(Status.PASS,"当前页面："+pageName +"<br> 元素：" + elementText +"<br> 动作："+action, MediaEntityBuilder.createScreenCaptureFromPath(screenPath).build());
        } catch (IOException e) {
            Log.logError("执行动作报告生成失败",e);
        }
    }

    public static void createSuccessLog(String pageName,String elementText, String action){
        parentsNode.log(Status.PASS,"当前页面："+pageName +"<br> 元素：" + elementText +"<br> 动作："+action);
    }

    public static void createFailLog(String message,Throwable e){
        parentsNode.log(Status.FAIL,message+ "<br>" +e);
    }
    public static void createFailLog(String pageName,String elementText, String action, String screenPath, Throwable e){
        try {
            parentsNode.log(Status.FAIL,"执行元素失败, 继续弹出下一个节点任务!<br>当前页面："+pageName +"<br> 元素：" +elementText +"<br> 动作："+action+ "<br>" +e, MediaEntityBuilder.createScreenCaptureFromPath(screenPath).build());
        } catch (IOException e1) {
            Log.logError("执行动作报告生成失败",e1);
        }
    }

    public static void createFailLog(String pageName,String elementText, String action, String screenPath){
        try {
            parentsNode.log(Status.FAIL,"未发现当前元素, 继续弹出下一个节点任务!<br>当前页面："+pageName +"<br> 元素：" +elementText +"<br> 动作："+action, MediaEntityBuilder.createScreenCaptureFromPath(screenPath).build());
        } catch (IOException e) {
            Log.logError("执行动作报告生成失败",e);
        }
    }

    private static ExtentReports createInstance(String fileName) {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(fileName);
        // 报告模板可通过config设置 或xml方式
        htmlReporter.loadXMLConfig(System.getProperty("user.dir") + "/src/main/resources/extent-v3.xml");
//        htmlReporter.config().enableTimeline(true);
//        htmlReporter.config().setAutoCreateRelativePathMedia(true);
//        htmlReporter.config().setCSS("css-string");
//        htmlReporter.config().setDocumentTitle("page title");
//        htmlReporter.config().setEncoding("utf-8");
//        htmlReporter.config().setJS("js-string");
//        htmlReporter.config().setProtocol(Protocol.HTTPS);
//        htmlReporter.config().setReportName("build name");
//        htmlReporter.config().setTheme(Theme.DARK);
//        htmlReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
        extentReports = new ExtentReports();
        extentReports.attachReporter(htmlReporter);
        //  创建父节点
        parentsNode = extentReports.createTest(Feature.class, "UI自动化遍历");
        return extentReports;
    }
}
