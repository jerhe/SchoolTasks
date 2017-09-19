package server.api.result;

/**
 * Created by 夜夜通宵 on 2017/9/15.
 */

public abstract class Result extends BaseResult{

    private int id;
    private boolean withToken;

    public Result(){}

    public Result(boolean withToken){
        this.withToken = withToken;
    }

    public void setResult(boolean ok, int code, String error, Object data){
        setOk(ok);
        setCode(code);
        setError(error);
        setData(data);
    }

    public void setResult(BaseResult result){
        setOk(result.isOk());
        setCode(result.getCode());
        setError(result.getError());
        setData(result.getData());
        setToken(result.getToken());
        setValidate(result.isValidate());
        setUpdate(result.isUpdate());
    }

    /**
     * 请求返回优先执行
     * @param id
     */
    public abstract void onResponse(int id);

    /**
     * 请求成功
     * @param id
     * @param data
     */
    public abstract void onSuccess(int id, Object data);

    /**
     * 请求失败
     * @param id
     * @param code
     * @param error
     */
    public abstract void onFailed(int id, int code, String error);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isWithToken() {
        return withToken;
    }

    public void setWithToken(boolean withToken) {
        this.withToken = withToken;
    }
}

