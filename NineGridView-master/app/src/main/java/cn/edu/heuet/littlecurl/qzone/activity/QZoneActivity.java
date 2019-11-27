package cn.edu.heuet.littlecurl.qzone.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.edu.heuet.littlecurl.R;
import cn.edu.heuet.littlecurl.qzone.adapter.RecyclerVidewAdapter;
import cn.edu.heuet.littlecurl.qzone.bean.RecyclerViewItem;
import cn.edu.heuet.littlecurl.qzone.bean.Location;
import cn.edu.heuet.littlecurl.qzone.bean.MyMedia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class QZoneActivity extends AppCompatActivity {

    // Log打印的通用Tag
    private final String TAG = "QZoneActivity:";

    // 下拉刷新控件
    @BindView(R.id.ptr)
    PtrClassicFrameLayout ptr;

    // 数据展示
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private RecyclerVidewAdapter recyclerViewAdapter;
    private ArrayList<RecyclerViewItem> recyclerViewItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qzone);
        ButterKnife.bind(this);

        final String url = "http://192.168.1.118:8082/admin/bl/findList";

        // 加载后台数据
        // loadBackendData(url);

        // 自定义数据
        loadMyTestDate();

        // 布局管理器必须有，否则不显示布局
        // No layout manager attached; skipping layout
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        // RecyclerView适配器
        recyclerViewAdapter = new RecyclerVidewAdapter(this, recyclerViewItemList);
        recyclerView.setAdapter(recyclerViewAdapter);

        // 下拉刷新控件设置参数
        ptr.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // 检查是否可以刷新，这里使用默认的PtrHandler进行判断
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                // 加载数据（先清空原来的数据）
                recyclerViewItemList.clear();
                // loadBackendData(url);
                loadMyTestDate();
                // 打乱顺序（为了确认确实是刷新了）
//                Collections.shuffle(RecyclerViewItemList);
                // 通知适配器数据已经改变
                recyclerViewAdapter.notifyDataSetChanged();
                // 下拉刷新完成
                ptr.refreshComplete();
            }

        });
    }

    // 自定义的测试数据（假装这是网络请求并解析后的数据）
    private void loadMyTestDate() {
        // 先构造MyMedia
        String imgUrl1 = "http://i2.tiimg.com/702441/6e3d61b352409f34.png";
        String imgUrl2 = "http://i2.tiimg.com/702441/ca8092e87a2f2b30.jpg";
        String imgUrl3 = "http://i2.tiimg.com/702441/081b443af609c94c.png";
        String videoUrl1 = "http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4";
        String videoUrl2 = "http://littlecurl.imwork.net/english.mp4";
        MyMedia myMedia1 = new MyMedia(imgUrl1, videoUrl1);
        MyMedia myMedia2 = new MyMedia(imgUrl2, videoUrl1);
        MyMedia myMedia3 = new MyMedia(imgUrl3, videoUrl1);
        // 再构造mediaList
        ArrayList<MyMedia> mediaList = new ArrayList<>();
        for (int i = 0; i < 3; i++) { // 加入9张图片
            mediaList.add(myMedia1);
            mediaList.add(myMedia2);
            mediaList.add(myMedia3);
        }
        Location location = new Location();
        location.setAddress("Test Address");
        // 最后构造EvaluationItem
        final RecyclerViewItem recyclerViewItem1 = new RecyclerViewItem(mediaList, "河北经贸大学自强社是在校学生处指导、学生资助管理中心主办下，于2008年4月15日注册成立的，一个以在校学生为主体的学生公益社团。历经十年的发展，在学生处、学生资助管理中心的大力支持下，在每一届自强人的团结努力下，自强社已经由成... ", "2019-11-02",
                "10080", "自强社", location, imgUrl1);
        final RecyclerViewItem recyclerViewItem2 = new RecyclerViewItem(mediaList, "河北经贸大学信息技术学院成立于1996年，由原计算机系/经济信息系合并组建而成，是我校建设的第一批学院。", "2019-11-02",
                "10080", "信息技术学院", location, imgUrl2);
        final RecyclerViewItem recyclerViewItem3 = new RecyclerViewItem(mediaList, "河北经贸大学雷雨话剧社是河北经贸大学唯一以话剧为主，兼小品，相声等多种表演艺术形式，由一批热爱表演，热爱话剧，热爱中国传统艺术与当代流行艺术结合的同学共同组成的文艺类大型社团。雷雨话剧社坚持以追求话剧“更新颖”、“更大型”、“更专业”为奋斗目标，坚持在继承传统文化和前辈的演出经验... ", "2019-11-02",
                "10080", "雷雨话剧社", location, imgUrl3);
        recyclerViewItemList.add(recyclerViewItem1);
        recyclerViewItemList.add(recyclerViewItem2);
        recyclerViewItemList.add(recyclerViewItem3);
    }

    // 加载网络数据的实现
    @SuppressLint("StaticFieldLeak")
    private void loadBackendData(final String url) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String responseBodyStr = getResponseBodyStrByokhttp(url, "1", "10");
                return responseBodyStr;
            }

            @Override
            protected void onPostExecute(String responseBodyStr) {
                if (!TextUtils.isEmpty(responseBodyStr)) {
                    ArrayList<RecyclerViewItem> itemList = parseJSONData(responseBodyStr);
                    recyclerViewItemList.addAll(itemList);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }

    // 通过Okhttp发送POST请求，四步走
    private String getResponseBodyStrByokhttp(final String url, final String pageIndex, final String pageSize) {
        // 1、创建client
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
                .build();
        // 2、构建请求体(后台需要的参数)
        FormBody requestBody = new FormBody.Builder()
                .add("pageIndex", pageIndex)
                .add("pageSize", pageSize)
                .build();
        // 3、构建POST请求(将请求体和URL结合)
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        // 4、调用client的execute()方法实现发送POST请求
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                return body.string();
            } else {
                showToastOnThread(this, "后台响应异常: " + response.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 解析ResponseBodyStr中的Json数据
    private ArrayList<RecyclerViewItem> parseJSONData(String responseBodyStr) {
        JSONObject json = JSONObject.parseObject(responseBodyStr);
        JSONObject dataJson = (JSONObject) json.get("data");
        JSONArray jsonArray = (JSONArray) dataJson.get("list");
        ArrayList<RecyclerViewItem> resultList = (ArrayList) JSONArray.parseArray(jsonArray.toString(), RecyclerViewItem.class);
        return resultList != null ? resultList : (ArrayList) Collections.emptyList();
    }

    // 实现在子线程中线显示Toast
    private void showToastOnThread(final Context context, final String toastText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
