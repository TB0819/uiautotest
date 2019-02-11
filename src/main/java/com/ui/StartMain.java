package com.ui;

import com.ui.auto.Crawler;
import com.ui.auto.ExtentReportManager;
import com.ui.auto.InitConfig;

/**
 * 启动类
 */
public class StartMain {

    public static void main(String[] args){
        // 初始化配置文件,失败则终止运行
        InitConfig initConfig = InitConfig.getInstance();
        if (initConfig.getInitStatus()){
            ExtentReportManager.getExtentReports().flush();
            System.exit(0);
        }
        Crawler crawler = new Crawler(initConfig.getDriver());
        // 执行进入遍历页面步骤
        crawler.initStartPage();
        // 遍历开始
        crawler.crawl();
    }
}
