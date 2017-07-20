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

    public IconMenuItem(){
        itemType = EMPTY;
    }

    public IconMenuItem(int itemType,int resId, String name){
        this.itemType = itemType;
        this.resId = resId;
        this.name = name;
    }

    public IconMenuItem(String title){
        this.itemType = TITLE;
        this.name = title;
    }

    public IconMenuItem(int itemType, String name, String hint){
        this.itemType = itemType;
        this.name = name;
        this.hint = hint;
    }

    public IconMenuItem(int itemType,int resId, String name, String hint){
        this.itemType = itemType;
        this.name = name;
        this.hint = hint;
        this.resId = resId;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
