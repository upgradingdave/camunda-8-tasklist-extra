package io.camunda.tasklist.facade;

import io.camunda.operate.rest.OperateRestClient;
import io.camunda.operate.rest.dto.ProcessDefinitionQuery;
import io.camunda.operate.rest.dto.ProcessDefinitionQueryResults;
import io.camunda.operate.rest.exception.OperateException;
import io.camunda.operate.rest.exception.OperateRestException;
import io.camunda.zeebe.client.ZeebeClient;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/process")
@CrossOrigin
public class ProcessController {

  private static final Logger LOG = LoggerFactory.getLogger(ProcessController.class);
  final ZeebeClient zeebe;
  final OperateRestClient operateRestClient;

  public ProcessController(
      @Autowired ZeebeClient client, @Autowired OperateRestClient operateRestClient) {
    this.zeebe = client;
    this.operateRestClient = operateRestClient;
  }

  @PostMapping("/start/{processId}")
  public void startProcessInstance(
      @PathVariable String processId, @RequestBody Map<String, Object> variables) {

    LOG.info("Starting process `" + processId + "` with variables: " + variables);

    zeebe
        .newCreateInstanceCommand()
        .bpmnProcessId(processId)
        .latestVersion()
        .variables(variables)
        .send();
  }

  @PostMapping("/message/{messageName}/{correlationKey}")
  public void publishMessage(
      @PathVariable String messageName,
      @PathVariable String correlationKey,
      @RequestBody Map<String, Object> variables) {

    LOG.info(
        "Publishing message `{}` with correlation key `{}` and variables: {}",
        messageName,
        correlationKey,
        variables);

    zeebe
        .newPublishMessageCommand()
        .messageName(messageName)
        .correlationKey(correlationKey)
        .variables(variables)
        .send();
  }

  @PostMapping("/process-definitions/search")
  public ProcessDefinitionQueryResults processDefinitionSearch(
      @RequestBody ProcessDefinitionQuery query) throws OperateRestException, OperateException {

    LOG.info("Searching for process definition `{}`", query);

    return operateRestClient.query(query);
  }

  @PostMapping("/complete-job/{jobId}")
  public void completeJob(@PathVariable String jobId, @RequestBody Map<String, Object> variables) {
    zeebe.newCompleteCommand(Long.parseLong(jobId)).variables(variables).send();
  }
}
