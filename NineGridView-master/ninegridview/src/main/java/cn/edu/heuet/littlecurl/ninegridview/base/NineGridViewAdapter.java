package cn.edu.heuet.littlecurl.ninegridview.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.List;

import cn.edu.heuet.littlecurl.R;
import cn.edu.heuet.littlecurl.ninegridview.bean.MediaItem;
import cn.edu.heuet.littlecurl.ninegridview.detail.MediaDetailActivity;
import cn.edu.heuet.littlecurl.ninegridview.ui.NineGridItemWrapperView;
import cn.edu.heuet.littlecurl.ninegridview.ui.NineGridViewGroup;

/**
 * 这个类是此模块的入口
 */
public class NineGridViewAdapter {

    private List<MediaItem> mediaItemList;

    public NineGridViewAdapter(List<MediaItem> mediaItemList) {
       this.mediaItemList = mediaItemList;
    }
    public List<MediaItem> getMediaItemList() {
        return mediaItemList;
    }

    public void onMediaItemClick(Context context, NineGridViewGroup nineGridViewGroup,
                                 int index, List<MediaItem> mediaItemList) {
        // 遍历 mediaItemList，计算每张图片的宽高和图片起始点
        for (int i = 0; i < mediaItemList.size(); i++) {
            MediaItem mediaItem = mediaItemList.get(i);
            View nineGridViewItem;
            if (i < nineGridViewGroup.getMaxSize()) {
                nineGridViewItem = nineGridViewGroup.getChildAt(i);
            } else {
                nineGridViewItem = nineGridViewGroup.getChildAt(nineGridViewGroup.getMaxSize() - 1);
            }
            mediaItem.nineGridViewItemWidth = nineGridViewItem.getWidth();
            mediaItem.nineGridViewItemHeight = nineGridViewItem.getHeight();
            int[] points = new int[2];
            nineGridViewItem.getLocationInWindow(points);
            mediaItem.nineGridViewItemX = points[0];
            mediaItem.nineGridViewItemY = points[1];
        }

        Intent intent = new Intent(context, MediaDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MediaDetailActivity.MEDIA_INFO, (Serializable) mediaItemList);
        bundle.putInt(MediaDetailActivity.CURRENT_ITEM, index);
        intent.putExtras(bundle);
        context.startActivity(intent);
        Log.d("日志0","点击图片，跳转到详情");
        ((Activity) context).overridePendingTransition(0, 0);
    }

    public ImageView generateImageView(Context context) {
        NineGridItemWrapperView imageView = new NineGridItemWrapperView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.drawable.ic_default_color);
        return imageView;
    }
}
