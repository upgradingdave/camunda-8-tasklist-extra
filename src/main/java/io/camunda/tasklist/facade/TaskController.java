package io.camunda.tasklist.facade;

import io.camunda.tasklist.TaskListener;
import io.camunda.tasklist.annotations.TaskListenerProcessor;
import io.camunda.tasklist.annotations.TaskListenerRegistry;
import io.camunda.tasklist.rest.TaskListRestApi;
import io.camunda.tasklist.rest.dto.TaskResponse;
import io.camunda.tasklist.rest.dto.TaskSearchRequest;
import io.camunda.tasklist.rest.dto.TaskSearchResponse;
import io.camunda.tasklist.rest.exception.TaskListException;
import io.camunda.tasklist.rest.exception.TaskListRestException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@CrossOrigin
public class TaskController {

  private static final Logger LOG = LoggerFactory.getLogger(TaskController.class);

  TaskListRestApi taskListRestApi;

  TaskListenerProcessor taskListenerProcessor;

  public TaskController(@Autowired TaskListRestApi client, @Autowired TaskListenerProcessor taskListenerProcessor) {
    this.taskListRestApi = client;
    this.taskListenerProcessor = taskListenerProcessor;
  }

  @PostMapping("/search")
  public List<TaskSearchResponse> startProcessInstance(@RequestBody TaskSearchRequest request)
      throws TaskListException, TaskListRestException {
    return taskListRestApi.searchTasks(request);
  }

  @GetMapping("/{taskId}")
  public TaskResponse getTask(@PathVariable String taskId)
      throws TaskListException, TaskListRestException {
    return taskListRestApi.getTask(taskId);
  }

  @PostMapping("/{taskId}/complete")
  public TaskResponse startProcessInstance(
      @PathVariable String taskId, @RequestBody Map<String, Object> request)
      throws TaskListException, TaskListRestException {

    return taskListRestApi.completeTask(taskId, request);
  }
}
