package cn.edu.heuet.littlecurl.qzone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import cn.edu.heuet.littlecurl.qzone.activity.QZoneActivity;

/**
 * 这个类主要就是为了跳转页面
 */
public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置Button点击事件
        initButtonAndListener();
    }

    private void initButtonAndListener() {
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherActivity.this, QZoneActivity.class));
            }
        });

    }
}