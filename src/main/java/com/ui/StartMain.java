package com.ui;

import com.ui.auto.Crawler;
import com.ui.auto.InitConfig;
import com.ui.util.CommonUtil;
import io.appium.java_client.AppiumDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 启动类
 */
public class StartMain {
    private InitConfig initConfig = InitConfig.getInstance();

    @BeforeClass
    public void beforeClass(){
        initConfig.init();
    }

    @Test
    public void startRun(){
        if (initConfig.getInitStatus()){
            return;
        }
        Crawler crawler = new Crawler(initConfig.getDriver());
        // 执行进入遍历页面步骤
        crawler.initStartPage();
        // 遍历开始
        crawler.crawl();
    }

    @AfterClass
    public void afterClass(){
        initConfig.stop();
    }
}
