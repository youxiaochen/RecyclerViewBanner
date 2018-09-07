package you.chen.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import you.chen.R;
import you.chen.banner.BannerPager;
import you.chen.bean.BannerBean;

/**
 * Created by you on 2018/9/7.
 */
public class TestAdapter extends BannerPager.BannerAdapter<BannerPager.BannerViewHolder> {

    List<BannerBean> bannerBeanList = new ArrayList<>();

    Context context;

    public TestAdapter(Context context) {
        this.context = context;
    }

    public void setNewDatas(List<BannerBean> list) {
        bannerBeanList.clear();
        if (list != null && !list.isEmpty()) {
            bannerBeanList.addAll(list);
        }
    }

    @Override
    public int getItemCount() {
        return bannerBeanList.size();
    }

    @Override
    public BannerPager.BannerViewHolder onCreateView(ViewGroup parent, int viewType) {
        //Log.i("you", "onCreateV " + viewType);
        int layoutRes = viewType == 0 ? R.layout.banner_item1 : R.layout.banner_item2;
        View v = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        final BannerPager.BannerViewHolder vh = new BannerPager.BannerViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getBannerPosition(vh.getAdapterPosition());
                Toast.makeText(context, "click position " + position, Toast.LENGTH_SHORT).show();
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(BannerPager.BannerViewHolder vh, int position) {
        Log.i("you", "onBindViewHolder " + position);
        BannerBean bean = bannerBeanList.get(position);
        if (vh.getItemViewType() == 0) {
            ImageView iv1 = vh.getView(R.id.iv1);
            TextView tv1 = vh.getView(R.id.tv1);
            tv1.setText(bean.des);
            Glide.with(context).load(bean.url).into(iv1);
        } else {
            ImageView iv2 = vh.getView(R.id.iv2);
            TextView tv2 = vh.getView(R.id.tv2);
            tv2.setText(bean.des);
            Glide.with(context).load(bean.url).into(iv2);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return bannerBeanList.get(position).type;
    }
}
