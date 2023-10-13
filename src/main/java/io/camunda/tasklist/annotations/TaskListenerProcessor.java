package io.camunda.tasklist.annotations;

import io.camunda.tasklist.TaskListener;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class TaskListenerProcessor implements BeanPostProcessor {

  Map<String, TaskListenerRegistry> registry = new HashMap<>();

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean.getClass().isAnnotationPresent(TaskListenerEnabled.class)) {
      TaskListenerEnabled[] listenerEnableds =
          bean.getClass().getAnnotationsByType(TaskListenerEnabled.class);
      for (TaskListenerEnabled listenerEnabled : listenerEnableds) {
        String activityId = listenerEnabled.activityId();
        if (activityId == null || activityId.length() <= 0) {
          activityId = "all";
        }
        TaskListenerRegistry taskListenerRegistry = new TaskListenerRegistry(activityId);
        if (registry.get(activityId) != null) {
          taskListenerRegistry = registry.get(activityId);
        }
        taskListenerRegistry.addListener((TaskListener) bean);
        registry.put(activityId, taskListenerRegistry);
      }
    }
    return bean;
  }

  public Map<String, TaskListenerRegistry> getRegistry() {
    return registry;
  }
}
