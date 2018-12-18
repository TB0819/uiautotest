package com.ui.auto;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.gherkin.model.Feature;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Protocol;
import com.aventstack.extentreports.reporter.configuration.Theme;
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

    public static void createScreenShotLog(String message,String screenPath){
        try {
            parentsNode.log(Status.PASS,message, MediaEntityBuilder.createScreenCaptureFromPath(screenPath).build());
        } catch (IOException e) {
            Log.logError("报告生成失败",e);
        }
    }

    public static void createFailLog(String message,Throwable e){
        parentsNode.log(Status.FAIL,message+ "<br>" +e);
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
