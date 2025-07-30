# Book Store Management Application

A full-stack web-based Book Store Management System that digitizes and simplifies the management of inventory, customer interactions, sales, and reporting for both physical and online bookstores.

## Project Objectives

- Allow customers to browse and order books
- Enable administrators to manage inventory, users, and orders
- Provide insights and reports on store activity

## Modules

1. Inventory Management Module  
2. User Management Module  
3. Sales and Order Processing Module  
4. Reporting and Analytics Module  
5. Search and Recommendation Module

## Technology Stack

| Layer      | Technologies Used                                       |
|------------|---------------------------------------------------------|
| Frontend   | Angular, TypeScript, SCSS, Tailwind CSS (optional)      |
| Backend    | Java, Spring Boot, Spring Data JPA, Maven               |
| Database   | MySQL (local, later shift to online)                    |
| Security   | Spring Security, JWT (to be added later)                |
| Payment    | Razorpay (to be integrated later)                       |
| Tools      | Git, GitHub, Postman, MySQL Workbench, Eclipse, VS Code |
| Reporting  | JasperReports / Apache POI                              |


## Folder Structure

BookStoreManagementApplication/
├── backend/ # Spring Boot application
├── frontend/ # Angular frontend application
├── database/ # SQL schema and seed data
├── docs/ # Sprint plans, ER diagrams, other docs
├── README.md
└── .gitignore


## How to Run Locally

### Backend (Spring Boot)

cd backend/bookstore-backend
./mvnw spring-boot:run


Make sure MySQL is running and application.properties is configured with correct DB credentials.

### Frontend (Angular)

cd frontend/bookstore-frontend
ng serve


App will be available at: http://localhost:4200



## Work Done So Far (Till Now)

## Sprint 0 – Progress

- Folder structure created
- Toolchain verified
- Backend setup done
- Database connected
- Frontend blank project created
- Git remotes set
- .gitignore finalized
- README.md written
- ER Diagram
- Sprint Plan updated

## Sprint 1 – Progress

- `Book` and `Category` Entity create
- Controller, Service, Repository layers create
- Features implement
  - Add / Edit / Delete / View books and categories
  - Filter/search books by title, ISBN, category 
  - Low stock alerts
  - Import books in bulk using CSV file

## Team

- **Sudhanshu Rout** (Team Lead)
- **Arya Bhardwaj Mishra**
- **Pranjal Sahu**
- **Nandani Singh**
- **Priti Mondal**

---

> This `README.md` will be updated incrementally with each sprint to reflect current progress, features, and deployment steps.
