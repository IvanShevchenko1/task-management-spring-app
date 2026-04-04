DELETE FROM tasks_labels;
DELETE FROM comments;
DELETE FROM attachments;
DELETE FROM labels;
DELETE FROM tasks;
DELETE FROM projects;

INSERT INTO projects (project_id, name, description, start_date, end_date, status, is_deleted, owner_id)
VALUES (1, 'User Project', 'Project owned by regular user', CURRENT_DATE, NULL, 'INITIATED', false, 2);

INSERT INTO projects (project_id, name, description, start_date, end_date, status, is_deleted, owner_id)
VALUES (2, 'Admin Project', 'Project owned by admin', CURRENT_DATE, NULL, 'IN_PROGRESS', false, 1);

INSERT INTO tasks (task_id, name, priority, status, due_date, is_deleted, project_id)
VALUES (1, 'Existing Task', 'HIGH', 'NOT_STARTED', DATEADD('DAY', 5, CURRENT_DATE), false, 1);

INSERT INTO tasks (task_id, name, priority, status, due_date, is_deleted, project_id)
VALUES (2, 'Admin Task', 'MEDIUM', 'IN_PROGRESS', DATEADD('DAY', 10, CURRENT_DATE), false, 2);

INSERT INTO labels (label_id, name, color, user_id, is_deleted)
VALUES (1, 'Urgent', '#FF0000', 2, false);

INSERT INTO labels (label_id, name, color, user_id, is_deleted)
VALUES (2, 'AdminLabel', '#00FF00', 1, false);

INSERT INTO comments (comment_id, task_id, user_id, text, timestamp, is_deleted)
VALUES (1, 1, 2, 'Existing comment', CURRENT_TIMESTAMP, false);

INSERT INTO tasks_labels (task_id, label_id)
VALUES (1, 1);

ALTER TABLE projects ALTER COLUMN project_id RESTART WITH 3;
ALTER TABLE tasks ALTER COLUMN task_id RESTART WITH 3;
ALTER TABLE labels ALTER COLUMN label_id RESTART WITH 3;
ALTER TABLE comments ALTER COLUMN comment_id RESTART WITH 2;
ALTER TABLE attachments ALTER COLUMN attachment_id RESTART WITH 1;