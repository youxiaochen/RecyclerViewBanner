package you.chen.bannertest.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import java.util.List;

import chen.you.banner.BaseAdapter;

/**
 * author: you : 2022/12/19
 */
public abstract class BannerBindingAdapter<T> extends BaseAdapter<T, BindingViewHolder> {

    public BannerBindingAdapter(Context context) {
        super(context);
    }

    public BannerBindingAdapter(Context context, List<T> initData) {
        super(context, initData);
    }

    @Override
    protected BindingViewHolder onCreateItem(@NonNull ViewGroup parent, int viewType) {
        return new BindingViewHolder(DataBindingUtil.inflate(getLayoutInflater(), getLayoutResId(viewType), parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder vh, int position) {
        vh.getBinding().setVariable(getVariableId(vh.getItemViewType()), getItem(position));
    }

    @Override
    public void onViewRecycled(@NonNull BindingViewHolder vh) {
        vh.getBinding().unbind();
    }

    @LayoutRes
    protected abstract int getLayoutResId(int viewType);

    protected abstract int getVariableId(int viewType);
}
