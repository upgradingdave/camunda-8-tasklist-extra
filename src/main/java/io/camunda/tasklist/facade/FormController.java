package io.camunda.tasklist.facade;

import io.camunda.tasklist.rest.TaskListRestApi;
import io.camunda.tasklist.rest.dto.FormResponse;
import io.camunda.tasklist.rest.exception.TaskListException;
import io.camunda.tasklist.rest.exception.TaskListRestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/forms")
@CrossOrigin
public class FormController {

  private static final Logger LOG = LoggerFactory.getLogger(FormController.class);

  TaskListRestApi taskListRestApi;

  public FormController(@Autowired TaskListRestApi client) {
    this.taskListRestApi = client;
  }

  @GetMapping("/{formId}")
  public FormResponse startProcessInstance(
      @PathVariable String formId,
      @RequestParam("processDefinitionKey") String processDefinitionKey)
      throws TaskListException, TaskListRestException {
    return taskListRestApi.getForm(processDefinitionKey, formId);
  }
}
