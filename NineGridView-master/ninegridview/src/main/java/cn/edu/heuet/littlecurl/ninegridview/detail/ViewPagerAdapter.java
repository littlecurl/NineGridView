package cn.edu.heuet.littlecurl.ninegridview.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import cn.edu.heuet.littlecurl.R;
import cn.edu.heuet.littlecurl.ninegridview.bean.MediaItem;
import cn.edu.heuet.littlecurl.ninegridview.ui.NineGridViewGroup;

import java.util.List;
import java.util.Map;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 此类实现了点击图片之后
 * 如果存在视频地址，则加载视频
 * 否则加载图片
 */

public class ViewPagerAdapter extends PagerAdapter implements PhotoViewAttacher.OnPhotoTapListener {

    private List<MediaItem> mediaItemList;
    private Map<Integer,ViewGroup> viewGroupMap;
    private Context context;
    private View currentView;

    ViewPagerAdapter(Context context, @NonNull List<MediaItem> mediaItemList) {
        super();
        this.mediaItemList = mediaItemList;
        this.context = context;
    }

    ViewPagerAdapter(Context context, @NonNull List<MediaItem> mediaItemList, Map<Integer,ViewGroup> viewGroupMap) {
        super();
        this.mediaItemList = mediaItemList;
        this.context = context;
        this.viewGroupMap = viewGroupMap;
    }

    @Override
    public int getCount() {
        return mediaItemList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        currentView = (View) object;
    }

    View getPrimaryItem() {
        return currentView;
    }

    ImageView getPrimaryImageView() {
        return (ImageView) currentView.findViewById(R.id.pv);
    }

    VideoView getPrimaryVideoView() {
        return (VideoView) currentView.findViewById(R.id.videoplayer);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (viewGroupMap.get(position) != null)
        // 如果mediaInfo中存在videoUrl的话，加载视频
        {
            container.addView(viewGroupMap.get(position));
            return viewGroupMap.get(position);
        }
        // 如果mediaInfo中不存在videoUrl的话，加载图片大图
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_media, container, false);
            final PhotoView imageView = view.findViewById(R.id.pv);          // 大图
            imageView.setVisibility(View.VISIBLE);
            MediaItem mediaItem = this.mediaItemList.get(position);
            imageView.setOnPhotoTapListener(this);
            showExcessPic(mediaItem, imageView);
            NineGridViewGroup.getImageLoader().onDisplayImage(view.getContext(), imageView, mediaItem.bigImageUrl);
            container.addView(view);
            return view;
        }

    }

    /**
     * 展示过度图片
     */
    private void showExcessPic(MediaItem mediaItem, PhotoView imageView) {
        //先获取大图的缓存图片
        Bitmap cacheImage = NineGridViewGroup.getImageLoader().getCacheImage(mediaItem.bigImageUrl);
        //如果大图的缓存不存在,在获取小图的缓存
        if (cacheImage == null)
            cacheImage = NineGridViewGroup.getImageLoader().getCacheImage(mediaItem.thumbnailUrl);
        //如果没有任何缓存,使用默认图片,否者使用缓存
        if (cacheImage == null) {
            imageView.setImageResource(R.drawable.ic_default_color);
        } else {
            imageView.setImageBitmap(cacheImage);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        if (viewGroupMap.get(position) != null)
            container.removeView(viewGroupMap.get(position));
    }

    /**
     * 单击屏幕关闭
     */
    @Override
    public void onPhotoTap(View view, float x, float y) {
        ((MediaDetailActivity) context).finishActivityAnim();
    }

    private boolean existVideoUrl(List<MediaItem> mediaItem, int position) {
        return !TextUtils.isEmpty(mediaItem.get(position).getVideoUrl());
    }
}