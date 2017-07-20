package server.api.base;

/**
 * Created by 夜夜通宵 on 2017/5/18.
 */

public class BaseEvent<T> {
    boolean ok;
    int code;
    String error;
    T data;

    public BaseEvent(){}

    public BaseEvent(boolean ok){
        this.ok = ok;
    }

    public BaseEvent(boolean ok, int code, String error){
        this.ok = ok;
        this.code = code;
        this.error = error;
    }

    public boolean isOk(){
        return ok;
    }

    public int getCode() {
        return code;
    }

    public BaseEvent setCode(int code) {
        this.code = code;
        return this;
    }

    public String getError() {
        return error;
    }

    public BaseEvent setError(String error) {
        this.error = error;
        return this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
