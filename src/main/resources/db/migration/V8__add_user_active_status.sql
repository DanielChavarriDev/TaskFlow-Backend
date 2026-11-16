
ALTER TABLE users
ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

CREATE INDEX idx_users_active
ON users(active);