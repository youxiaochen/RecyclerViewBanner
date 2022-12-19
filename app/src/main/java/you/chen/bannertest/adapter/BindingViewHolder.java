package you.chen.bannertest.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import chen.you.banner.BannerPager;

/**
 * author: you : 2022/12/19
 */
public final class BindingViewHolder extends BannerPager.ViewHolder {

    public BindingViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public <T extends ViewDataBinding> T getBinding() {
        return DataBindingUtil.getBinding(itemView);
    }
}
