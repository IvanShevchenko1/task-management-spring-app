DELETE FROM users_roles;
DELETE FROM users;

INSERT INTO users (user_id, username, password, email, first_name, last_name, is_deleted)
VALUES (
           1,
           'adminuser',
           '$2a$10$VHhn10y00DEVeysy8ConXeBAQWKdcvX0XaqXDYnAAY1MrNf0.zktW',
           'admin@example.com',
           'Admin',
           'User',
           false
       );

INSERT INTO users (user_id, username, password, email, first_name, last_name, is_deleted)
VALUES (
           2,
           'regularuser',
           '$2a$10$VHhn10y00DEVeysy8ConXeBAQWKdcvX0XaqXDYnAAY1MrNf0.zktW',
           'user@example.com',
           'Regular',
           'User',
           false
       );

INSERT INTO users (user_id, username, password, email, first_name, last_name, is_deleted)
VALUES (
           3,
           'anotheruser',
           '$2a$10$VHhn10y00DEVeysy8ConXeBAQWKdcvX0XaqXDYnAAY1MrNf0.zktW',
           'another@example.com',
           'Another',
           'User',
           false
       );

INSERT INTO users_roles (user_id, role_id)
SELECT 1, role_id FROM roles WHERE role = 'ADMIN';

INSERT INTO users_roles (user_id, role_id)
SELECT 2, role_id FROM roles WHERE role = 'USER';

INSERT INTO users_roles (user_id, role_id)
SELECT 3, role_id FROM roles WHERE role = 'USER';

ALTER TABLE users ALTER COLUMN user_id RESTART WITH 4;