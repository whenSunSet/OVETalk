package talk.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.IOException;

import talk.Globle.GlobleMethod;
import talk.util.DiskLruCache;

/**
 * Created by heshixiyang on 2016/3/23.
 */
public class BasicAdapter extends ArrayAdapter {

    private LruCache<String,Bitmap> mMemoryCache;

    private DiskLruCache mDiskLruCache;

    private int mResource;

    private Context mContext;

    public BasicAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };

        try {
            // 获取图片缓存路径
            File cacheDir = new File(GlobleMethod.getCacheDir(context));
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            // 创建DiskLruCache实例，初始化缓存数据
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

}
