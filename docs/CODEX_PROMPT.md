# Codex prompt — copy everything below into Codex with the project folder open

You are the implementation engineer for our BACSE202 VIT Team B23 Employee Payroll and HR Management DBMS project. I have attached the team's Phase 1 PPT and faculty guidelines. The project folder already contains a functional-design starter: Oracle SQL schema/seed/demo scripts, Java Spring Boot JDBC API, and a responsive HTML/CSS/JS website.

**First inspect the EXISTING files. Do not replace the 6-table relational schema with unrelated tables or hardcoded data. Do not silently switch the application to Supabase REST or Firebase.** Oracle Database + JDBC is intentional because the faculty requires proof of ODBC/JDBC and SQL*Plus/SQL Developer demonstrations.

PHASE 1 SCHEMA (logical entities): Department(department_id,department_name,location); Employee(employee_id,employee_name,date_of_birth,gender,phone,email,address,join_date,designation,department_id); Attendance(attendance_id,employee_id,attendance_date,check_in_time,check_out_time,attendance_status); Leave(leave_id,employee_id,leave_type,from_date,to_date,reason,leave_status); Salary(salary_id,employee_id,basic_salary,allowance,bonus,deduction,effective_from); Payroll(payroll_id,employee_id,salary_id,payroll_month,payroll_year,gross_salary,total_deduction,net_salary,payment_date). Physical Oracle table EMPLOYEE_LEAVE is intentional and mapped to logical Leave.

Faculty Phase 2: complete DDL with constraints, DML realistic seeds, working front end, validation/business logic, initial live JDBC retrieval + DML. Phase 3: all CRUD, constraint testing, documentation and demo.

DO THIS IN ORDER:
1. Read database/01_schema.sql, database/02_seed.sql, database/03_demo_queries.sql, pom.xml, Api.java, index.html and README.md. Write a concise gap checklist tied to faculty Phase 2 / Phase 3. Preserve the exact columns and relationship structure.
2. Resolve actual compile/runtime errors; verify Maven dependencies; run `mvn test` and `mvn package`. If Oracle is not available locally, say so explicitly, and do not fake database test results. Do not claim the project works against Oracle without a real connection.
3. Make sure SQL DDL succeeds on Oracle FREEPDB1/SQL Developer with primary keys, foreign keys, UNIQUE, CHECK, NOT NULL. Confirm each seed INSERT and table row counts match the README; correct any mistakes. Never delete real existing schema/data without checking with the user.
4. Confirm Spring Boot JDBC uses environment variables and Java parameterized SQL, not Oracle credentials hardcoded in React/HTML. Include a clear demo GET employee, POST employee, SQL SELECT proof and UPDATE/DELETE demonstration.
5. Strengthen UI into a polished minimal professional HR website: left sidebar, live dashboard, departments, employee directory, attendance, leave approvals, salary structures, monthly payroll, useful search/filters, date formatting, consistent validation errors and loading indicators. All live data must come from Oracle API. No fake JavaScript arrays standing in for database records.
6. Repair input validation, add server-side validation for required fields, enum values, employee-department relationship, unique employee email, unique attendance/date, leave date range, non-negative salary components, unique month/year payroll. Keep payroll server-calculated and immutable except carefully designed administrative correction workflow.
7. Add basic authentication and authorization for admin, HR, payroll staff and employee read-only scope before public deployment; don't create fabricated login security. If security is unfinished, keep the app strictly local and disclose the limitation.
8. Add JUnit tests, SQL negative test cases using SAVEPOINT/ROLLBACK and a test results matrix. Add `docs/PHASE2_PPT_CONTENT.md` with 10 slides: intro, architecture, DDL, constraints, DML, UI, modules, validation, JDBC retrieval/DML demo, conclusion. Add `docs/PHASE3_REPORT.md` covering normalization, SQL, tests, results and limitations.
9. Verify all CRUD actions produce live changes in Oracle. Add a precise Windows installation and start guide for Oracle DB Free, SQL Developer/SQL*Plus, Java, Maven and browser, with troubleshooting for ORA-01017, ORA-12514, ORA-00942, and port 8080 conflicts.
10. At completion give the EXACT commands to run, what you actually tested, unresolved blockers, and a live demonstration checklist. Never mark tasks completed without verifying.

UI DESIGN: refined, businesslike and readable; light-gray canvas, dark-navy sidebar, white cards, gentle teal accents, elegant tables and forms. Avoid distracting gradients and cartoon icons. Use responsive layout.

Critical: A browser connected only to Supabase SDK DOES NOT satisfy the faculty ODBC/JDBC requirement; we must explicitly demonstrate Java JDBC against Oracle. Do not confuse the two connectivity methods.
