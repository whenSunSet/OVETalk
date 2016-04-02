package talk.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by heshixiyang on 2016/3/20.
 */
public class AsyncHttpClientUtil {
        private static AsyncHttpClient client = new AsyncHttpClient();

        public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.get(url, params, responseHandler);
            client.setConnectTimeout(500000);
            client.setTimeout(500000);
        }

        public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.post(url, params, responseHandler);
        }
}
