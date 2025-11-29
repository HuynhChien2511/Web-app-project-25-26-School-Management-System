# Database Update Instructions for GPA System

## Overview
The database schema has been updated to support a comprehensive GPA calculation system with:
- **3-semester academic year** (Semester 1 & 2: 16 weeks, Semester 3: 8 weeks)
- **Configurable grade components** (inclass, midterm, final percentages)
- **Attendance tracking** with auto-drop after 3 absences
- **GPA calculation** on 4.0 scale with letter grades

## Changes Made

### New Tables Created:
1. **semesters** - Manages academic semesters (3 per year)
2. **grade_components** - Grade percentage configuration per course/semester
3. **attendance** - Daily attendance tracking
4. **grades** - Individual student grades with calculated totals
5. **gpa_records** - Semester and cumulative GPA tracking

### Modified Tables:
- **enrollments** - Added `semester_id` foreign key field

## How to Apply the Database Changes

### Method 1: Fresh Database (Recommended for Development)

1. **Backup your current database** (if you have important data):
   ```sql
   mysqldump -u root -p school_management > backup_before_gpa_update.sql
   ```

2. **Drop and recreate the database**:
   ```sql
   DROP DATABASE IF EXISTS school_management;
   CREATE DATABASE school_management;
   USE school_management;
   ```

3. **Run the updated schema.sql file**:
   - Open MySQL Workbench or command line
   - Execute the entire `schema.sql` file
   - This will create all tables with sample data

4. **Verify the update**:
   ```sql
   -- Check new tables exist
   SHOW TABLES;
   
   -- Verify enrollments has semester_id
   DESCRIBE enrollments;
   
   -- Check sample data
   SELECT * FROM semesters;
   SELECT * FROM grade_components;
   ```

### Method 2: Update Existing Database (Production)

If you need to preserve existing data:

1. **Backup your database first**:
   ```sql
   mysqldump -u root -p school_management > backup_before_gpa_update.sql
   ```

2. **Create new tables only** (extract from schema.sql):
   ```sql
   -- Run these CREATE TABLE statements from schema.sql:
   CREATE TABLE semesters (...);
   CREATE TABLE grade_components (...);
   CREATE TABLE attendance (...);
   CREATE TABLE grades (...);
   CREATE TABLE gpa_records (...);
   ```

3. **Modify enrollments table**:
   ```sql
   -- Add semester_id column
   ALTER TABLE enrollments 
   ADD COLUMN semester_id INT AFTER course_id;
   
   -- Create and activate a default semester
   INSERT INTO semesters (semester_name, semester_type, academic_year, start_date, end_date, weeks, is_active)
   VALUES ('Fall 2024', 'SEMESTER_1', '2024-2025', '2024-09-01', '2024-12-20', 16, TRUE);
   
   -- Update existing enrollments to use the new semester
   UPDATE enrollments SET semester_id = 1 WHERE semester_id IS NULL;
   
   -- Add foreign key constraint
   ALTER TABLE enrollments
   ADD CONSTRAINT fk_enrollments_semester 
   FOREIGN KEY (semester_id) REFERENCES semesters(semester_id);
   ```

4. **Create grade components for existing courses**:
   ```sql
   -- Add default grade percentages for all active courses
   INSERT INTO grade_components (course_id, semester_id, inclass_percentage, midterm_percentage, final_percentage)
   SELECT course_id, 1, 20.00, 30.00, 50.00
   FROM courses;
   ```

## Verify Database Connection

Your application is configured to connect to:
- **Host**: 127.0.0.1:3306
- **Database**: school_management
- **Username**: root
- **Password**: 1234

Make sure MySQL is running and accessible with these credentials.

## Sample Data Included

The schema includes sample data for:
- ✅ **Fall 2024 semester** (active)
- ✅ **Spring 2025 & Summer 2025** semesters (inactive)
- ✅ **Grade components** for all 7 courses (CS101, CS102, CS201, MATH201, MATH202, ENG101, ENG201)
- ✅ **Updated enrollments** with semester assignments

## Next Steps After Database Update

1. **Restart your application server** (Tomcat/GlassFish)
2. **Clear compiled classes**: Delete `target/` folder
3. **Rebuild the project**: `mvn clean install`
4. **Test enrollment**: Try enrolling a student - should automatically assign to active semester

## Files Updated in Java Code

- ✅ `Enrollment.java` - Added `semesterId` field
- ✅ `EnrollmentDAO.java` - Updated all queries to include `semester_id`
- ✅ Created 5 new model classes: `Semester.java`, `GradeComponent.java`, `Attendance.java`, `Grade.java`, `GpaRecord.java`

## Upcoming Features (Not Yet Implemented)

The following will be implemented in upcoming updates:
- [ ] DAO classes for new models
- [ ] Servlets for grade management
- [ ] Admin UI for semester and grade component management
- [ ] Teacher UI for attendance and grade entry
- [ ] Student UI for viewing grades and GPA
- [ ] Auto-drop logic for >3 absences
- [ ] GPA calculation engine

## Troubleshooting

### Error: "Unknown column 'semester_id' in 'field list'"
**Solution**: You need to run the database update. Follow Method 1 or Method 2 above.

### Error: "Table 'semesters' doesn't exist"
**Solution**: Run the complete `schema.sql` file to create all new tables.

### Error: "Cannot add foreign key constraint"
**Solution**: Make sure the `semesters` table exists before adding the foreign key to `enrollments`.

### Students can't enroll in courses
**Solution**: Make sure there's an active semester:
```sql
SELECT * FROM semesters WHERE is_active = TRUE;
-- If no active semester, update one:
UPDATE semesters SET is_active = TRUE WHERE semester_id = 1;
```

## Database Schema Version
- **Version**: 2.0.0-GPA
- **Date**: November 29, 2025
- **Compatibility**: Requires MySQL 5.7+ or MariaDB 10.2+
