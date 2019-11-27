package cn.edu.heuet.littlecurl.qzone;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import cn.edu.heuet.littlecurl.R;
import cn.edu.heuet.littlecurl.ninegridview.ui.NineGridViewGroup;

/**
 * 此类配置在AndroidManifest.xml文件中
 * android:name=".qzone.App"
 * 只会在应用启东时加载一次
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        setImageLoader();
    }

    private void setImageLoader() {
        NineGridViewGroup.setImageLoader(new GlideImageLoader());
    }

    /**
     * Glide 加载
     */
    private class GlideImageLoader implements NineGridViewGroup.ImageLoader {
        GlideImageLoader() {
        }
        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.ic_default_color)   // 图片未加载时的占位图或背景色
                    .error(R.drawable.ic_default_color)         // 图片加载失败时显示的图或背景色
                    .diskCacheStrategy(DiskCacheStrategy.ALL)   // 开启本地缓存
                    .into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }
}
