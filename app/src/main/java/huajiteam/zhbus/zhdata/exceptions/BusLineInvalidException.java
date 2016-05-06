package huajiteam.zhbus.zhdata.exceptions;

import okhttp3.Response;

import java.io.IOException;

/**
 * Created by KelaKim on 2016/5/4.
 */
public class BusLineInvalidException extends IOException {
    private final Response response;
    public BusLineInvalidException(Response response) {
        this.response = response;
    }
    public Response getResponse() {
        return this.response;
    }
}
