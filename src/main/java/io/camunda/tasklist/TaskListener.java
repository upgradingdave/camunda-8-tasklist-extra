package io.camunda.tasklist;

import io.camunda.tasklist.rest.dto.TaskResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.CustomHeaders;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import java.util.Map;

public abstract class TaskListener {

  public void onTaskCreate(
      final JobClient client,
      final ActivatedJob job,
      @VariablesAsType Map<String, Object> variables,
      @CustomHeaders Map<String, String> headers) {
    // not implemented
  }

  public void onTaskComplete(TaskResponse taskResponse) {
    // not implemented
  }
}
