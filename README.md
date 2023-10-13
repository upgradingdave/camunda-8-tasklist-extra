[![Community Extension](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)

![Compatible with: Camunda Platform 8](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%208-0072Ce)

[![](https://img.shields.io/badge/Lifecycle-Proof%20of%20Concept-blueviolet)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#proof-of-concept-)

# Camunda 8 Task List Extra

This project contains a rest api that includes all the Task List Rest API calls, plus extra functionality commonly
needed for custom Task List implementations

# Getting Started

Build and compile using:

```shell
./mvnw clean install
```

Start the Spring Boot Application using:

```shell
./mvnw spring-boot:run
```

Then browse to [http://localhost:8087/swagger-ui/index.html](http://localhost:8087/swagger-ui/index.html) to see available
rest api endpoints

# Task Listeners

To register a Task Listenr, create a class that implements [TaskListener](src/main/java/io/camunda/tasklist/TaskListener.java) interface and annotated it with `@TaskListener`. Then register it as a Spring Bean.

For example, the following code will be called whenever an instance arrives at a User Task with Activity Id `"chooseFavoriteColor"`:

```java
@TaskListenerEnabled(activityId = "chooseFavoriteColor")
public class FavoriteColorTaskListener extends TaskListener {

  Logger LOGGER = LoggerFactory.getLogger(FavoriteColorTaskListener.class);

  @Override
  public void onTaskCreate(
      final JobClient client,
      final ActivatedJob job,
      Map<String, Object> variables,
      Map<String, String> headers) {
    LOGGER.info(FavoriteColorTaskListener.class + ": onTaskCreate");
  }
}
```

The listener will run for all User Tasks if you set `ativityId` to `"all"`, or to an empty String `""`.

## WebSocket Task Listener

When an instance moves from one User Task to the next, it may take a few seconds for TaskList (and TaskList rest
api calls) to "catch up" with Zeebe. This is inconvenient because the end user experience may feel "slow" even though
the Zeebe Engine has already created the new task.

In other words, here's one approach:

1. Poll for assigned tasks
2. Display task
3. User completes task
4. Go to step 1

Unfortunately, step 1 has to wait for TaskList to do some elasticsearch indexing.

A workaround is to get the task directly from Zeebe instead of waiting for TaskList. Here's a different approach:

1. Poll for assigned tasks
2. Display task
3. User completes task
4. User Task Job Worker immediately recognizes new assigned tasks
5. Go to step 1

*Note that steps 4 and 5 can happen asynchronously

The [WebSocketTaskListener](src/main/java/io/camunda/tasklist/listeners/WebSocketTaskListener.java) shows how we can take
advantage of this new approach. As soon as a User Task Job Worker sees the new task, the [WebSocketTaskListener](src/main/java/io/camunda/tasklist/listeners/WebSocketTaskListener.java) will get triggered. Inside the listener, the new task is sent directly to the javascript app running in the browser via web socket.

The javascript app can then immediately display the form for the next assigned task.

At the same time, the javascript app can also continue to poll the TaskList api as before, and eventually, a second
later, usually before the user can even complete the form, the task is also available via TaskList rest api.

# Sample ReactJs Front End

This project includes a sample front end written in ReactJs with NodeJS v18.5.0 (npm v8.12.1) and uses [Carbon](https://github.com/carbon-design-system/carbon) for css styling.

See [src/main/js/README.md](src/main/js/README.md) for more details on how to build and run the ReactJs App.

The first time the ReactJs starts, unless a user is signed in, [welcome.form.json](src/main/js/src/forms/welcome.form.json) will be displayed.

To simulate a user has already signed in, pass the query param `?userName=<userName>`.



