package cn.edu.heuet.littlecurl.qzone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import cn.edu.heuet.littlecurl.R;
import cn.edu.heuet.littlecurl.ninegridview.detail.DetailPlayer;
import cn.edu.heuet.littlecurl.ninegridview.ui.NineGridViewGroup;
import cn.edu.heuet.littlecurl.qzone.activity.QZoneActivity;
import java.util.ArrayList;

/**
 * 这个类主要就是为了跳转页面
 */
public class LauncherActivity extends AppCompatActivity
        implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButtonAndListener();
    }

    private void initButtonAndListener(){
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button1:
                startActivity(new Intent(this, DetailPlayer.class));
                break;
            case R.id.button2:
                startActivity(new Intent(this, QZoneActivity.class));
                break;
        }
    }

}