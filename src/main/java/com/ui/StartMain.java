package com.ui;

import com.ui.auto.Crawler;
import com.ui.auto.InitializeConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 启动类
 */
public class StartMain {
    private InitializeConfiguration initializeConfiguration = InitializeConfiguration.getInstance();

    @BeforeClass
    public void beforeClass(){
        initializeConfiguration.init();
    }

    @Test
    public void startRun(){
        if (initializeConfiguration.getInitStatus()){
            return;
        }
        Crawler crawler = new Crawler(initializeConfiguration.getDriver());
        crawler.run();
    }

    @AfterClass
    public void afterClass(){
        initializeConfiguration.stop();
    }
}
