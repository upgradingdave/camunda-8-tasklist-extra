package io.camunda.tasklist;

import io.camunda.tasklist.rest.dto.TaskResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;

public abstract class TaskListener {

  public void onTaskCreate(final JobClient client, final ActivatedJob job) {
    // not implemented
  }

  public void onTaskComplete(TaskResponse taskResponse) {
    // not implemented
  }
}
