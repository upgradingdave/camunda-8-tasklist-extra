package io.camunda.tasklist.facade;

import io.camunda.tasklist.rest.TaskListRestApi;
import io.camunda.tasklist.rest.dto.FormResponse;
import io.camunda.tasklist.rest.exception.TaskListException;
import io.camunda.tasklist.rest.exception.TaskListRestException;
import io.camunda.tasklist.service.BpmnUtils;
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
  public FormResponse getFormById(
      @PathVariable String formId,
      @RequestParam("processDefinitionKey") String processDefinitionKey)
      throws TaskListException, TaskListRestException {
    return taskListRestApi.getForm(processDefinitionKey, formId);
  }

  @GetMapping("/{bpmnFileName}/{formId}")
  public FormResponse getLocalForm(@PathVariable String bpmnFileName, @PathVariable String formId) {
    String schema = BpmnUtils.getFormSchemaFromFile(bpmnFileName + ".bpmn", formId);
    FormResponse formResponse = new FormResponse();
    formResponse.setSchema(schema);
    return formResponse;
  }
}
