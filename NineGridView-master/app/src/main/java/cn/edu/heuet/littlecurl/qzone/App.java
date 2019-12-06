package cn.edu.heuet.littlecurl.qzone;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.multidex.MultiDexApplication;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import cn.edu.heuet.littlecurl.ninegridview.preview.NineGridViewGroup;

/**
 * 此类继承自Application
 * 一般Application的作用就是在App启动的时候，
 * 加载那些只需要加载一次的东西
 * 比如：图片加载器
 * 该类需要在AndroidManifest.xml文件中进行配置
 * android:name=".App"
 */
public class App extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        setImageLoader();
    }

    private void setImageLoader() {
        NineGridViewGroup.setImageLoader(new GlideImageLoader());
    }

    /**
     * Glide 加载图片
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
