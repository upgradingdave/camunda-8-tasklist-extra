package io.camunda.tasklist.listeners;

import static io.camunda.tasklist.rest.dto.Constants.TASK_STATE_CREATED;

import com.fasterxml.jackson.core.type.TypeReference;
import io.camunda.tasklist.TaskListener;
import io.camunda.tasklist.annotations.TaskListenerEnabled;
import io.camunda.tasklist.rest.dto.TaskResponse;
import io.camunda.tasklist.service.JsonUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@TaskListenerEnabled(activityId = "all")
public class WebSocketTaskListener extends TaskListener {

  Logger LOGGER =
      LoggerFactory.getLogger(io.camunda.tasklist.listeners.WebSocketTaskListener.class);

  @Autowired private SimpMessagingTemplate simpMessagingTemplate;

  @Override
  public void onTaskCreate(
      final JobClient client,
      final ActivatedJob job,
      Map<String, Object> variables,
      Map<String, String> headers) {

    LOGGER.info("onTaskCreate");

    // Create a TaskResponse based on information passed to Job Worker
    TaskResponse taskResponse = new TaskResponse();

    taskResponse.setTaskState(TASK_STATE_CREATED);
    taskResponse.setProcessDefinitionKey(Long.toString(job.getProcessDefinitionKey()));
    taskResponse.setFormKey(headers.get("io.camunda.zeebe:formKey"));
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    String creationTime = sdf.format(new Date());
    taskResponse.setCreationDate(creationTime);
    taskResponse.setId(Long.toString(job.getKey()));

    // taskResponse.setProcessName(job.getBpmnProcessId());
    // task.setProcessName(bpmnService.getProcessName(bpmnProcessId, processDefinitionKey));
    // task.setVariables(variables);
    // String taskActivityId = job.getElementId();
    // !!! The name of the bpmn file in the "src/main/resources/models" directory must match the
    // process id in order for this to work!
    // String taskName = bpmnService.getTaskName(bpmnProcessId, processDefinitionKey,
    // taskActivityId);
    // task.setName(taskName);

    if (!job.getCustomHeaders().isEmpty()) {
      if (job.getCustomHeaders().containsKey("io.camunda.zeebe:assignee")) {
        taskResponse.setAssignee(job.getCustomHeaders().get("io.camunda.zeebe:assignee"));
      }
      if (job.getCustomHeaders().containsKey("io.camunda.zeebe:candidateGroups")) {
        String groups = job.getCustomHeaders().get("io.camunda.zeebe:candidateGroups");
        taskResponse.setCandidateGroups(
            JsonUtils.toParametrizedObject(groups, new TypeReference<List<String>>() {}));
      }
    }

    simpMessagingTemplate.convertAndSend("/topic/tasks", taskResponse);

    /*if (taskResponse.getAssignee() != null) {
      simpMessagingTemplate.convertAndSend(
          "/topic/" + taskResponse.getAssignee() + "/userTask", taskResponse);
    } else {
      simpMessagingTemplate.convertAndSend("/topic/userTask", taskResponse);
    }*/
  }
}
