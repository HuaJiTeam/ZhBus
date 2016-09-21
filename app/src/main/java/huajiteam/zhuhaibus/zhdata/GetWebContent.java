package huajiteam.zhuhaibus.zhdata;

import okhttp3.*;

import java.io.IOException;

/**
 * Created by KelaKim on 2016/5/4.
 */
class GetWebContent {
    private OkHttpClient okHttpClinet = new OkHttpClient();
    /*Response postJsonData(String httpUrl, String jsonData) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonData);
        String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_1 like Mac OS X) " +
                "AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13E238 " +
                "MicroMessenger/6.3.15 NetType/MOBILE Language/zh_CN";
        Request request = new Request.Builder()
                .url(httpUrl)
                .post(body)
                .header("User-Agent", USER_AGENT)
                .build();
        return okHttpClinet.newCall(request).execute();
    }*/

    Response getData(String httpUrl) throws IOException {
        String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_1 like Mac OS X) " +
                "AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13E238 " +
                "MicroMessenger/6.3.15 NetType/MOBILE Language/zh_CN";
        Request request = new Request.Builder()
                .url(httpUrl)
                .header("User-Agent", USER_AGENT)
                .build();
        return okHttpClinet.newCall(request).execute();
    }
}
