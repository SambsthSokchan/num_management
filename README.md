# 🎓 NUM Management System

A comprehensive, Spring Boot-based web application for managing students, teachers, classes, and attendance at NUM. Designed with a premium, user-friendly interface using Thymeleaf and Bootstrap 5.

## 🌟 Features

*   **Dashboard**: Real-time overview of statistics (students, teachers, classes) and interactive charts.
*   **Student Management**: Full CRUD capabilities for student records, including photo management.
*   **Teacher Management**: Manage instructor profiles and subject assignments.
*   **Class & Subject Organization**: Structure academic programs with classes and subjects.
*   **Attendance Tracking**: Record and monitor student attendance with visual status indicators.
*   **User Authentication**: Secure login/signup system with role-based access control (Admin, Teacher, Student).
*   **Responsive Design**: Mobile-friendly interface optimized for all devices.

## 🛠️ Technology Stack

*   **Backend**: Java 17, Spring Boot 3 (Web, Data JPA, Security, Validation).
*   **Database**: PostgreSQL.
*   **Frontend**: Thymeleaf, Bootstrap 5, FontAwesome 6, Chart.js.
*   **Build Tool**: Maven.

## 🚀 Getting Started

### Prerequisites

*   Java Development Kit (JDK) 17 or later.
*   PostgreSQL installed and running.
*   Maven (optional, wrapper included).

### Configuration

1.  **Database Setup**:
    Create a PostgreSQL database named `num_management`.
    ```sql
    CREATE DATABASE num_management;
    ```

2.  **Application Properties**:
    Verify the database credentials in `e:\Management\NUM_Management\src\main\resources\application.properties`.
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/num_management
    spring.datasource.username=postgres
    spring.datasource.password=Pa$$w0rd
    ```

### Installation & Run

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/num-management.git
    cd num-management
    ```

2.  **Build and Run**:
    ```bash
    mvn spring-boot:run
    ```
    *Alternatively, run the `NumManagementApplication` class from your IDE.*

3.  **Access the Application**:
    Open your browser and navigate to:
    > [http://localhost:8080](http://localhost:8080)

4.  **Initial Login**:
    *   Sign up for a new account at `/signup`.
    *   Or use the default seeded admin account (if configured in `DataSeeder`).

    *   Or use the default seeded admin account (if configured in `DataSeeder`).

## 🔐 Role Permissions (Admin vs. Other Roles)

| Feature | Admin | Teacher | Student |
| :--- | :--- | :--- | :--- |
| **Full System Access** | ✅ Yes | ❌ No | ❌ No |
| **Create/Delete Users** | ✅ Yes | ❌ No | ❌ No |
| **Manage Subjects** | ✅ Yes | ✅ View Only | ❌ No |
| **Mark Attendance** | ✅ View Global | ✅ Primary Task | ❌ View Own |
| **Change System Year** | ✅ Yes | ❌ No | ❌ No |

## 📂 Project Structure

```
src/main/java/com/num/management
├── config/       # Security & App Configuration
├── controller/   # Web & REST Controllers
├── dto/          # Data Transfer Objects
├── model/        # JPA Entities
├── repository/   # Database Access Layers
└── service/      # Business Logic
```

```
src/main/resources
├── static/       # CSS, JS, Images
├── templates/    # HTML Views (Thymeleaf)
└── application.properties
```

---
*Built for the National University of Management.*
