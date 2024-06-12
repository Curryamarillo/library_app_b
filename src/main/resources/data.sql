INSERT INTO books (title, author, isbn, is_available, created_date)
VALUES ('The Great Gatsby', 'F. Scott Fitzgerald', '978-3-16-148410-0', true, CURRENT_TIMESTAMP),
('To Kill a Mockingbird', 'Harper Lee', '978-3-16-148411-0', true, CURRENT_TIMESTAMP),
('1984', 'George Orwell', '978-3-16-148412-0', true, CURRENT_TIMESTAMP),
('Pride and Prejudice', 'Jane Austen', '978-0-14-043528-8', true, CURRENT_TIMESTAMP),
('The Lord of the Rings: The Fellowship of the Ring', 'J.R.R. Tolkien', '978-0-547-52951-3', true, CURRENT_TIMESTAMP),
('The Hitchhikers Guide to the Galaxy', 'Douglas Adams', '978-0-345-39180-3', true, CURRENT_TIMESTAMP);
INSERT INTO users (name, surname, email, is_admin, password)
VALUES
  ('John', 'Doe', 'john.doe@example.com', false, 'password123'),
  ('Jane', 'Smith', 'jane.smith@example.com', false, 'password456'),
  ('Michael', 'Johnson', 'michael.johnson@example.com', true, 'adminPassword'),
  ('Alice', 'Williams', 'alice.williams@example.com', false, 'secretPassword'),
  ('David', 'Miller', 'david.miller@example.com', false, 'anotherPassword'),
  ('Emily', 'Garcia', 'emily.garcia@example.com', false, 'strongPassword');
INSERT INTO loans (user_id, book_id, loan_date)
VALUES (
  (SELECT id FROM users WHERE email = 'john.doe@example.com'),
  (SELECT id FROM books WHERE title = 'The Great Gatsby'),
  CURRENT_TIMESTAMP
),
(
  (SELECT id FROM users WHERE email = 'jane.smith@example.com'),
  (SELECT id FROM books WHERE title = 'To Kill a Mockingbird'),
  CURRENT_TIMESTAMP
),
(
  (SELECT id FROM users WHERE email = 'alice.williams@example.com'),
  (SELECT id FROM books WHERE title = 'Pride and Prejudice'),
  CURRENT_TIMESTAMP
),
(
  (SELECT id FROM users WHERE email = 'david.miller@example.com'),
  (SELECT id FROM books WHERE title = 'The Lord of the Rings: The Fellowship of the Ring'),
  CURRENT_TIMESTAMP
),
(
  (SELECT id FROM users WHERE email = 'emily.garcia@example.com'),
  (SELECT id FROM books WHERE title = 'The Hitchhikers Guide to the Galaxy'),
  CURRENT_TIMESTAMP
),
(
  (SELECT id FROM users WHERE email = 'michael.johnson@example.com'),
  (SELECT id FROM books WHERE title = '1984'),
  CURRENT_TIMESTAMP
);