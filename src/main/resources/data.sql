INSERT INTO books (title, author, isbn, is_available, created_date)
VALUES ('The Great Gatsby', 'F. Scott Fitzgerald', '978-3-16-148410-0', false, CURRENT_TIMESTAMP),
('To Kill a Mockingbird', 'Harper Lee', '978-3-16-148411-0', false, CURRENT_TIMESTAMP),
('1984', 'George Orwell', '978-3-16-148412-0', false, CURRENT_TIMESTAMP),
('The Catcher in the Rye', 'J.D. Salinger', '978-0-316-76948-0', true, CURRENT_TIMESTAMP),
('Brave New World', 'Aldous Huxley', '978-0-06-085052-4', true, CURRENT_TIMESTAMP),
('The Lord of the Rings', 'J.R.R. Tolkien', '978-0-618-64015-7', true, CURRENT_TIMESTAMP),
('The Hobbit', 'J.R.R. Tolkien', '978-0-618-00221-4', true, CURRENT_TIMESTAMP),
('Fahrenheit 451', 'Ray Bradbury', '978-0-7432-4722-1', true, CURRENT_TIMESTAMP),
('Pride and Prejudice', 'Jane Austen', '978-0-14-043528-8', false, CURRENT_TIMESTAMP),
('Pride and Glory', 'Jane Doe', '977-0-14-043530-8', true, CURRENT_TIMESTAMP),
('Moby Dick', 'Herman Melville', '978-0-14-243724-7', true, CURRENT_TIMESTAMP),
('War and Peace', 'Leo Tolstoy', '978-0-679-64037-3', true, CURRENT_TIMESTAMP),
('The Lord of the Rings: The Fellowship of the Ring', 'J.R.R. Tolkien', '978-0-547-52951-3', true, CURRENT_TIMESTAMP),
('The Hitchhikers Guide to the Galaxy', 'Douglas Adams', '978-0-345-39180-3', false, CURRENT_TIMESTAMP);
INSERT INTO users (name, surname, email, is_admin, password)
VALUES
  ('John', 'Doe', 'john.doe@example.com', false, '$2a$10$FBoSaPy6NA5bFCZv786tI.G4KZDqwVGERr8RBFHLKY5crP/aM2alu'),
  ('Jane', 'Smith', 'jane.smith@example.com', false, '$2a$10$v6c3OB9yK0BlfKwQkD0b0.Sthu.bqBsbKy9WXudKoKFhruX1n0sEa'),
  ('Michael', 'Johnson', 'michael.johnson@example.com', true, '$2a$10$4VdpRLU3KHHkKzF8lcgGTOD20eXbpLazgklyE8/y2B8jHahLxupYS'),
  ('Alice', 'Williams', 'alice.williams@example.com', false, '$2a$10$G3m7M2P2aNKWXUO8z8eC0Oojj0qq7uJQ35p38VvIcnxlcfpzRWhBe'),
  ('David', 'Miller', 'david.miller@example.com', false, '$2a$10$Aikt6ZMRieVv1y/v8ci.Wuj070bWtT.14GHG/lWiOVxUlVBoy8lji'),
  ('Emily', 'Garcia', 'emily.garcia@example.com', false, '$2a$10$4L1g1SXLwODg95puLgnWVO3Ej1RKewLSFBymHrHBeakszZxjeXZyu');
INSERT INTO loans (user_id, book_id, loan_date)
VALUES (
  (SELECT id FROM users WHERE email = 'john.doe@example.com'),
  (SELECT id FROM books WHERE title = 'The Great Gatsby'),
  CURRENT_TIMESTAMP
),
(
  (SELECT id FROM users WHERE email = 'john.doe@example.com'),
  (SELECT id FROM books WHERE title = '1984'),
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
  (SELECT id FROM users WHERE email = 'emily.garcia@example.com'),
  (SELECT id FROM books WHERE title = 'The Hitchhikers Guide to the Galaxy'),
  CURRENT_TIMESTAMP
);
/*
Original: password123 | Encoded: $2a$10$FBoSaPy6NA5bFCZv786tI.G4KZDqwVGERr8RBFHLKY5crP/aM2alu
Original: password456 | Encoded: $2a$10$v6c3OB9yK0BlfKwQkD0b0.Sthu.bqBsbKy9WXudKoKFhruX1n0sEa
Original: adminPassword | Encoded: $2a$10$4VdpRLU3KHHkKzF8lcgGTOD20eXbpLazgklyE8/y2B8jHahLxupYS
Original: secretPassword | Encoded: $2a$10$G3m7M2P2aNKWXUO8z8eC0Oojj0qq7uJQ35p38VvIcnxlcfpzRWhBe
Original: anotherPassword | Encoded: $2a$10$Aikt6ZMRieVv1y/v8ci.Wuj070bWtT.14GHG/lWiOVxUlVBoy8lji
Original: strongPassword | Encoded: $2a$10$4L1g1SXLwODg95puLgnWVO3Ej1RKewLSFBymHrHBeakszZxjeXZyu
*/