package com.pathnet.utils;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.pathnet.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoUtil {
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;// 4.4
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 根据文件Uri获取路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getFilePathByFileUri(Context context, Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 回收垃圾 recycle
     *
     * @throws
     */
    public static void recycle(Bitmap bitmap) {
        // 先判断是否已经回收
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    /**
     * 获取指定路径下的图片的指定大小的缩略图
     *
     * @return Bitmap
     * @throws
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * saveBitmap
     *
     * @param @param filename---完整的路径格式-包含目录以及文件名
     * @param @param bitmap
     * @param @param isDelete --是否只留一张
     * @return void
     * @throws
     */
    public static void saveBitmap(String dirpath, String filename, Bitmap bitmap, boolean isDelete) {
        File dir = new File(dirpath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dirpath, filename);
        // 若存在即删除-默认只保留一张
        if (isDelete) {
            if (file.exists()) {
                file.delete();
            }
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // Auto-generated catch block
                e.printStackTrace();
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param filePath
     * @param fileName
     * @return
     */
    public static File getFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片一定角度 rotaingImageView
     *
     * @return Bitmap
     * @throws
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 根据路径获得图片并压缩返回bitmap用于显示
     *
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 将图片变为圆角
     *
     * @param bitmap 原Bitmap图片
     * @param pixels 图片圆角的弧度(单位:像素(px))
     * @return 带有圆角的图片(Bitmap 类型)
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 将图片转化为圆形头像
     *
     * @throws
     * @Title: toRoundBitmap
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);// 设置画笔无锯齿
        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        // 以下有两种方法画圆,drawRounRect和drawCircle
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        // canvas.drawCircle(roundPx, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        return output;
    }

    /**
     * 获取图片的旋转角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照指定的角度进行旋转
     *
     * @param bitmap 需要旋转的图片
     * @param degree 指定的旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bitmap, int degree) {
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return newBitmap;
    }

    /**
     * @param context
     * @param imageView 显示的控件
     * @param imagePath url
     * @Title: imageLoaderLoadImg
     * @Description: 图片加载
     * @return: void
     */
    public static void imageLoaderLoadImg(Context context, ImageView imageView, String imagePath) {
        ImageLoaderUtil.init(context).displayListItemImage(imagePath, imageView);
    }

    /**
     * @param context
     * @param imageView 显示的控件
     * @param imagePath url
     * @param width     压缩的宽度
     * @param height    压缩的高度
     * @param scale     压缩率
     * @Title: imageLoaderLoadImg
     * @Description: 图片加载ImageLoader
     * @return: void
     */
    public static void imageLoaderLoadImg(Context context, ImageView imageView, String imagePath, int width, int height, int scale) {
    }

    public static void glideLoadImg(Context context, ImageView imageView, String imagePath) {
        Glide.with(context).load(imagePath)
                // 手动格式化
                .placeholder(R.mipmap.ic_launcher)
                // 手动格式化
                .fallback(R.mipmap.ic_launcher)
                // 手动格式化
                .error(R.mipmap.ic_launcher)
                // 手动格式化
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                // 手动格式化
                .into(imageView);
    }

    public static void glideLoadImg(Context context, ImageView imageView, String imagePath, int defImage) {
        Glide.with(context)
                // 手动格式化
                .load(imagePath)
                // 手动格式化
                .placeholder(defImage)// 加载过程中的占住位
                // 手动格式化
                .fallback(defImage)
                // 手动格式化
                .error(defImage)
                // 手动格式化
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                // 手动格式化
                .into(imageView);
    }

    public static void glideLoadImgTransformation(Context context, ImageView imageView, String imagePath, int defImage) {
        Glide.with(context).load(imagePath)
                // .asGif()
                .transform(new MyTransformation(context))// 圆形图片
                // 手动格式化
                .placeholder(defImage)// 加载过程中的占住位
                // 手动格式化
                .fallback(defImage)
                // 手动格式化
                .error(defImage)
                // 手动格式化
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                // 手动格式化
                .into(imageView);
    }

    public static void glideLoadImgGif(Context context, ImageView imageView, String imagePath, int defImage) {
        Glide.with(context).load(imagePath)
                // 手动格式化
                .asGif()
                // 手动格式化
                .placeholder(defImage)// 加载过程中的占住位
                // 手动格式化
                .fallback(defImage)
                // 手动格式化
                .error(defImage)
                // 手动格式化
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                // 手动格式化
                .into(imageView);
    }

    /**
     * @param context
     * @param imageView 图片控件
     * @param imagePath url
     * @param width     图片的宽度
     * @param height    图片的高度
     * @Title: glideLoadImg
     * @Description: 加载图片带压缩参数
     * @return: void
     */
    public static void glideLoadImg(Context context, ImageView imageView, String imagePath, int defImage, int width, int height, int scale) {
//		Glide.with(context)
        // 手动格式化
//				.load(imagePath + "?imageView2/2/w/" + Util.dip2px(context, width) + "/h/" + Util.dip2px(context, height) + "/interlace/0/q/" + scale)
        // 手动格式化
//				.placeholder(defImage)
        // 手动格式化
//				.error(defImage)
        // 手动格式化
//				.into(imageView);
    }

    /**
     * @param context
     * @param imageView 显示的控件
     * @param imagePath url
     * @param defImage  默认图片
     * @param width     压缩的宽度
     * @param height    压缩的高度
     * @Title: glideLoadImg
     * @Description: 图片加载--默认压缩率60
     * @return: void
     */
    public static void glideLoadImg(Context context, ImageView imageView, String imagePath, int defImage, int width, int height) {
//		Glide.with(context)
//		// 手动格式化
//				.load(imagePath + "?imageView2/2/w/" + Util.dip2px(context, width) + "/h/" + Util.dip2px(context, height) + "/interlace/0/q/60")
//				// 手动格式化
//				.placeholder(defImage)
//				// 手动格式化
//				.error(defImage)
//				// 手动格式化
//				.into(imageView);
    }

    /**
     * @param context
     * @param imageView 显示的控件
     * @param imagePath url
     * @param defImage  默认图片
     * @param width     压缩的宽度
     * @param height    压缩的高度
     * @Title: glideLoadImg
     * @Description: 图片加载--默认压缩率60
     * @return: void
     */
    public static void glideLoadImgAll(Context context, ImageView imageView, String imagePath, int defImage, int width, int height) {
//		Glide.with(context)
//		// 设置load失败时显示的Drawable
//				.load(imagePath + "?imageView2/2/w/" + Util.dip2px(context, width) + "/h/" + Util.dip2px(context, height) + "/interlace/0/q/60")
//				// 手动格式化
//				.placeholder(defImage)
//				// 手动格式化
//				.error(defImage)
//				// 设置是否跳过内存缓存，但不保证一定不被缓存（比如请求已经在加载资源且没设置跳过内存缓存，这个资源就会被缓存在内存中）
//				.skipMemoryCache(true)
//				// 监听资源加载的请求状态，可以使用两个回调
//				.listener(new RequestListener<String, GlideDrawable>() {
//					@Override
//					public boolean onException(Exception arg0, String arg1, Target<GlideDrawable> arg2, boolean arg3) {
//						return false;
//					}
//
//					@Override
//					public boolean onResourceReady(GlideDrawable arg0, String arg1, Target<GlideDrawable> arg2, boolean arg3, boolean arg4) {
//						return false;
//					}
//				})
//				// 手动格式化
//				.into(imageView);
    }

    /**
     * @param context
     * @param imageView 图片控件
     * @param imagePath 图片路径
     * @Title: picassoLoadImg
     * @Description:
     * @return: void
     */
    public static void picassoLoadImg(Context context, ImageView imageView, String imagePath) {
//		Picasso.with(context)
//		// 手动格式化
//				.load(imagePath)
//				// 手动格式化
//				.placeholder(R.drawable.normal_icon)
//				// 手动格式化
//				.error(R.drawable.normal_icon)
//				// 手动格式化
//				.into(imageView);
    }

    /**
     * @param context
     * @param imageView 图片控件
     * @param imagePath url
     * @param width     图片的宽度
     * @param height    图片的高度
     * @Title: picassoLoadImg
     * @Description: 加载图片带压缩参数
     * @return: void
     */
    public static void picassoLoadImg(Context context, ImageView imageView, String imagePath, int defImage, int width, int height, int scale) {
//		Picasso.with(context)
//		// 手动格式化
//				.load(imagePath + "?imageView2/2/w/" + Util.dip2px(context, width) + "/h/" + Util.dip2px(context, height) + "/interlace/0/q/" + scale)
//				// 预加载
//				.placeholder(defImage)
//				// 错误时显示的图片
//				.error(defImage)
//				// 加载控件
//				.into(imageView);
    }

    /**
     * @param context
     * @param imageView 图片控件
     * @param imagePath url
     * @param width     图片的宽度
     * @param height    图片的高度
     * @Title: picassoLoadImg
     * @Description: 加载图片带压缩参数-指定大小
     * @return: void
     */
    public static void picassoLoadImgSize(Context context, ImageView imageView, String imagePath, int defImage, int width, int height, int scale) {
//		String img = "?imageView2/2/w/" + Util.dip2px(context, width) + "/h/" + Util.dip2px(context, height) + "/interlace/0/q/" + scale;
//		Log.e("path", img);
//		Picasso.with(context)
//		// 手动格式化
//				.load(imagePath)
//				// 手动格式化
//				.placeholder(defImage)
//				// 指定剪裁大小
//				// .resize(width, height)
//				// 剪裁
//				// .centerCrop()
//				// 错误时
//				.error(defImage)
//				// 手动格式化
//				.into(imageView);
    }

    /**
     * @param context
     * @param imageView 图片控件
     * @param imagePath 图片路径
     * @Title: picassoLoadImg
     * @Description:
     * @return: void
     */
    public static void picassoLoadImg(final Context context, final ImageView imageView, final String imagePath, final int defImage) {
        Picasso.with(context).load(imagePath).placeholder(defImage).error(defImage).into(imageView);
    }

    /**
     * @param context
     * @param imageView 图片控件
     * @param imagePath 图片路径
     * @Title: picassoLoadImg
     * @Description:
     * @return: void
     */
    public static void picassoLoadImgAll(final Context context, final ImageView imageView, final String imagePath, int defImage) {
        Picasso.with(context)
                // 图片的URL
                .load(imagePath)
                // 裁剪图片尺寸
                .resize(200, 200)
                // 设置图片圆角
                .centerCrop()
                // 手动格式化
                .centerInside()
                // NO_CACHE是指图片加载时放弃在内存缓存中查找，NO_STORE是指图片加载完不缓存在内存中
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                // 加载过程中的图片显示
                .placeholder(defImage)
                // 如果重试3次还是无法成功加载图片，则用错误占位符图片显示
                .error(defImage)
                // 加速显示图片???
                .noFade()
                // 自动
                .fit()
                // 图片最终要展示的地方
                .into(imageView);
    }

    public static View makeMeasureSpec(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        return view;
    }

    /**
     * @Copyright © 2016 上海安捷力信息系统有限公司. All rights reserved.
     * @Title: PhotoUtil.java
     * @Prject: ShopSales1017
     * @Package: com.asc.businesscontrol.util
     * @Description: 圆形图片剪裁
     * @author:孙波
     * @date: 2016-11-16 下午2:03:47
     * @version: V2.1.0
     */
    private static class MyTransformation extends BitmapTransformation {
        public MyTransformation(Context context) {
            super(context);
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Bitmap result = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
            // 如果BitmapPool中找不到符合该条件的Bitmap，get()方法会返回null，就需要我们自己创建Bitmap了
            if (result == null) {
                // 如果想让Bitmap支持透明度，就需要使用ARGB_8888
                result = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
            }
            // 创建最终Bitmap的Canvas.
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setAlpha(128);
            // 将原始Bitmap处理后画到最终Bitmap中
            canvas.drawBitmap(toTransform, 0, 0, paint);
            // 由于我们的图片处理替换了原始Bitmap，就return我们新的Bitmap就行。
            // Glide会自动帮我们回收原始Bitmap。
            return result;
        }

        @Override
        public String getId() {
            // Return some id that uniquely identifies your transformation.
            return "com.example.myapp.MyTransformation";
        }
    }

    /**
     * @Copyright © 2016 上海安捷力信息系统有限公司. All rights reserved.
     * @Title: PhotoUtil.java
     * @Prject: ShopSales1017
     * @Package: com.asc.businesscontrol.util
     * @Description: 圆角图片的处理
     * @author:孙波
     * @date: 2016-11-16 下午2:04:13
     * @version: V2.1.0
     */
    public class MyRoundedCornersTransformation implements Transformation<Bitmap> {
        private BitmapPool mBitmapPool;
        private int mRadius;

        public MyRoundedCornersTransformation(Context context, int mRadius) {
            this(Glide.get(context).getBitmapPool(), mRadius);
        }

        public MyRoundedCornersTransformation(BitmapPool mBitmapPool, int mRadius) {
            this.mBitmapPool = mBitmapPool;
            this.mRadius = mRadius;
        }

        @Override
        public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
            // 从其包装类中拿出Bitmap
            Bitmap source = resource.get();
            int width = source.getWidth();
            int height = source.getHeight();
            Bitmap result = mBitmapPool.get(width, height, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            // 以上已经算是教科书式写法了
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(new RectF(0, 0, width, height), mRadius, mRadius, paint);
            // 返回包装成Resource的最终Bitmap
            return BitmapResource.obtain(result, mBitmapPool);
        }

        @Override
        public String getId() {
            return "RoundedTransformation(radius=" + mRadius + ")";
        }
    }
}
