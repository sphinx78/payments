# Friend Group Expense Tracker

A demo-ready Java web application for managing shared expenses within friend groups, with complete SQL/DBMS integration.

## Academic Project

This project serves as a comprehensive submission for both:
- **DBMS Subject**: Complete SQL schema, views, triggers, transactions
- **Java Subject**: Layered web application architecture

## Features

- Create friend groups and add members
- Record expenses with equal or custom split
- Track partial and full payments
- View outstanding balances
- Settlement simplification algorithm
- Transaction history

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Presentation Layer                  │
│         (HTML/CSS/JavaScript - Static Files)         │
├─────────────────────────────────────────────────────┤
│                  Controller Layer                    │
│     (Java Servlets - GroupController, etc.)          │
├─────────────────────────────────────────────────────┤
│                 Business Logic Layer                 │
│    (GroupService, ExpenseService, PaymentService)    │
├─────────────────────────────────────────────────────┤
│                 Data Access Layer                    │
│          (DAO Interfaces & Implementations)          │
├─────────────────────────────────────────────────────┤
│                   Database Layer                     │
│        (MySQL/In-Memory - via DataStore)             │
└─────────────────────────────────────────────────────┘
```

## Project Structure

```
expense-tracker/
├── pom.xml                 # Maven build configuration
├── sql/                    # SQL/DBMS components
│   ├── schema.sql         # Table definitions (3NF normalized)
│   ├── data.sql           # Sample data
│   ├── views.sql          # Database views
│   ├── triggers.sql       # Automatic updates
│   └── queries.sql        # Demo queries for viva
└── src/main/
    ├── java/com/expensetracker/
    │   ├── model/         # POJO classes
    │   ├── dao/           # DAO interfaces
    │   ├── dao/impl/      # In-memory implementations
    │   ├── service/       # Business logic
    │   ├── controller/    # Servlets
    │   └── util/          # DataStore utility
    └── webapp/
        ├── WEB-INF/web.xml
        ├── css/style.css
        ├── index.html
        ├── add-expense.html
        ├── record-payment.html
        └── settlement.html
```

## Running the Application

### Prerequisites
- Java 8 or higher
- Maven 3.6+

### Quick Start

```bash
# Navigate to project directory
cd expense-tracker

# Compile and run
mvn jetty:run

# Open browser at
http://localhost:8080
```

## DBMS Concepts Covered

1. **Normalization**: Schema is in 3NF
2. **Primary Keys**: Auto-increment IDs
3. **Foreign Keys**: Referential integrity
4. **Constraints**: CHECK, NOT NULL, UNIQUE
5. **Views**: Pre-computed summaries
6. **Triggers**: Automatic settlement updates
7. **Transactions**: ACID compliance demos
8. **Aggregate Functions**: SUM, COUNT, AVG
9. **Joins**: INNER, LEFT joins

## Java Concepts Covered

1. **OOP**: Encapsulation, abstraction
2. **Design Patterns**: DAO, Service Layer
3. **Servlets**: HTTP request handling
4. **JSON APIs**: RESTful endpoints
5. **Collections**: Maps, Lists
6. **BigDecimal**: Precision arithmetic
7. **Enums**: Type-safe constants
8. **Layer Separation**: Clean architecture

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/group` | GET | List all groups |
| `/api/group` | POST | Create new group |
| `/api/group/{id}/members` | GET | Get group members |
| `/api/expense` | GET | Get group expenses |
| `/api/expense` | POST | Add new expense |
| `/api/payment` | POST | Record payment |
| `/api/settlement/summary` | GET | Get settlement summary |
| `/api/settlement/simplify` | GET | Get simplified debts |

## Sample Data

The application initializes with sample data:
- 5 Users: Alice, Bob, Charlie, Diana, Eve
- 2 Groups: "Roommates", "Trip to Goa"
- Multiple expenses and transactions

## Author

Academic Project for DBMS & Java Subjects
