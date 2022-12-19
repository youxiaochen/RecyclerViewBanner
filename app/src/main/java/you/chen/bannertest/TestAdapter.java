package you.chen.bannertest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

import chen.you.banner.BannerPager;
import chen.you.banner.BaseAdapter;
import you.chen.bannertest.bean.BannerBean;

/**
 * author: you : 2022/12/19
 */
public final class TestAdapter extends BaseAdapter<BannerBean, TestAdapter.TestViewHolder> {

    RequestManager requestManager;

    public TestAdapter(Context context) {
        this(context, null);
    }

    public TestAdapter(Context context, List<BannerBean> initData) {
        super(context, initData);
        requestManager = Glide.with(context);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(view.getContext(), "Pos = " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder vh, int position) {
        BannerBean bean = getItem(position);
        vh.tv.setText(bean.des);
        requestManager.load(bean.url).into(vh.iv);
    }

    @Override
    protected TestAdapter.TestViewHolder onCreateItem(@NonNull ViewGroup parent, int viewType) {
        View itemView = getLayoutInflater().inflate(R.layout.item_main_banner, parent, false);
        return new TestViewHolder(itemView);
    }

    static class TestViewHolder extends BannerPager.ViewHolder {
        ImageView iv;
        TextView tv;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            tv = itemView.findViewById(R.id.tv);
        }
    }
}
