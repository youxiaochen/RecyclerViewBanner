package you.chen.bannertest;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.File;
import java.io.InputStream;

/**
 * Created by You on 2018-02-17.
 */
@GlideModule
public class OkHttpAppGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(final Context context, GlideBuilder builder) {
        DiskLruCacheFactory.CacheDirectoryGetter cacheGetter =
                () -> new File(getCacheDir(context), "imageCaches");
        //设置sdk img路径及大小
        builder.setDiskCache(new DiskLruCacheFactory(cacheGetter, 1024 * 1024 * 100));
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    /**
     * 获取缓存目录
     *
     * @return
     */
    static File getCacheDir(Context context) {
        File directory;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                directory = new File(context.getExternalCacheDir(), FILE_DIR);
            } else {
                directory = new File(Environment.getExternalStorageDirectory(), FILE_DIR);
            }
        } else {
            directory = new File(context.getCacheDir(), FILE_DIR);
        }
        if (!directory.exists()) directory.mkdirs();
        return directory;
    }

    /**
     * 缓存文件根目录名
     */
    private static final String FILE_DIR = "youbanner";

}
