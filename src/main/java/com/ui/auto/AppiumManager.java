package com.ui.auto;

import com.ui.entity.Config;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

/**
 * 启动AppiumDriver 服务管理类
 */
public class AppiumManager {
    private Config config = InitConfig.getInstance().config;
    private AppiumDriverLocalService Service;
    private AppiumDriver driver;

    /**
     * 启动android服务
     * @return
     */
    public AppiumDriver driverForAndroid(){
        ExtentReportManager.createSuccessLog("开始启动Appium服务");
        AppiumServiceBuilder builder =  new AppiumServiceBuilder().withAppiumJS(new File(config.getAppiumJsPath()));
        Service = builder.build();
        Service.start();
        // 设置设备属性
        DesiredCapabilities androidCapabilities = new DesiredCapabilities();
        androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        androidCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, config.getDeviceName());
        androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, config.getMobileVersion());
        androidCapabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, config.getAppActivity());
        androidCapabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, config.getAppPackage());
        androidCapabilities.setCapability(AndroidMobileCapabilityType.UNICODE_KEYBOARD, true);
        androidCapabilities.setCapability(AndroidMobileCapabilityType.RESET_KEYBOARD, true);
        androidCapabilities.setCapability(MobileCapabilityType.NO_RESET, true);
        androidCapabilities.setCapability(MobileCapabilityType.UDID, config.getUdid());
        driver = new AppiumDriver(Service.getUrl(), androidCapabilities);
        return driver;
    }

    /**
     * 获取手机分辨率
     */
    public void getDeviceWindowSize(){
        config.setDeviceHeight(driver.manage().window().getSize().getHeight());
        config.setDeviceWidth(driver.manage().window().getSize().getWidth());
    }
}
