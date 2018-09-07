package you.chen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import you.chen.R;
import you.chen.banner.BannerIndicator;
import you.chen.banner.BannerLayoutManager;
import you.chen.banner.BannerPager;
import you.chen.bean.BannerBean;

/**
 * Created by you on 2018/9/7.
 */

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    public ListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return 1 + 20;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == 0) {
            return new BannerHolder(inflater.inflate(R.layout.public_bannerpager, parent, false));
        } else {
            return new SimpleHolder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false));
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder.getItemViewType() == 0) {
            Log.i("you", "onViewDetachedFromWindow ");
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder.getItemViewType() == 0) {
            Log.i("you", "onViewAttachedToWindow ");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 1) {
            SimpleHolder vh = (SimpleHolder) holder;
            vh.text1.setText("position " + (position - 1));
        } else {
            BannerHolder vh = (BannerHolder) holder;
            if (vh.bp.getBannerAdapter() == null) {
                TestAdapter adapter = new TestAdapter(context);
                adapter.setNewDatas(BannerBean.test2());
                vh.bp.setBannerAdapter(adapter);
                vh.bi.setIndicatorCount(adapter.getItemCount());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position > 0) return 1;
        return 0;
    }

    class BannerHolder extends RecyclerView.ViewHolder {
        BannerPager bp;
        BannerIndicator bi;
        public BannerHolder(View itemView) {
            super(itemView);
            bp = (BannerPager) itemView.findViewById(R.id.bp);
            bi = (BannerIndicator) itemView.findViewById(R.id.bi);
            bp.setLayoutManager(new BannerLayoutManager(context));

            bp.addOnPageChangeListener(new BannerPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    bi.setCurrentIndicator(position);
                }
            });
        }
    }

    class SimpleHolder extends RecyclerView.ViewHolder {
        TextView text1;
        public SimpleHolder(View itemView) {
            super(itemView);
            text1 = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }

}
