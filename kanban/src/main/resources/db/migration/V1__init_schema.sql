-- =========================
-- USERS
-- =========================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    avatar_color VARCHAR(255),
    updated_at TIMESTAMP NOT NULL,
    order_index INTEGER NOT NULL
);

-- =========================
-- PROJECTS
-- =========================
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT
);

-- =========================
-- PROJECT_USERS (M:N)
-- =========================
CREATE TABLE project_users (
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (project_id, user_id),
    CONSTRAINT fk_project_users_project
        FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_project_users_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =========================
-- TASKS
-- =========================
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    project_id BIGINT NOT NULL,
    assignee_id BIGINT,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    order_index INTEGER NOT NULL,

    CONSTRAINT fk_tasks_project
        FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_tasks_assignee
        FOREIGN KEY (assignee_id) REFERENCES users(id)
);

-- =========================
-- INDEXES
-- =========================
CREATE INDEX idx_tasks_project ON tasks(project_id);
CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);