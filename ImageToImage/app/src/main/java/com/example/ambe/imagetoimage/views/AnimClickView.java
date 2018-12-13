package com.example.ambe.imagetoimage.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.example.ambe.imagetoimage.R;

/**
 * Created by AMBE on 12/11/2018.
 */
public class AnimClickView extends AppCompatImageView {
    public AnimClickView(Context context) {
        super(context);
        init(null);
    }

    public AnimClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AnimClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.GPSImageView, 0, 0);
        boolean clickAble = array.getBoolean(R.styleable.GPSImageView_clickAble, true);
        array.recycle();
        setClickable(clickAble);
        if (clickAble) {
            setBackgroundResource(R.drawable.ripple_click_img);
        }
    }
}
