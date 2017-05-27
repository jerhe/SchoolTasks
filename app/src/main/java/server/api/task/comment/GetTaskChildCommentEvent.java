package server.api.task.comment;

import com.edu.schooltask.beans.TaskComment;

import java.util.List;

import server.api.base.BaseEvent;

/**
 * Created by 夜夜通宵 on 2017/5/20.
 */

public class GetTaskChildCommentEvent extends BaseEvent {
    List<TaskComment> taskComments;

    public GetTaskChildCommentEvent() {
    }

    public GetTaskChildCommentEvent(boolean ok) {
        super(ok);
    }

    public GetTaskChildCommentEvent(boolean ok, int code, String error) {
        super(ok, code, error);
    }

    public List<TaskComment> getTaskComments() {
        return taskComments;
    }

    public void setTaskComments(List<TaskComment> taskComments) {
        this.taskComments = taskComments;
    }
}
