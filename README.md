# 📚 SchoolHub - Android School Management App

SchoolHub is a complete Android-based school management system supporting three roles: **students**, **teachers**, and **registrars**. It provides robust features such as marks entry, attendance tracking, schedule management, assignment submission, and notifications — all through an intuitive role-based UI.

> 🎨 The app interface was **initially designed in Figma**, then developed in **Android Studio (Java)** with a **PHP-MySQL backend** hosted on a **global hosting server**, allowing remote access and real-time data syncing.

---

## 🚀 Features Overview

### 👨‍🎓 Student Features
- 📊 View marks (Quiz, Assignment, Mid, Final) by subject
- 📆 View today's schedule and upcoming events
- 📁 Submit and download assignments
- ✅ Track attendance history
- 🔔 Receive and manage notifications
- 🏆 View class leaderboard
- 📅 Add school events to calendar

### 👩‍🏫 Teacher Features
- 📝 Publish marks for any student/class/subject
- 📋 View grade summary and top students
- 📷 QR code-based student attendance
- 📅 Schedule exams with time conflict detection
- 🔔 Push notifications for reminders and updates
- 🏆 Class leaderboard with medals
- 📥 View student submissions

### 🧑‍💼 Registrar Features
- 🏫 Manage class schedules (class, teacher, subject, time)
- ⏱️ Conflict detection to avoid overlapping schedules
- 📚 Assign teachers to subjects and classes
- 📅 Create and update the full timetable

### 🔔 Notifications
- System-wide and role-based notifications
- Push notification support
- Mark as read/unread with visual status (green/red dot)
- Expandable cards on tap

---

## 🖼️ Screenshots

> 💡 All screenshots are from the actual running app. Images are resized for readability.

### 🔐 Login Interface
<img src="screenshots/Login.png" width="350"/>

---

### 👨‍🎓 Student Interfaces

#### Student Dashboard
<img src="screenshots/StudentDashboard.png" width="350"/>

#### Student Marks
<img src="screenshots/StudentMarks.png" width="350"/>

#### Attendance Page
<img src="screenshots/StudentAttendance.png" width="350"/>

#### Assignments Page
<img src="screenshots/StudentAssignment.png" width="350"/>

#### Event Calendar
<img src="screenshots/StudentCalendar.png" width="350"/>

#### Event Board (Details Page)
<img src="screenshots/StudentEvent.png" width="350"/>

#### Student Leaderboard
<img src="screenshots/StudentLeaderboard.png" width="350"/>

---

### 👩‍🏫 Teacher Interfaces

#### Teacher Dashboard
<img src="screenshots/TeacherDashboard.png" width="350"/>

#### Publish Marks Page
<img src="screenshots/TeacherPublishMarks.png" width="350"/>

#### View Grade Summary
<img src="screenshots/TeacherViewGrade.png" width="350"/>

#### Take Attendance
<img src="screenshots/TeacherTakeAttendance.png" width="350"/>

#### Take Attendance Using QR Scanner
<img src="screenshots/TeacherQR.png" width="350"/>

#### Schedule Exam Page
<img src="screenshots/TeacherScheduleExam.png" width="350"/>

#### Leaderboard with Medals
<img src="screenshots/TeacherLeaderboard.png" width="350"/>

---

### 🧑‍💼 Registrar Interfaces

#### Registrar Dashboard
<img src="screenshots/RegDashboard.png" width="350"/>

#### Schedule Builder
<img src="screenshots/RegBuildSchedule.png" width="350"/>

#### Add Class
<img src="screenshots/RegAddClass.png" width="350"/>

#### Add Student
<img src="screenshots/RegAddStudent.png" width="350"/>

#### Add Teacher
<img src="screenshots/RegAddTeacher.png" width="350"/>

---

### 🔔 Notifications Page

#### Notification List with Status Dots
<img src="screenshots/Notification.png" width="350"/>

---

## 🎬 Demo Video

Watch a full walkthrough of the app showcasing all user roles and features:

[![Watch Demo](https://drive.google.com/file/d/1AisuFVfjIBRLnmEVMk65rnXug5HLnmzU/view?usp=sharing)

---

## 🛠️ Technologies Used

- **Android Studio (Java)**
- **PHP (backend APIs)** hosted on a **remote server**
- **MySQL** database (global access via hosting)
- **Volley** for API communication
- **Zxing** for QR scanner functionality
- **Figma** for UI/UX design prototypes
- **Custom layouts** with RecyclerView, CardView, and Fragments

