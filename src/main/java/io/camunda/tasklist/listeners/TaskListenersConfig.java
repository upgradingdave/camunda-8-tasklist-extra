package io.camunda.tasklist.listeners;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskListenersConfig {

  @Bean
  public FavoriteColorTaskListener favoriteColorTaskListener() {
    return new FavoriteColorTaskListener();
  }

  @Bean
  public EverythingTaskListener everythingTaskListener() {
    return new EverythingTaskListener();
  }

  @Bean
  public WebSocketTaskListener webSocketTaskListener() {
    return new WebSocketTaskListener();
  }
}
