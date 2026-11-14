-- Execute individually in SQL Developer, or as a script in SQL*Plus.
SELECT table_name FROM user_tables WHERE table_name IN ('DEPARTMENT','EMPLOYEE','ATTENDANCE','EMPLOYEE_LEAVE','SALARY','PAYROLL') ORDER BY table_name;
SELECT constraint_name,table_name,constraint_type,status FROM user_constraints WHERE table_name IN ('DEPARTMENT','EMPLOYEE','ATTENDANCE','EMPLOYEE_LEAVE','SALARY','PAYROLL') ORDER BY table_name;
SELECT e.employee_id,e.employee_name,e.designation,d.department_name FROM employee e JOIN department d ON e.department_id=d.department_id ORDER BY e.employee_id;
SELECT e.employee_name,a.attendance_date,a.attendance_status FROM attendance a JOIN employee e ON e.employee_id=a.employee_id ORDER BY a.attendance_date,a.employee_id;
SELECT e.employee_name,p.payroll_year,p.payroll_month,p.gross_salary,p.total_deduction,p.net_salary FROM payroll p JOIN employee e ON e.employee_id=p.employee_id ORDER BY p.payroll_id;
SELECT 'DEPARTMENT' entity,COUNT(*) total FROM department UNION ALL SELECT 'EMPLOYEE',COUNT(*) FROM employee UNION ALL SELECT 'ATTENDANCE',COUNT(*) FROM attendance UNION ALL SELECT 'LEAVE',COUNT(*) FROM employee_leave UNION ALL SELECT 'SALARY',COUNT(*) FROM salary UNION ALL SELECT 'PAYROLL',COUNT(*) FROM payroll;
-- Safe transaction demo (rolled back):
SAVEPOINT demo_start;
INSERT INTO department(department_name,location) VALUES ('Temporary Demo','Vellore');
SELECT * FROM department WHERE department_name='Temporary Demo';
ROLLBACK TO demo_start;
SELECT * FROM department WHERE department_name='Temporary Demo';
