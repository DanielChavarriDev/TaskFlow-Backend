
-- 1. Actualiza los estados permitidos de las tareas.
ALTER TABLE tasks
    DROP CONSTRAINT chk_tasks_status;

ALTER TABLE tasks
    ADD CONSTRAINT chk_tasks_status
    CHECK (
        status IN (
            'PENDING',
            'IN_PROGRESS',
            'IN_REVIEW',
            'COMPLETED'
        )
    );

-- 2. Registra las intervenciones administrativas.
CREATE TABLE task_status_audit (
    id UUID PRIMARY KEY,
    task_id UUID NOT NULL,
    admin_user_id UUID NOT NULL,
    previous_status VARCHAR(20) NOT NULL,
    new_status VARCHAR(20) NOT NULL,
    reason VARCHAR(500) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_task_status_audit_admin
        FOREIGN KEY (admin_user_id)
        REFERENCES users(id),

    CONSTRAINT chk_task_status_audit_previous_status
        CHECK (
            previous_status IN (
                'PENDING',
                'IN_PROGRESS',
                'IN_REVIEW',
                'COMPLETED'
            )
        ),

    CONSTRAINT chk_task_status_audit_new_status
        CHECK (
            new_status IN (
                'PENDING',
                'IN_PROGRESS',
                'IN_REVIEW',
                'COMPLETED'
            )
        ),

    CONSTRAINT chk_task_status_audit_reason
        CHECK (LENGTH(TRIM(reason)) > 0)
);

CREATE INDEX idx_task_status_audit_task_id
    ON task_status_audit(task_id);

CREATE INDEX idx_task_status_audit_admin_user_id
    ON task_status_audit(admin_user_id);

CREATE INDEX idx_task_status_audit_created_at
    ON task_status_audit(created_at);