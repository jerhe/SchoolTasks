package com.edu.schooltask.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.EmojiAdapter;
import com.edu.schooltask.utils.KeyBoardUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/16.
 */

public class MessageInputBoard extends LinearLayout implements View.OnClickListener{
    Activity activity;

    EditText inputText;
    ImageView imageBtn;
    TextView sendBtn;
    ImageView emojiBtn;
    ViewPager viewPager;
    List<RecyclerView> recyclerViews = new ArrayList<>();

    SendMessageListener listener;
    SelectImageListener selectImageListener;

    int boardHeight;
    boolean isBoardShow;
    public boolean isEmojiShow;

    List<String> emojiList1 = new ArrayList<>();

    public MessageInputBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.message_input_board,this);
        inputText = (EditText) findViewById(R.id.mib_input);
        imageBtn = (ImageView) findViewById(R.id.mib_image);
        sendBtn = (TextView) findViewById(R.id.mib_send);
        emojiBtn = (ImageView) findViewById(R.id.mib_emoji);
        viewPager = (ViewPager) findViewById(R.id.mib_vp);
        inputText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(isEmojiShow && !isBoardShow){
                        hideEmoji();
                    }
                }
            }
        });
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    imageBtn.setVisibility(INVISIBLE);
                    sendBtn.setVisibility(VISIBLE);
                }
                else{
                    imageBtn.setVisibility(VISIBLE);
                    sendBtn.setVisibility(INVISIBLE);
                }
            }
        });

        inputText.getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                Rect r = new Rect();
                                inputText.getWindowVisibleDisplayFrame(r);
                                int screenHeight = inputText.getRootView().getHeight();
                                int heightDifference = screenHeight - r.bottom;
                                isBoardShow = heightDifference > screenHeight / 3;
                                if(isBoardShow) {
                                    boardHeight = heightDifference;
                                }
                            }
                        });
        RecyclerView recyclerView1 = new RecyclerView(viewPager.getContext());
        recyclerView1.setLayoutManager(new GridLayoutManager(viewPager.getContext(),4));
        EmojiAdapter emojiAdapter1 = new EmojiAdapter(R.layout.item_emoji, emojiList1);
        recyclerView1.setAdapter(emojiAdapter1);
        recyclerViews.add(recyclerView1);
        emojiAdapter1.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String oldText = inputText.getText().toString();
                String text = oldText + emojiList1.get(position);
                inputText.setText(text);
                inputText.setSelection(text.length());
            }
        });
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return recyclerViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View)object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(recyclerViews.get(position));
                return recyclerViews.get(position);
            }

            @Override
            public int getItemPosition(Object object) {
                return recyclerViews.indexOf(object);
            }
        };
        viewPager.setAdapter(pagerAdapter);

        initEmoji();

        emojiBtn.setOnClickListener(this);

        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onSend(inputText.getText().toString());
                }
            }
        });

        imageBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectImageListener != null){
                    selectImageListener.onSelectImage();
                }
            }
        });
    }

    private void initEmoji(){
        emojiList1.add("(*°∀°)=3");
        emojiList1.add("٩(^ᴗ^)۶");
        emojiList1.add("(^-^)");
        emojiList1.add("(︶ω︶)");
        emojiList1.add("(´･_･`)");
        emojiList1.add("(°д°)");
        emojiList1.add("T_T");
        emojiList1.add("( •̀ 3 •́ )");
        emojiList1.add("(ಥ﹏ಥ)");
        emojiList1.add("(⋟﹏⋞)");
        emojiList1.add("^o^");
        emojiList1.add("(╯-╰)/ ");
        emojiList1.add("(o´Д`)");
        emojiList1.add("(▰˘o˘▰)");
        emojiList1.add("(✿ ◕‿◕)");
        emojiList1.add("→_→ ");
        emojiList1.add("(⊙o⊙)");
        emojiList1.add(">_<");
        emojiList1.add("(ˇ^ˇ)");
        emojiList1.add("<<<");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.mib_emoji:
                if(viewPager.isShown()) {
                    hideEmoji();
                    KeyBoardUtil.inputKeyBoard(inputText);
                }
                else {
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    KeyBoardUtil.hideKeyBoard(activity);
                    ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
                    if(boardHeight != 0)layoutParams.height = boardHeight;
                    viewPager.setVisibility(VISIBLE);
                }
                isEmojiShow = viewPager.isShown();
                break;
        }
    }

    public void hideEmoji(){
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        viewPager.setVisibility(GONE);
        isEmojiShow = false;
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public void clear(){
        inputText.setText("");
    }

    public void setSendMessageListener(SendMessageListener listener){
        this.listener = listener;
    }

    public interface SendMessageListener{
        void onSend(String message);
    }

    public void setSelectImageListener(SelectImageListener listener){
        this.selectImageListener = listener;
    }

    public interface SelectImageListener{
        void onSelectImage();
    }
}
