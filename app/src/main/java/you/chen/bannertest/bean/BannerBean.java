package you.chen.bannertest.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dgaz on 2018/2/27.
 */

public class BannerBean implements Serializable {

    public String url;

    public String des;

    public int type; // 0 url为图片, 1 url为背景

    public BannerBean() {
    }

    public BannerBean(String url, String des, int type) {
        this.url = url;
        this.des = des;
        this.type = type;
    }

    public static List<BannerBean> test1() {
        List<BannerBean> list = new ArrayList<>();
        list.add(new BannerBean("https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=3594295685,223203405&fm=58&bpow=400&bpoh=578", "这是banner0", 1));
        list.add(new BannerBean("https://upload.jianshu.io/users/upload_avatars/5967497/9576ae38-d524-49f2-99dc-c406c13296be?imageMogr2/auto-orient/strip|imageView2/1/w/240/h/240", "这是banner1", 1));
        return list;
    }

    public static List<BannerBean> test2() {
        List<BannerBean> list = new ArrayList<>();
        list.add(new BannerBean("https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=3594295685,223203405&fm=58&bpow=400&bpoh=578", "这是banner0", 1));
        list.add(new BannerBean("https://upload.jianshu.io/users/upload_avatars/5967497/9576ae38-d524-49f2-99dc-c406c13296be?imageMogr2/auto-orient/strip|imageView2/1/w/240/h/240", "这是banner1", 1));
        list.add(new BannerBean("https://desk-fd.zol-img.com.cn/t_s208x130c5/g5/M00/01/0E/ChMkJ1bKwYeIX2fUAAiuu_mEKkgAALGZgNRy6AACK7T964.jpg", "这是banner2", 0));
        list.add(new BannerBean("https://desk-fd.zol-img.com.cn/t_s208x130c5/g5/M00/01/0E/ChMkJ1bKwXaIXdnDAAjG6xki36wAALGYgPXmb0ACMcD412.jpg", "这是banner3", 0));
        list.add(new BannerBean("https://upload.jianshu.io/users/qrcodes/5967497/IMG_0658.JPG?imageMogr2/auto-orient/strip|imageView2/1/w/84/h/84", "这是banner4", 1));
        return list;
    }

}
