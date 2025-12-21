# 🎓 School Management System

<div align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/Apache%20Tomcat-F8DC75?style=for-the-badge&logo=apache-tomcat&logoColor=black" alt="Tomcat">
</div>

---

## 👥 Team Information

| Name | Student ID |
|------|------------|
| 👨‍💻 Huỳnh Văn Quang Chiến | ITCSIU22022 |
| 👨‍💻 Nguyễn Việt Thảo | ITCSIU23058 |
| 👨‍💻 Lưu Đức Mạnh | ITITIU23016 |

---

## 🛠️ Technologies Used

**Architecture:** MVC Framework

### Frontend
- 📄 JSP (JavaServer Pages)
- 🎨 HTML5
- 💅 CSS3

### Backend
- ☕ Java Servlets (Jakarta EE 10)
- 🗄️ MySQL with JDBC

### Tools & Servers
- 🐱 Apache Tomcat
- 🔨 Apache Maven

---

## 📊 Database Setup

### Prerequisites
- MySQL Server installed
- MySQL Workbench (recommended)

### Installation Steps

1. **Create Database Connection**
   ```
   Connection Name: school_management
   ```

2. **Import Database Schema**
   - Open `schema.sql` file
   - Execute the SQL script in your MySQL connection

3. **Configure Database Properties**
   - Navigate to `src/main/resources/db.properties`
   - Update the following properties:
     ```properties
     db.username=YOUR_MYSQL_USERNAME
     db.password=YOUR_MYSQL_PASSWORD
     ```

4. **Build and Run**
   ```bash
   mvn clean install
   mvn tomcat:run
   ```

---

## ✨ Features

### 🔐 Authentication & Authorization
- Secure login system with role-based access control
- Session management and validation
- Password change functionality
- Three user roles: **Admin**, **Teacher**, and **Student**

### 👨‍💼 Admin Features
- **User Management**
  - Add, edit, and delete users (students and teachers)
  - View all users in the system
  - Manage user roles and permissions

- **Course Management**
  - Create, update, and delete courses
  - Assign teachers to courses
  - View all courses in the system

- **Semester Management**
  - Create and manage academic semesters
  - Set semester start and end dates

- **Grade Management**
  - Configure grade components (assignments, exams, projects)
  - View student grades across all courses
  - Monitor student GPA

- **Attendance Management**
  - View attendance records for all courses
  - Generate attendance reports

- **Announcements**
  - Create and manage system-wide announcements
  - Post important notices for all users

### 👨‍🏫 Teacher Features
- **Course Management**
  - View assigned courses
  - Access course details and enrolled students
  - Manage course timetable

- **Grade Management**
  - Enter and update student grades
  - Define grade components for courses
  - View grade distribution

- **Attendance Management**
  - Take daily attendance for classes
  - View attendance history
  - Track student attendance patterns

- **Announcements**
  - Create course-specific announcements
  - Communicate with enrolled students

- **Dashboard**
  - View teaching schedule
  - Quick access to course statistics

### 👨‍🎓 Student Features
- **Course Enrollment**
  - Browse available courses
  - Enroll in courses
  - View course details and schedules

- **Academic Records**
  - View enrolled courses
  - Check grades for all assignments and exams
  - Monitor GPA and academic performance
  - Access semester-wise GPA dashboard

- **Attendance**
  - View personal attendance records
  - Track attendance percentage per course

- **Timetable**
  - View personalized class schedule
  - Check course timings and locations

- **Forum/Discussion**
  - Participate in course discussions
  - Create and reply to forum posts
  - Collaborate with classmates

- **Announcements**
  - View course announcements
  - Stay updated with important notices

---

## 📁 Project Structure

```
Web-app-project-25-26-real/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── school/
│   │   │           ├── controller/      # Servlets handling HTTP requests
│   │   │           │   ├── AdminServlet.java
│   │   │           │   ├── AnnouncementServlet.java
│   │   │           │   ├── AttendanceServlet.java
│   │   │           │   ├── ChangePasswordServlet.java
│   │   │           │   ├── ForumServlet.java
│   │   │           │   ├── GpaServlet.java
│   │   │           │   ├── GradeServlet.java
│   │   │           │   ├── LoginServlet.java
│   │   │           │   ├── LogoutServlet.java
│   │   │           │   ├── SemesterServlet.java
│   │   │           │   ├── StudentServlet.java
│   │   │           │   └── TeacherServlet.java
│   │   │           ├── dao/              # Data Access Objects
│   │   │           │   ├── AnnouncementDAO.java
│   │   │           │   ├── AttendanceDAO.java
│   │   │           │   ├── CourseDAO.java
│   │   │           │   ├── EnrollmentDAO.java
│   │   │           │   ├── ForumDAO.java
│   │   │           │   ├── GpaRecordDAO.java
│   │   │           │   ├── GradeComponentDAO.java
│   │   │           │   ├── GradeDAO.java
│   │   │           │   ├── SemesterDAO.java
│   │   │           │   └── UserDAO.java
│   │   │           ├── filter/           # Security filters
│   │   │           │   ├── AdminFilter.java
│   │   │           │   └── AuthFilter.java
│   │   │           ├── model/            # Entity classes
│   │   │           │   ├── Announcement.java
│   │   │           │   ├── Attendance.java
│   │   │           │   ├── Course.java
│   │   │           │   ├── Enrollment.java
│   │   │           │   ├── ForumPost.java
│   │   │           │   ├── GpaRecord.java
│   │   │           │   ├── Grade.java
│   │   │           │   ├── GradeComponent.java
│   │   │           │   ├── Semester.java
│   │   │           │   └── User.java
│   │   │           └── util/             # Utility classes
│   │   │               ├── DatabaseConnection.java
│   │   │               └── SessionValidator.java
│   │   ├── resources/
│   │   │   ├── db.properties            # Database configuration
│   │   │   └── META-INF/
│   │   │       └── persistence.xml
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml              # Web application configuration
│   │       │   ├── beans.xml
│   │       │   ├── includes/            # Reusable JSP components
│   │       │   │   ├── header.jsp
│   │       │   │   └── footer.jsp
│   │       │   └── views/               # JSP pages
│   │       │       ├── admin/           # Admin views
│   │       │       ├── teacher/         # Teacher views
│   │       │       └── student/         # Student views
│   │       ├── images/                  # Static images
│   │       ├── index.html
│   │       └── login.jsp
├── schema.sql                           # Database schema
├── DATABASE_UPDATE_INSTRUCTIONS.md
├── pom.xml                              # Maven configuration
└── README.md
```

---

## 🗄️ Database Schema

The system uses a relational database with the following key tables:

- **users** - Stores user information (students, teachers, admins)
- **courses** - Course catalog and details
- **semesters** - Academic semester information
- **enrollments** - Student course enrollments
- **grades** - Student grade records
- **grade_components** - Grade component definitions (assignments, exams)
- **attendance** - Attendance records
- **announcements** - System and course announcements
- **forum_posts** - Discussion forum posts
- **gpa_records** - Student GPA calculations

---

## 🚀 Getting Started

### Prerequisites

Before running the application, ensure you have:

- ☕ **Java Development Kit (JDK) 21** or higher
- 🗄️ **MySQL Server 8.0** or higher
- 🐱 **Apache Tomcat 10.x** (supports Jakarta EE)
- 🔨 **Apache Maven 3.6** or higher

### Installation

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd Web-app-project-25-26-real
   ```

2. **Set Up Database**
   - Open MySQL Workbench or your preferred MySQL client
   - Create a new database connection named `school_management`
   - Execute the `schema.sql` file to create tables and sample data

3. **Configure Database Connection**
   - Open `src/main/resources/db.properties`
   - Update with your MySQL credentials:
     ```properties
     db.url=jdbc:mysql://localhost:3306/school_management
     db.username=YOUR_USERNAME
     db.password=YOUR_PASSWORD
     ```

4. **Build the Project**
   ```bash
   mvn clean install
   ```

5. **Deploy to Tomcat**
   
   **Option 1: Using Maven Tomcat Plugin**
   ```bash
   mvn tomcat7:run
   ```
   
   **Option 2: Manual Deployment**
   - Copy the WAR file from `target/school-management-mvc.war` to Tomcat's `webapps` directory
   - Start Tomcat server
   - Access the application at `http://localhost:8080/school-management-mvc`

6. **Access the Application**
   - Open your browser and navigate to: `http://localhost:8080/school-management-mvc`
   - Use the default credentials (check `schema.sql` for sample users)

### Default Login Credentials

After importing the database schema, you can use these default accounts:

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Teacher | teacher | teacher123 |
| Student | student | student123 |

*(Note: Change these credentials in production!)*

---

## 🎯 Usage Guide

### For Administrators
1. Log in with admin credentials
2. Navigate to **User Management** to add teachers and students
3. Set up **Semesters** for the academic year
4. Create **Courses** and assign teachers
5. Configure **Grade Components** for each course
6. Post **Announcements** for the entire school

### For Teachers
1. Log in with teacher credentials
2. View your assigned courses on the dashboard
3. Take **Attendance** for your classes
4. Enter **Grades** for students
5. Post **Course Announcements**
6. View your teaching timetable

### For Students
1. Log in with student credentials
2. Browse and **Enroll in Courses**
3. Check your **Timetable**
4. View **Grades** and **GPA**
5. Monitor your **Attendance**
6. Participate in **Course Forums**
7. Read **Announcements**

---

## 🏗️ Architecture

### MVC Pattern

The application follows the **Model-View-Controller (MVC)** architecture:

- **Model**: POJO classes in `com.school.model` package represent database entities
- **View**: JSP pages in `webapp/WEB-INF/views` handle presentation logic
- **Controller**: Servlets in `com.school.controller` package manage request/response flow

### Design Patterns Used

1. **DAO (Data Access Object)**: Separates data persistence logic from business logic
2. **Singleton**: Database connection management
3. **Filter Pattern**: Authentication and authorization filters
4. **Front Controller**: Servlets handle incoming requests and route to appropriate views

### Request Flow

```
Client Request → Filter (Auth/Admin) → Servlet → DAO → Database
                                          ↓
                                        Model
                                          ↓
Client Response ← JSP View ← Servlet Response
```

---

## 🔒 Security Features

- **Session-based Authentication**: Secure user sessions with timeout
- **Role-based Authorization**: Different access levels for admin, teacher, and student
- **Filter-based Security**: `AuthFilter` and `AdminFilter` protect resources
- **SQL Injection Prevention**: Prepared statements in all DAO operations
- **Password Security**: (Recommend implementing password hashing in production)

---

## 📝 API Endpoints

### Admin Endpoints
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/users` - List all users
- `POST /admin/add-user` - Add new user
- `POST /admin/edit-user` - Edit user details
- `POST /admin/delete-user` - Delete user
- `GET /admin/courses` - Manage courses
- `POST /admin/course` - Add/edit course
- `GET /admin/semesters` - Manage semesters
- `POST /admin/semester` - Add/edit semester

### Teacher Endpoints
- `GET /teacher/dashboard` - Teacher dashboard
- `GET /teacher/courses` - View assigned courses
- `GET /teacher/attendance` - Attendance management
- `POST /teacher/take-attendance` - Record attendance
- `GET /teacher/grades` - Grade management
- `POST /teacher/enter-grades` - Submit grades
- `GET /teacher/timetable` - View schedule

### Student Endpoints
- `GET /student/dashboard` - Student dashboard
- `GET /student/available-courses` - Browse courses
- `POST /student/enroll` - Enroll in course
- `GET /student/my-courses` - View enrolled courses
- `GET /student/gpa-dashboard` - View GPA
- `GET /student/timetable` - View schedule
- `GET /student/forum` - Discussion forum

### Common Endpoints
- `GET /login` - Login page
- `POST /login` - Process login
- `GET /logout` - Logout
- `GET /change-password` - Change password page
- `POST /change-password` - Update password
- `GET /announcements` - View announcements

---

## 🛠️ Development

### Building from Source

```bash
# Clean and compile
mvn clean compile

# Run tests (if available)
mvn test

# Package as WAR
mvn package

# Skip tests during build
mvn clean install -DskipTests
```

### Project Configuration Files

- **pom.xml**: Maven dependencies and build configuration
- **web.xml**: Servlet mappings and filters
- **db.properties**: Database connection settings
- **persistence.xml**: JPA configuration (if using JPA)

---

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify MySQL is running
   - Check credentials in `db.properties`
   - Ensure database `school_management` exists

2. **Tomcat Won't Start**
   - Check if port 8080 is already in use
   - Verify Java version compatibility (Java 21+)
   - Check Tomcat logs in `logs/catalina.out`

3. **404 Error After Deployment**
   - Verify the context path in URL
   - Check if WAR file is deployed correctly
   - Review `web.xml` servlet mappings

4. **Session Timeout**
   - Sessions expire after inactivity
   - Simply log in again

---

## 📚 Technologies & Dependencies

### Core Technologies
- **Java 21** - Programming language
- **Jakarta EE 10** - Enterprise Java platform
- **MySQL 8.0** - Relational database
- **Apache Tomcat 10.1** - Web server & servlet container
- **Apache Maven 3.9** - Build automation

### Key Dependencies (from pom.xml)
- `jakarta.servlet-api:6.0.0` - Servlet API
- `jakarta.servlet.jsp-api:3.1.1` - JSP API
- `jakarta.servlet.jsp.jstl-api:3.0.1` - JSTL API
- `mysql-connector-j:8.0.33` - MySQL JDBC driver

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is developed for educational purposes as part of a university course project.

---

## 🙏 Acknowledgments

- International University - Vietnam National University HCMC
- Course Instructors and Teaching Assistants
- Open source community for various tools and libraries

---

<div align="center">
  <p>Made with ❤️ by Team Web Development</p>
  <p>© 2025 School Management System. All rights reserved.</p>
</div>

