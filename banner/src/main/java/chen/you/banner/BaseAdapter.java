package chen.you.banner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * author: you : 2020/06/13
 * 如果需要更多功能可自实现{@link BannerPager.Adapter}
 * 亦可继承此类实现BindingAdapter, 见sample中的示例BannerBindingAdapter
 */
public abstract class BaseAdapter<T, VH extends BannerPager.ViewHolder> extends BannerPager.Adapter<VH> {

    private final LayoutInflater mInflater;
    //Adapter Data
    final List<T> mData = new ArrayList<>();
    //adapter view click call back
    private OnItemClickListener onItemClickListener;

    public BaseAdapter(Context context) {
        this(context, null);
    }

    public BaseAdapter(Context context, List<T> initData) {
        mInflater = LayoutInflater.from(context);
        if (initData != null) {
            mData.addAll(initData);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VH vh = onCreateItem(parent, viewType);
        onItemCreated(vh, viewType);
        initItemClickListener(vh);
        return vh;
    }

    protected abstract VH onCreateItem(@NonNull ViewGroup parent, int viewType);

    protected void onItemCreated(VH vh, int viewType) {
        //noting 可以做些子控件的事件绑定
    }

    public final LayoutInflater getLayoutInflater() {
        return mInflater;
    }

    public final List<T> getData() {
        return mData;
    }

    public final T getItem(int position) {
        return mData.get(position);
    }

    public void setNewData(List<T> newData) {
        if (mData.isEmpty()) {
            if (newData == null || newData.isEmpty()) return;
            mData.addAll(newData);
            notifyDataSetChanged();
        } else {
            mData.clear();
            if (newData != null) {
                mData.addAll(newData);
            }
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (mData.isEmpty()) return;
        mData.clear();
        notifyDataSetChanged();
    }

    /**
     * 初始化ViewHolder点击事件
     * @param vh
     */
    void initItemClickListener(final VH vh) {
        if (onItemClickListener != null) {
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, vh.getPosition());
                }
            });
        }
    }

    /**
     * Register a callback in this AdapterView has been clicked.
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        /**
         * @param view     viewholder itemview
         * @param position adapter position
         */
        void onItemClick(View view, int position);
    }
}
