# Book Store Management Application

A full-stack web-based Book Store Management System that digitizes and simplifies the management of inventory, customer interactions, sales, and reporting for both physical and online bookstores.

## Project Objectives

- Allow customers to browse and order books
- Enable administrators to manage inventory, users, and orders
- Provide insights and reports on store activity

## Modules

User Management
Inventory Management  
Sales, Order and Payment Processing
Reporting and Analytics  
Search and Recommendation

## Technology Stack

| Layer      | Technologies Used                                       |
|------------|---------------------------------------------------------|
| Frontend   | Angular, TypeScript, SCSS, Tailwind CSS (optional)      |
| Backend    | Java, Spring Boot, Spring Data JPA, Maven               |
| Database   | MySQL (local, later shift to online)                    |
| Security   | Spring Security, JWT                                    |
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

## Sprint 2 – Progress

- `User` and `Role` Entity create
- JWT authentication and authorization setup
- Features implement
  - User registration, login, and profile update
  - Role-based access for Admin and Customer
  - Token refresh using refresh token
  - Password encryption using PasswordEncoder
  - Admin access to user list and stats

## Sprint 3 – Progress

- Bug fixes and validations added
- DTO validations using annotations
  - Duplicate checks and Unauthorized access handled
  - Custom exception handling implemented
  - Error response formatting
  - Token flow verified and refresh functionality tested

## Sprint 4 – Progress

- `Cart`, `CartItem`, `Order`, `OrderItem` and `Payment` Entity create
- Controller, Service, Repository layers create
- Features implement
  - Add / Remove / Update books in cart
  - Place order from cart
  - View order history by customer and admin
  - Admin can update order status (SHIPPED, DELIVERED, RETURNED, CANCELLED)
  - Enum-based status handling for order and payment

---

> This `README.md` will be updated incrementally with each sprint to reflect current progress, features, and deployment steps.
