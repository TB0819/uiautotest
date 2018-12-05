package com.ui.auto;

import com.ui.entity.ComConstant;
import com.ui.entity.Config;
import com.ui.util.Log;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 初始化配置类
 * @author  cz
 */
public class InitConfig {
	public Config config;
	private boolean status;

	private InitConfig() {
		this.status = loadConfig();
	}

	private List<String> stringConvertList(Map map, String type) {
		if(map.get(type) ==null){
			return null;
		}
		String str = String.valueOf(map.get(type));
		str = str.substring(1, str.length() - 1);
		return Arrays.asList(str.split(", "));
	}

	/**
	 * 加载配置文件
	 */
	public boolean loadConfig(){
		boolean flag = false;
		Yaml yaml = new Yaml();
		try {
			config = yaml.loadAs(new FileInputStream(new File(ComConstant.CONFIG_PATH)), Config.class);
			createSnapshotPath();
			flag = false;
		} catch (FileNotFoundException e) {
			flag = true;
			Log.logError("初始化Config配置文件失败",e);
		}
		return flag;
	}

	/**
	 * 获取加载配置文件状态
	 * @return
	 */
	public boolean getInitStatus(){
		return status;
	}
	/**
	 * 创建系统截图目录(Android或者IOS)
	 */
	private void createSnapshotPath(){
		String screenshot_path = config.getScreenshotPath();
		File file2, file;
		if (screenshot_path.isEmpty() || screenshot_path == null){
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
				Log.logInfo("Android Directory is created!");
			} else {
				Log.logInfo("Failed to create directory!");
			}
		}
	}

	public static InitConfig getInstance() {
		return InitConfig.ConfigHolder.instance;
	}

	private static class ConfigHolder {
		private static InitConfig instance = new InitConfig();
	}
}
