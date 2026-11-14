# Phase 2 mapping to faculty rubric (10 marks)

| Criterion | Evidence to show | File/location |
|---|---|---|
| DDL (2 marks) | 6 tables, PK, FK, UNIQUE, CHECK, NOT NULL | database/01_schema.sql |
| DML (2 marks) | 4 departments, 5 employees, 8 attendance, 3 leave, 5 salary, 5 payroll | database/02_seed.sql |
| Front end (2 marks) | Dashboard, 6 module pages, live search and forms | src/main/resources/static/index.html |
| Business logic (2 marks) | Validations, salary checks, monthly payroll computation | Api.java and Oracle constraints |
| Initial JDBC (2 marks) | React-free browser -> Spring REST -> JdbcTemplate -> Oracle SELECT/INSERT | Api.java + SQL Developer live verification |

**Not yet validated in this environment:** Maven compile, a running Oracle instance, and browser-to-Oracle end-to-end operations. Capture actual evidence only after local run.
