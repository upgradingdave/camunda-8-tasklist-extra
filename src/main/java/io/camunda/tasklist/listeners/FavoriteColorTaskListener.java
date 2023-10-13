package io.camunda.tasklist.listeners;

import io.camunda.tasklist.TaskListener;
import io.camunda.tasklist.annotations.TaskListenerEnabled;
import io.camunda.tasklist.rest.dto.TaskResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TaskListenerEnabled(activityId = "chooseFavoriteColor")
public class FavoriteColorTaskListener extends TaskListener {

  Logger LOGGER = LoggerFactory.getLogger(FavoriteColorTaskListener.class);

  @Override
  public void onTaskCreate(
      final JobClient client,
      final ActivatedJob job,
      Map<String, Object> variables,
      Map<String, String> headers) {
    LOGGER.info("onTaskCreate");
  }

  public void onTaskComplete(TaskResponse taskResponse) {
    LOGGER.info("onTaskComplete");
  }
}
