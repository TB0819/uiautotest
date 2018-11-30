package com.ui.entity;

/**
 * 常量类
 */
public class ComConstant {
    /*============================================= 元素 =============================================*/
    public static final String TEXT = "text";
    public static final String CLASS = "class";
    public static final String CONTENT_DESC = "content-desc";
    public static final String RESOURCE_ID = "resource-id";
    public static final String BOUNDS = "bounds";

    public static final String EDIT_TEXT = "android.widget.EditText";

    public static final String Message_Url = "//*[@resource-id='zmsoft.rest.supply:id/message']";
    public static final String T_Title_Url = "//*[@resource-id='zmsoft.rest.supply:id/t_title']";
    public static final String T_Title_Help = "//*[@resource-id='zmsoft.rest.supply:id/t_title_help']";
    public static final String ANDROID_APPNAME_XPATH = "(//*[@package!=''])[1]";
    public static final String IOS_APPNAME_XPATH = "//*[contains(@type,\"Application\")]";
    public static final String APP_PACKAGE = "package";

    /*============================================= 路径 =============================================*/
    public static final String CONFIG_PATH = System.getProperty("user.dir") + "/src/main/resources/config.yml";
    public static final String DEFAULT_SCREENSHOT_PATH = System.getProperty("user.dir") + "/target/screenshot";
    public static final String DEFAULT_SCREENSHOT_ANDROID_PATH = "/android";
    public static final String DEFAULT_SCREENSHOT_IOS_PATH = "/ios";
}
