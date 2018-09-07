# RecyclerViewBanner
## 用RecyclerView实现Banner无限轮播效果, 效果与ViewPager一样不上传效果图

## 为何要用RecyclerView实现
#### 1: 既然是轮播就是内容模块大致相同,RecyclerView拥有强大的缓存机制可以直接实现相同Item的缓存
#### 2: RecyclerView的Adapter可以针对不同Item的  ViewType来缓存不同的Item
#### 3: ViewPager在生成Item的时候(Object instantiateItem(ViewGroup container, final int position))控件还未添加到ViewPager中,此时生成的Item中的控件的生命周期都没有触发,(比如获取Item控件的大小就无法做到), 而RecyclerView适配器中有onBindViewHolder机制与onViewRecycled, onViewAttachedToWindow,onViewDetachedFromWindow等强大的生命周期机制
#### 4: RecyclerView已经自带的解决了与ViewPager, RecyclerView等滑动控件中的嵌套滑动冲突
#### 5: 当Banner嵌套在RecyclerView或ListView中时, 在Banner滑动动画还结束时,如果列表滑动缓存了banner,此时ViewPager方式的Banner会出现动画停留甚至白屏的效果,而RecyclerView可以完美的解决这问题





