package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/5/8.
 */

public class ImageDisplayItem extends RelativeLayout {
    private ImageView imageView;
    private ImageView deleteBtn;

    public ImageDisplayItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_image_display_item,this);
        imageView = (ImageView) findViewById(R.id.idi_image);
        deleteBtn = (ImageView) findViewById(R.id.idi_delete);
        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
    }

    public void loadImage(String path){
        Glide.with(getContext()).load(path).into(imageView);
    }

    public void delete(){
        this.setVisibility(GONE);
    }
}
