package com.edu.schooltask.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ImageAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.TaskUploadKey;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.event.DeleteImageEvent;
import com.edu.schooltask.filter.MoneyFilter;
import com.edu.schooltask.filter.NumberFilter;
import com.edu.schooltask.filter.SchoolFilter;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.other.SchoolAutoComplement;
import com.edu.schooltask.utils.AnimationUtil;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.Content;
import com.edu.schooltask.view.ImageRecyclerView;
import com.edu.schooltask.view.InputText;
import com.edu.schooltask.view.SelectText;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.orhanobut.dialogplus.DialogPlus;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import server.api.SchoolTask;
import server.api.qiniu.GetTaskUploadKeyEvent;
import server.api.qiniu.UploadTaskImageEvent;
import server.api.task.release.ReleaseTaskEvent;

public class ReleaseTaskActivity extends BaseActivity {
    private static final int SELECT_IMAGE_CODE = 0;

    @BindView(R.id.rt_school) InputText schoolText;
    @BindView(R.id.rt_des) SelectText desText;
    @BindView(R.id.rt_cost) InputText costText;
    @BindView(R.id.rt_content) Content contentText;
    @BindView(R.id.rt_limit_time) InputText limitTimeText;
    @BindView(R.id.rt_irv) ImageRecyclerView imageRecyclerView;

    ProgressDialog progressDialog;

    DialogPlus payDialog;


    List<File> tempFiles = new ArrayList<>();
    ImgSelConfig config;
    ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }

    };

    String orderId;
    String school;
    String description;
    String content;
    String cost;
    BigDecimal money;
    String limitTime;
    int time;
    String payPwd;

    boolean uploadResult = true;
    int uploadNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_task);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        imageRecyclerView.addImage(new ImageItem(0));
        imageRecyclerView.setImageClickListener(new ImageRecyclerView.ImageClickListener() {
            @Override
            public void onImageClick(int position, ImageItem imageItem) {
                switch (imageItem.getType()){
                    case 0:
                        multiSelect();
                        break;
                    case 1:
                        Intent intent = new Intent(ReleaseTaskActivity.this, ImageActivity.class);
                        intent.putExtra("editable", true);
                        intent.putExtra("index", position);
                        intent.putExtra("images", (Serializable) imageRecyclerView.getImages());
                        startActivity(intent);
                        break;
                }
            }
        });

        //输入过滤器
        schoolText.setInputFilter(new SchoolFilter());
        costText.setInputFilter(new MoneyFilter());
        limitTimeText.setInputFilter(new NumberFilter());

        schoolText.setText(UserUtil.getLoginUser().getSchool());
        schoolText.getInputText().addTextChangedListener(
                new SchoolAutoComplement(schoolText.getInputText(), mDataCache.getSchool()));

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
        clear();
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
        school = schoolText.getText();
        description = desText.getText();
        content = contentText.getText();
        cost = costText.getText();
        limitTime = limitTimeText.getText();

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
        if(!StringUtil.isMoney(cost)){
            toastShort("金额错误，请重新输入");
            return;
        }
        money = new BigDecimal(cost);
        if(money.compareTo(new BigDecimal(1)) == -1){
            toastShort("最小金额为1元，请重新输入");
            costText.clear();
            return;
        }
        if(money.compareTo(new BigDecimal(10000)) == 1){
            toastShort("最大金额为10000元，请重新输入");
            return;
        }
        time = Integer.parseInt(limitTime);
        if(time == 0 || time >= 7 * 24){
            toastShort("时限错误,请重新输入");
            limitTimeText.clear();
            return;
        }
        if(UserUtil.hasLogin()){
            //压缩图片
            List<ImageItem> imageItems = imageRecyclerView.getImages();
            for(int i=0; i<imageItems.size()-1; i++){
                ImageItem imageItem = imageItems.get(i);
                String path = imageItem.getPath();
                tempFiles.add(new Compressor.Builder(this)
                        .setMaxWidth(1000)
                        .setMaxHeight(800)
                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                        .setQuality(100).build().compressToFile(new File(path)));
            }
            //支付密码
            payDialog = DialogUtil.createPayDialog(this, new DialogUtil.OnPayListener() {
                @Override
                public void onPay(String pwd) {
                    KeyBoardUtil.hideKeyBoard(ReleaseTaskActivity.this);
                    progressDialog = ProgressDialog.show(ReleaseTaskActivity.this, "", "发布中...", true, false);
                    payPwd = StringUtil.getMD5(pwd);
                    if(tempFiles.size() == 0){ //无图片发布
                        SchoolTask.releaseTask("", school, description, content, money, time, payPwd, 0);
                    }
                    else{   //带图片发布
                        SchoolTask.getTaskUploadKey();
                    }
                }
            },cost,new ReleaseTaskEvent());
            payDialog.show();
        }
    }

    public void multiSelect() {
        int imageCount = 9 - imageRecyclerView.getImages().size() + 1;
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
        imageRecyclerView.removeImage(event.getIndex());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUploadKey(GetTaskUploadKeyEvent event){
        if(event.isOk()){
            TaskUploadKey taskUploadKey = GsonUtil.toTaskUploadKey(event.getData());
            orderId = taskUploadKey.getOrderId();
            SchoolTask.uploadTaskImage(taskUploadKey, tempFiles);
        }
        else{
            clear();
            toastShort("发布失败");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadTaskImageEvent(UploadTaskImageEvent event){
        uploadNum ++;
        if(!event.isOk()){
            uploadResult = false;
        }
        if(uploadNum == tempFiles.size()){
            if(uploadResult){
                SchoolTask.releaseTask(orderId, school, description, content, money, time, payPwd, tempFiles.size());
            }
            else{
                clear();
                toastShort("发布失败");
            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            List<String> paths = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            imageRecyclerView.removeLastImage();
            List<ImageItem> imageItems = new ArrayList<>();
            for(String path : paths){
                imageItems.add(new ImageItem(1,path));
            }
            imageRecyclerView.addImages(imageItems);
            imageRecyclerView.addImage(new ImageItem(0));
        }
    }

    private void clear(){
        uploadNum = 0;
        if(progressDialog != null)
            if(progressDialog.isShowing()) progressDialog.dismiss();
        tempFiles.clear();
    }
}
