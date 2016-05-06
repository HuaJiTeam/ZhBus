package huajiteam.zhbus.zhdata.exceptions;

import okhttp3.Response;

import java.io.IOException;

/**
 * Created by KelaKim on 2016/5/4.
 */
public class HttpCodeInvalidException extends IOException {
    private final Response response;
    public HttpCodeInvalidException(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return this.response;
    }
}
