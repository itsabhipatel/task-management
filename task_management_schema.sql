-- INSERT DATA

INSERT INTO app_user (user_id, password, role) VALUES
('admin', '$2a$10$uUnS9TkG.ze7Hu9d5kgmQe..p8YbMq.j9HfpyVgUyntay5MYjHMaa', 'ROLE_ADMIN'),
('user', '$2a$10$kdeC2R3GZ.reN9pQIV6WVuNSVpmdcK/CeRkZRB.05POevcbAJ/Xpe', 'ROLE_USER');

INSERT INTO category (name) VALUES
('Development'),
('Testing'),
('Documentation');

INSERT INTO employee (name) VALUES
('Abhi Patel'),
('Neha Sharma'),
('Rahul Mehta'),
('Priya Nair'),
('Amit Shah');

INSERT INTO task (title, status, created_date, employee_id, category_id) VALUES
('Build login API', 'IN_PROGRESS', NOW() - INTERVAL 2 DAY, 1, 1),
('Write JWT filter tests', 'TODO', NOW() - INTERVAL 1 DAY, 2, 2),
('Prepare API documentation', 'DONE', NOW() - INTERVAL 4 DAY, 3, 3),
('Review task sorting endpoint', 'TODO', NOW(), 1, 1),
('Create task pagination API', 'DONE', NOW() - INTERVAL 5 DAY, 4, 1),
('Test H2 database setup', 'IN_PROGRESS', NOW() - INTERVAL 3 DAY, 5, 2),
('Update Swagger security docs', 'TODO', NOW() - INTERVAL 6 DAY, 3, 3),
('Fix task update validation', 'TODO', NOW() - INTERVAL 7 DAY, 2, 1),
('Check authorization errors', 'DONE', NOW() - INTERVAL 8 DAY, 4, 2),
('Clean demo test cases', 'IN_PROGRESS', NOW() - INTERVAL 9 DAY, 5, 3);