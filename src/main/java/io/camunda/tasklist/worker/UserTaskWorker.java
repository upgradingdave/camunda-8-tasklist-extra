package io.camunda.tasklist.worker;

import io.camunda.tasklist.TaskListener;
import io.camunda.tasklist.annotations.TaskListenerProcessor;
import io.camunda.tasklist.annotations.TaskListenerRegistry;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.CustomHeaders;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTaskWorker {

  private static final Logger LOG = LoggerFactory.getLogger(UserTaskWorker.class);

  @Autowired TaskListenerProcessor taskListenerProcessor;

  @JobWorker(
      type = "io.camunda.zeebe:userTask",
      autoComplete = false,
      timeout = 2592000000L) // set timeout to 30 days
  public void listenUserTask(
      final JobClient client,
      final ActivatedJob job,
      @VariablesAsType Map<String, Object> variables,
      @CustomHeaders Map<String, String> headers) {

    try {

      LOG.info("User Task Worker triggered with variables: " + variables);

      // Call any Task Listeners that match the Activity Id of this User Task
      String activityId = job.getElementId();

      Map<String, TaskListenerRegistry> registryMap = taskListenerProcessor.getRegistry();
      for (String taskListenerActivityId : registryMap.keySet()) {
        if (taskListenerActivityId.equals("all") || taskListenerActivityId.equals(activityId)) {

          List<TaskListener> listeners = registryMap.get(taskListenerActivityId).getTaskListeners();
          for (TaskListener listener : listeners) {
            listener.onTaskCreate(client, job, variables, headers);
          }
        }
      }

    } catch (Exception e) {
      LOG.error("Exception occurred in UserTaskWorker", e);
      client
          .newFailCommand(job.getKey())
          .retries(0)
          .errorMessage("Exception occurred in UserTaskWorker - " + e.getMessage())
          .send();
    }
  }
}
