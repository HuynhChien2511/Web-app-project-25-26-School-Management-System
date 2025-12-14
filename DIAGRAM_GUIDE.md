# Hướng Dẫn Vẽ Use Case Diagram, ERD và Schema
## School Management System

---

## 1. USE CASE DIAGRAM

### 1.1. Các Actor (Người Dùng)
- **Admin** (Quản trị viên)
- **Teacher** (Giáo viên)
- **Student** (Học sinh)

### 1.2. Use Cases Chi Tiết

#### **ADMIN USE CASES**
1. **User Management** (Quản lý người dùng)
   - Create User (Tạo người dùng mới)
   - Edit User (Chỉnh sửa thông tin người dùng)
   - Delete User (Xóa người dùng)
   - View All Users (Xem danh sách người dùng)

2. **Course Management** (Quản lý khóa học)
   - Create Course (Tạo khóa học mới)
   - Edit Course (Chỉnh sửa khóa học)
   - Delete Course (Xóa khóa học)
   - Assign Teacher to Course (Phân công giáo viên)
   - View All Courses (Xem danh sách khóa học)

3. **Semester Management** (Quản lý học kỳ)
   - Create Semester (Tạo học kỳ mới)
   - Edit Semester (Chỉnh sửa học kỳ)
   - Set Active Semester (Đặt học kỳ hiện hành)
   - View Semesters (Xem danh sách học kỳ)

4. **Attendance Management** (Quản lý điểm danh)
   - View Attendance Reports (Xem báo cáo điểm danh)
   - View Course Attendance (Xem điểm danh theo khóa học)

5. **Grade Management** (Quản lý điểm số)
   - View All Grades (Xem tất cả điểm)
   - Configure Grade Components (Cấu hình thành phần điểm)
   - View Course Grades (Xem điểm theo khóa học)

6. **GPA Management** (Quản lý GPA)
   - View Student GPA (Xem GPA học sinh)
   - Recalculate GPA (Tính lại GPA)

7. **Announcement Management** (Quản lý thông báo)
   - Create Announcement (Tạo thông báo)
   - Edit Announcement (Chỉnh sửa thông báo)
   - Delete Announcement (Xóa thông báo)
   - Mark as Important (Đánh dấu quan trọng)

8. **Dashboard** (Bảng điều khiển)
   - View System Statistics (Xem thống kê hệ thống)

#### **TEACHER USE CASES**
1. **Course Management** (Quản lý khóa học)
   - View My Courses (Xem khóa học của tôi)
   - View Course Students (Xem danh sách sinh viên)
   - View Timetable (Xem thời khóa biểu)

2. **Attendance Management** (Quản lý điểm danh)
   - Take Attendance (Điểm danh)
   - View Attendance History (Xem lịch sử điểm danh)
   - Edit Attendance (Chỉnh sửa điểm danh)

3. **Grade Management** (Quản lý điểm số)
   - Enter Grades (Nhập điểm)
   - Edit Grades (Chỉnh sửa điểm)
   - Finalize Grades (Hoàn thiện điểm)
   - View Student Grades (Xem điểm sinh viên)

4. **Announcement Management** (Quản lý thông báo)
   - Create Course Announcement (Tạo thông báo khóa học)
   - Edit Announcement (Chỉnh sửa thông báo)
   - Delete Announcement (Xóa thông báo)

5. **Dashboard** (Bảng điều khiển)
   - View My Statistics (Xem thống kê của tôi)

#### **STUDENT USE CASES**
1. **Course Management** (Quản lý khóa học)
   - View Available Courses (Xem khóa học có sẵn)
   - Enroll in Course (Đăng ký khóa học)
   - View My Courses (Xem khóa học của tôi)
   - View Course Details (Xem chi tiết khóa học)
   - View Timetable (Xem thời khóa biểu)

2. **Grade Management** (Quản lý điểm số)
   - View My Grades (Xem điểm của tôi)

3. **GPA Management** (Quản lý GPA)
   - View My GPA (Xem GPA của tôi)
   - View GPA Dashboard (Xem bảng điều khiển GPA)

4. **Announcement** (Thông báo)
   - View Announcements (Xem thông báo)

5. **Forum** (Diễn đàn)
   - Create Post (Tạo bài viết)
   - Reply to Post (Trả lời bài viết)
   - View Posts (Xem bài viết)

6. **Dashboard** (Bảng điều khiển)
   - View My Dashboard (Xem bảng điều khiển)

#### **COMMON USE CASES (Tất cả người dùng)**
1. **Authentication** (Xác thực)
   - Login (Đăng nhập)
   - Logout (Đăng xuất)
   - Change Password (Đổi mật khẩu)

### 1.3. Cách Vẽ Use Case Diagram

#### Công cụ đề xuất:
- **Draw.io** (https://app.diagrams.net/) - Miễn phí, online
- **Lucidchart** (https://www.lucidchart.com/)
- **Visual Paradigm** (https://www.visual-paradigm.com/)
- **StarUML** (https://staruml.io/)

#### Các bước vẽ:

1. **Vẽ hệ thống (System Boundary)**
   - Tạo hình chữ nhật lớn, ghi "School Management System"

2. **Thêm Actors**
   - Vẽ 3 stick figures bên ngoài system boundary
   - Đặt tên: Admin, Teacher, Student

3. **Thêm Use Cases**
   - Vẽ các hình oval bên trong system boundary
   - Mỗi oval đại diện cho 1 chức năng
   - Ghi tên chức năng vào trong oval

4. **Kết nối Actors với Use Cases**
   - Dùng đường thẳng nối actor với use case
   - Admin → tất cả use cases quản lý
   - Teacher → use cases liên quan đến giảng dạy
   - Student → use cases liên quan đến học tập

5. **Thêm quan hệ extend và include (nếu cần)**
   - `<<include>>`: Chức năng bắt buộc (VD: View Grades include Login)
   - `<<extend>>`: Chức năng mở rộng (VD: Mark as Important extend Create Announcement)

#### Ví dụ cấu trúc:
```
                    [Admin]
                       |
        +--------------+---------------+
        |              |               |
    (Manage       (Manage          (Manage
     Users)       Courses)         Semesters)
        
                   [Teacher]
                       |
        +--------------+---------------+
        |              |               |
    (Take         (Enter           (Create
  Attendance)     Grades)      Announcement)
        
                   [Student]
                       |
        +--------------+---------------+
        |              |               |
    (Enroll       (View            (View
    Course)       Grades)           GPA)
```

---

## 2. ERD (Entity Relationship Diagram)

### 2.1. Các Entities (Bảng) và Attributes

#### **1. USERS** 👤
- **Primary Key:** user_id (INT, AUTO_INCREMENT)
- **Attributes:**
  - username (VARCHAR(50), UNIQUE, NOT NULL)
  - password (VARCHAR(255), NOT NULL)
  - full_name (VARCHAR(100), NOT NULL)
  - email (VARCHAR(100), UNIQUE, NOT NULL)
  - phone (VARCHAR(20))
  - user_type (ENUM: 'ADMIN', 'TEACHER', 'STUDENT')
  - created_at (TIMESTAMP)
  - updated_at (TIMESTAMP)

#### **2. COURSES** 📚
- **Primary Key:** course_id (INT, AUTO_INCREMENT)
- **Foreign Keys:** teacher_id → users(user_id)
- **Attributes:**
  - course_code (VARCHAR(20), UNIQUE, NOT NULL)
  - course_name (VARCHAR(100), NOT NULL)
  - description (TEXT)
  - credits (INT, NOT NULL)
  - teacher_id (INT)
  - max_students (INT, DEFAULT 30)
  - schedule_days (VARCHAR(50))
  - schedule_time (VARCHAR(50))
  - room_number (VARCHAR(20))
  - created_at (TIMESTAMP)
  - updated_at (TIMESTAMP)

#### **3. ENROLLMENTS** 📝
- **Primary Key:** enrollment_id (INT, AUTO_INCREMENT)
- **Foreign Keys:** 
  - student_id → users(user_id)
  - course_id → courses(course_id)
  - semester_id → semesters(semester_id)
- **Attributes:**
  - student_id (INT, NOT NULL)
  - course_id (INT, NOT NULL)
  - semester_id (INT)
  - enrollment_date (TIMESTAMP)
  - grade (VARCHAR(5))
  - status (ENUM: 'ACTIVE', 'COMPLETED', 'DROPPED')

#### **4. SEMESTERS** 📅
- **Primary Key:** semester_id (INT, AUTO_INCREMENT)
- **Attributes:**
  - semester_name (VARCHAR(50), NOT NULL)
  - semester_type (ENUM: 'SEMESTER_1', 'SEMESTER_2', 'SEMESTER_3')
  - academic_year (VARCHAR(20), NOT NULL)
  - start_date (DATE, NOT NULL)
  - end_date (DATE, NOT NULL)
  - weeks (INT, NOT NULL)
  - is_active (BOOLEAN, DEFAULT FALSE)
  - created_at (TIMESTAMP)
  - updated_at (TIMESTAMP)

#### **5. GRADE_COMPONENTS** 📊
- **Primary Key:** component_id (INT, AUTO_INCREMENT)
- **Foreign Keys:**
  - course_id → courses(course_id)
  - semester_id → semesters(semester_id)
- **Attributes:**
  - course_id (INT, NOT NULL)
  - semester_id (INT, NOT NULL)
  - inclass_percentage (DECIMAL(5,2), DEFAULT 20.00)
  - midterm_percentage (DECIMAL(5,2), DEFAULT 30.00)
  - final_percentage (DECIMAL(5,2), DEFAULT 50.00)
  - created_at (TIMESTAMP)
  - updated_at (TIMESTAMP)

#### **6. ATTENDANCE** ✅
- **Primary Key:** attendance_id (INT, AUTO_INCREMENT)
- **Foreign Keys:**
  - enrollment_id → enrollments(enrollment_id)
  - recorded_by → users(user_id)
- **Attributes:**
  - enrollment_id (INT, NOT NULL)
  - attendance_date (DATE, NOT NULL)
  - status (ENUM: 'PRESENT', 'ABSENT', 'LATE', 'EXCUSED')
  - notes (TEXT)
  - recorded_by (INT)
  - created_at (TIMESTAMP)
  - updated_at (TIMESTAMP)

#### **7. GRADES** 💯
- **Primary Key:** grade_id (INT, AUTO_INCREMENT)
- **Foreign Keys:** enrollment_id → enrollments(enrollment_id)
- **Attributes:**
  - enrollment_id (INT, NOT NULL)
  - inclass_score (DECIMAL(5,2))
  - midterm_score (DECIMAL(5,2))
  - final_score (DECIMAL(5,2))
  - total_score (DECIMAL(5,2))
  - letter_grade (VARCHAR(5))
  - grade_point (DECIMAL(3,2))
  - created_at (TIMESTAMP)
  - updated_at (TIMESTAMP)

#### **8. GPA_RECORDS** 📈
- **Primary Key:** gpa_id (INT, AUTO_INCREMENT)
- **Foreign Keys:**
  - student_id → users(user_id)
  - semester_id → semesters(semester_id)
- **Attributes:**
  - student_id (INT, NOT NULL)
  - semester_id (INT)
  - gpa (DECIMAL(3,2), NOT NULL)
  - total_credits (INT, DEFAULT 0)
  - total_grade_points (DECIMAL(7,2), DEFAULT 0.00)
  - is_cumulative (BOOLEAN, DEFAULT FALSE)
  - created_at (TIMESTAMP)
  - updated_at (TIMESTAMP)

#### **9. ANNOUNCEMENTS** 📢
- **Primary Key:** announcement_id (INT, AUTO_INCREMENT)
- **Foreign Keys:**
  - author_id → users(user_id)
  - course_id → courses(course_id)
- **Attributes:**
  - title (VARCHAR(200), NOT NULL)
  - content (TEXT, NOT NULL)
  - author_id (INT, NOT NULL)
  - course_id (INT)
  - is_important (BOOLEAN, DEFAULT FALSE)
  - created_at (TIMESTAMP)
  - updated_at (TIMESTAMP)

#### **10. FORUM_POSTS** 💬
- **Primary Key:** post_id (INT, AUTO_INCREMENT)
- **Foreign Keys:**
  - course_id → courses(course_id)
  - author_id → users(user_id)
  - parent_post_id → forum_posts(post_id)
- **Attributes:**
  - course_id (INT, NOT NULL)
  - author_id (INT, NOT NULL)
  - parent_post_id (INT)
  - title (VARCHAR(200))
  - content (TEXT, NOT NULL)
  - created_at (TIMESTAMP)
  - updated_at (TIMESTAMP)

### 2.2. Relationships (Quan hệ)

#### **1. USERS ↔ COURSES**
- Relationship: **TEACHES** (1:N)
- 1 Teacher (User) có thể dạy nhiều Courses
- 1 Course có 1 Teacher

#### **2. USERS ↔ ENROLLMENTS**
- Relationship: **ENROLLS** (1:N)
- 1 Student (User) có thể đăng ký nhiều Enrollments
- 1 Enrollment thuộc về 1 Student

#### **3. COURSES ↔ ENROLLMENTS**
- Relationship: **HAS** (1:N)
- 1 Course có nhiều Enrollments
- 1 Enrollment thuộc về 1 Course

#### **4. SEMESTERS ↔ ENROLLMENTS**
- Relationship: **CONTAINS** (1:N)
- 1 Semester có nhiều Enrollments
- 1 Enrollment thuộc về 1 Semester

#### **5. COURSES ↔ GRADE_COMPONENTS**
- Relationship: **HAS** (1:N)
- 1 Course có nhiều Grade Components (mỗi semester)
- 1 Grade Component thuộc về 1 Course

#### **6. SEMESTERS ↔ GRADE_COMPONENTS**
- Relationship: **DEFINES** (1:N)
- 1 Semester định nghĩa nhiều Grade Components
- 1 Grade Component thuộc về 1 Semester

#### **7. ENROLLMENTS ↔ ATTENDANCE**
- Relationship: **RECORDS** (1:N)
- 1 Enrollment có nhiều Attendance records
- 1 Attendance record thuộc về 1 Enrollment

#### **8. ENROLLMENTS ↔ GRADES**
- Relationship: **HAS** (1:1)
- 1 Enrollment có 1 Grade record
- 1 Grade record thuộc về 1 Enrollment

#### **9. USERS ↔ GPA_RECORDS**
- Relationship: **ACHIEVES** (1:N)
- 1 Student có nhiều GPA records (mỗi semester)
- 1 GPA record thuộc về 1 Student

#### **10. SEMESTERS ↔ GPA_RECORDS**
- Relationship: **CALCULATED_FOR** (1:N)
- 1 Semester có nhiều GPA records
- 1 GPA record thuộc về 1 Semester

#### **11. USERS ↔ ANNOUNCEMENTS**
- Relationship: **CREATES** (1:N)
- 1 User (Admin/Teacher) tạo nhiều Announcements
- 1 Announcement được tạo bởi 1 User

#### **12. COURSES ↔ ANNOUNCEMENTS**
- Relationship: **POSTED_IN** (1:N)
- 1 Course có nhiều Announcements
- 1 Announcement có thể thuộc về 1 Course (hoặc NULL cho school-wide)

#### **13. USERS ↔ FORUM_POSTS**
- Relationship: **AUTHORS** (1:N)
- 1 User tạo nhiều Forum Posts
- 1 Forum Post được tạo bởi 1 User

#### **14. COURSES ↔ FORUM_POSTS**
- Relationship: **DISCUSSED_IN** (1:N)
- 1 Course có nhiều Forum Posts
- 1 Forum Post thuộc về 1 Course

#### **15. FORUM_POSTS ↔ FORUM_POSTS**
- Relationship: **REPLIES_TO** (1:N) - Self-referencing
- 1 Forum Post có thể có nhiều replies
- 1 Reply thuộc về 1 Parent Post

### 2.3. Cardinality Summary

```
USERS (1) ──TEACHES──> (N) COURSES
USERS (1) ──ENROLLS──> (N) ENROLLMENTS
COURSES (1) ──HAS──> (N) ENROLLMENTS
SEMESTERS (1) ──CONTAINS──> (N) ENROLLMENTS
ENROLLMENTS (1) ──RECORDS──> (N) ATTENDANCE
ENROLLMENTS (1) ──HAS──> (1) GRADES
COURSES (1) ──HAS──> (N) GRADE_COMPONENTS
SEMESTERS (1) ──DEFINES──> (N) GRADE_COMPONENTS
USERS (1) ──ACHIEVES──> (N) GPA_RECORDS
SEMESTERS (1) ──CALCULATED_FOR──> (N) GPA_RECORDS
USERS (1) ──CREATES──> (N) ANNOUNCEMENTS
COURSES (1) ──POSTED_IN──> (N) ANNOUNCEMENTS
USERS (1) ──AUTHORS──> (N) FORUM_POSTS
COURSES (1) ──DISCUSSED_IN──> (N) FORUM_POSTS
FORUM_POSTS (1) ──REPLIES_TO──> (N) FORUM_POSTS
```

### 2.4. Cách Vẽ ERD

#### Công cụ đề xuất:
- **MySQL Workbench** (đã có trong MySQL)
- **dbdiagram.io** (https://dbdiagram.io/) - Online, miễn phí
- **Draw.io** (https://app.diagrams.net/)
- **Lucidchart**
- **ER/Studio**

#### Các bước vẽ:

1. **Vẽ Entities (Bảng)**
   - Mỗi bảng là 1 hình chữ nhật
   - Chia thành 3 phần:
     - Phần trên: Tên bảng (in hoa, đậm)
     - Phần giữa: Primary Key (có biểu tượng khóa 🔑)
     - Phần dưới: Các attributes khác

2. **Đánh dấu Primary Keys**
   - Gạch chân hoặc thêm icon 🔑
   - Ví dụ: user_id (PK)

3. **Đánh dấu Foreign Keys**
   - Thêm icon 🔗 hoặc (FK)
   - Ví dụ: teacher_id (FK)

4. **Vẽ Relationships**
   - Dùng đường kẻ nối giữa các bảng
   - Thêm ký hiệu cardinality:
     - `1` hoặc `|` = One
     - `N`, `*` hoặc `crow's foot` = Many
     - `0..1` = Zero or One (optional)

5. **Thêm Relationship Labels**
   - Ghi tên quan hệ trên đường kẻ
   - VD: "teaches", "enrolls", "has"

#### Ví dụ cấu trúc trong dbdiagram.io:

```dbml
Table users {
  user_id int [pk, increment]
  username varchar(50) [unique, not null]
  password varchar(255) [not null]
  full_name varchar(100) [not null]
  email varchar(100) [unique, not null]
  phone varchar(20)
  user_type enum('ADMIN','TEACHER','STUDENT') [not null]
  created_at timestamp
  updated_at timestamp
}

Table courses {
  course_id int [pk, increment]
  course_code varchar(20) [unique, not null]
  course_name varchar(100) [not null]
  description text
  credits int [not null]
  teacher_id int [ref: > users.user_id]
  max_students int
  schedule_days varchar(50)
  schedule_time varchar(50)
  room_number varchar(20)
  created_at timestamp
  updated_at timestamp
}

Table enrollments {
  enrollment_id int [pk, increment]
  student_id int [ref: > users.user_id, not null]
  course_id int [ref: > courses.course_id, not null]
  semester_id int [ref: > semesters.semester_id]
  enrollment_date timestamp
  grade varchar(5)
  status enum('ACTIVE','COMPLETED','DROPPED')
}

// ... thêm các bảng khác tương tự
```

#### Chú thích ký hiệu Crow's Foot Notation:

```
|     = Exactly one
||    = One or more
|o    = Zero or one
o|    = Zero or more (many)
```

---

## 3. DATABASE SCHEMA

### 3.1. Schema Overview

Database Name: **school_management**
- Character Set: **utf8mb4**
- Collation: **utf8mb4_unicode_ci**
- Engine: **InnoDB**

### 3.2. Tables Structure

#### **Tổng quan các bảng:**

| # | Table Name | Primary Key | Foreign Keys | Description |
|---|------------|-------------|--------------|-------------|
| 1 | users | user_id | - | Lưu thông tin người dùng (Admin, Teacher, Student) |
| 2 | courses | course_id | teacher_id → users | Lưu thông tin khóa học |
| 3 | enrollments | enrollment_id | student_id → users, course_id → courses, semester_id → semesters | Lưu đăng ký khóa học |
| 4 | semesters | semester_id | - | Lưu thông tin học kỳ |
| 5 | grade_components | component_id | course_id → courses, semester_id → semesters | Cấu hình thành phần điểm |
| 6 | attendance | attendance_id | enrollment_id → enrollments, recorded_by → users | Lưu điểm danh |
| 7 | grades | grade_id | enrollment_id → enrollments | Lưu điểm số chi tiết |
| 8 | gpa_records | gpa_id | student_id → users, semester_id → semesters | Lưu GPA theo học kỳ |
| 9 | announcements | announcement_id | author_id → users, course_id → courses | Lưu thông báo |
| 10 | forum_posts | post_id | course_id → courses, author_id → users, parent_post_id → forum_posts | Lưu bài viết diễn đàn |

### 3.3. Indexes

#### **users table:**
- `PRIMARY KEY (user_id)`
- `UNIQUE KEY (username)`
- `UNIQUE KEY (email)`
- `INDEX idx_username (username)`
- `INDEX idx_email (email)`
- `INDEX idx_user_type (user_type)`

#### **courses table:**
- `PRIMARY KEY (course_id)`
- `UNIQUE KEY (course_code)`
- `INDEX idx_course_code (course_code)`
- `INDEX idx_teacher_id (teacher_id)`

#### **enrollments table:**
- `PRIMARY KEY (enrollment_id)`
- `UNIQUE KEY unique_enrollment (student_id, course_id, semester_id)`
- `INDEX idx_student_id (student_id)`
- `INDEX idx_course_id (course_id)`
- `INDEX idx_semester_id (semester_id)`
- `INDEX idx_status (status)`

#### **semesters table:**
- `PRIMARY KEY (semester_id)`
- `INDEX idx_academic_year (academic_year)`
- `INDEX idx_is_active (is_active)`

#### **grade_components table:**
- `PRIMARY KEY (component_id)`
- `UNIQUE KEY unique_course_semester (course_id, semester_id)`

#### **attendance table:**
- `PRIMARY KEY (attendance_id)`
- `UNIQUE KEY unique_attendance (enrollment_id, attendance_date)`
- `INDEX idx_enrollment_id (enrollment_id)`
- `INDEX idx_attendance_date (attendance_date)`
- `INDEX idx_status (status)`

#### **grades table:**
- `PRIMARY KEY (grade_id)`
- `UNIQUE KEY unique_enrollment_grade (enrollment_id)`
- `INDEX idx_enrollment_id (enrollment_id)`

#### **gpa_records table:**
- `PRIMARY KEY (gpa_id)`
- `UNIQUE KEY unique_student_semester (student_id, semester_id, is_cumulative)`
- `INDEX idx_student_id (student_id)`
- `INDEX idx_semester_id (semester_id)`

#### **announcements table:**
- `PRIMARY KEY (announcement_id)`
- `INDEX idx_author_id (author_id)`
- `INDEX idx_course_id (course_id)`
- `INDEX idx_created_at (created_at)`

#### **forum_posts table:**
- `PRIMARY KEY (post_id)`
- `INDEX idx_course_id (course_id)`
- `INDEX idx_author_id (author_id)`
- `INDEX idx_parent_post_id (parent_post_id)`
- `INDEX idx_created_at (created_at)`

### 3.4. Constraints

#### **Foreign Key Constraints:**

```sql
-- COURSES
FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE SET NULL

-- ENROLLMENTS
FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE
FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
FOREIGN KEY (semester_id) REFERENCES semesters(semester_id) ON DELETE CASCADE

-- GRADE_COMPONENTS
FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
FOREIGN KEY (semester_id) REFERENCES semesters(semester_id) ON DELETE CASCADE
CONSTRAINT check_percentages CHECK (inclass_percentage + midterm_percentage + final_percentage = 100.00)

-- ATTENDANCE
FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE
FOREIGN KEY (recorded_by) REFERENCES users(user_id) ON DELETE SET NULL

-- GRADES
FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE

-- GPA_RECORDS
FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE
FOREIGN KEY (semester_id) REFERENCES semesters(semester_id) ON DELETE CASCADE

-- ANNOUNCEMENTS
FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE

-- FORUM_POSTS
FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
FOREIGN KEY (parent_post_id) REFERENCES forum_posts(post_id) ON DELETE CASCADE
```

### 3.5. Data Types Reference

#### **Numeric Types:**
- `INT` - Số nguyên (4 bytes, -2147483648 to 2147483647)
- `DECIMAL(5,2)` - Số thập phân chính xác (VD: 100.00)
- `DECIMAL(3,2)` - Số thập phân chính xác (VD: 4.00 cho GPA)

#### **String Types:**
- `VARCHAR(n)` - Chuỗi ký tự có độ dài biến đổi
- `TEXT` - Văn bản dài (lên đến 65,535 ký tự)

#### **Date/Time Types:**
- `DATE` - Ngày (YYYY-MM-DD)
- `TIMESTAMP` - Ngày giờ (YYYY-MM-DD HH:MM:SS)

#### **Enum Types:**
- `ENUM('value1', 'value2', ...)` - Danh sách giá trị cố định

#### **Boolean Type:**
- `BOOLEAN` - True/False (stored as TINYINT(1))

### 3.6. Cách Vẽ Schema Diagram

#### Công cụ đề xuất:
- **MySQL Workbench** - Reverse Engineer từ database
- **DBeaver** - Database management tool
- **dbdiagram.io** - Online schema designer
- **SchemaSpy** - Tự động generate schema documentation

#### Các bước vẽ Schema bằng MySQL Workbench:

1. **Connect to Database**
   ```
   File → New Model → Database → Reverse Engineer
   ```

2. **Select Connection**
   - Chọn connection đến database `school_management`
   - Next → Select schema → Next

3. **Auto-generate Diagram**
   - MySQL Workbench sẽ tự động tạo EER Diagram
   - Hiển thị tất cả tables, relationships, và keys

4. **Customize Layout**
   - Drag & drop để sắp xếp lại vị trí tables
   - Group related tables lại gần nhau

5. **Export Diagram**
   ```
   File → Export → Export as PNG/PDF/SVG
   ```

#### Manual Drawing Tips:

1. **Nhóm các bảng theo chức năng:**
   - **Core**: users, courses, enrollments, semesters
   - **Academic**: grades, grade_components, gpa_records, attendance
   - **Communication**: announcements, forum_posts

2. **Sắp xếp bố cục:**
   ```
   [users]──┬──[courses]──[grade_components]
            │       │
            │       └──[enrollments]──┬──[attendance]
            │                         │
            │                         ├──[grades]
            │                         │
            └──[gpa_records]         [semesters]
   
   [announcements]    [forum_posts]
   ```

3. **Color coding:**
   - Blue: Core entities
   - Green: Academic entities
   - Yellow: Communication entities
   - Red: System/Admin entities

---

## 4. TỔNG KẾT

### 4.1. Workflow Tạo Diagrams

1. **Bước 1: Vẽ Use Case Diagram**
   - Xác định actors (Admin, Teacher, Student)
   - Liệt kê tất cả use cases
   - Vẽ relationships giữa actors và use cases

2. **Bước 2: Vẽ ERD**
   - Liệt kê tất cả entities (tables)
   - Xác định attributes cho mỗi entity
   - Đánh dấu Primary Keys và Foreign Keys
   - Vẽ relationships với cardinality

3. **Bước 3: Tạo Database Schema**
   - Tạo database từ schema.sql
   - Sử dụng MySQL Workbench reverse engineer
   - Hoặc vẽ thủ công theo ERD

### 4.2. Best Practices

#### **Use Case Diagram:**
- ✅ Tập trung vào "what" (chức năng gì), không phải "how" (làm như thế nào)
- ✅ Mỗi use case nên là 1 verb phrase (động từ + danh từ)
- ✅ Tránh quá chi tiết, chỉ hiển thị high-level features
- ✅ Nhóm các use cases liên quan

#### **ERD:**
- ✅ Sử dụng naming convention nhất quán
- ✅ Mỗi entity nên có 1 Primary Key duy nhất
- ✅ Foreign Keys phải reference đúng Primary Keys
- ✅ Đánh dấu rõ cardinality (1:1, 1:N, N:M)
- ✅ Normalize database để tránh redundancy

#### **Database Schema:**
- ✅ Sử dụng appropriate data types
- ✅ Thêm indexes cho các columns thường xuyên query
- ✅ Thêm constraints để đảm bảo data integrity
- ✅ Thêm comments để document mục đích của mỗi column
- ✅ Sử dụng ON DELETE CASCADE/SET NULL hợp lý

### 4.3. Checklist

#### **Use Case Diagram:**
- [ ] Đã vẽ tất cả actors?
- [ ] Đã liệt kê đầy đủ use cases cho mỗi actor?
- [ ] Các use cases có rõ ràng, dễ hiểu?
- [ ] Đã thêm quan hệ include/extend nếu cần?

#### **ERD:**
- [ ] Đã vẽ tất cả 10 entities?
- [ ] Mỗi entity có Primary Key?
- [ ] Đã đánh dấu tất cả Foreign Keys?
- [ ] Đã vẽ tất cả relationships?
- [ ] Cardinality đã chính xác?

#### **Database Schema:**
- [ ] Đã tạo database với charset utf8mb4?
- [ ] Tất cả tables đã có Primary Keys?
- [ ] Foreign Key constraints đã được thiết lập?
- [ ] Indexes đã được tạo cho các columns quan trọng?
- [ ] Constraints (CHECK, UNIQUE) đã được thêm?

---

## 5. RESOURCES

### 5.1. Công cụ miễn phí

#### **Use Case Diagram:**
- Draw.io: https://app.diagrams.net/
- Lucidchart Free: https://www.lucidchart.com/
- PlantUML: https://plantuml.com/

#### **ERD:**
- dbdiagram.io: https://dbdiagram.io/
- Draw.io: https://app.diagrams.net/
- MySQL Workbench: https://www.mysql.com/products/workbench/

#### **Database Schema:**
- MySQL Workbench (đi kèm MySQL)
- DBeaver: https://dbeaver.io/
- phpMyAdmin (web-based)

### 5.2. Tutorials

#### **Use Case Diagram:**
- Visual Paradigm Tutorial: https://www.visual-paradigm.com/guide/uml-unified-modeling-language/what-is-use-case-diagram/
- Lucidchart Tutorial: https://www.lucidchart.com/pages/uml-use-case-diagram

#### **ERD:**
- Lucidchart ERD Tutorial: https://www.lucidchart.com/pages/er-diagrams
- dbdiagram.io Docs: https://dbdiagram.io/docs

#### **Database Schema:**
- MySQL Workbench Reverse Engineering: https://dev.mysql.com/doc/workbench/en/wb-reverse-engineer-live.html

### 5.3. Templates

Bạn có thể sử dụng file `schema.sql` trong project để:
1. Tạo database mới
2. Reverse engineer để tạo ERD tự động
3. Tham khảo cấu trúc để vẽ diagrams

---

## 6. NOTES

### Key Points về School Management System:

1. **3 Loại User:** Admin, Teacher, Student
2. **Period-based Scheduling:** 10 periods/day (P1-P10)
3. **Grade Calculation:** In-class + Midterm + Final = 100%
4. **GPA System:** 4.0 scale với semester và cumulative GPA
5. **Attendance Tracking:** Present, Absent, Late, Excused
6. **Course Forum:** Threaded discussions với replies
7. **Announcements:** School-wide hoặc course-specific

### Database Features:

- ✅ CASCADE delete cho dependent records
- ✅ SET NULL cho optional references
- ✅ UNIQUE constraints để prevent duplicates
- ✅ CHECK constraints cho data validation
- ✅ Comprehensive indexing cho performance
- ✅ Timestamp tracking (created_at, updated_at)

---

**Good luck với việc vẽ diagrams! 🎨📊**
