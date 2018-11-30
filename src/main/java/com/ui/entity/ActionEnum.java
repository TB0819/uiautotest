package com.ui.entity;

/**
 * 页面元素执行动作类型
 * Created by cz on 2018-11-26.
 * @author cz
 */
public enum ActionEnum {

    CLICK("click",1),
    INPUT("input",2),
    BACK("back",3) ;

    private String description;
    private int value;

    ActionEnum(String description, int value){
        this.description =description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
