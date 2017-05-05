package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/5.
 */

public class ViewAssess extends LinearLayout implements View.OnClickListener{
    private int normalStar = R.drawable.ic_icon_star;
    private int lightStar = R.drawable.ic_icon_star_light;

    private int count = 0;

    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private ImageView star5;

    private TextView textView;

    private String[] values = new String[]{"1分:很差","2分:差","3分:一般","4分:好","5分:非常好"};

    private List<ImageView> starList = new ArrayList<>();

    public ViewAssess(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_assess,this);
        star1 = (ImageView)findViewById(R.id.assess_star_1);
        star2 = (ImageView)findViewById(R.id.assess_star_2);
        star3 = (ImageView)findViewById(R.id.assess_star_3);
        star4 = (ImageView)findViewById(R.id.assess_star_4);
        star5 = (ImageView)findViewById(R.id.assess_star_5);

        star1.setOnClickListener(this);
        star2.setOnClickListener(this);
        star3.setOnClickListener(this);
        star4.setOnClickListener(this);
        star5.setOnClickListener(this);

        textView = (TextView)findViewById(R.id.assess_value);

        starList.add(star1);
        starList.add(star2);
        starList.add(star3);
        starList.add(star4);
        starList.add(star5);
    }

    /**
     * getCount 获取分数
     * @return 返回分数
     */
    public int getCount(){
        return count;
    }

    @Override
    public void onClick(View v) {
        int position = starList.indexOf(v);
        if(position != -1){
            count = position + 1;
            for(int index=0; index <= position; index++){   //设为高亮
                starList.get(index).setImageResource(lightStar);
            }
            for(int index=position+1; index <= 4; index++){   //设为默认
                starList.get(index).setImageResource(normalStar);
            }
            textView.setText(values[position]); //设置文本
        }
    }
}
