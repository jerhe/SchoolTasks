package com.edu.schooltask.item;


import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class IconMenuItem implements MultiItemEntity{
    public static int EMPTY = 0;
    public static int HORIZONTAL = 1;
    public static int VERTICAL = 2;
    public static int SWITCH = 3;
    public static int VALUE = 4;
    public static int TITLE = 5;

    private int itemType;
    private int resId;
    private String name;
    private String hint;
    private String value;
    private boolean checked;

    //空项，作为分割条
    public IconMenuItem(){
        this.itemType = EMPTY;
    }

    //标题
    public IconMenuItem(String name){
        this.itemType = TITLE;
        this.name = name;
    }

    //水平|竖直|Switch|Value
    public IconMenuItem(int itemType,int resId, String name){
        this.itemType = itemType;
        this.resId = resId;
        this.name = name;
    }

    //水平|Switch|Value
    public IconMenuItem(int itemType,int resId, String name, String hint){
        this.itemType = itemType;
        this.resId = resId;
        this.name = name;
        this.hint = hint;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
