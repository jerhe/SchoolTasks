package server.api.task.get;

import com.edu.schooltask.item.TaskItem;

import java.util.ArrayList;
import java.util.List;

import server.api.base.BaseEvent;

/**
 * Created by 夜夜通宵 on 2017/5/19.
 */

public class GetTaskListEvent extends BaseEvent {
    List<TaskItem> taskItems = new ArrayList<>();

    public GetTaskListEvent() {
        super();
    }

    public GetTaskListEvent(boolean ok) {
        super(ok);
    }

    public GetTaskListEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public List<TaskItem> getTaskItems() {
        return taskItems;
    }

    public void setTaskItems(List<TaskItem> taskItems) {
        this.taskItems = taskItems;
    }
}
