package you.chen.bannertest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import chen.you.banner.BannerPager;
import chen.you.banner.IndicatorView;
import you.chen.bannertest.bean.BannerBean;

/**
 * author: you : 2022/12/19
 */
public final class TwoActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView rv;
    TwoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        rv = findViewById(R.id.rv);
        adapter = new TwoAdapter(this);
        rv.setAdapter(adapter);

        findViewById(R.id.bt).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt:
                adapter.testAdapter.setNewData(BannerBean.test2());
                break;
        }
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        TextViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(android.R.id.text1);
        }
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        BannerPager bp;
        IndicatorView iv;
        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bp = itemView.findViewById(R.id.bp);
            iv = itemView.findViewById(R.id.iv);
        }
    }

    static class TwoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        final TestAdapter testAdapter;

        LayoutInflater inflater;

        LifecycleOwner owner;

        public TwoAdapter(TwoActivity context) {
            inflater = LayoutInflater.from(context);
            this.testAdapter = new TestAdapter(context);
            owner = context;
        }

        @Override
        public int getItemCount() {
            return 50;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return 1;
            return super.getItemViewType(position);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View itemView = inflater.inflate(R.layout.item_two, parent, false);
                BannerViewHolder bvh = new BannerViewHolder(itemView);
//                Log.d("BannerPager", "onCreateBannerViewHolder...");
                bvh.bp.setAdapter(testAdapter);
                bvh.bp.setLifecycleOwner(owner);
                bvh.bp.setupWithIndicator(bvh.iv);
                return bvh;
            }
            View itemView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new TextViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                TextViewHolder vh = (TextViewHolder) holder;
                vh.tv.setText("this is item " + (position - 1));
            }
        }
    }
}
