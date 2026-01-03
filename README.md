# 🏥 Mortuary Management System

## 📱 Android Application

### **Project Overview**
A comprehensive Android application for managing mortuary operations with role-based access control. The system facilitates seamless communication between police, mortuary staff, doctors, and administrators.

---

## 👥 **User Roles & Features**

### **1. Police Officer** 👮
- Report unidentified bodies
- Generate unique Case IDs
- Check case status
- Receive notifications
- Update identity information

### **2. Mortuary Staff** 🏥
- Accept/Reject incoming cases
- Reserve mortuary slots
- Manage body storage
- Confirm body receipt
- Track case progress

### **3. Doctor** 🩺
- Perform autopsy/examination
- Submit medical reports
- Upload findings and images
- Document cause of death

### **4. Administrator** 👑
- User management
- System monitoring
- Generate reports
- Audit logs

---

## 🔧 **Technical Stack**

- **Language:** Java
- **IDE:** Android Studio
- **Database:** SQLite
- **Minimum SDK:** 21 (Android 5.0)
- **Target SDK:** 34 (Android 14)

---

## 📁 **Project Structure**
MortuaryManagementSystem/
├── app/
│ ├── manifests/
│ │ └── AndroidManifest.xml
│ ├── java/
│ │ └── com/example/mortuarymanagementsystem/
│ │ ├── LoginActivity.java
│ │ ├── DashboardActivity.java
│ │ ├── PoliceDashboardActivity.java
│ │ ├── ReportBodyActivity.java
│ │ ├── MortuaryStaffDashboardActivity.java
│ │ ├── DoctorDashboardActivity.java
│ │ ├── AdminDashboardActivity.java
│ │ ├── CasesListActivity.java
│ │ ├── NotificationsActivity.java
│ │ ├── MortuaryCaseActionActivity.java
│ │ ├── DatabaseHelper.java
│ │ ├── ThemeManager.java
│ │ └── WebViewActivity.java
│ └── res/
│ ├── layout/ (All XML layouts)
│ ├── drawable/ (Icons and images)
│ ├── menu/ (Navigation menus)
│ └── values/ (Strings, colors, themes)
└── build.gradle
