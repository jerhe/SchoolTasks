package com.edu.schooltask.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ImageAdapter;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.DeleteImageEvent;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.TextUtil;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.orhanobut.dialogplus.DialogPlus;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.Content;
import com.edu.schooltask.view.InputText;

import net.bither.util.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import server.api.SchoolTask;
import server.api.task.release.ReleaseTaskEvent;

public class ReleaseTaskActivity extends BaseActivity {
    private String moneyReg = "^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$";
    private static final int SELECT_IMAGE_CODE = 0;

    private InputText schoolText;
    private MaterialSpinner desText;
    private Content contentText;
    private InputText costText;
    private InputText limitTimeText;

    private RecyclerView imageRecyclerView;
    ImageAdapter adapter;
    List<ImageItem> imageItems = new ArrayList<>();

    ProgressDialog progressDialog;

    DialogPlus payDialog;


    List<String> tempPaths = new ArrayList<>();
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
        setContentView(R.layout.activity_release_task);
        EventBus.getDefault().register(this);
        schoolText = (InputText) findViewById(R.id.rt_school);
        desText = (MaterialSpinner) findViewById(R.id.rt_des);
        contentText = (Content) findViewById(R.id.rt_content);
        costText = (InputText) findViewById(R.id.rt_cost);
        limitTimeText = (InputText) findViewById(R.id.rt_limit_time);
        imageRecyclerView = (RecyclerView) findViewById(R.id.rt_irv);
        imageRecyclerView.setLayoutManager(new GridLayoutManager(this,5));
        adapter = new ImageAdapter(R.layout.item_image, imageItems);
        imageRecyclerView.setAdapter(adapter);
        imageItems.add(new ImageItem(0));
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ImageItem imageItem = imageItems.get(position);
                switch (imageItem.getType()){
                    case 0:
                        multiSelect();
                        break;
                    case 1:
                        Intent intent = new Intent(ReleaseTaskActivity.this, ImageActivity.class);
                        intent.putExtra("editable", true);
                        intent.putExtra("index", position);
                        intent.putExtra("images", (Serializable) imageItems);
                        startActivity(intent);
                        break;
                }
            }
        });

        schoolText.setInputFilter(1);
        costText.setInputFilter(4);
        limitTimeText.setInputFilter(5);

        schoolText.setText(mDataCache.getUser().getSchool());

        desText.setItems("请选择任务类型","学习","生活","娱乐","运动","商家","其他");

        costText.requestFocus();   //设置标题为默认焦点


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.release_task,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        release();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (payDialog != null){
            if(payDialog.isShowing()){
                payDialog.dismiss();
                EventBus.getDefault().post(new ReleaseTaskEvent("取消支付"));
            }
            else{
                finish();
            }
        }
        else{
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReleaseTask(ReleaseTaskEvent event){
        if(progressDialog != null)
            if(progressDialog.isShowing()) progressDialog.dismiss();
        for(String path : tempPaths){
            NativeUtil.deleteBitmap(path);
        }
        tempPaths.clear();
        if(event.isOk()){
            toastShort("发布成功");
            finish();
        }
        else{
            toastShort(event.getError());
            if(event.getCode() == 4){
                //TODO 跳转到充值页面
            }
            if(event.getCode() == 3){
                openActivity(SetPayPwdActivity.class);
            }
        }
    }

    private void release(){
        final String school = schoolText.getText();
        final String description = desText.getText().toString();
        final String content = contentText.getText();
        final String cost = costText.getText();
        final String limitTime = limitTimeText.getText();

        if(school.length() == 0){
            toastShort("请输入学校");
            return;
        }

        if("请选择任务类型".equals(description)){
            toastShort("请选择任务类型");
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
        Pattern pattern = Pattern.compile(moneyReg);
        Matcher matcher = pattern.matcher(cost);
        if(!matcher.matches()){
            toastShort("金额错误，请重新输入");
            return;
        }
        final BigDecimal money = new BigDecimal(cost);
        if(money.compareTo(new BigDecimal(1)) == -1){
            toastShort("最小金额为1元，请重新输入");
            costText.clean();
            return;
        }
        if(money.compareTo(new BigDecimal(10000)) == 1){
            toastShort("最大金额为10000元，请重新输入");
            return;
        }
        final int time = Integer.parseInt(limitTime);
        if(time == 0 || time >= 7 * 24){
            toastShort("时限错误,请重新输入");
            limitTimeText.clean();
            return;
        }
        final User user = mDataCache.getUser();
        if(user != null){
            //压缩图片
            for(int i=0; i<imageItems.size()-1; i++){
                ImageItem imageItem = imageItems.get(i);
                String path = imageItem.getPath();
                int pointIndex = path.lastIndexOf(".");
                String tempPath = path.substring(0,pointIndex) + "_temp" + path.substring(pointIndex);
                NativeUtil.compressBitmap(path, tempPath);
                tempPaths.add(tempPath);
            }
            //支付密码
            payDialog = DialogUtil.createPayDialog(this, new DialogUtil.OnPayListener() {
                @Override
                public void onPay(String pwd) {
                    KeyBoardUtil.hideKeyBoard(ReleaseTaskActivity.this);
                    progressDialog = ProgressDialog.show(ReleaseTaskActivity.this, "", "发布中...", true, false);
                    Map<String,File> images = new LinkedHashMap<>();
                    for(String path : tempPaths){
                        images.put(path,new File(path));
                    }
                    SchoolTask.releaseTask(school, description, content, money, time, TextUtil.getMD5(pwd), images);
                }
            },cost,new ReleaseTaskEvent());
            payDialog.show();
        }
    }

    public void multiSelect() {
        int imageCount = 9 - imageItems.size() + 1;
        if(imageCount == 0){
            toastShort("最多添加9张图片");
            return;
        }
        config = new ImgSelConfig.Builder(this, loader)
                // 是否多选, 默认true
                .multiSelect(true)
                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .rememberSelected(false)
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
                .maxNum(imageCount)
                .build();
        // 跳转到图片选择器
        ImgSelActivity.startActivity(this, config, SELECT_IMAGE_CODE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteImage(DeleteImageEvent event){
        imageItems.remove(event.getIndex());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            List<String> paths = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            imageItems.remove(imageItems.size()-1);
            for(String path : paths){
                imageItems.add(new ImageItem(1,path));
            }
            imageItems.add(new ImageItem(0));
            adapter.notifyDataSetChanged();
        }
    }
}
