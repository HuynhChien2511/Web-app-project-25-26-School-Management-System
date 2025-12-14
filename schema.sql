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
    semester_id INT COMMENT 'Foreign key to semesters table',
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'When the student enrolled',
    grade VARCHAR(5) COMMENT 'Final grade (e.g., "A", "B+", "C")',
    status ENUM('ACTIVE', 'COMPLETED', 'DROPPED') DEFAULT 'ACTIVE' COMMENT 'Enrollment status',
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE KEY unique_enrollment (student_id, course_id, semester_id) COMMENT 'Prevent duplicate enrollments',
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_semester_id (semester_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Manages student course enrollments with schedule conflict prevention';

-- =====================================================
-- TABLE: semesters
-- Stores academic year and semester information
-- =====================================================
CREATE TABLE semesters (
    semester_id INT PRIMARY KEY AUTO_INCREMENT,
    semester_name VARCHAR(50) NOT NULL COMMENT 'Semester name (e.g., "Fall 2024", "Spring 2025")',
    semester_type ENUM('SEMESTER_1', 'SEMESTER_2', 'SEMESTER_3') NOT NULL COMMENT 'Semester type',
    academic_year VARCHAR(20) NOT NULL COMMENT 'Academic year (e.g., "2024-2025")',
    start_date DATE NOT NULL COMMENT 'Semester start date',
    end_date DATE NOT NULL COMMENT 'Semester end date',
    weeks INT NOT NULL COMMENT 'Number of weeks (16 for sem 1&2, 8 for sem 3)',
    is_active BOOLEAN DEFAULT FALSE COMMENT 'Whether this is the current active semester',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_academic_year (academic_year),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Stores academic semester information';

-- =====================================================
-- TABLE: grade_components
-- Stores grading breakdown configuration for courses
-- =====================================================
CREATE TABLE grade_components (
    component_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL COMMENT 'Foreign key to courses table',
    semester_id INT NOT NULL COMMENT 'Foreign key to semesters table',
    inclass_percentage DECIMAL(5,2) NOT NULL DEFAULT 20.00 COMMENT 'In-class/attendance percentage (0-100)',
    midterm_percentage DECIMAL(5,2) NOT NULL DEFAULT 30.00 COMMENT 'Midterm exam percentage (0-100)',
    final_percentage DECIMAL(5,2) NOT NULL DEFAULT 50.00 COMMENT 'Final exam percentage (0-100)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(semester_id) ON DELETE CASCADE,
    UNIQUE KEY unique_course_semester (course_id, semester_id),
    CONSTRAINT check_percentages CHECK (inclass_percentage + midterm_percentage + final_percentage = 100.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Stores grading component percentages for each course per semester';

-- =====================================================
-- TABLE: attendance
-- Tracks student attendance for each class session
-- =====================================================
CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT NOT NULL COMMENT 'Foreign key to enrollments table',
    attendance_date DATE NOT NULL COMMENT 'Date of the class session',
    status ENUM('PRESENT', 'ABSENT', 'LATE', 'EXCUSED') NOT NULL DEFAULT 'PRESENT' COMMENT 'Attendance status',
    notes TEXT COMMENT 'Additional notes about attendance',
    recorded_by INT COMMENT 'Foreign key to users table (teacher who recorded)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(user_id) ON DELETE SET NULL,
    UNIQUE KEY unique_attendance (enrollment_id, attendance_date),
    INDEX idx_enrollment_id (enrollment_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Tracks student attendance with automatic course drop for excessive absences';

-- =====================================================
-- TABLE: grades
-- Stores individual grade components for each enrollment
-- =====================================================
CREATE TABLE grades (
    grade_id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT NOT NULL COMMENT 'Foreign key to enrollments table',
    inclass_score DECIMAL(5,2) COMMENT 'In-class/attendance score (0-100)',
    midterm_score DECIMAL(5,2) COMMENT 'Midterm exam score (0-100)',
    final_score DECIMAL(5,2) COMMENT 'Final exam score (0-100)',
    total_score DECIMAL(5,2) COMMENT 'Calculated total score (0-100)',
    letter_grade VARCHAR(5) COMMENT 'Letter grade (A, A-, B+, B, etc.)',
    grade_point DECIMAL(3,2) COMMENT 'Grade point for GPA (4.0 scale)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    UNIQUE KEY unique_enrollment_grade (enrollment_id),
    INDEX idx_enrollment_id (enrollment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Stores detailed grade breakdowns and calculated GPA';

-- =====================================================
-- TABLE: gpa_records
-- Stores semester and cumulative GPA for each student
-- =====================================================
CREATE TABLE gpa_records (
    gpa_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL COMMENT 'Foreign key to users table (STUDENT)',
    semester_id INT COMMENT 'Foreign key to semesters table (NULL for cumulative)',
    gpa DECIMAL(3,2) NOT NULL COMMENT 'GPA value (0.00-4.00)',
    total_credits INT NOT NULL DEFAULT 0 COMMENT 'Total credits for this period',
    total_grade_points DECIMAL(7,2) NOT NULL DEFAULT 0.00 COMMENT 'Total grade points earned',
    is_cumulative BOOLEAN DEFAULT FALSE COMMENT 'Whether this is cumulative GPA',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(semester_id) ON DELETE CASCADE,
    UNIQUE KEY unique_student_semester (student_id, semester_id, is_cumulative),
    INDEX idx_student_id (student_id),
    INDEX idx_semester_id (semester_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Stores semester and cumulative GPA calculations';

-- =====================================================
-- TABLE: announcements
-- Stores announcements posted by admins and teachers
-- =====================================================
CREATE TABLE announcements (
    announcement_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT 'Announcement title',
    content TEXT NOT NULL COMMENT 'Announcement content/message',
    author_id INT NOT NULL COMMENT 'Foreign key to users table (ADMIN or TEACHER)',
    course_id INT COMMENT 'Foreign key to courses table (NULL for school-wide announcements)',
    is_important BOOLEAN DEFAULT FALSE COMMENT 'Flag for important/urgent announcements',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'When the announcement was posted',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    INDEX idx_author_id (author_id),
    INDEX idx_course_id (course_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Stores system-wide and course-specific announcements';

-- =====================================================
-- TABLE: forum_posts
-- Stores forum posts and replies for course discussions
-- =====================================================
CREATE TABLE forum_posts (
    post_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL COMMENT 'Foreign key to courses table',
    author_id INT NOT NULL COMMENT 'Foreign key to users table',
    parent_post_id INT COMMENT 'Foreign key to parent post (NULL for top-level posts)',
    title VARCHAR(200) COMMENT 'Post title (only for top-level posts)',
    content TEXT NOT NULL COMMENT 'Post content/message',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'When the post was created',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_post_id) REFERENCES forum_posts(post_id) ON DELETE CASCADE,
    INDEX idx_course_id (course_id),
    INDEX idx_author_id (author_id),
    INDEX idx_parent_post_id (parent_post_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Stores forum posts and threaded replies for course discussions';

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
INSERT INTO enrollments (student_id, course_id, semester_id, status, grade) VALUES
(5, 1, 1, 'ACTIVE', NULL),        -- CS101
(5, 4, 1, 'ACTIVE', NULL),        -- MATH201
(5, 6, 1, 'ACTIVE', NULL);        -- ENG101

-- Bob Williams (student_id=6) enrollments
INSERT INTO enrollments (student_id, course_id, semester_id, status, grade) VALUES
(6, 1, 1, 'ACTIVE', NULL),        -- CS101
(6, 2, 1, 'ACTIVE', NULL),        -- CS102
(6, 5, 1, 'COMPLETED', 'B+');     -- MATH202 (completed)

-- Charlie Brown (student_id=7) enrollments
INSERT INTO enrollments (student_id, course_id, semester_id, status, grade) VALUES
(7, 1, 1, 'ACTIVE', NULL),        -- CS101
(7, 3, 1, 'ACTIVE', NULL),        -- CS201
(7, 7, 1, 'ACTIVE', NULL);        -- ENG201

-- Diana Ross (student_id=8) enrollments
INSERT INTO enrollments (student_id, course_id, semester_id, status, grade) VALUES
(8, 2, 1, 'ACTIVE', NULL),        -- CS102
(8, 4, 1, 'ACTIVE', NULL),        -- MATH201
(8, 6, 1, 'ACTIVE', NULL);        -- ENG101

-- Eric Taylor (student_id=9) enrollments
INSERT INTO enrollments (student_id, course_id, semester_id, status, grade) VALUES
(9, 5, 1, 'ACTIVE', NULL),        -- MATH202
(9, 7, 1, 'ACTIVE', NULL);        -- ENG201

-- =====================================================
-- SAMPLE DATA: Semesters (Academic Year 2024-2025)
-- =====================================================
INSERT INTO semesters (semester_name, semester_type, academic_year, start_date, end_date, weeks, is_active) VALUES
('Fall 2024', 'SEMESTER_1', '2024-2025', '2024-09-01', '2024-12-20', 16, TRUE),
('Spring 2025', 'SEMESTER_2', '2024-2025', '2025-01-15', '2025-05-15', 16, FALSE),
('Summer 2025', 'SEMESTER_3', '2024-2025', '2025-06-01', '2025-07-31', 8, FALSE);

-- =====================================================
-- SAMPLE DATA: Grade Components (Default grading breakdown)
-- =====================================================
-- CS101 - Introduction to Programming
INSERT INTO grade_components (course_id, semester_id, inclass_percentage, midterm_percentage, final_percentage) VALUES
(1, 1, 20.00, 30.00, 50.00);

-- CS102 - Data Structures
INSERT INTO grade_components (course_id, semester_id, inclass_percentage, midterm_percentage, final_percentage) VALUES
(2, 1, 15.00, 35.00, 50.00);

-- CS201 - Database Systems
INSERT INTO grade_components (course_id, semester_id, inclass_percentage, midterm_percentage, final_percentage) VALUES
(3, 1, 25.00, 25.00, 50.00);

-- MATH201 - Calculus I
INSERT INTO grade_components (course_id, semester_id, inclass_percentage, midterm_percentage, final_percentage) VALUES
(4, 1, 10.00, 40.00, 50.00);

-- MATH202 - Linear Algebra
INSERT INTO grade_components (course_id, semester_id, inclass_percentage, midterm_percentage, final_percentage) VALUES
(5, 1, 10.00, 40.00, 50.00);

-- ENG101 - English Composition
INSERT INTO grade_components (course_id, semester_id, inclass_percentage, midterm_percentage, final_percentage) VALUES
(6, 1, 30.00, 20.00, 50.00);

-- ENG201 - Literature Analysis
INSERT INTO grade_components (course_id, semester_id, inclass_percentage, midterm_percentage, final_percentage) VALUES
(7, 1, 30.00, 20.00, 50.00);

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

-- Thêm value để test pagination

-- Thêm ADMIN
INSERT INTO users (username, password, full_name, email, user_type) VALUES
('admin2','admin123','Admin Two','admin2@school.com','ADMIN'),
('admin3','admin123','Admin Three','admin3@school.com','ADMIN'),
('admin4','admin123','Admin Four','admin4@school.com','ADMIN'),
('admin5','admin123','Admin Five','admin5@school.com','ADMIN'),
('admin6','admin123','Admin Six','admin6@school.com','ADMIN');

-- Thêm TEACHER
INSERT INTO users (username, password, full_name, email, phone, user_type) VALUES
('teacher4','teacher123','Emily Clark','emily.c@school.com','111-222-3001','TEACHER'),
('teacher5','teacher123','David Lee','david.l@school.com','111-222-3002','TEACHER'),
('teacher6','teacher123','Sophia Kim','sophia.k@school.com','111-222-3003','TEACHER'),
('teacher7','teacher123','Michael Tran','michael.t@school.com','111-222-3004','TEACHER'),
('teacher8','teacher123','Linda Nguyen','linda.n@school.com','111-222-3005','TEACHER');

-- Thêm STUDENT
INSERT INTO users (username, password, full_name, email, phone, user_type) VALUES
('student6','student123','Frank Moore','frank.m@school.com','222-333-4001','STUDENT'),
('student7','student123','Grace Hall','grace.h@school.com','222-333-4002','STUDENT'),
('student8','student123','Henry Adams','henry.a@school.com','222-333-4003','STUDENT'),
('student9','student123','Ivy Baker','ivy.b@school.com','222-333-4004','STUDENT'),
('student10','student123','Jack Turner','jack.t@school.com','222-333-4005','STUDENT');

INSERT INTO semesters (semester_name, semester_type, academic_year, start_date, end_date, weeks, is_active) VALUES
('Fall 2023','SEMESTER_1','2023-2024','2023-09-01','2023-12-20',16,FALSE),
('Spring 2024','SEMESTER_2','2023-2024','2024-01-10','2024-05-15',16,FALSE),
('Summer 2024','SEMESTER_3','2023-2024','2024-06-01','2024-07-31',8,FALSE),
('Fall 2025','SEMESTER_1','2025-2026','2025-09-01','2025-12-20',16,FALSE),
('Spring 2026','SEMESTER_2','2025-2026','2026-01-10','2026-05-15',16,FALSE),
('Summer 2026','SEMESTER_3','2025-2026','2026-06-01','2026-07-31',8,FALSE),
('Fall 2026','SEMESTER_1','2026-2027','2026-09-01','2026-12-20',16,FALSE),
('Spring 2027','SEMESTER_2','2026-2027','2027-01-10','2027-05-15',16,FALSE),
('Summer 2027','SEMESTER_3','2026-2027','2027-06-01','2027-07-31',8,FALSE),
('Fall 2027','SEMESTER_1','2027-2028','2027-09-01','2027-12-20',16,FALSE),
('Spring 2028','SEMESTER_2','2027-2028','2028-01-10','2028-05-15',16,FALSE),
('Summer 2028','SEMESTER_3','2027-2028','2028-06-01','2028-07-31',8,FALSE);

INSERT INTO courses (course_code, course_name, credits, teacher_id, schedule_days, schedule_time, room_number) VALUES
('PHY101','Physics I',3,3,'Mon,Wed','P1-P2','D101'),
('CHEM101','Chemistry I',3,4,'Tue,Thu','P3-P4','D102'),
('BIO101','Biology I',3,5,'Mon,Wed','P5-P6','D103'),
('HIST101','World History',3,6,'Tue,Thu','P7-P8','E201'),
('GEO101','Geography',3,7,'Mon,Fri','P2-P3','E202'),
('ECON101','Economics',3,8,'Wed,Fri','P4-P5','E203'),
('CS301','Operating Systems',4,2,'Tue,Thu','P8-P10','A201'),
('CS302','Computer Networks',4,2,'Mon,Wed','P7-P9','A202'),
('CS303','Software Engineering',3,2,'Tue','P1-P4','A203');


INSERT INTO enrollments (student_id, course_id, semester_id, status, grade) VALUES
(5,1,2,'ACTIVE', NULL),
(5,2,1,'ACTIVE', NULL),
(6,3,1,'ACTIVE', NULL),
(6,4,1,'ACTIVE', NULL),
(7,5,1,'ACTIVE', NULL),
(7,6,1,'ACTIVE', NULL),
(8,7,1,'ACTIVE', NULL),
(8,8,1,'ACTIVE', NULL),
(9,9,1,'ACTIVE', NULL),
(9,10,1,'ACTIVE', NULL),
(10,11,1,'ACTIVE', NULL),
(10,12,1,'ACTIVE', NULL),
(11,13,1,'ACTIVE', NULL),
(12,14,1,'ACTIVE', NULL),
(13,15,1,'ACTIVE', NULL);

INSERT INTO grade_components(course_id, semester_id, inclass_percentage, midterm_percentage, final_percentage) VALUES
(1,2,20,30,50),
(2,2,15,35,50),
(3,2,25,25,50),
(4,2,10,40,50),
(5,2,20,30,50),
(6,2,20,30,50),
(7,2,30,20,50),
(8,2,25,25,50),
(9,2,20,30,50),
(10,2,15,35,50),
(11,2,10,40,50),
(12,2,20,30,50),
(13,2,30,20,50),
(14,2,25,25,50),
(15,2,20,30,50);

INSERT INTO attendance (enrollment_id, attendance_date, status, recorded_by) VALUES
(1,'2024-09-02','PRESENT',2),
(2,'2024-09-02','ABSENT',2),
(3,'2024-09-03','PRESENT',3),
(4,'2024-09-03','LATE',3),
(5,'2024-09-04','PRESENT',4),
(6,'2024-09-04','EXCUSED',4),
(7,'2024-09-05','PRESENT',2),
(8,'2024-09-05','ABSENT',2),
(9,'2024-09-06','PRESENT',3),
(10,'2024-09-06','PRESENT',3),
(11,'2024-09-07','LATE',4),
(12,'2024-09-07','PRESENT',4),
(13,'2024-09-08','ABSENT',5),
(14,'2024-09-08','PRESENT',5),
(15,'2024-09-09','PRESENT',6);

INSERT INTO grades (enrollment_id, inclass_score, midterm_score, final_score, total_score, letter_grade, grade_point) VALUES
(1,85,78,90,86,'A',4.0),
(2,70,65,80,72,'B',3.0),
(3,88,82,91,88,'A',4.0),
(4,75,70,78,74,'B',3.0),
(5,90,85,93,90,'A',4.0),
(6,60,65,70,66,'C+',2.3),
(7,92,88,95,92,'A',4.0),
(8,68,72,75,72,'B',3.0),
(9,80,78,85,81,'B+',3.5),
(10,77,74,80,77,'B',3.0),
(11,85,80,88,85,'A-',3.7),
(12,70,68,75,71,'B-',2.7),
(13,88,85,90,88,'A',4.0),
(14,82,79,85,82,'B+',3.5),
(15,75,70,78,74,'B',3.0);

INSERT INTO gpa_records (student_id, semester_id, gpa, total_credits, total_grade_points, is_cumulative) VALUES
(5,1,3.8,9,34.2,FALSE),
(6,1,3.2,7,22.4,FALSE),
(7,1,3.5,10,35.0,FALSE),
(8,1,3.0,8,24.0,FALSE),
(9,1,3.6,6,21.6,FALSE),
(10,1,3.1,9,27.9,FALSE),
(11,1,3.9,6,23.4,FALSE),
(12,1,2.8,6,16.8,FALSE),
(13,1,3.7,9,33.3,FALSE),
(14,1,3.4,6,20.4,FALSE),
(5,NULL,3.6,30,108,TRUE),
(6,NULL,3.1,28,86.8,TRUE),
(7,NULL,3.5,32,112,TRUE),
(8,NULL,3.0,26,78,TRUE),
(9,NULL,3.4,24,81.6,TRUE);

INSERT INTO announcements (title, content, author_id, course_id, is_important) VALUES
('Welcome','Welcome to new semester',1,NULL,TRUE),
('Exam Notice','Midterm exam next week',2,1,TRUE),
('Homework','Homework 1 released',2,1,FALSE),
('Schedule Change','Room changed',3,4,TRUE),
('Holiday','School closed Friday',1,NULL,TRUE),
('Project','Group project assigned',2,2,FALSE),
('Reminder','Attendance mandatory',3,4,FALSE),
('Quiz','Quiz this Thursday',4,6,FALSE),
('Materials','Lecture slides uploaded',2,3,FALSE),
('Forum','Use forum for questions',1,NULL,FALSE),
('Final Exam','Final exam schedule',2,1,TRUE),
('Grade Policy','Updated grading policy',3,4,TRUE),
('Library','New books available',1,NULL,FALSE),
('Workshop','Tech workshop Saturday',1,NULL,FALSE),
('Survey','Course feedback survey',2,2,FALSE);

INSERT INTO forum_posts (course_id, author_id, title, content) VALUES
(1,5,'Question about loops','Can someone explain loops?'),
(1,6,'Arrays','Difference between array and list'),
(2,7,'Homework help','Need help with assignment'),
(3,8,'SQL JOIN','How does JOIN work?'),
(4,9,'Limits','Struggling with limits'),
(5,10,'Vectors','Vector space explanation'),
(6,11,'Essay format','Essay structure question'),
(7,12,'Book list','Which books are required'),
(1,13,'Exam tips','Any exam tips?'),
(2,14,'Graphs','Graph traversal confusion'),
(3,15,'Normalization','3NF explanation'),
(4,5,'Derivatives','Chain rule help'),
(5,6,'Matrices','Matrix multiplication'),
(6,7,'Grammar','Grammar resources'),
(7,8,'Themes','Literary themes discussion');