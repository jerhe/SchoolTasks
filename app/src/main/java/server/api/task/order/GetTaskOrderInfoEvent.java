package server.api.task.order;

import com.edu.schooltask.beans.TaskOrder;

import java.util.ArrayList;
import java.util.List;

import server.api.token.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/22.
 */

public class GetTaskOrderInfoEvent extends BaseTokenEvent {
    TaskOrder taskOrder;

    public GetTaskOrderInfoEvent() {
    }

    public GetTaskOrderInfoEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public GetTaskOrderInfoEvent(boolean validate, boolean update, String token) {
        super(validate, update, token);
    }

    public TaskOrder getTaskOrder() {
        return taskOrder;
    }

    public void setTaskOrder(TaskOrder taskOrder) {
        this.taskOrder = taskOrder;
    }
}
