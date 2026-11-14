package com.team23.hr;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api")
public class Api {
 private final JdbcTemplate jdbc;
 private final NamedParameterJdbcTemplate named;
 public Api(JdbcTemplate jdbc, NamedParameterJdbcTemplate named){this.jdbc=jdbc;this.named=named;}
 record Spec(String table,String pk,List<String> columns,List<String> dates){}
 private static final Map<String,Spec> SPECS=Map.of(
 "departments",new Spec("department","department_id",List.of("department_name","location"),List.of()),
 "employees",new Spec("employee","employee_id",List.of("employee_name","date_of_birth","gender","phone","email","address","join_date","designation","department_id"),List.of("date_of_birth","join_date")),
 "attendance",new Spec("attendance","attendance_id",List.of("employee_id","attendance_date","check_in_time","check_out_time","attendance_status"),List.of("attendance_date")),
 "leaves",new Spec("employee_leave","leave_id",List.of("employee_id","leave_type","from_date","to_date","reason","leave_status"),List.of("from_date","to_date")),
 "salaries",new Spec("salary","salary_id",List.of("employee_id","basic_salary","allowance","bonus","deduction","effective_from"),List.of("effective_from")),
 "payroll",new Spec("payroll","payroll_id",List.of("employee_id","salary_id","payroll_month","payroll_year","gross_salary","total_deduction","net_salary","payment_date"),List.of("payment_date"))
 );
 private Spec spec(String module){ Spec s=SPECS.get(module);if(s==null)throw new IllegalArgumentException("Unknown module");return s; }
 private Map<String,Object> clean(Spec s,Map<String,Object> input){
  Map<String,Object> result=new LinkedHashMap<>();
  for(String key:s.columns()) if(input.containsKey(key)){
   Object val=input.get(key);
   if(val instanceof String str && str.isBlank())val=null;
   if(val!=null && s.dates().contains(key))val=Date.valueOf(LocalDate.parse(val.toString().substring(0,10)));
   result.put(key,val);
  }
  return result;
 }
 @GetMapping("/health") public Map<String,Object> health(){return Map.of("database","ORACLE JDBC","connected",jdbc.queryForObject("SELECT COUNT(*) FROM department",Integer.class)>=0);}
 @GetMapping("/{module}") public List<Map<String,Object>> all(@PathVariable String module){Spec s=spec(module);return jdbc.queryForList("SELECT * FROM "+s.table()+" ORDER BY "+s.pk());}
 @GetMapping("/{module}/{id}") public Map<String,Object> one(@PathVariable String module,@PathVariable long id){Spec s=spec(module);List<Map<String,Object>> out=jdbc.queryForList("SELECT * FROM "+s.table()+" WHERE "+s.pk()+"=?",id);if(out.isEmpty())throw new NoSuchElementException("Record not found");return out.get(0);}
 @PostMapping("/{module}") public Map<String,Object> create(@PathVariable String module,@RequestBody Map<String,Object> input){
  if(module.equals("payroll"))throw new IllegalArgumentException("Use /api/payroll/generate for server-side calculation");
  Spec s=spec(module);Map<String,Object> payload=clean(s,input);if(payload.isEmpty())throw new IllegalArgumentException("No valid fields supplied");
  String fields=String.join(",",payload.keySet());String vals=String.join(",",payload.keySet().stream().map(k->":"+k).toList());
  named.update("INSERT INTO "+s.table()+" ("+fields+") VALUES ("+vals+")",payload);
  return Map.of("message","Created","module",module);
 }
 @PutMapping("/{module}/{id}") public Map<String,Object> update(@PathVariable String module,@PathVariable long id,@RequestBody Map<String,Object> input){
  if(module.equals("payroll"))throw new IllegalArgumentException("Generated payroll records cannot be edited via generic CRUD");
  Spec s=spec(module);Map<String,Object> payload=clean(s,input);if(payload.isEmpty())throw new IllegalArgumentException("No valid fields supplied");
  String set=String.join(",",payload.keySet().stream().map(k->k+" = :"+k).toList());payload.put("id",id);
  int count=named.update("UPDATE "+s.table()+" SET "+set+" WHERE "+s.pk()+"=:id",payload);
  if(count==0)throw new NoSuchElementException("Record not found");return Map.of("message","Updated");
 }
 @DeleteMapping("/{module}/{id}") public Map<String,Object> delete(@PathVariable String module,@PathVariable long id){
  Spec s=spec(module);int count=jdbc.update("DELETE FROM "+s.table()+" WHERE "+s.pk()+"=?",id);
  if(count==0)throw new NoSuchElementException("Record not found");return Map.of("message","Deleted");
 }
 @GetMapping("/dashboard/summary") public Map<String,Object> dashboard(){
  Map<String,Object> m=new LinkedHashMap<>();
  for(var e:SPECS.entrySet())m.put(e.getKey(),jdbc.queryForObject("SELECT COUNT(*) FROM "+e.getValue().table(),Integer.class));
  m.put("pendingLeaves",jdbc.queryForObject("SELECT COUNT(*) FROM employee_leave WHERE leave_status='Pending'",Integer.class));
  m.put("totalNetPayroll",jdbc.queryForObject("SELECT NVL(SUM(net_salary),0) FROM payroll",BigDecimal.class));return m;
 }
 @PostMapping("/payroll/generate") @Transactional
 public Map<String,Object> generate(@RequestBody Map<String,Object> request){
  int employeeId=Integer.parseInt(request.get("employee_id").toString());
  int month=Integer.parseInt(request.get("payroll_month").toString());
  int year=Integer.parseInt(request.get("payroll_year").toString());
  if(month<1||month>12||year<2000||year>2100)throw new IllegalArgumentException("Invalid payroll month/year");
  Date end=Date.valueOf(LocalDate.of(year,month,1).plusMonths(1).minusDays(1));
  List<Map<String,Object>> salary=jdbc.queryForList("SELECT * FROM salary WHERE employee_id=? AND effective_from<=? ORDER BY effective_from DESC, salary_id DESC FETCH FIRST 1 ROW ONLY",employeeId,end);
  if(salary.isEmpty())throw new IllegalArgumentException("No salary effective for selected payroll month");
  Map<String,Object> s=salary.get(0);
  BigDecimal gross=new BigDecimal(s.get("BASIC_SALARY").toString()).add(new BigDecimal(s.get("ALLOWANCE").toString())).add(new BigDecimal(s.get("BONUS").toString()));
  BigDecimal deduction=new BigDecimal(s.get("DEDUCTION").toString());BigDecimal net=gross.subtract(deduction);
  if(net.signum()<0)throw new IllegalArgumentException("Net salary cannot be negative");
  jdbc.update("INSERT INTO payroll(employee_id,salary_id,payroll_month,payroll_year,gross_salary,total_deduction,net_salary) VALUES(?,?,?,?,?,?,?)", employeeId,s.get("SALARY_ID"),month,year,gross,deduction,net);
  return Map.of("message","Payroll generated","grossSalary",gross,"deduction",deduction,"netSalary",net);
 }
 @ExceptionHandler({IllegalArgumentException.class,NoSuchElementException.class})
 public ResponseEntity<Map<String,String>> invalid(RuntimeException ex){return ResponseEntity.status(ex instanceof NoSuchElementException?404:400).body(Map.of("error",ex.getMessage()));}
 @ExceptionHandler(DataAccessException.class)
 public ResponseEntity<Map<String,String>> dbError(DataAccessException ex){return ResponseEntity.status(409).body(Map.of("error","Database rejected operation. Check keys, required fields, unique constraints and references.","detail",Objects.toString(ex.getMostSpecificCause().getMessage(),"SQL error")));}
}
