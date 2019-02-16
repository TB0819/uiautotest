package com.ui.auto;

import com.ui.entity.ActionEnum;
import com.ui.entity.ComConstant;
import com.ui.entity.Config;
import com.ui.entity.Trigger;
import com.ui.util.Log;
import com.ui.util.XmindUtil;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * 初始化配置类
 * @author  cz
 */
public class InitializeConfiguration {
	public Config config;
	private boolean status = true;
	private AppiumDriver driver;

	private InitializeConfiguration() {
	}

	public void init(){
		try {
			initializeDefaultConfig();
			status = false;
		}catch (Exception e){
			ExtentReportManager.createFailLog("初始化配置失败",e);
		}
	}

	/**
	 * 初始化默认配置
	 * @throws FileNotFoundException
	 */
	private void initializeDefaultConfig() throws FileNotFoundException {
		initializeExtentReport();
		initializeYmlFile();
		initializeSnapshotFile();
		initializeAppiumDriver();
	}

	/**
	 * 初始化extentReport
	 */
	private void initializeExtentReport(){
		ExtentReportManager.getInstance();
	}

	/**
	 * 初始化Appium driver 服务
	 */
	private void initializeAppiumDriver(){
		AppiumManager manager =  new AppiumManager(this.config);
		this.driver = manager.driverForAndroid();
	}
	/**
	 * 初始加载配置文件
	 */
	private void initializeYmlFile() throws FileNotFoundException {
		Yaml yaml = new Yaml();
		File ymlFile = new File(ComConstant.CONFIG_PATH);
		config = yaml.loadAs(new FileInputStream(ymlFile), Config.class);
		ExtentReportManager.createSuccessLog(String.format("YML配置文件加载成功，路径：%s",ymlFile.getAbsolutePath()));
		setTriggerAction(config);
	}

	private void setAction(List<Trigger> triggerList){
		for (Trigger trigger: triggerList){
			switch (trigger.getAction()){
				case "click":
					trigger.setActionEnum(ActionEnum.CLICK);
					break;
				case "input":
					trigger.setActionEnum(ActionEnum.INPUT);
					break;
				case "back":
					trigger.setActionEnum(ActionEnum.BACK);
					break;
				default:
					Log.logError("操作类型错误!",null);
			}
		}
	}
	private void setTriggerAction(Config config){
		List<Trigger> triggers = new ArrayList<Trigger>();
		triggers.addAll(config.getStartPageAndroidStep());
		triggers.addAll(config.getTriggerList());
		setAction(triggers);
	}

	/**
	 * 关闭appium服务、报告、xmind
	 */
	public void stop(){
		if (driver != null){
			driver.quit();
		}
		ExtentReportManager.getExtentReports().flush();
		XmindUtil.getInstance().saveWorkBook();
	}

	/**
	 * 获取加载配置状态
	 * @return
	 */
	public boolean getInitStatus(){
		return status;
	}

	public AppiumDriver getDriver(){
		return driver;
	}
	/**
	 * 初始创建系统截图目录(Android或者IOS)
	 */
	private void initializeSnapshotFile(){
		String screenshot_path = config.getScreenshotPath();
		File file2, file;

		if (StringUtils.isBlank(screenshot_path)){
			file2 = new File(ComConstant.DEFAULT_SCREENSHOT_PATH);
			file = new File(ComConstant.DEFAULT_SCREENSHOT_PATH + ComConstant.DEFAULT_SCREENSHOT_ANDROID_PATH);
		}else {
			file2 = new File(screenshot_path);
			file = new File(screenshot_path + ComConstant.DEFAULT_SCREENSHOT_ANDROID_PATH);
		}
		//	创建目录
		if (!file2.exists()) {
			file2.mkdir();
		}
		// 创建子目录
		if (!file.exists()) {
			if (!file.mkdir()) {
				ExtentReportManager.createFailLog(String.format("截图目录初始化失败，路径：%s",file.getAbsolutePath()),null);
			}
		}
		config.setScreenshotPath(file2.getAbsolutePath());
		ExtentReportManager.createSuccessLog(String.format("截图目录初始化成功，路径：%s",file.getAbsolutePath()));
	}

	public static InitializeConfiguration getInstance() {
		return InitializeConfiguration.ConfigHolder.instance;
	}

	private static class ConfigHolder {
		private static InitializeConfiguration instance = new InitializeConfiguration();
	}
}
