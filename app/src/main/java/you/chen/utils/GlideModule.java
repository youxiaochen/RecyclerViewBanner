package you.chen.utils;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpGlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;

import java.io.File;

/**
 * you
 */

public class GlideModule extends OkHttpGlideModule {

    @Override
    public void applyOptions(final Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        builder.setDiskCache(new DiskLruCacheFactory(new DiskLruCacheFactory.CacheDirectoryGetter() {
            @Override
            public File getCacheDirectory() {
                return new File(FileUtils.getCacheDir(context), "imageCaches");
            }
        }, 1024 * 1024 * 100));//设置sdk img路径及大小
    }

}
