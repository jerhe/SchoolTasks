package server.api.task.comment;

import com.edu.schooltask.item.TaskComment;

import java.util.ArrayList;
import java.util.List;

import server.api.base.BaseEvent;

/**
 * Created by 夜夜通宵 on 2017/5/20.
 */

public class GetTaskCommentEvent extends BaseEvent {
    List<TaskComment> taskComments;

    public GetTaskCommentEvent() {
    }

    public GetTaskCommentEvent(boolean ok) {
        super(ok);
    }

    public GetTaskCommentEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public List<TaskComment> getTaskComments() {
        return taskComments;
    }

    public void setTaskComments(List<TaskComment> taskComments) {
        this.taskComments = taskComments;
    }
}
