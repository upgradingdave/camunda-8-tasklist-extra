package io.camunda.tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.camunda.tasklist.annotations.TaskListenerProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ListenersTest {

  @Test
  public void loadContext() {}

  /*

  public void getTaskListenersForActivityId(String activityId) {
    //AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TaskListenersConfig.class);
    //GenericApplicationContext applicationContext1 = new GenericApplicationContext();
    Map<String, Object> beans = applicationContext.getBeansWithAnnotation(TaskListenerEnabled.class);
    assertNotNull(beans);
    for(String key : beans.keySet()) {
      TaskListener bean = (TaskListener) beans.get(key);
      ConfigurableListableBeanFactory factory = applicationContext.getBeanFactory();
      BeanDefinition beanDefinition = factory.getBeanDefinition(key);
      if(beanDefinition.getSource() instanceof AnnotatedTypeMetadata) {
        AnnotatedTypeMetadata metadata = (AnnotatedTypeMetadata) beanDefinition.getSource();
        Map<String, Object> attributes = metadata.getAnnotationAttributes(TaskListenerEnabled.class.getName());
        bean.onTaskCreate(null, null);
      }
    }
  } */

  @Autowired TaskListenerProcessor taskListenerProcessor;

  @Test
  public void testFindAnnotatedListeners() {
    assertEquals(taskListenerProcessor.getRegistry().size(), 1);
  }

  @SpringBootApplication
  public static class ListenersTestApp {
    public static void main(String[] args) {
      SpringApplication.run(ListenersTestApp.class, args);
    }
  }
}
