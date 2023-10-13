package io.camunda.tasklist.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface TaskListenerEnabled {

  /**
   * Set the activityId to match a User Tasks's Activity Id. By default, this is set to an empty
   * string which leads to the class name being used
   */
  String activityId() default "";
}
