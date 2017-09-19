package com.edu.schooltask.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.VoucherAdapter;
import com.edu.schooltask.beans.TaskUploadKey;
import com.edu.schooltask.beans.Voucher;
import com.edu.schooltask.beans.task.TaskPostBody;
import com.edu.schooltask.filter.MoneyFilter;
import com.edu.schooltask.filter.NumberFilter;
import com.edu.schooltask.filter.SchoolFilter;
import com.edu.schooltask.other.SchoolAutoComplement;
import com.edu.schooltask.base.BaseReleaseActivity;
import com.edu.schooltask.view.ContentView;
import com.edu.schooltask.view.InputTextView;
import com.edu.schooltask.view.PayPasswordView;
import com.edu.schooltask.view.PayView;
import com.edu.schooltask.view.TextSpinner;
import com.edu.schooltask.view.recyclerview.RefreshRecyclerView;
import com.edu.schooltask.utils.DensityUtil;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import server.api.client.QiniuClient;
import server.api.client.TaskClient;
import server.api.client.VoucherClient;
import server.api.result.Result;

public class ReleaseTaskActivity extends BaseReleaseActivity {

    @BindView(R.id.rt_school) InputTextView schoolText;
    @BindView(R.id.rt_des) TextSpinner desText;
    @BindView(R.id.rt_reward) InputTextView rewardText;
    @BindView(R.id.rt_content) ContentView contentViewText;
    @BindView(R.id.rt_content_hide) ContentView contentViewHideText;
    @BindView(R.id.rt_limit_time) InputTextView limitTimeText;
    @BindView(R.id.rt_voucher_btn) TextView voucherBtn;
    @BindView(R.id.rt_release_btn) TextView releaseBtn;
    @BindView(R.id.rt_pay_text) TextView payText;
    @BindView(R.id.rt_shadow) View shadowView;
    @BindView(R.id.rt_pay) PayView payView;

    @OnClick(R.id.rt_release_btn)
    public void releaseTask(){
        release();
    }

    @OnClick(R.id.rt_voucher_btn)
    public void voucher(){
        if(layout.isShown())  hideVoucherView();
        else showVoucherView();
    }

    @OnClick(R.id.rt_shadow)
    public void shadow(){
       hideVoucherView();
    }

    private Result releaseTaskResult = new Result(true) {
        @Override
        public void onResponse(int id) {
            progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int id, Object data) {
            toastShort(getString(R.string.releaseSuccess));
            finish();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
            payView.hide();
            switch (code){
                case 301:   //未设置支付密码
                    openActivity(SetPayPwdActivity.class);
                    break;
                case 305:   //余额不足
                    break;
            }
        }
    };
    private Result getTaskUploadKeyResult = new Result(true) {
        @Override
        public void onResponse(int id) {}

        @Override
        public void onSuccess(int id, Object data) {
            TaskUploadKey taskUploadKey = GsonUtil.toTaskUploadKey(data);
            task.setOrderId(taskUploadKey.getOrderId());
            QiniuClient.uploadTaskImage(taskUploadKey, tempFiles);
        }

        @Override
        public void onFailed(int id, int code, String error) {
            tempFiles.clear();
            progressDialog.dismiss();
            payView.hide();
            toastShort(error);
            switch (code){
                case 301:   //未设置支付密码
                    openActivity(SetPayPwdActivity.class);
                    break;
                case 304:   //支付密码错误
                    break;
            }
        }
    };

    private RefreshRecyclerView<Voucher> voucherRecyclerView;

    private Animation fadeInAnimation;
    private Animation fadeOutAnimation;
    private Animation voucherInAnimation;
    private Animation voucherOutAnimation;

    private TaskPostBody task = new TaskPostBody();

    @Override
    public int getLayout() {
        return R.layout.activity_release_task;
    }

    @Override
    public void init() {
        initDialog(9);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        voucherInAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_bottom_in);
        voucherOutAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_bottom_out);
        schoolText.setInputFilter(new SchoolFilter());  //输入过滤器
        rewardText.setInputFilter(new MoneyFilter());
        limitTimeText.setInputFilter(new NumberFilter());
        if(UserUtil.hasLogin()) schoolText.setText(UserUtil.getLoginUser().getSchool());    //默认学校
        schoolText.setInputEnable(false);
        /*schoolText.getInputText().addTextChangedListener(   //学校自动匹配
                new SchoolAutoComplement(schoolText.getInputText(), mDataCache.getSchool()));*/
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
                int imageNum = tempFiles.size();
                task.setImageNum(imageNum);
                if(imageNum == 0){ //无图片发布
                    TaskClient.releaseTask(releaseTaskResult, task);
                }
                else{   //带图片发布
                    QiniuClient.getTaskUploadKey(getTaskUploadKeyResult, payPwd);
                }
            }
        });
        //获取代金券
        voucherRecyclerView = new RefreshRecyclerView<Voucher>(this, true) {
            @Override
            protected BaseQuickAdapter initAdapter(List<Voucher> list) {
                return new VoucherAdapter(list);
            }

            @Override
            protected void requestData(Result result) {
                VoucherClient.getAvailableVouchers(result);
            }

            @Override
            protected void onSuccess(int id, Object data) {
                List<Voucher> vouchers = GsonUtil.toVoucherList(data);
                voucherRecyclerView.add(vouchers);
            }

            @Override
            protected void onFailed(int id, int code, String error) {
                toastShort(error);
            }

            @Override
            protected void onItemClick(int position, Voucher voucher) {
                task.setVoucher(voucher);
                updatePayText();
                hideVoucherView();
                voucherBtn.setText(voucher.getMoney().intValue() + "元代金券");
            }
        };
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
        addView(voucherRecyclerView);
        voucherRecyclerView.refresh();
    }

    @Override
    public void onBackPressed() {
        if(layout.isShown()) {
            hideVoucherView();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected boolean exit() {
        return StringUtil.isEmpty(rewardText.getText())
                && StringUtil.isEmpty(limitTimeText.getText())
                && StringUtil.isEmpty(contentViewText.getText())
                && "请选择任务类型".equals(desText.getText())
                && StringUtil.isEmpty(contentViewHideText.getText());
    }

    private void showVoucherView(){
        shadowView.setVisibility(View.VISIBLE);
        shadowView.startAnimation(fadeInAnimation);
        layout.setVisibility(View.VISIBLE);
        layout.startAnimation(voucherInAnimation);
    }

    private void hideVoucherView(){
        shadowView.setVisibility(View.GONE);
        shadowView.startAnimation(fadeOutAnimation);
        layout.setVisibility(View.GONE);
        layout.startAnimation(voucherOutAnimation);
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


    private void release(){
        String school = schoolText.getText();
        String description = desText.getText();
        String content = contentViewText.getText();
        String reward = rewardText.getText();
        String limitTime = limitTimeText.getText();
        //学校为空
        if(school.length() == 0){
            toastShort(s(R.string.input_tip, schoolText.getName()));
            return;
        }
        //报酬为空
        if(reward.length() == 0){
            toastShort(s(R.string.input_tip, rewardText.getName()));
            return;
        }
        //时限为空
        if(limitTime.length() == 0){
            toastShort(s(R.string.input_tip, limitTimeText.getName()));
            return;
        }
        //未选择类别
        if(desText.getSelectedIndex() == 0){
            toastShort(description);
            return;
        }
        //内容为空
        if(content.length() == 0){
            toastShort(s(R.string.input_tip, "内容"));
            return;
        }
        //报酬格式错误
        if(!StringUtil.isMoney(reward)){
            toastShort(s(R.string.input_error, rewardText.getName()));
            return;
        }
        //报酬过低
        BigDecimal rewardDecimal = new BigDecimal(reward);
        if(rewardDecimal.compareTo(new BigDecimal(1)) == -1){
            toastShort(s(R.string.input_money_min));
            rewardText.clear();
            return;
        }
        //报酬过高
        if(rewardDecimal.compareTo(new BigDecimal(10000)) == 1){
            toastShort(s(R.string.input_money_max));
            return;
        }
        int limitTimeInteger = Integer.parseInt(limitTime);
        if(limitTimeInteger == 0 || limitTimeInteger >= 7 * 24){
            toastShort(s(R.string.input_error, limitTimeText.getName()));
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
        task.setHideContent(contentViewHideText.getText());
        //压缩图片
        for(String path : paths){
            tempFiles.add(new Compressor.Builder(ReleaseTaskActivity.this)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setQuality(100).build().compressToFile(new File(path)));
        }
        KeyBoardUtil.hideKeyBoard(this);
        //支付界面
        payView.setTitle("支付金额：" + cost.toString() + "元");
        payView.show();
    }

    @Override
    protected void uploadSuccess() {
        TaskClient.releaseTask(releaseTaskResult, task);
    }

    @Override
    protected void uploadFailed() {
        payView.hide();
        toastShort(s(R.string.task_release_failed));
    }
}
