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

