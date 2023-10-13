package io.camunda.tasklist.listeners;

public class WebSocketTaskListener {

    /*
  Task task = new Task();

      task.setVariables(variables);

      String processDefinitionKey = Long.toString(job.getProcessDefinitionKey());
      task.setProcessDefinitionId(processDefinitionKey);

      String formKey = headers.get("io.camunda.zeebe:formKey");
      task.setFormKey(formKey);

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
      String creationTime = sdf.format(new Date());
      task.setCreationTime(creationTime);

      // since 8.1.5, it seems that taskId and jobKey have become the same...
      String jobKey = Long.toString(job.getKey());

      // obsolete : But good news, job.getElementInstanceKey() is the "taskId" that is expected by
      // task list graphql
      // String taskId = Long.toString(job.getElementInstanceKey());
      task.setId(jobKey);
      task.setJobKey(jobKey);

      String bpmnProcessId = job.getBpmnProcessId();

      task.setProcessName(bpmnService.getProcessName(bpmnProcessId, processDefinitionKey));

      String taskActivityId = job.getElementId();
      // !!! The name of the bpmn file in the "src/main/resources/models" directory must match the
      // process id in order for this to work!
      String taskName =
          bpmnService.getTaskName(bpmnProcessId, processDefinitionKey, taskActivityId);
      task.setName(taskName);

      if (!job.getCustomHeaders().isEmpty()) {
        if (job.getCustomHeaders().containsKey("io.camunda.zeebe:assignee")) {
          task.setAssignee(job.getCustomHeaders().get("io.camunda.zeebe:assignee"));
        }
        if (job.getCustomHeaders().containsKey("io.camunda.zeebe:candidateGroups")) {
          String groups = job.getCustomHeaders().get("io.camunda.zeebe:candidateGroups");
          task.setCandidateGroups(
              JsonUtils.toParametrizedObject(groups, new TypeReference<List<String>>() {}));
        }
      }

      TaskState taskState = TaskState.CREATED;
      task.setTaskState(taskState);
      if (task.getAssignee() != null) {
        simpMessagingTemplate.convertAndSend("/topic/" + task.getAssignee() + "/userTask", task);
      } else {
        simpMessagingTemplate.convertAndSend("/topic/userTask", task);
      }
   */

}
