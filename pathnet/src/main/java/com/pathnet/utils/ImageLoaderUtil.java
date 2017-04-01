package com.pathnet.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pathnet.R;

import java.io.File;

/**
 * 配置全局的 Android-Universal-Image-Loader
 */
public class ImageLoaderUtil {
    private static ImageLoaderUtil instance = null;

    private ImageLoader mImageLoader;

    // 列表中默认的图片
    private DisplayImageOptions mListItemOptions;

    private ImageLoaderUtil(Context context) {
        mImageLoader = ImageLoader.getInstance();
        mListItemOptions = new DisplayImageOptions.Builder()
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showStubImage(R.mipmap.ic_launcher)
                // 设置图片加载/解码过程中错误时候显示的图片
                .showImageOnFail(R.mipmap.ic_launcher)
                // 加载图片时会在内存、磁盘中加载缓存
                .cacheInMemory()
                .cacheOnDisc()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .delayBeforeLoading(300)
                .build();

    }

    public synchronized static ImageLoaderUtil init(Context context) {
        if (instance == null) {
            instance = new ImageLoaderUtil(context);
        }
        File cacheDir = context.getExternalFilesDir("news/pictures");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(
                Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                // .imageDownloader(imageDownloader).imageDecoder(imageDecoder)
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).memoryCacheExtraOptions(
                        360, 360).memoryCache(new UsingFreqLimitedMemoryCache(4 * 1024 * 1024)).discCache(
                        new UnlimitedDiscCache(cacheDir)).build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        return instance;
    }

    /**
     * 列表图片
     *
     * @param uri
     * @param imageView
     */
    public void displayListItemImage(String uri, ImageView imageView) {
        mImageLoader.displayImage(null == uri ? "" : uri, imageView, mListItemOptions);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
