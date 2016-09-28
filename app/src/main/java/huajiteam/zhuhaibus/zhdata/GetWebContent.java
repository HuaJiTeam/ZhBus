package huajiteam.zhuhaibus.zhdata;

import android.util.Log;

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

    Response getData(String httpUrl, String staticIP) throws IOException {
        String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_1 like Mac OS X) " +
                "AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13E238 " +
                "MicroMessenger/6.3.15 NetType/MOBILE Language/zh_CN";
        int endNumber;
        String flag = httpUrl.substring(0, 7);
        String host;
        String query;
        if (!flag.equals("http://")){
            if (flag.equals("https:/")) {
                flag = "https://";
                endNumber = httpUrl.substring(8).indexOf("/");
                if (endNumber != -1) {
                    host = httpUrl.substring(8, endNumber + 8);
                    query = httpUrl.substring(endNumber + 8);
                } else {
                    host = httpUrl.substring(8);
                    query = "";
                }
            } else {
                flag = "http://";
                endNumber = httpUrl.indexOf("/");
                if (endNumber != -1) {
                    host = httpUrl.substring(0, endNumber);
                    query = httpUrl.substring(endNumber);
                } else {
                    host = httpUrl;
                    query = "";
                }
            }
        } else {
            endNumber = httpUrl.substring(7).indexOf("/");
            if (endNumber != -1) {
                host = httpUrl.substring(7, endNumber + 7);
                query = httpUrl.substring(endNumber + 7);
            } else {
                host = httpUrl.substring(7);
                query = "";
            }
        }
        Log.i("flag", flag);
        Log.i("host", host);
        Log.i("query", query);
        Request request = new Request.Builder()
                .url(flag + staticIP + query)
                .header("User-Agent", USER_AGENT)
                .header("Host", host)
                .build();
        return okHttpClinet.newCall(request).execute();
    }
}
