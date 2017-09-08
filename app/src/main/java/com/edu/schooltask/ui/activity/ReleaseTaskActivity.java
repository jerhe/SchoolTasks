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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
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
import com.edu.schooltask.ui.view.PayPasswordView;
import com.edu.schooltask.ui.view.PayView;
import com.edu.schooltask.ui.view.recyclerview.BaseRecyclerView;
import com.edu.schooltask.utils.DensityUtil;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.ui.view.TaskContentView;
import com.edu.schooltask.ui.view.InputTextView;
import com.edu.schooltask.ui.view.TextSpinner;
import com.edu.schooltask.ui.view.recyclerview.ImageRecyclerView;
import com.edu.schooltask.ui.view.recyclerview.VoucherRecyclerView;
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
import server.api.event.qiniu.GetTaskUploadKeyEvent;
import server.api.event.qiniu.UploadTaskImageEvent;
import server.api.event.task.ReleaseTaskEvent;
import server.api.event.voucher.GetAvailableVouchersEvent;

public class ReleaseTaskActivity extends BaseActivity {
    private static final int SELECT_IMAGE_CODE = 0;

    @BindView(R.id.rt_school) InputTextView schoolText;
    @BindView(R.id.rt_des)
    TextSpinner desText;
    @BindView(R.id.rt_cost) InputTextView costText;
    @BindView(R.id.rt_content) TaskContentView taskContentViewText;
    @BindView(R.id.rt_limit_time) InputTextView limitTimeText;
    @BindView(R.id.rt_irv) ImageRecyclerView imageRecyclerView;
    @BindView(R.id.rt_voucher_btn) TextView voucherBtn;
    @BindView(R.id.rt_release_btn) TextView releaseBtn;
    @BindView(R.id.rt_pay_text) TextView payText;
    @BindView(R.id.rt_voucher_layout) LinearLayout voucherLayout;
    @BindView(R.id.rt_prl) PullRefreshLayout refreshLayout;
    @BindView(R.id.rt_vrv) VoucherRecyclerView voucherRecyclerView;
    @BindView(R.id.rt_shadow) View shadowView;
    @BindView(R.id.rt_pay) PayView payView;

    @OnClick(R.id.rt_release_btn)
    public void releaseTask(){
        release();
    }
    @OnClick(R.id.rt_voucher_btn)
    public void voucher(){
        if(refreshLayout.isShown())  hideVoucherView();
        else showVoucherView();
    }
    @OnClick(R.id.rt_shadow)
    public void shadow(){
       hideVoucherView();
    }

    ProgressDialog progressDialog;

    Animation fadeInAnimation;
    Animation fadeOutAnimation;

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
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
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
        desText.setItems(getResources().getStringArray(R.array.taskDescriptionOption));
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
        //支付界面
        payView.setPayPasswordFinishedListener(new PayPasswordView.PayPasswordFinishedListener() {
            @Override
            public void onFinished(String password) {
                progressDialog = ProgressDialog.show(ReleaseTaskActivity.this, "",
                        getString(R.string.releasing), true, false);
                payPwd = EncriptUtil.getMD5(password);
                if(tempFiles.size() == 0){ //无图片发布
                    SchoolTask.releaseTask("", school, description, content, money, taskCost, time, payPwd,
                            voucher == null ? 0 : voucher.getId(),
                            voucher == null ? new BigDecimal(0) : voucher.getMoney(), 0);
                }
                else{   //带图片发布
                    SchoolTask.getTaskUploadKey(payPwd);
                }
            }
        });
        //获取代金券
        voucherRecyclerView.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                voucher = voucherRecyclerView.get(position);
                updatePayText();
                hideVoucherView();
                voucherBtn.setText(voucher.getMoney().intValue() + "元代金券");
            }
        });
        voucherRecyclerView.setOnGetDataListener(new BaseRecyclerView.OnGetDataListener() {
            @Override
            public void onGetData() {
                SchoolTask.getAvailableVouchers();
            }
        });
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                voucherRecyclerView.refresh();
            }
        });
        voucherRecyclerView.bind();
        voucherRecyclerView.setEmptyView(R.layout.empty_voucher);
        TextView textView = new TextView(this);
        textView.setText(getString(R.string.withoutVoucher));
        textView.setPadding(DensityUtil.dipToPx(this, 30), DensityUtil.dipToPx(this, 20),
                DensityUtil.dipToPx(this, 10), DensityUtil.dipToPx(this, 10));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideVoucherView();
                voucher = null;
                voucherBtn.setText(getString(R.string.useVoucher));
                updatePayText();
            }
        });
        voucherRecyclerView.addFooter(textView);
        voucherRecyclerView.refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if(voucherRecyclerView.isShown()) {
            hideVoucherView();
            return;
        }
        finish();
    }

    private void showVoucherView(){
        shadowView.setVisibility(View.VISIBLE);
        shadowView.startAnimation(fadeInAnimation);
        voucherLayout.setVisibility(View.VISIBLE);
        voucherLayout.startAnimation(fadeInAnimation);
    }

    private void hideVoucherView(){
        shadowView.setVisibility(View.GONE);
        shadowView.startAnimation(fadeOutAnimation);
        voucherLayout.setVisibility(View.GONE);
        voucherLayout.startAnimation(fadeOutAnimation);
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
                payText.setText(getString(R.string.moneySign, money));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReleaseTask(ReleaseTaskEvent event){
        clear();
        if(event.isOk()){
            toastShort(getString(R.string.releaseSuccess));
            finish();
        }
        else{
            toastShort(event.getError());
            payView.hide();
            switch (event.getCode()){
                case 301:   //未设置支付密码
                    openActivity(SetPayPwdActivity.class);
                    break;
                case 305:   //余额不足
                    break;
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
            toastShort(getString(R.string.inputTip, schoolText.getName()));
            return;
        }

        if(desText.getSelectedIndex() == 0){
            toastShort(description);
            return;
        }

        if(content.length() == 0){
            toastShort(getString(R.string.inputTip, "内容"));
            return;
        }
        if(cost.length() == 0){
            toastShort(getString(R.string.inputTip, costText.getName()));
            return;
        }
        if(limitTime.length() == 0){
            toastShort(getString(R.string.inputTip, limitTimeText.getName()));
            return;
        }
        if(!StringUtil.isMoney(cost)){
            toastShort(getString(R.string.inputError, costText.getName()));
            return;
        }
        taskCost = new BigDecimal(cost);
        if(taskCost.compareTo(new BigDecimal(1)) == -1){
            toastShort(getString(R.string.inputMoneyMin));
            costText.clear();
            return;
        }
        if(taskCost.compareTo(new BigDecimal(10000)) == 1){
            toastShort(getString(R.string.inputMoneyMax));
            return;
        }
        time = Integer.parseInt(limitTime);
        if(time == 0 || time >= 7 * 24){
            toastShort(getString(R.string.inputError, limitTimeText.getName()));
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
            KeyBoardUtil.hideKeyBoard(this);
            //支付界面
            payView.setTitle("支付金额：" + money.toString() + "元");
            payView.show();
        }
    }

    public void multiSelect() {
        int imageCount = 9 - imageRecyclerView.get().size() + 1;
        if(imageCount == 0){
            toastShort(getString(R.string.imageMax));
            return;
        }
        config = new ImgSelConfig.Builder(this, loader)
                .multiSelect(true)//是否多选, 默认true
                .rememberSelected(false)//是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .btnBgColor(Color.WHITE)//确定按钮背景色
                .btnTextColor(getResources().getColor(R.color.colorPrimary))//确定按钮文字颜色
                .statusBarColor(Color.BLACK)//使用沉浸式状态栏
                .backResId(R.drawable.ic_action_back)//返回图标ResId
                .title(getString(R.string.imageSelectTitle))//标题
                .titleColor(Color.WHITE)//标题文字颜色
                .titleBgColor(getResources().getColor(R.color.colorPrimary))//TitleBar背景色
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
    public void onGetTaskUploadKey(GetTaskUploadKeyEvent event){
        if(event.isOk()){
            TaskUploadKey taskUploadKey = GsonUtil.toTaskUploadKey(event.getData());
            orderId = taskUploadKey.getOrderId();
            SchoolTask.uploadTaskImage(taskUploadKey, tempFiles);
        }
        else{
            clear();
            payView.hide();
            toastShort(event.getError());
            switch (event.getCode()){
                case 301:   //未设置支付密码
                    openActivity(SetPayPwdActivity.class);
                    break;
                case 304:   //支付密码错误
                    break;
            }
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
                        payPwd, voucher == null ? 0 : voucher.getId(),
                        voucher == null ? new BigDecimal(0) : voucher.getMoney(), uploadNum);
            else
                toastShort(getString(R.string.releaseFailed));
            tempFiles.clear();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetVouchers(GetAvailableVouchersEvent event){
        refreshLayout.setRefreshing(false);
        if(event.isOk()){
            List<Voucher> vouchers = GsonUtil.toVoucherList(event.getData());
            voucherRecyclerView.add(vouchers);
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
        if(progressDialog != null) progressDialog.dismiss();
    }
}
