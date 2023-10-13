package io.camunda.tasklist.annotations;

import io.camunda.tasklist.TaskListener;
import java.util.ArrayList;
import java.util.List;

public class TaskListenerRegistry {

  final String activityId;
  final List<TaskListener> taskListeners;

  TaskListenerRegistry(String activityId) {
    this.activityId = activityId;
    taskListeners = new ArrayList<>();
  }

  public void addListener(TaskListener listener) {
    taskListeners.add(listener);
  }

  public List<TaskListener> getTaskListeners() {
    return taskListeners;
  }
}
