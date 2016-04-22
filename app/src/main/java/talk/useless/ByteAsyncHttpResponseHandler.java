package talk.useless;

import android.content.Context;

import com.loopj.android.http.BinaryHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import talk.Globle.GlobleData;
import talk.util.DialogUtil;

/**
 * Created by heshixiyang on 2016/4/7.
 */
public class ByteAsyncHttpResponseHandler extends BinaryHttpResponseHandler {
    private Context context;
    private int statu;
    private byte[] bytes;
    private boolean isSuccess;

    public ByteAsyncHttpResponseHandler(Context context, int statu) {
        this.context = context;
        this.statu = statu;
    }

    @Override
    public void onSuccess(int i, Header[] headers, byte[] bytes) {
        switch (statu){
            case GlobleData.GET_TASK_FILE:
                DialogUtil.showToast(context,"下载任务文件成功");
                break;
            case GlobleData.GET_HOMEWORK_FILE:
                DialogUtil.showToast(context,"下载作业文件成功");
                break;
        }
        this.bytes=bytes;
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
        DialogUtil.showToast(context,"下载失败");
    }

    public byte[] getBytes() {
        return bytes;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
