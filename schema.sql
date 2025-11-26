-- =====================================================
-- School Management System - Database Schema
-- =====================================================
-- This script creates a complete fresh installation of the school_management database.
-- It includes all tables, indexes, sample data, and initial user accounts.
--
-- USAGE:
--   To create a fresh database, run this entire script.
--   This will DROP the existing database if it exists and recreate everything.
--
-- PERIOD-BASED SCHEDULING SYSTEM:
--   Courses use a period-based scheduling system with 10 periods per day:
--   - Period 1 (P1):  08:00 - 08:45
--   - Period 2 (P2):  08:45 - 09:30
--   - Period 3 (P3):  09:30 - 10:15
--   - Period 4 (P4):  10:15 - 11:00
--   - Period 5 (P5):  11:00 - 11:45
--   - Period 6 (P6):  11:45 - 12:30
--   - LUNCH BREAK:    12:30 - 13:00
--   - Period 7 (P7):  13:00 - 13:45
--   - Period 8 (P8):  13:45 - 14:30
--   - Period 9 (P9):  14:30 - 15:15
--   - Period 10 (P10): 15:15 - 16:00
--
--   Schedule format: "P3-P4 (09:30-11:00)" means Period 3 to Period 4
--   Maximum consecutive periods: 4 periods (3 hours)
--
-- DEFAULT CREDENTIALS:
--   Admin:   username: admin          password: admin123
--   Teacher: username: john.smith     password: teacher123
--   Student: username: alice.johnson  password: student123
-- =====================================================

-- Drop existing database to ensure clean installation
DROP DATABASE IF EXISTS school_management;

-- Create fresh database
CREATE DATABASE school_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE school_management;

-- =====================================================
-- TABLE: users
-- Stores all system users (admins, teachers, students)
-- =====================================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT 'Unique login username',
    password VARCHAR(255) NOT NULL COMMENT 'Plain text password (should be hashed in production)',
    full_name VARCHAR(100) NOT NULL COMMENT 'Full name of the user',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT 'Email address (must be unique)',
    phone VARCHAR(20) COMMENT 'Phone number (optional)',
    user_type ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL COMMENT 'Role: ADMIN, TEACHER, or STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Account creation timestamp',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_user_type (user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Stores all system users with role-based access';

-- =====================================================
-- TABLE: courses
-- Stores course information with period-based schedules
-- =====================================================
CREATE TABLE courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20) UNIQUE NOT NULL COMMENT 'Unique course code (e.g., CS101)',
    course_name VARCHAR(100) NOT NULL COMMENT 'Full name of the course',
    description TEXT COMMENT 'Detailed course description',
    credits INT NOT NULL COMMENT 'Number of credit hours',
    teacher_id INT COMMENT 'Foreign key to users table (TEACHER)',
    max_students INT DEFAULT 30 COMMENT 'Maximum enrollment capacity',
    schedule_days VARCHAR(50) COMMENT 'Days of the week (e.g., "Mon, Wed, Fri")',
    schedule_time VARCHAR(50) COMMENT 'Period-based time slot (e.g., "P3-P4 (09:30-11:00)")',
    room_number VARCHAR(20) COMMENT 'Classroom or room assignment',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Course creation timestamp',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_course_code (course_code),
    INDEX idx_teacher_id (teacher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Stores course information with period-based scheduling';

-- =====================================================
-- TABLE: enrollments
-- Manages student course enrollments and grades
-- =====================================================
CREATE TABLE enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL COMMENT 'Foreign key to users table (STUDENT)',
    course_id INT NOT NULL COMMENT 'Foreign key to courses table',
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'When the student enrolled',
    grade VARCHAR(5) COMMENT 'Final grade (e.g., "A", "B+", "C")',
    status ENUM('ACTIVE', 'COMPLETED', 'DROPPED') DEFAULT 'ACTIVE' COMMENT 'Enrollment status',
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE KEY unique_enrollment (student_id, course_id) COMMENT 'Prevent duplicate enrollments',
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Manages student course enrollments with schedule conflict prevention';

-- =====================================================
-- INITIAL DATA: System Administrator
-- =====================================================
INSERT INTO users (username, password, full_name, email, user_type) VALUES
('admin', 'admin123', 'System Administrator', 'admin@school.com', 'ADMIN');

-- =====================================================
-- SAMPLE DATA: Teachers
-- =====================================================
INSERT INTO users (username, password, full_name, email, phone, user_type) VALUES
('john.smith', 'teacher123', 'John Smith', 'john.smith@school.com', '123-456-7890', 'TEACHER'),
('jane.doe', 'teacher123', 'Jane Doe', 'jane.doe@school.com', '123-456-7891', 'TEACHER'),
('robert.chen', 'teacher123', 'Robert Chen', 'robert.chen@school.com', '123-456-7892', 'TEACHER');

-- =====================================================
-- SAMPLE DATA: Students
-- =====================================================
INSERT INTO users (username, password, full_name, email, phone, user_type) VALUES
('alice.johnson', 'student123', 'Alice Johnson', 'alice.j@school.com', '123-456-7900', 'STUDENT'),
('bob.williams', 'student123', 'Bob Williams', 'bob.w@school.com', '123-456-7901', 'STUDENT'),
('charlie.brown', 'student123', 'Charlie Brown', 'charlie.b@school.com', '123-456-7902', 'STUDENT'),
('diana.ross', 'student123', 'Diana Ross', 'diana.r@school.com', '123-456-7903', 'STUDENT'),
('eric.taylor', 'student123', 'Eric Taylor', 'eric.t@school.com', '123-456-7904', 'STUDENT');

-- =====================================================
-- SAMPLE DATA: Courses (Period-Based Schedule)
-- =====================================================
-- Computer Science Courses (Teacher: John Smith, user_id=2)
INSERT INTO courses (course_code, course_name, description, credits, teacher_id, max_students, schedule_days, schedule_time, room_number) VALUES
('CS101', 'Introduction to Programming', 'Basic programming concepts using Java. Covers variables, loops, conditionals, and object-oriented programming fundamentals.', 3, 2, 30, 'Mon, Wed, Fri', 'P3-P4 (09:30-11:00)', 'A101'),
('CS102', 'Data Structures', 'Introduction to data structures and algorithms. Topics include arrays, linked lists, stacks, queues, trees, and graphs.', 4, 2, 25, 'Tue, Thu', 'P7-P9 (13:00-15:15)', 'A102'),
('CS201', 'Database Systems', 'Relational database design, SQL programming, normalization, and transaction management.', 3, 2, 28, 'Mon, Wed', 'P1-P2 (08:00-09:30)', 'A103');

-- Mathematics Courses (Teacher: Jane Doe, user_id=3)
INSERT INTO courses (course_code, course_name, description, credits, teacher_id, max_students, schedule_days, schedule_time, room_number) VALUES
('MATH201', 'Calculus I', 'Differential and integral calculus. Covers limits, derivatives, and applications of derivatives.', 4, 3, 35, 'Mon, Wed, Fri', 'P5-P6 (11:00-12:30)', 'B201'),
('MATH202', 'Linear Algebra', 'Vector spaces, matrices, determinants, eigenvalues, and linear transformations.', 3, 3, 30, 'Tue, Thu', 'P3-P4 (09:30-11:00)', 'B202');

-- English Courses (Teacher: Robert Chen, user_id=4)
INSERT INTO courses (course_code, course_name, description, credits, teacher_id, max_students, schedule_days, schedule_time, room_number) VALUES
('ENG101', 'English Composition', 'Academic writing and composition. Focus on essay structure, argumentation, and research skills.', 3, 4, 30, 'Tue, Thu', 'P5-P6 (11:00-12:30)', 'C105'),
('ENG201', 'Literature Analysis', 'Critical reading and analysis of classic and contemporary literature.', 3, 4, 25, 'Mon, Wed', 'P8-P9 (13:45-15:15)', 'C106');

-- =====================================================
-- SAMPLE DATA: Enrollments
-- =====================================================
-- Alice Johnson (student_id=5) enrollments
INSERT INTO enrollments (student_id, course_id, status, grade) VALUES
(5, 1, 'ACTIVE', NULL),        -- CS101
(5, 4, 'ACTIVE', NULL),        -- MATH201
(5, 6, 'ACTIVE', NULL);        -- ENG101

-- Bob Williams (student_id=6) enrollments
INSERT INTO enrollments (student_id, course_id, status, grade) VALUES
(6, 1, 'ACTIVE', NULL),        -- CS101
(6, 2, 'ACTIVE', NULL),        -- CS102
(6, 5, 'COMPLETED', 'B+');     -- MATH202 (completed)

-- Charlie Brown (student_id=7) enrollments
INSERT INTO enrollments (student_id, course_id, status, grade) VALUES
(7, 1, 'ACTIVE', NULL),        -- CS101
(7, 3, 'ACTIVE', NULL),        -- CS201
(7, 7, 'ACTIVE', NULL);        -- ENG201

-- Diana Ross (student_id=8) enrollments
INSERT INTO enrollments (student_id, course_id, status, grade) VALUES
(8, 2, 'ACTIVE', NULL),        -- CS102
(8, 4, 'ACTIVE', NULL),        -- MATH201
(8, 6, 'ACTIVE', NULL);        -- ENG101

-- Eric Taylor (student_id=9) enrollments
INSERT INTO enrollments (student_id, course_id, status, grade) VALUES
(9, 5, 'ACTIVE', NULL),        -- MATH202
(9, 7, 'ACTIVE', NULL);        -- ENG201

-- =====================================================
-- VERIFICATION QUERIES (Optional - for testing)
-- =====================================================
-- Uncomment the following lines to verify the installation:

-- SELECT 'Users Created:' AS Info, COUNT(*) AS Count FROM users;
-- SELECT 'Courses Created:' AS Info, COUNT(*) AS Count FROM courses;
-- SELECT 'Enrollments Created:' AS Info, COUNT(*) AS Count FROM enrollments;

-- SELECT 'Admin Users:' AS Info, username, full_name FROM users WHERE user_type = 'ADMIN';
-- SELECT 'Teacher Users:' AS Info, username, full_name FROM users WHERE user_type = 'TEACHER';
-- SELECT 'Student Users:' AS Info, username, full_name FROM users WHERE user_type = 'STUDENT';

-- =====================================================
-- END OF SCHEMA
-- =====================================================
