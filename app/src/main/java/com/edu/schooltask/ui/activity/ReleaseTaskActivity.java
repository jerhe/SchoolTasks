package com.edu.schooltask.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.TaskUploadKey;
import com.edu.schooltask.beans.Voucher;
import com.edu.schooltask.beans.task.TaskPostBody;
import com.edu.schooltask.event.DeleteImageEvent;
import com.edu.schooltask.filter.MoneyFilter;
import com.edu.schooltask.filter.NumberFilter;
import com.edu.schooltask.filter.SchoolFilter;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.other.SchoolAutoComplement;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.ui.view.InputTextView;
import com.edu.schooltask.ui.view.PayPasswordView;
import com.edu.schooltask.ui.view.PayView;
import com.edu.schooltask.ui.view.TaskContentView;
import com.edu.schooltask.ui.view.TextSpinner;
import com.edu.schooltask.ui.view.recyclerview.BaseRecyclerView;
import com.edu.schooltask.ui.view.recyclerview.ImageRecyclerView;
import com.edu.schooltask.ui.view.recyclerview.VoucherRecyclerView;
import com.edu.schooltask.utils.DensityUtil;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;

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
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import server.api.SchoolTask;
import server.api.event.qiniu.GetTaskUploadKeyEvent;
import server.api.event.qiniu.UploadTaskImageEvent;
import server.api.event.task.ReleaseTaskEvent;
import server.api.event.voucher.GetAvailableVouchersEvent;

public class ReleaseTaskActivity extends BaseActivity {
    private static final int SELECT_IMAGE_CODE = 0;

    @BindView(R.id.rt_school) InputTextView schoolText;
    @BindView(R.id.rt_des) TextSpinner desText;
    @BindView(R.id.rt_reward) InputTextView rewardText;
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
    Animation voucherInAnimation;
    Animation voucherOutAnimation;
    List<File> tempFiles = new ArrayList<>();
    ArrayList<String> paths = new ArrayList<>();    //选中图片

    TaskPostBody task = new TaskPostBody();

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
        voucherInAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_bottom_in);
        voucherOutAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_bottom_out);
        imageRecyclerView.add(new ImageItem(0));
        imageRecyclerView.setImageClickListener(new ImageRecyclerView.ImageClickListener() {
            @Override
            public void onImageClick(int position, ImageItem imageItem) {
                switch (imageItem.getType()){
                    case 0:
                        Intent imageIntent = new Intent(ReleaseTaskActivity.this, ImageSelectActivity.class);
                        imageIntent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                        imageIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
                        imageIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                        imageIntent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, paths);
                        startActivityForResult(imageIntent, SELECT_IMAGE_CODE);
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
        rewardText.setInputFilter(new MoneyFilter());
        limitTimeText.setInputFilter(new NumberFilter());
        schoolText.setText(UserUtil.getLoginUser().getSchool());    //默认学校
        schoolText.getInputText().addTextChangedListener(   //学校自动匹配
                new SchoolAutoComplement(schoolText.getInputText(), mDataCache.getSchool()));
        desText.setItems(getResources().getStringArray(R.array.taskDescriptionOption));
        rewardText.requestFocus();   //设置标题为默认焦点
        //需支付金额
        rewardText.getInputText().addTextChangedListener(new TextWatcher() {
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
                String payPwd = EncriptUtil.getMD5(password);
                task.setPayPwd(payPwd);
                if(tempFiles.size() == 0){ //无图片发布
                    task.setImageNum(0);
                    SchoolTask.releaseTask(task);
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
                Voucher voucher = voucherRecyclerView.get(position);
                task.setVoucher(voucher);
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
                task.setVoucher(null);
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
        voucherLayout.startAnimation(voucherInAnimation);
    }

    private void hideVoucherView(){
        shadowView.setVisibility(View.GONE);
        shadowView.startAnimation(fadeOutAnimation);
        voucherLayout.setVisibility(View.GONE);
        voucherLayout.startAnimation(voucherOutAnimation);
    }

    private void updatePayText(){
        StringBuilder payStr = new StringBuilder();
        String reward = rewardText.getText();
        String limitTime = limitTimeText.getText();
        if(reward.length() != 0 && limitTime.length() != 0) {
            BigDecimal cost = new BigDecimal(reward);
            cost = cost.add(new BigDecimal(Integer.parseInt(limitTime) * 0.1))
                        .setScale(2, BigDecimal.ROUND_HALF_DOWN);
            if(task.getVoucher() != null) cost = cost.subtract(task.getVoucher().getMoney());
            if(cost.compareTo(new BigDecimal(0)) == -1) cost = new BigDecimal(0);
            payStr.append(cost);
        }
        if(payStr.length() == 0) payText.setText("");
        else payText.setText(getString(R.string.moneySign, payStr));
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
        String school = schoolText.getText();
        String description = desText.getText();
        String content = taskContentViewText.getText();
        String reward = rewardText.getText();
        String limitTime = limitTimeText.getText();
        //学校为空
        if(school.length() == 0){
            toastShort(getString(R.string.inputTip, schoolText.getName()));
            return;
        }
        //报酬为空
        if(reward.length() == 0){
            toastShort(getString(R.string.inputTip, rewardText.getName()));
            return;
        }
        //时限为空
        if(limitTime.length() == 0){
            toastShort(getString(R.string.inputTip, limitTimeText.getName()));
            return;
        }
        //未选择类别
        if(desText.getSelectedIndex() == 0){
            toastShort(description);
            return;
        }
        //内容为空
        if(content.length() == 0){
            toastShort(getString(R.string.inputTip, "内容"));
            return;
        }
        //报酬格式错误
        if(!StringUtil.isMoney(reward)){
            toastShort(getString(R.string.inputError, rewardText.getName()));
            return;
        }
        //报酬过低
        BigDecimal rewardDecimal = new BigDecimal(reward);
        if(rewardDecimal.compareTo(new BigDecimal(1)) == -1){
            toastShort(getString(R.string.inputMoneyMin));
            rewardText.clear();
            return;
        }
        //报酬过高
        if(rewardDecimal.compareTo(new BigDecimal(10000)) == 1){
            toastShort(getString(R.string.inputMoneyMax));
            return;
        }
        int limitTimeInteger = Integer.parseInt(limitTime);
        if(limitTimeInteger == 0 || limitTimeInteger >= 7 * 24){
            toastShort(getString(R.string.inputError, limitTimeText.getName()));
            limitTimeText.clear();
            return;
        }
        //计算支付金额
        BigDecimal limitTimeMoney = BigDecimal.valueOf(limitTimeInteger * 0.1); //时限费用
        BigDecimal cost = rewardDecimal.add(limitTimeMoney).setScale(2, BigDecimal.ROUND_HALF_DOWN);    //+报酬
        if(task.getVoucher() != null) cost = cost.subtract(task.getVoucher().getMoney());   //-代金券
        if(cost.compareTo(new BigDecimal(0)) == -1) cost = new BigDecimal(0);   //最低支付0元
        //赋值
        task.setSchool(school);
        task.setLimitTime(limitTimeInteger);
        task.setReward(rewardDecimal);
        task.setCost(cost);
        task.setDescription(description);
        task.setContent(content);
        //压缩图片
        List<ImageItem> imageItems = imageRecyclerView.get();
        for(int i=0; i<imageItems.size()-1; i++){
            ImageItem imageItem = imageItems.get(i);
            final String path = imageItem.getPath();
            tempFiles.add(new Compressor.Builder(ReleaseTaskActivity.this)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setQuality(100).build().compressToFile(new File(path)));
        }
        KeyBoardUtil.hideKeyBoard(this);
        //支付界面
        payView.setTitle("支付金额：" + cost.toString() + "元");
        payView.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteImage(DeleteImageEvent event){
        paths.remove(event.getIndex());
        imageRecyclerView.remove(event.getIndex());
        imageRecyclerView.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskUploadKey(GetTaskUploadKeyEvent event){
        if(event.isOk()){
            TaskUploadKey taskUploadKey = GsonUtil.toTaskUploadKey(event.getData());
            task.setOrderId(taskUploadKey.getOrderId());
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
        if(uploadNum == tempFiles.size()){  //全部图片上传完成
            if(uploadResult){
                task.setImageNum(uploadNum);
                SchoolTask.releaseTask(task);
            }
            else toastShort(getString(R.string.releaseFailed));
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
        if (requestCode == SELECT_IMAGE_CODE && resultCode == RESULT_OK) {
            imageRecyclerView.clear(true);
            paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
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
