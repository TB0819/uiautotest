package com.ui.auto;

import com.ui.entity.ActionEnum;
import com.ui.entity.ComConstant;
import com.ui.entity.Config;
import com.ui.entity.Trigger;
import com.ui.util.Log;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * 初始化配置类
 * 	1、创建报告对象
 * 	2、加载yml配置文件
 * 	3、创建截图路径
 * 	4、创建appium服务
 * @author  cz
 */
public class InitConfig {
	public Config config;
	private boolean status;
	private AppiumDriver driver;

	private InitConfig() {
		ExtentReportManager.getInstance();
		try {
			loadYml();
			createSnapshotPath();
			AppiumManager manager =  new AppiumManager(this.config);
			this.driver = manager.driverForAndroid();
			this.status = false;
		}catch (FileNotFoundException e) {
			this.status = true;
			ExtentReportManager.createFailLog("初始化配置文件失败",e);
		}catch (Exception e){
			this.status = true;
			ExtentReportManager.createFailLog("初始化配置失败",e);
		}
	}

	/**
	 * 加载配置文件
	 */
	public void loadYml() throws FileNotFoundException {
		ExtentReportManager.createSuccessLog("开始加载配置文件");
		Yaml yaml = new Yaml();
		File ymlFile = new File(ComConstant.CONFIG_PATH);
		config = yaml.loadAs(new FileInputStream(ymlFile), Config.class);
		ExtentReportManager.createSuccessLog("YML配置加载成功，路径："+ymlFile.getAbsolutePath());
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
		List<Trigger> startList = config.getStartPageAndroidStep();
		List<Trigger> triggerList = config.getTriggerList();
		setAction(startList);
		setAction(triggerList);
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
	 * 创建系统截图目录(Android或者IOS)
	 */
	private void createSnapshotPath(){
		ExtentReportManager.createSuccessLog("开始创建截图目录");
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
			if (file.mkdir()) {
				ExtentReportManager.createSuccessLog("截图目录创建成功，路径："+file.getAbsolutePath());
			} else {
				ExtentReportManager.createFailLog("截图目录创建成功，路径："+file.getAbsolutePath(),null);
			}
		}
		ExtentReportManager.createSuccessLog("截图目录已存在，路径："+file.getAbsolutePath());
	}

	public static InitConfig getInstance() {
		return InitConfig.ConfigHolder.instance;
	}

	private static class ConfigHolder {
		private static InitConfig instance = new InitConfig();
	}
}
