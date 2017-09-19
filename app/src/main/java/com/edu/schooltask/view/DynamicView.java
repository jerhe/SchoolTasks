package com.edu.schooltask.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.activity.DynamicActivity;
import com.edu.schooltask.adapter.NineAdapter;
import com.edu.schooltask.beans.dynamic.Dynamic;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.view.useritem.UserItemDynamicView;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;

import java.util.ArrayList;
import java.util.List;

import server.api.SchoolTask;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class DynamicView extends LinearLayout{
    private UserItemDynamicView userItemDynamicView;
    private TextView contentText;
    private NineGridView nineGridView;
    private TextView locationText;

    public DynamicView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_dynamic, this);

        userItemDynamicView = (UserItemDynamicView) findViewById(R.id.dynamic_uidv);
        contentText = (TextView) findViewById(R.id.dynamic_content);
        nineGridView = (NineGridView) findViewById(R.id.dynamic_image);
        locationText = (TextView) findViewById(R.id.dynamic_location);
    }

    public void setAll(final Dynamic dynamic, boolean isInfo){
        userItemDynamicView.setAll(dynamic);
        String content = dynamic.getContent();
        if(StringUtil.isEmpty(content)) contentText.setVisibility(GONE);
        else {
            contentText.setVisibility(VISIBLE);
            contentText.setText(dynamic.getContent());
        }
        setLocation(dynamic.getLocation());
        //图片
        int imageNum = dynamic.getImageNum();
        if(imageNum == 0){
            nineGridView.setVisibility(GONE);
        }
        else{
            nineGridView.setVisibility(VISIBLE);
            List<ImageInfo> imageInfos = new ArrayList<>();
            for(int i = 0; i < imageNum; i++){
                ImageInfo imageInfo = new ImageInfo();
                String url = SchoolTask.DYNAMIC_IMAGE + dynamic.getImageUrl() + "/" + i + ".png";
                imageInfo.setThumbnailUrl(url);
                imageInfo.setBigImageUrl(url);
                imageInfos.add(imageInfo);
            }
            nineGridView.setAdapter(new NineAdapter(getContext(), imageInfos));
        }
        if(!isInfo) setLengthLimit();
    }

    public void setLocation(String location){
        if(StringUtil.isEmpty(location)) locationText.setVisibility(GONE);
        else {
            locationText.setVisibility(VISIBLE);
            locationText.setText(location);
        }
    }

    public void setLengthLimit(){
        contentText.setEllipsize(TextUtils.TruncateAt.END);
        contentText.setMaxLines(5);
    }

    public String getContent(){
        return contentText.getText().toString();
    }
}
