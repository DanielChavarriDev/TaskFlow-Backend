
-- Los proyectos y sus tareas existentes son datos de prueba.
-- La FK de tasks elimina en cascada las tareas asociadas.
DELETE FROM projects;

-- Cada proyecto debe pertenecer a un usuario.
ALTER TABLE projects
    ADD COLUMN user_id UUID NOT NULL;

-- Garantiza que el propietario exista en la tabla users.
ALTER TABLE projects
    ADD CONSTRAINT fk_projects_user
    FOREIGN KEY (user_id)
    REFERENCES users(id);

-- Índice para consultar rápidamente los proyectos de un usuario.
CREATE INDEX idx_projects_user_id
    ON projects(user_id);