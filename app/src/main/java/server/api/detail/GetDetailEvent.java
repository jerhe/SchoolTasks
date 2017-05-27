package server.api.detail;

import com.edu.schooltask.base.Detail;

import java.util.List;

import server.api.token.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/26.
 */

public class GetDetailEvent extends BaseTokenEvent {
    List<Detail> details;

    public GetDetailEvent() {
        super();
    }

    public GetDetailEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public GetDetailEvent(boolean validate, boolean update, String token) {
        super(validate, update, token);
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }
}
