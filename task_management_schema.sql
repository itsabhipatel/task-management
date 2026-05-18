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

INSERT INTO task (
    title,
    description,
    status,
    priority,
    due_date,
    created_date,
    updated_date,
    completed_date,
    progress_percentage,
    employee_id,
    category_id
) VALUES
('Build login API', 'Create the authentication endpoint and token response contract.', 'IN_PROGRESS', 'HIGH', NOW() + INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 1 DAY, NULL, 65, 1, 1),
('Write JWT filter tests', 'Cover valid, expired, and missing bearer token scenarios.', 'TODO', 'HIGH', NOW() + INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL, 10, 2, 2),
('Prepare API documentation', 'Document auth headers, task filters, and response examples.', 'DONE', 'MEDIUM', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, 100, 3, 3),
('Review task sorting endpoint', 'Verify sorting behavior across title, status, priority, and due date.', 'TODO', 'LOW', NOW() + INTERVAL 4 DAY, NOW(), NOW(), NULL, 0, 1, 1),
('Create task pagination API', 'Return stable page metadata for larger task backlogs.', 'DONE', 'MEDIUM', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, 100, 4, 1),
('Test H2 database setup', 'Validate local test profile behavior and schema compatibility.', 'IN_PROGRESS', 'MEDIUM', NOW() + INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 1 DAY, NULL, 45, 5, 2),
('Update Swagger security docs', 'Add bearer auth examples and protected endpoint notes.', 'TODO', 'LOW', NOW() + INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 5 DAY, NULL, 0, 3, 3),
('Fix task update validation', 'Prevent invalid progress, blank titles, and unknown priorities.', 'TODO', 'HIGH', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 6 DAY, NULL, 20, 2, 1),
('Check authorization errors', 'Confirm 401 and 403 handlers produce consistent JSON payloads.', 'DONE', 'HIGH', NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY, 100, 4, 2),
('Clean demo test cases', 'Refactor noisy tests and keep fixtures reusable.', 'IN_PROGRESS', 'LOW', NOW() + INTERVAL 5 DAY, NOW() - INTERVAL 9 DAY, NOW() - INTERVAL 4 DAY, NULL, 75, 5, 3);
