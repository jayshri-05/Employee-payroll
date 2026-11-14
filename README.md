# Employee Payroll & HR Management – Team B23

## Technologies
- Frontend: HTML, CSS, JavaScript
- Backend: Java Spring Boot
- Database: Oracle
- Connectivity: JDBC Template

## Modules
1. Departments
2. Employees
3. Attendance
4. Leave Management
5. Salary Management
6. Payroll

## Requirements
- JDK 17+
- Maven 3.9+
- Oracle Database (FREEPDB1)
- Oracle SQL Developer

## Database Setup
1. Open SQL Developer and connect as DBA.
2. Create the `hr_app` user and grant privileges.
3. Connect as `hr_app`.
4. Execute these scripts in order:
   - `database/01_schema.sql`
   - `database/02_seed.sql`
   - `database/03_demo_queries.sql`

## Run the Application

Open the project folder in VS Code and run these commands in PowerShell:

```powershell
$env:DB_URL="jdbc:oracle:thin:@localhost:1521/FREEPDB1"
$env:DB_USER="hr_app"
$env:DB_PASSWORD="YourPassword"
mvn spring-boot:run
```

Open the browser:

`http://localhost:8080`

Check for **Oracle JDBC connected**.

## Testing
- Add, edit and delete employee records.
- Verify changes in Oracle using:

```sql
SELECT * FROM employee;
```

- Generate payroll for Employee ID 101, Month 10, Year 2026.
- Expected net salary: ₹39,000.

## Phase 2 Screenshots
- Oracle tables and SQL queries.
- Application dashboard and six modules.
- Employee insertion and database verification.
- Payroll result and validation errors.
- JDBC source code.

## Note
This is an educational demo. Authentication is not implemented. Use fictional data and run locally only.
