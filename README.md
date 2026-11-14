# Employee Payroll & HR Management — Team B23

A starter implementation aligned with the uploaded Phase 1 design and BACSE202 Phase 2 rubric.

## Architecture
Browser (HTML/CSS/JS) -> Spring Boot REST API -> Spring JdbcTemplate -> Oracle Database (FREEPDB1).
All 6 logical Phase 1 entities are included. SQL physical table `EMPLOYEE_LEAVE` represents the logical `Leave` entity to avoid identifier clashes. Data is **not hardcoded in the browser**. No Supabase dependency.

## Prerequisites
- Oracle Database Free / XE (local) with SQL Developer or SQL*Plus; adjust service name if using `XEPDB1` rather than `FREEPDB1`.
- JDK 17+, Maven 3.9+, internet for Maven dependency download on first build.
- Important: starter is for a trusted local classroom demo only; **authentication and authorization are not implemented**. Do not expose it to the internet or use actual employee information.

## First: create Oracle account
Connect in SQL Developer to the `FREEPDB1` pluggable database with a privileged DBA account and execute:
```sql
CREATE USER hr_app IDENTIFIED BY "Choose_A_Strong_Password_123";
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO hr_app;
ALTER USER hr_app QUOTA UNLIMITED ON USERS;
```
Use your own strong secret. If your Oracle PDB has a different default tablespace, adapt quota accordingly. If your lab supplies a ready-to-use Oracle user/schema with CREATE TABLE privileges, use that instead.

Connect as `hr_app` to FREEPDB1. Run scripts in order (SQL Developer F5 / SQL*Plus `@`):
1. `database/01_schema.sql` — **six related tables, keys, foreign keys and CHECK/UNIQUE constraints**.
2. `database/02_seed.sql` — **4 departments, 5 employees, 8 attendance records, 3 leave requests, 5 salary records, 5 payroll records**.
3. `database/03_demo_queries.sql` — SELECT/JOIN/COUNT and transaction demonstration.

The schema is intended for a fresh Oracle schema. Re-running DDL or seed INSERTs without cleaning existing objects will yield table-exists or unique-key errors.

## Second: run Java application
From project root in PowerShell (example; use actual password):
```powershell
$env:DB_URL='jdbc:oracle:thin:@localhost:1521/FREEPDB1'
$env:DB_USER='hr_app'
$env:DB_PASSWORD='YOUR_ACTUAL_PASSWORD'
mvn spring-boot:run
```

Go to `http://localhost:8080`. Website is automatically served by Spring Boot; **do not open index.html by double-clicking**. On the top right it should say `Oracle JDBC connected`.

### Proof that front end and SQL database are truly connected
1. In SQL Developer: `SELECT * FROM employee ORDER BY employee_id;`
2. On website: Employees -> + Add record. Complete all required fields (including valid `department_id` and dates).
3. Rerun SQL Developer SELECT. The new row should appear.
4. In website, edit the designation, check SQL again; then delete that test row, and verify it disappears.
5. Show validation: duplicate email, invalid department ID, duplicate attendance, reversed leave dates.
6. On website, Payroll -> Generate payroll for employee 101, month 10, year 2026; expected ₹39,000 net; rerunning the same request must fail due to unique month/employee constraint.

## API routes
- GET `/api/health`, GET `/api/dashboard/summary`
- GET `/api/{departments|employees|attendance|leaves|salaries|payroll}`
- GET `/api/{module}/{id}`
- POST `/api/{module}` for all but payroll
- PUT `/api/{module}/{id}` for all but payroll
- DELETE `/api/{module}/{id}` (FK restrictions apply)
- POST `/api/payroll/generate` with `{"employee_id":101,"payroll_month":10,"payroll_year":2026}`

## Current limitations (to address before Phase 3)
- No authentication/role access control. The app must remain local demo only.
- No formal automated integration tests. Execute the testing matrix and add JUnit tests for Phase 3.
- Simplified pay calculation: gross = basic + allowance + bonus; deduction comes from salary structure; no tax/leave proration/payment workflows.
- The demo interface permits generic salary updates, but saved payroll snapshots remain historical.
- Sample employee and monetary records are fictional and only intended for educational demonstrations.

## What to screenshot for Phase 2 PPT
- Oracle tables (navigator), SQL Developer schema / DESC output, rows from SELECT, JOIN results
- Landing dashboard and each module table
- Add employee in browser and new row visible in Oracle SQL Developer
- Payroll result and validation error
- JDBC class/source and Oracle environment configuration (never show password)
