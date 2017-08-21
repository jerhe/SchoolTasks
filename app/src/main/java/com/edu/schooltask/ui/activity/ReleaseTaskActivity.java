package com.edu.schooltask.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.beans.TaskUploadKey;
import com.edu.schooltask.beans.Voucher;
import com.edu.schooltask.event.DeleteImageEvent;
import com.edu.schooltask.filter.MoneyFilter;
import com.edu.schooltask.filter.NumberFilter;
import com.edu.schooltask.filter.SchoolFilter;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.other.SchoolAutoComplement;
import com.edu.schooltask.utils.DensityUtil;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.ui.view.TaskContentView;
import com.edu.schooltask.ui.view.Inputtextview.InputTextView;
import com.edu.schooltask.ui.view.SelectText;
import com.edu.schooltask.ui.view.recyclerview.ImageRecyclerView;
import com.edu.schooltask.ui.view.recyclerview.VoucherRecyclerView;
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
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import server.api.SchoolTask;
import server.api.qiniu.GetTaskUploadKeyEvent;
import server.api.qiniu.UploadTaskImageEvent;
import server.api.task.release.ReleaseTaskEvent;
import server.api.voucher.GetAvailableVouchersEvent;

public class ReleaseTaskActivity extends BaseActivity {
    private static final int SELECT_IMAGE_CODE = 0;

    @BindView(R.id.rt_school) InputTextView schoolText;
    @BindView(R.id.rt_des) SelectText desText;
    @BindView(R.id.rt_cost) InputTextView costText;
    @BindView(R.id.rt_content)
    TaskContentView taskContentViewText;
    @BindView(R.id.rt_limit_time) InputTextView limitTimeText;
    @BindView(R.id.rt_irv) ImageRecyclerView imageRecyclerView;
    @BindView(R.id.rt_voucher_btn) Button voucherBtn;
    @BindView(R.id.rt_release_btn) Button releaseBtn;
    @BindView(R.id.rt_pay_text) TextView payText;
    @BindView(R.id.rt_vrv) VoucherRecyclerView voucherRecyclerView;
    @BindView(R.id.rt_shadow) View shadowView;

    @OnClick(R.id.rt_release_btn)
    public void releaseTask(){
        release();
    }
    @OnClick(R.id.rt_voucher_btn)
    public void voucher(){
        if(voucherRecyclerView.isShown())  hideVoucherView();
        else showVoucherView();
    }
    @OnClick(R.id.rt_shadow)
    public void shadow(){
       hideVoucherView();
    }

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
    BigDecimal money;   //支付金额
    BigDecimal taskCost;    //任务金额
    String limitTime;
    int time;
    String payPwd;

    Voucher voucher;

    boolean uploadResult = true;
    int uploadNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_task);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        imageRecyclerView.add(new ImageItem(0));
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
                        intent.putExtra("images", (Serializable) imageRecyclerView.get());
                        startActivity(intent);
                        break;
                }
            }
        });
        schoolText.setInputFilter(new SchoolFilter());  //输入过滤器
        costText.setInputFilter(new MoneyFilter());
        limitTimeText.setInputFilter(new NumberFilter());
        schoolText.setText(UserUtil.getLoginUser().getSchool());    //默认学校
        schoolText.getInputText().addTextChangedListener(   //学校自动匹配
                new SchoolAutoComplement(schoolText.getInputText(), mDataCache.getSchool()));
        desText.setItems("请选择任务类型","跑腿","学习","生活","娱乐","其他");
        costText.requestFocus();   //设置标题为默认焦点
        //需支付金额
        costText.getInputText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updatePayText();
            }
        });
        limitTimeText.getInputText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updatePayText();
            }
        });
        //获取代金券
        SchoolTask.getAvailableVouchers();
        voucherRecyclerView.setEmptyView(R.layout.empty_voucher);
        voucherRecyclerView.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                voucher = voucherRecyclerView.get(position);
                updatePayText();
                hideVoucherView();
                voucherBtn.setText(voucher.getMoney().intValue() + "元代金券");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (payDialog != null){
            if(payDialog.isShowing()){
                payDialog.dismiss();
                EventBus.getDefault().post(new ReleaseTaskEvent("取消支付"));
                return;
            }
            else finish();
        }
        if(voucherRecyclerView.isShown()) {
            hideVoucherView();
            return;
        }
        finish();
    }

    private void showVoucherView(){
        shadowView.setVisibility(View.VISIBLE);
        shadowView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        voucherRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideVoucherView(){
        shadowView.setVisibility(View.GONE);
        shadowView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        voucherRecyclerView.setVisibility(View.GONE);
    }

    private void updatePayText(){
        cost = costText.getText();
        limitTime = limitTimeText.getText();
        if(cost.length() == 0 || limitTime.length() == 0) payText.setText("");
        else{
            money = new BigDecimal(cost);
            if(money.compareTo(new BigDecimal(1)) == -1) payText.setText("");
            else{
                money = money.add(new BigDecimal(Integer.parseInt(limitTime) * 0.1))
                        .setScale(2, BigDecimal.ROUND_HALF_DOWN);
                if(voucher != null) money = money.subtract(voucher.getMoney());
                if(money.compareTo(new BigDecimal(0)) == -1) money = new BigDecimal(0);
                payText.setText("¥" + money);
            }
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
            if(event.getCode() == 3){   //未设置支付密码
                openActivity(SetPayPwdActivity.class);
            }
        }
    }

    private void release(){
        school = schoolText.getText();
        description = desText.getText();
        content = taskContentViewText.getText();
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
        taskCost = new BigDecimal(cost);
        if(taskCost.compareTo(new BigDecimal(1)) == -1){
            toastShort("最小金额为1元，请重新输入");
            costText.clear();
            return;
        }
        if(taskCost.compareTo(new BigDecimal(10000)) == 1){
            toastShort("最大金额为10000元，请重新输入");
            return;
        }
        time = Integer.parseInt(limitTime);
        if(time == 0 || time >= 7 * 24){
            toastShort("时限错误,请重新输入");
            limitTimeText.clear();
            return;
        }
        BigDecimal limitTimeMoney = BigDecimal.valueOf(time * 0.1);
        money = taskCost.add(limitTimeMoney).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        if(voucher != null) money = money.subtract(voucher.getMoney());
        if(money.compareTo(new BigDecimal(0)) == -1) money = new BigDecimal(0);
        if(UserUtil.hasLogin()){
            //压缩图片
            List<ImageItem> imageItems = imageRecyclerView.get();
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
                        SchoolTask.releaseTask("", school, description, content, money, taskCost, time, payPwd,
                                voucher == null ? 0 : voucher.getId(), 0);
                    }
                    else{   //带图片发布
                        SchoolTask.getTaskUploadKey(payPwd);
                    }
                }
            },money.toString() ,new ReleaseTaskEvent());
            payDialog.show();
        }
    }

    public void multiSelect() {
        int imageCount = 9 - imageRecyclerView.get().size() + 1;
        if(imageCount == 0){
            toastShort("最多添加9张图片");
            return;
        }
        config = new ImgSelConfig.Builder(this, loader)
                .multiSelect(true)//是否多选, 默认true
                .rememberSelected(false)//是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .btnBgColor(Color.WHITE)//确定按钮背景色
                .btnTextColor(Color.parseColor("#1B9DFF"))//确定按钮文字颜色
                .statusBarColor(Color.parseColor("#000000"))//使用沉浸式状态栏
                .backResId(R.drawable.ic_action_back)//返回图标ResId
                .title("选择图片")//标题
                .titleColor(Color.WHITE)//标题文字颜色
                .titleBgColor(Color.parseColor("#1B9DFF"))//TitleBar背景色
                .needCamera(true)//第一个是否显示相机，默认true
                .maxNum(imageCount)//最大选择图片数量，默认9
                .build();
        ImgSelActivity.startActivity(this, config, SELECT_IMAGE_CODE);//跳转到图片选择器
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteImage(DeleteImageEvent event){
        imageRecyclerView.remove(event.getIndex());
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
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadTaskImageEvent(UploadTaskImageEvent event){
        uploadNum ++;
        if(!event.isOk()){
            uploadResult = false;
        }
        if(uploadNum == tempFiles.size()){
            if(uploadResult)
                SchoolTask.releaseTask(orderId, school, description, content, money, taskCost, time,
                        payPwd, voucher == null ? 0 : voucher.getId(), uploadNum);
            else
                toastShort("发布失败");
            tempFiles.clear();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetVouchers(GetAvailableVouchersEvent event){
        if(event.isOk()){
            List<Voucher> vouchers = GsonUtil.toVoucherList(event.getData());
            voucherRecyclerView.add(vouchers);
            if(vouchers.size() != 0){
                TextView textView = new TextView(this);
                textView.setText("不使用代金券");
                textView.setPadding(DensityUtil.dipToPx(this, 30), DensityUtil.dipToPx(this, 20),
                        DensityUtil.dipToPx(this, 10), DensityUtil.dipToPx(this, 10));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideVoucherView();
                        voucher = null;
                        voucherBtn.setText("使用代金券");
                        updatePayText();
                    }
                });
                voucherRecyclerView.addFooter(textView);
            }
        }
        else{
            toastShort(event.getError());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            List<String> paths = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            imageRecyclerView.removeLast();
            List<ImageItem> imageItems = new ArrayList<>();
            for(String path : paths){
                imageItems.add(new ImageItem(1,path));
            }
            imageRecyclerView.add(imageItems);
            imageRecyclerView.add(new ImageItem(0));
        }
    }

    private void clear(){
        tempFiles.clear();
        uploadNum = 0;
        if(progressDialog != null)
            if(progressDialog.isShowing())
                progressDialog.dismiss();
    }
}
