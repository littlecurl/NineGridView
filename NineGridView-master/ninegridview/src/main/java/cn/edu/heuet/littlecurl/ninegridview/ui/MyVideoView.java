package cn.edu.heuet.littlecurl.ninegridview.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import java.math.BigDecimal;

/**
 * 自定义VideoView 配合 ViewPagerAdapter.java 中的 videoView.setOnPreparedListener()方法
 * 解决了VideoView在加载时候闪一下屏的问题
 */
public class MyVideoView extends VideoView {
    public MyVideoView(Context context) {
        super(context);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 设备宽高
        int deviceWidth = getResources().getDisplayMetrics().widthPixels;
        int deviceHeight = getResources().getDisplayMetrics().heightPixels;
        // 如果把这两个参数调换位置，AS竟然会检测出来，并用深黄色表示！！！
        setMeasuredDimension(deviceWidth, deviceHeight);

//        int width = getDefaultSize(getWidth(), widthMeasureSpec);
//        int height = getDefaultSize(getHeight(), heightMeasureSpec);
//        setMeasuredDimension(width, height);
    }
}
