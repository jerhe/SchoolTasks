package com.edu.schooltask.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.ReleaseEvent;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import java.util.ArrayList;
import java.util.List;

import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.view.Content;
import com.edu.schooltask.view.ImageDisplayLoader;
import com.edu.schooltask.view.InputText;

import net.bither.util.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ReleaseActivity extends BaseActivity {
    private static final int REQUEST_CODE = 0;

    private InputText schoolText;
    private Content contentText;
    private InputText costText;
    private InputText limitTimeText;
    private Button releaseBtn;
    private ImageDisplayLoader imageDisplayLoader;

    ProgressDialog progressDialog;

    List<String> paths = new ArrayList<>();
    ImgSelConfig config;
    ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        EventBus.getDefault().register(this);
        schoolText = (InputText) findViewById(R.id.release_school);
        contentText = (Content) findViewById(R.id.release_content);
        costText = (InputText) findViewById(R.id.release_cost);
        limitTimeText = (InputText) findViewById(R.id.release_limit_time);
        releaseBtn = (Button) findViewById(R.id.release_release);
        imageDisplayLoader = (ImageDisplayLoader) findViewById(R.id.release_idl);

        schoolText.setInputFilter(1);
        costText.setInputFilter(4);
        limitTimeText.setInputFilter(5);

        schoolText.setText(mDataCache.getUser().getSchool());

        releaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                release();
            }
        });

        costText.requestFocus();   //设置标题为默认焦点

        imageDisplayLoader.setOnAddClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiSelect();
            }
        });

        imageDisplayLoader.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiSelect();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelease(ReleaseEvent event){
        releaseBtn.setText("发布");
        if(progressDialog.isShowing())progressDialog.dismiss();
        for(String path : paths){
            NativeUtil.deleteBitmap(path);
        }
        if(event.isOk()){
            toastShort("发布成功");
            finish();
        }
        else{
            toastShort(event.getError());
            if(event.getCode() == 1){
                //TODO 跳转到充值页面
            }
        }
    }

    private void release(){
        if(releaseBtn.getText().equals("发布中..."))return;
        final String school = schoolText.getText();
        final String content = contentText.getText();
        final String cost = costText.getText();
        final String limitTime = limitTimeText.getText();

        if(school.length() == 0){
            toastShort("请输入学校");
            return;
        }

        if(content.length() == 0){
            toastShort("请输入内容");
            return;
        }
        if(cost.length() == 0){
            toastShort("请输入金额");
            return;
        }
        if(limitTime.length() == 0){
            toastShort("请输入时限");
            return;
        }
        if(".".equals(cost)){
            toastShort("金额错误，请重新输入");
            costText.clean();
            return;
        }
        final float money = Float.parseFloat(cost);
        if(money < 1){
            toastShort("最小金额为1元，请重新输入");
            costText.clean();
            return;
        }
        final int time = Integer.parseInt(limitTime);
        if(time == 0 || time >= 7 * 24){
            toastShort("时限错误,请重新输入");
            limitTimeText.clean();
            return;
        }
        User user = mDataCache.getUser();
        if(user != null){
            releaseBtn.setText("发布中...");
            //压缩图片
            progressDialog = ProgressDialog.show(this, "", "发布中...", true, false);
            for(int i=0; i<paths.size(); i++){
                String path = paths.get(i);
                int pointIndex = path.lastIndexOf(".");
                String tempPath = path.substring(0,pointIndex) + "_temp" + path.substring(pointIndex);
                NativeUtil.compressBitmap(path, tempPath);
                paths.set(i,tempPath);
            }
            HttpUtil.releaseOrder(user.getToken(), user.getUserId(), school, content, money, time, paths);
        }
    }

    public void multiSelect() {
        config = new ImgSelConfig.Builder(this, loader)
                // 是否多选, 默认true
                .multiSelect(true)
                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .rememberSelected(true)
                // “确定”按钮背景色
                .btnBgColor(Color.WHITE)
                // “确定”按钮文字颜色
                .btnTextColor(Color.parseColor("#1B9DFF"))
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#000000"))
                // 返回图标ResId
                .backResId(R.drawable.ic_action_back)
                // 标题
                .title("选择图片")
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(Color.parseColor("#1B9DFF"))
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(3)
                .build();
        // 跳转到图片选择器
        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            paths = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            imageDisplayLoader.loadImages(paths);
        }
    }
}
