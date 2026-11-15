-- Garantiza la integridad referencial entre las auditorías y las tareas.
ALTER TABLE task_status_audit
    ADD CONSTRAINT fk_task_status_audit_task
    FOREIGN KEY (task_id)
    REFERENCES tasks(id)
    ON DELETE CASCADE;