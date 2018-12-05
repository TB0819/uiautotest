package com.ui.entity;

import java.util.List;

/**
 * 配置类，读取yml配置文件信息
 * Created by cz on 2018-11-26.
 * @author cz
 */
public class Config {
    /**
     * pageUrl：        页面唯一标识定位
     * maxDepth：       遍历最大深度
     * waitSec：        操作元素等待时间
     * inputTestList： 输入框输入内容，随机取值
     * firstList：       最新遍历集合
     * selectedList：    中序遍历集合
     * lastList：        最后遍历集合
     * blackList：       页面元素黑名单
     * whiteList：       页面元素白名单
     * triggerList：     特殊处理机制
     * startPageAndroidStep：    启动步骤
     * pressBackPackageList：    当发现App跳转到以下app时 会触发back键
     * AndroidValidPackageList： 除了APP本身的包名外 根据以下包名判断是否跳出了APP,当app跳转到以下app时被认为是合法，会继续遍历操作
     * IosValidPackageList：     除了APP本身的包名外 根据以下包名判断是否跳出了APP,当app跳转到以下app时被认为是合法，会继续遍历操作
     */
    private String pageUrl;
    private Integer maxDepth;
    private Integer waitSec;
    private List<String> inputTestList;
    private List<String> firstList;
    private List<String> selectedList;
    private List<String> lastList;
    private List<String> blackList;
    private List<String> whiteList;
    private List<Trigger> triggerList;
    private List<Trigger> startPageAndroidStep;
    private String screenshotPath;
    private List<String> pressBackPackageList;
    private List<String> androidValidPackageList;
    private List<String> iosValidPackageList;

    /**
     * driver信息
     */
    private String appiumJsPath;
    private String mobileVersion;
    private String appActivity;
    private String appPackage;
    private String udid;
    private String deviceName;
    private int deviceHeight;
    private int deviceWidth;

    public List<Trigger> getStartPageAndroidStep() {
        return startPageAndroidStep;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setStartPageAndroidStep(List<Trigger> startPageAndroidStep) {
        this.startPageAndroidStep = startPageAndroidStep;
    }

    public int getDeviceHeight() {
        return deviceHeight;
    }

    public void setDeviceHeight(int deviceHeight) {
        this.deviceHeight = deviceHeight;
    }

    public int getDeviceWidth() {
        return deviceWidth;
    }

    public void setDeviceWidth(int deviceWidth) {
        this.deviceWidth = deviceWidth;
    }

    public String getAppiumJsPath() {
        return appiumJsPath;
    }

    public void setAppiumJsPath(String appiumJsPath) {
        this.appiumJsPath = appiumJsPath;
    }

    public String getMobileVersion() {
        return mobileVersion;
    }

    public void setMobileVersion(String mobileVersion) {
        this.mobileVersion = mobileVersion;
    }

    public String getAppActivity() {
        return appActivity;
    }

    public void setAppActivity(String appActivity) {
        this.appActivity = appActivity;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public List<String> getPressBackPackageList() {
        return pressBackPackageList;
    }

    public void setPressBackPackageList(List<String> pressBackPackageList) {
        this.pressBackPackageList = pressBackPackageList;
    }

    public List<String> getAndroidValidPackageList() {
        return androidValidPackageList;
    }

    public void setAndroidValidPackageList(List<String> androidValidPackageList) {
        this.androidValidPackageList = androidValidPackageList;
    }

    public List<String> getIosValidPackageList() {
        return iosValidPackageList;
    }

    public void setIosValidPackageList(List<String> iosValidPackageList) {
        this.iosValidPackageList = iosValidPackageList;
    }

    public String getScreenshotPath() {
        return screenshotPath;
    }

    public void setScreenshotPath(String screenshotPath) {
        this.screenshotPath = screenshotPath;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public Integer getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(Integer maxDepth) {
        this.maxDepth = maxDepth;
    }

    public Integer getWaitSec() {
        return waitSec;
    }

    public void setWaitSec(Integer waitSec) {
        this.waitSec = waitSec;
    }

    public List<String> getInputTestList() {
        return inputTestList;
    }

    public void setInputTestList(List<String> inputTestList) {
        this.inputTestList = inputTestList;
    }

    public List<String> getFirstList() {
        return firstList;
    }

    public void setFirstList(List<String> firstList) {
        this.firstList = firstList;
    }

    public List<String> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(List<String> selectedList) {
        this.selectedList = selectedList;
    }

    public List<String> getLastList() {
        return lastList;
    }

    public void setLastList(List<String> lastList) {
        this.lastList = lastList;
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public List<Trigger> getTriggerList() {
        return triggerList;
    }

    public void setTriggerList(List<Trigger> triggerList) {
        this.triggerList = triggerList;
    }
}
