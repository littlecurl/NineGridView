package cn.edu.heuet.littlecurl.ninegridview.detail;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.viewpager.widget.ViewPager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.heuet.littlecurl.R;
import cn.edu.heuet.littlecurl.ninegridview.bean.MediaItem;

/**
 * 重点关注 implements 的那些接口
 * 及其对应的实现方法
 */
public class MediaDetailActivity extends Activity implements
        ViewTreeObserver.OnPreDrawListener,
        ViewPager.OnPageChangeListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnCompletionListener {

    public static final String MEDIA_INFO = "MEDIA_INFO";
    public static final String CURRENT_ITEM = "CURRENT_ITEM";
    private static final int ANIMATE_DURATION = 200;

    public static Map<Integer, ViewGroup> viewGroupMap;         // 父布局，ViewPagerAdapter中需要用到

    private Map<Integer, Boolean> isPlayingMap;                 // 记录每个page播放状态
    private Map<Integer, Integer> playingPositionMap;           // 记录每个page播放进度
    private Map<Integer, VideoView> videoViewMap;               // 记录每个VideoView位置
    private Map<Integer, MediaController> mediaControllerMap;   // 页面播放进度控制器
    private Map<Integer, View> videoIconMap;                    // 页面视频缓冲图
    private Map<Integer, View> progressBarMap;                  // 旋转进度条

    private RelativeLayout rootView;                            // 当前页面根布局
    private View mediaItemView;                                 // 视频或图片布局
    private List<MediaItem> mediaItemList;                      // 视频地址集合
    private int currentItem;                                    // 当前页面索引
    private int lastItem;                                       // 上一个页面索引

    private ViewPagerAdapter viewPagerAdapter;
    private int mediaItemViewRealHeight;
    private int mediaItemViewRealWidth;
    private int screenWidth;
    private int screenHeight;
    private ViewPager viewPager;
    private TextView tv_pager;
    private VideoView currentVideoView;
    private MediaController currentMediaController;
    private MediaController lastMediaController;
    private VideoView lastVideoView;
    private View currentVideoIconView;
    private View pb;

    @SuppressLint("StringFormatMatches")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Log.i("日志1", "进入 MediaDetailActivity");
        // 当前页面布局
        viewPager = findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(this);

        tv_pager = findViewById(R.id.tv_pager);
        rootView = findViewById(R.id.rootView);

        // 测量整个屏幕的宽高
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;

        // 获取传递过来的数据
        Intent intent = getIntent();
        mediaItemList = (List<MediaItem>) intent.getSerializableExtra(MEDIA_INFO);
        currentItem = intent.getIntExtra(CURRENT_ITEM, 0);

        Log.i("日志1", "开始为ViewPager准备数据");

        // 为ViewPager准备数据
        initDataOfViewPager();

        tv_pager.setText(String.format(getString(R.string.select), currentItem + 1, mediaItemList.size()));
    }


    @SuppressLint("UseSparseArrays")
    private void initDataOfViewPager() {
        // 12 * 0.75 = 9
        viewGroupMap = new HashMap<Integer, ViewGroup>(12);

        videoIconMap = new HashMap<Integer, View>(12);
        mediaControllerMap = new HashMap<Integer, MediaController>(12);
        videoViewMap = new HashMap<Integer, VideoView>(12);
        playingPositionMap = new HashMap<Integer, Integer>(12);
        isPlayingMap = new HashMap<Integer, Boolean>(12);
        progressBarMap = new HashMap<Integer, View>(12);
        int mediaCount = mediaItemList.size();

        Log.i("日志1", "开始为循环遍历传递过来的数据，准备视图");
        for (int i = 0; i < mediaCount; i++) {
            // 如果是视频，就加载相关内容
            if (existVideoUrl(mediaItemList, i)) {
                // 获取item_media布局
                ViewGroup mediaItemView = (ViewGroup) View.inflate(this, R.layout.item_media, null);
                VideoView videoView = mediaItemView.findViewById(R.id.videoplayer); // 视频
                ProgressBar pb = mediaItemView.findViewById(R.id.pb);               // 进度条
                MediaController mc = new MediaController(this);             // 视频控制器
                videoView.setMediaController(mc);                                   // 给视频设置控制器
                ImageView videoIcon = mediaItemView.findViewById(R.id.iv_video_cover);
                setListener(videoView);                                 // 给VideoView设置相关监听器

                viewGroupMap.put(i, mediaItemView);                      // 设置根布局，ViewPagerAdapter要用到
                videoIconMap.put(i,videoIcon);                          // 视频遮罩
                videoViewMap.put(i, videoView);                         // VideoView
                progressBarMap.put(i, pb);                              // 进度条
                mediaControllerMap.put(i, mc);                          // MediaController
                playingPositionMap.put(i, 0);                           // 每个页面的初始播放进度为0
                isPlayingMap.put(i, false);                             // 每个页面的初始播放状态false
            }

        }
        Log.i("日志1", "循环遍历数据结束，开始给ViewPager设置适配器");
        // 设置ViewPager相关
        // viewPagerAdapter = new ViewPagerAdapter(this, mediaItemList);
        viewPagerAdapter = new ViewPagerAdapter(this, mediaItemList, viewGroupMap);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setCurrentItem(currentItem);
        viewPager.getViewTreeObserver().addOnPreDrawListener(this);

        Log.i("日志1", "onCreate()方法中，当前Item ===>" + currentItem);
        currentVideoView = videoViewMap.get(currentItem);
        currentVideoIconView = videoIconMap.get(currentItem);
        if (currentVideoView != null) {
            String videoUrl = mediaItemList.get(currentItem).getVideoUrl();
            // 加载视频，按说这里应该写在子线程里
            currentVideoView.setVideoURI(Uri.parse(videoUrl));             // 设置网络视频地址
            currentVideoView.start();
            Log.i("日志1", "onCreate()方法中，视频start()");
            currentVideoIconView.setVisibility(View.VISIBLE);
            currentVideoView.setVisibility(View.VISIBLE);
            isPlayingMap.put(currentItem, true);
        }

    }

    // 给VideoView设置相关监听器
    private void setListener(VideoView vv) {
        // 播放内容监听
        vv.setOnInfoListener(this);
        // 准备完成监听
        vv.setOnPreparedListener(this);
        // 播放完成监听
        vv.setOnCompletionListener(this);
        // 播放出错监听
        vv.setOnErrorListener(this);
    }

    //-------------------------------- ViewPager页面切换监听 ↓↓↓ --------------------------------

    /**
     * @param position             当前所在页面
     * @param positionOffset       当前所在页面偏移百分比
     * @param positionOffsetPixels 当前所在页面偏移量
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 当页面被选中开始滑动时
     *
     * @param position
     */
    @SuppressLint("StringFormatMatches")
    @Override
    public void onPageSelected(int position) {
        lastItem = currentItem;
        currentItem = position;
        Log.i("日志1", "开始滑动，lastItem ===> " + lastItem + " currentItem ===> " + currentItem);
        tv_pager.setText(String.format(getString(R.string.select), currentItem + 1, mediaItemList.size()));

        // 上一个播放的视频
        lastVideoView = videoViewMap.get(lastItem);
        if (lastVideoView != null) {
            if (lastVideoView.isPlaying()) {
                Log.i("日志1", "上个视频正在播放，开始尝试暂停上个视频");
                isPlayingMap.put(lastItem, false);
                Log.i("日志1", "设置上个视频的正在播放状态为false");
                lastVideoView.pause();
                Log.i("日志1", "上个视频已暂停");
            } else {
                isPlayingMap.put(lastItem, false);
                Log.i("日志1", "上个视频没有开始播放");
                Log.i("日志1", "设置上个视频的正在播放状态为false");
            }
            playingPositionMap.put(lastItem, lastVideoView.getCurrentPosition()); // 记录播放进度
            Log.i("日志1", "记录上个视频的播放位置");
        }

        // 上一个视频的控制器
        lastMediaController = mediaControllerMap.get(lastItem);
        if (lastMediaController != null && lastMediaController.isShowing()) {
            lastMediaController.hide();
            Log.i("日志1", "隐藏上个视频的控制器");
        }

        currentVideoView = videoViewMap.get(currentItem);               // 当前视频
        if (currentVideoView != null) {
            Log.i("日志2","当前视频 "+currentItem+" 存在，给视频设置地址");
            String videoUrl = mediaItemList.get(currentItem).getVideoUrl();
            currentVideoView.setVideoURI(Uri.parse(videoUrl));             // 设置网络视频地址

            currentVideoIconView = videoIconMap.get(currentItem);           // 当前视频的遮罩
            currentMediaController = mediaControllerMap.get(currentItem);   // 当前视频的控制器
            pb = progressBarMap.get(currentItem);                           // 旋转进度条

            // 跳转到记录的播放位置
            currentVideoView.seekTo(playingPositionMap.get(currentItem));
            Log.i("日志1", "从 " + lastItem + " 切换到 " + currentItem);
            Log.i("日志1", "从当前视频记忆播放的位置 " + playingPositionMap.get(currentItem) + " 开始播放");
            Log.i("日志1", "当前视频是否正在播放：" + isPlayingMap.get(currentItem));
            if (!isPlayingMap.get(currentItem)) {
                currentVideoView.start();
                Log.i("日志1", "当前视频start()");
            }


//-----------*******************------------这里左右切换时会闪一下屏，需要优化--------------**********************---------------------//
            currentVideoView.setVisibility(View.VISIBLE);                 // 视频不可见
            currentVideoIconView.setVisibility(View.INVISIBLE);           // 视频遮罩可见
            pb.setVisibility(View.INVISIBLE);
            currentMediaController.show(1000);


        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    //-------------------------------- MediaPlayer视频控制监听 ↓↓↓ --------------------------------
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i("日志1", "视频播放完成");
        currentVideoView = videoViewMap.get(currentItem);
        if (currentVideoView != null) {
            playingPositionMap.put(currentItem, 0); // 重新播放，进度重置为0
            isPlayingMap.put(currentItem, true);    // 播放状态：正在播放
            currentVideoView.resume();              // 重置播放，重新开始
            currentVideoView.start();
            Log.i("日志1", "重置播放");
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // 这里设置为true防止弹出对话框，屏蔽原始出错的处理
        Log.i("日志1", "视频加载错误");
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        currentVideoView = videoViewMap.get(currentItem);               // 当前视频
        currentVideoIconView = videoIconMap.get(currentItem);           // 当前视频遮罩
        currentMediaController = mediaControllerMap.get(currentItem);   // 当前视频的控制器
        pb = progressBarMap.get(currentItem);                           // 旋转进度条

        // 如果获取到视频渲染的第一帧
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            Log.i("日志1", "获取到视频的第一帧");
            currentVideoIconView.setVisibility(View.INVISIBLE);
            Log.i("日志1", "隐藏视频遮罩");
            currentVideoView.setVisibility(View.VISIBLE);
            Log.i("日志1", "显示VideoView");
            Log.i("日志1", "等待 1s 后出现视频控制器");
            currentMediaController.show(1000);
            return true;
        }
        // 如果视频需要缓冲
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            currentVideoView.pause();
            currentVideoView.setVisibility(View.INVISIBLE);
            currentVideoIconView.setVisibility(View.INVISIBLE);
            currentMediaController.hide();
            pb.setVisibility(View.VISIBLE);
            return true;
        }
        // 如果视频缓冲完成
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            currentVideoView.start();
            currentVideoView.setVisibility(View.VISIBLE);
            currentVideoIconView.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.INVISIBLE);
            return true;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
    }


    //-------------------------------- Activity生命周期函数 ↓↓↓ --------------------------------

    /**
     * Activity生命周期 ：页面失去焦点
     * 停止正在播放的视频
     */
    @Override
    protected void onPause() {
        super.onPause();
        currentVideoView = videoViewMap.get(currentItem);
        if (currentVideoView != null) {
            Log.i("日志1", "页面失去焦点，尝试暂停视频");
            if (currentVideoView.isPlaying()) {
                isPlayingMap.put(currentItem, true);
                currentVideoView.pause();
                Log.i("日志1", "页面失去焦点，暂停视频成功");
            } else {
                isPlayingMap.put(currentItem, false);
                Log.i("日志1", "页面失去焦点，视频未播放，无需暂停");
            }
            playingPositionMap.put(currentItem, currentVideoView.getCurrentPosition());
            Log.i("日志1", "页面失去焦点，暂停视频后记录视频播放位置");
        }
    }

    /**
     * Activity生命周期 ：页面销毁
     * 要清空数据集合，释放内存资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentVideoView = videoViewMap.get(currentItem);
        if (currentVideoView != null) {
            Log.i("日志1", "页面被销毁，开始回收资源");
            currentVideoView.stopPlayback();
            currentVideoView.suspend();
        }
        clearList();
        Log.i("日志1", "页面被销毁，回收资源完成");
    }

    /**
     * 清空static数据
     */
    private void clearList() {
        videoViewMap.clear();
        mediaControllerMap.clear();
        videoIconMap.clear();
        playingPositionMap.clear();
        isPlayingMap.clear();
    }

    /**
     * Activity生命周期 ：页面重新对用户可见时，
     * 根据记录的播放进度设置进度再判断是否播放
     */
    @Override
    protected void onResume() {
        super.onResume();
        currentVideoView = videoViewMap.get(currentItem);
        if (currentVideoView != null) {
            Log.i("日志1", "页面重新可见，重新开始播放视频");
            currentVideoView.seekTo(playingPositionMap.get(currentItem));
            if (isPlayingMap.get(currentItem)) {
                currentVideoView.start();
            }
        }

    }

    //-------------------------------- 动画相关方法 ↓↓↓ --------------------------------
    @Override
    public void onBackPressed() {
        finishActivityAnim();
    }

    /**
     * 绘制前开始动画
     */
    @Override
    public boolean onPreDraw() {
        rootView.getViewTreeObserver().removeOnPreDrawListener(this);
        final View view = viewPagerAdapter.getPrimaryItem();
        final ImageView imageView = viewPagerAdapter.getPrimaryImageView();
        final VideoView videoView = viewPagerAdapter.getPrimaryVideoView();

        if (existVideoUrl(mediaItemList, currentItem)) {
            mediaItemView = (VideoView) videoView;
            computeMediaViewWidthAndHeight(mediaItemView);
        } else {
            mediaItemView = (ImageView) imageView;
            computeMediaViewWidthAndHeight(mediaItemView);
        }

        final MediaItem mediaItem = mediaItemList.get(currentItem);
        final float vx = mediaItem.nineGridViewItemWidth * 1.0f / mediaItemViewRealWidth;
        final float vy = mediaItem.nineGridViewItemHeight * 1.0f / mediaItemViewRealHeight;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long duration = animation.getDuration();
                long playTime = animation.getCurrentPlayTime();
                float fraction = duration > 0 ? (float) playTime / duration : 1f;
                if (fraction > 1)
                    fraction = 1;
                view.setTranslationX(evaluateInt(fraction, mediaItem.nineGridViewItemX + mediaItem.nineGridViewItemWidth / 2 - mediaItemView.getWidth() / 2, 0));
                view.setTranslationY(evaluateInt(fraction, mediaItem.nineGridViewItemY + mediaItem.nineGridViewItemHeight / 2 - mediaItemView.getHeight() / 2, 0));
                // 缩放
                view.setScaleX(evaluateFloat(fraction, vx, 1));
                view.setScaleY(evaluateFloat(fraction, vy, 1));
                view.setAlpha(fraction);
                rootView.setBackgroundColor(evaluateArgb(fraction, Color.TRANSPARENT, Color.BLACK));
            }
        });
        addIntoListener(valueAnimator);
        valueAnimator.setDuration(ANIMATE_DURATION);
        valueAnimator.start();

        return true;
    }

    /**
     * activity的退场动画
     */
    public void finishActivityAnim() {
        final View view = viewPagerAdapter.getPrimaryItem();
        // Activity中调用适配器里的方法
        final ImageView imageView = viewPagerAdapter.getPrimaryImageView();
        final VideoView videoView = viewPagerAdapter.getPrimaryVideoView();
        if (existVideoUrl(mediaItemList, currentItem)) {
            mediaItemView = (VideoView) videoView;
            computeMediaViewWidthAndHeight(mediaItemView);
        } else {
            mediaItemView = (ImageView) imageView;
            computeMediaViewWidthAndHeight(mediaItemView);
        }

        final MediaItem mediaItem = mediaItemList.get(currentItem);
        final float vx = mediaItem.nineGridViewItemWidth * 1.0f / mediaItemViewRealWidth;
        final float vy = mediaItem.nineGridViewItemHeight * 1.0f / mediaItemViewRealHeight;
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long duration = animation.getDuration();
                long playTime = animation.getCurrentPlayTime();
                float fraction = duration > 0 ? (float) playTime / duration : 1f;
                if (fraction > 1) fraction = 1;
                view.setTranslationX(evaluateInt(fraction, mediaItem.nineGridViewItemX + mediaItem.nineGridViewItemWidth / 2 - mediaItemView.getWidth() / 2, 0));
                view.setTranslationY(evaluateInt(fraction, mediaItem.nineGridViewItemY + mediaItem.nineGridViewItemHeight / 2 - mediaItemView.getHeight() / 2, 0));
                view.setScaleX(evaluateFloat(fraction, 1, vx));
                view.setScaleY(evaluateFloat(fraction, 1, vy));
                view.setAlpha(1 - fraction);
                rootView.setBackgroundColor(evaluateArgb(fraction, Color.BLACK, Color.TRANSPARENT));
            }
        });
        addOutListener(valueAnimator);
        valueAnimator.setDuration(ANIMATE_DURATION);
        valueAnimator.start();
    }

    /**
     * 计算图片/视频的宽高
     */
    private void computeMediaViewWidthAndHeight(View view) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            // 获取真实大小
            Drawable drawable = imageView.getDrawable();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            int intrinsicWidth = drawable.getIntrinsicWidth();
            // 计算出与屏幕的比例，用于比较以宽的比例为准还是高的比例为准，因为很多时候不是高度没充满，就是宽度没充满
            float h = screenHeight * 1.0f / intrinsicHeight;
            float w = screenWidth * 1.0f / intrinsicWidth;
            if (h > w) h = w;
            else w = h;
            // 得出当宽高至少有一个充满的时候图片对应的宽高
            mediaItemViewRealHeight = (int) (intrinsicHeight * h);
            mediaItemViewRealWidth = (int) (intrinsicWidth * w);
        } else if (view instanceof VideoView) {
            VideoView videoView = (VideoView) view;
            int intrinsicHeight = videoView.getHeight();
            int intrinsicWidth = videoView.getHeight();
            // 计算出与屏幕的比例，用于比较以宽的比例为准还是高的比例为准，因为很多时候不是高度没充满，就是宽度没充满
            float h = screenHeight * 1.0f / intrinsicHeight;
            float w = screenWidth * 1.0f / intrinsicWidth;
            if (h > w) h = w;
            else w = h;
            // 得出当宽高至少有一个充满的时候图片对应的宽高
            mediaItemViewRealHeight = (int) (intrinsicHeight * h);
            mediaItemViewRealWidth = (int) (intrinsicWidth * w);
        }
    }

    /**
     * 进场动画过程监听
     */
    private void addIntoListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootView.setBackgroundColor(0x0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 退场动画过程监听
     */
    private void addOutListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootView.setBackgroundColor(0x0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * Integer 估值器
     */
    public Integer evaluateInt(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int) (startInt + fraction * (endValue - startInt));
    }

    /**
     * Float 估值器
     */
    public Float evaluateFloat(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    /**
     * Argb 估值器
     */
    public int evaluateArgb(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        return (startA + (int) (fraction * (endA - startA))) << 24//
                | (startR + (int) (fraction * (endR - startR))) << 16//
                | (startG + (int) (fraction * (endG - startG))) << 8//
                | (startB + (int) (fraction * (endB - startB)));
    }

    /**
     * @param mediaItemList
     * @param position
     * @return
     */
    private boolean existVideoUrl(List<MediaItem> mediaItemList, int position) {
        return !TextUtils.isEmpty(mediaItemList.get(position).getVideoUrl());
    }
}
