package huajiteam.zhbus;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by KelaKim on 2016/5/15.
 */
public class GetWebContent {
    OkHttpClient client = new OkHttpClient();

    Response httpGet(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }
}
