package cn.edu.heuet.littlecurl.qzone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.heuet.littlecurl.ninegridview.base.NineGridViewAdapter;
import cn.edu.heuet.littlecurl.ninegridview.bean.NineGridItem;
import cn.edu.heuet.littlecurl.ninegridview.preview.NineGridViewGroup;
import cn.edu.heuet.littlecurl.qzone.R;
import cn.edu.heuet.littlecurl.qzone.bean.MyMedia;
import cn.edu.heuet.littlecurl.qzone.bean.RecyclerViewItem;

public class RecyclerVidewAdapter extends RecyclerView.Adapter<RecyclerVidewAdapter.ViewHolder> {

    private Context context;
    private List<RecyclerViewItem> recyclerViewItemList;

    public RecyclerVidewAdapter() {
    }

    /**
     * 接受外部传来的数据
     */
    public RecyclerVidewAdapter(Context context, List<RecyclerViewItem> recyclerViewItemList) {
        this.context = context;
        this.recyclerViewItemList = recyclerViewItemList;
    }

    /**
     * 填充视图
     */
    @NonNull
    @Override
    public RecyclerVidewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 获取控件
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView avatar;
        private final TextView tv_username;
        private final TextView tv_createTime;
        private final TextView tv_content;
        private final NineGridViewGroup nineGridViewGroup;
        private final TextView tv_location;
        private final ImageView iv_detail_triangle;
        private final ImageView iv_eye;
        private final ImageView iv_share;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 头像
            avatar = itemView.findViewById(R.id.avatar);
            // 用户名
            tv_username = itemView.findViewById(R.id.tv_username);
            // 创建时间
            tv_createTime = itemView.findViewById(R.id.tv_createTime);
            // 内容
            tv_content = itemView.findViewById(R.id.tv_content);
            // 图片九宫格控件
            nineGridViewGroup = itemView.findViewById(R.id.nineGrid);
            // 位置
            tv_location = itemView.findViewById(R.id.tv_location);
            // 位置详情三角小图标
            iv_detail_triangle = itemView.findViewById(R.id.iv_detail_triangle);
            // 围观眼睛小图标
            iv_eye = itemView.findViewById(R.id.iv_eye);
            // 分享小图标
            iv_share = itemView.findViewById(R.id.iv_share);

        }
    }

    /**
     * 绑定控件
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerVidewAdapter.ViewHolder holder, int position) {
        // 获取对应的数据
        RecyclerViewItem recyclerViewItem = recyclerViewItemList.get(position);

        // 往控件上绑定数据
        NineGridViewGroup.getImageLoader().onDisplayImage(context,holder.avatar,recyclerViewItem.getHeadImageUrl());
        holder.tv_username.setText(recyclerViewItem.getNickName());
        holder.tv_createTime.setText(recyclerViewItem.getCreateTime());
        holder.tv_content.setText(recyclerViewItem.getContent());
        holder.tv_location.setText(recyclerViewItem.getLocation().getAddress());
        holder.iv_detail_triangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "位置详情图标点击事件还未开发", Toast.LENGTH_SHORT).show();
            }
        });
        holder.iv_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "围观图标点击事件还未开发", Toast.LENGTH_SHORT).show();
            }
        });
        holder.iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "分享图标点击事件还未开发", Toast.LENGTH_SHORT).show();
            }
        });

        // 为满足九宫格适配器数据要求，需要构造对应的List
        ArrayList<MyMedia> mediaList = recyclerViewItem.getMediaList();
        // 没有数据就没有九宫格
        if (mediaList != null && mediaList.size() > 0) {
            ArrayList<NineGridItem> nineGridItemList = new ArrayList<>();
            for (MyMedia myMedia : mediaList) {
                String thumbnailUrl = myMedia.getImageUrl();
                String bigImageUrl = thumbnailUrl;
                String videoUrl = myMedia.getVideoUrl();
                nineGridItemList.add(new NineGridItem(thumbnailUrl, bigImageUrl, videoUrl));
            }
            NineGridViewAdapter nineGridViewAdapter = new NineGridViewAdapter(nineGridItemList);
            holder.nineGridViewGroup.setAdapter(nineGridViewAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return recyclerViewItemList.size();
    }
}
