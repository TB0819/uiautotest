package com.ui;

import com.ui.auto.AppiumManager;
import com.ui.auto.Crawler;
import com.ui.auto.InitConfig;
import com.ui.entity.Config;
import io.appium.java_client.AppiumDriver;

/**
 * 启动类
 */
public class StartMain {

    public static void main(String[] args){
        // 初始化配置文件,加载失败则终止运行
        InitConfig initConfig = InitConfig.getInstance();
        if (initConfig.getInitStatus()){
            System.exit(0);
        }
        // 启动服务
        AppiumManager manager =  new AppiumManager();
        AppiumDriver driver = manager.driverForAndroid();
        //进入遍历
        Crawler crawler = new Crawler(driver);
        // 执行进入遍历页面步骤
        crawler.initStartPage();
        // 遍历开始
        crawler.crawl();
    }
}
