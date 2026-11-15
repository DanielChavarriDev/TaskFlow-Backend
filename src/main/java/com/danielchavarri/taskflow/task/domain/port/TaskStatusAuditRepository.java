
package com.danielchavarri.taskflow.task.domain.port;

import com.danielchavarri.taskflow.task.domain.model.TaskStatusAudit;

import java.util.List;
import java.util.UUID;

public interface TaskStatusAuditRepository {

    TaskStatusAudit save(TaskStatusAudit audit);

    List<TaskStatusAudit> findAllByTaskId(UUID taskId);
}