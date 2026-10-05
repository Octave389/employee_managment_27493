# Employee Management System — Requirements & Test Report (Iteration 1)

| | |
|---|---|
| **Organisation / case study** | Organisation X — employee management, payments, reporting |
| **Iteration under test** | Iteration 1: *Employee* class (4 fields, console input, validation) |
| **Language / tools** | Java 17 target (JDK 23 installed), Maven 3.9.9, JUnit 5.10.2, JaCoCo 0.8.13 |
| **Test date** | 2026-10-05 |
| **Tester** | ____________________ |
| **Source** | `src/main/java/employee/Employee.java` |
| **Tests** | `src/test/java/employee/EmployeeBlackBoxTest.java` (39), `EmployeeWhiteBoxTest.java` (34) |

> The `test-case-report-template` file was not in the project folder, so the tables below use the usual columns
> (ID, description, input, expected, actual, status). Copy them into the template, or tell me its layout and I will reshape this report to match.

**Run it**

```bash
mvn clean verify                                   # runs all 73 tests + coverage report
java -cp target/classes employee.Employee          # run the program interactively
# coverage report (white-box): target/site/jacoco/index.html
```

---

## 1. Functional requirements (from the case study)

The scenario describes software that **manages employees, processes their payments and produces reports**. Splitting that into testable requirements:

| ID | Module | Requirement | Iteration 1 |
|---|---|---|---|
| FR-01 | Employee mgmt | The system shall capture an employee's **name, age, email and salary** from the user. | ✅ Implemented |
| FR-02 | Employee mgmt | The system shall **validate** every field, reject bad values with a clear message and ask again. | ✅ Implemented |
| FR-03 | Employee mgmt | The system shall let the user view / search employee records. | ⏳ Planned |
| FR-04 | Employee mgmt | The system shall let the user update an employee's details. | ⏳ Planned |
| FR-05 | Employee mgmt | The system shall let the user remove / deactivate an employee. | ⏳ Planned |
| FR-06 | Payments | The system shall calculate an employee's pay for a period from their salary (gross → net). *Deduction rules are not in the scenario — needs clarification.* | ⏳ Planned |
| FR-07 | Payments | The system shall process a payment and record it (employee, date, amount, status). | ⏳ Planned |
| FR-08 | Payments | The system shall generate a payslip for a payment. | ⏳ Planned |
| FR-09 | Reporting | The system shall produce an employee list report. | ⏳ Planned |
| FR-10 | Reporting | The system shall produce a payroll / payments summary report for a chosen period. | ⏳ Planned |
| FR-11 | Reporting | The system shall export reports (e.g. CSV / PDF). | ⏳ Planned |

Statements in the scenario about *how* it was built (agile model, fault injection, test documentation, schedule and budget) are **process / project constraints**, not software functions, so they are not listed as FRs. The testing approach below is driven by them: automated tests (JUnit) keep re-testing cheap, which protects the schedule and budget.

### 1.1 Validation rules implemented for FR-01 / FR-02

The scenario gives no field rules, so these are **assumptions** (each one is a constant at the top of `Employee.java` and easy to change).

| Rule | Field | Rule text |
|---|---|---|
| VR-1 | Name | Required; 2–50 characters after trimming; letters, spaces, hyphens, apostrophes only; must start with a letter |
| VR-2 | Age | Whole number, 18–65 inclusive |
| VR-3 | Email | Required; form `name@domain.tld` (no spaces) |
| VR-4 | Salary | Number with at most 2 decimals; greater than 0 and at most 1,000,000 |

---

## 2. Black-box (functional) testing

**Technique.** The tests were designed from the rules above only, without looking at the code. I used **equivalence partitioning (EP)** — one test per valid and invalid class of input — and **boundary value analysis (BVA)** — min, min−1, max, max+1. Each test calls the public API with the text a user would type.

**Result: 39 run, 39 passed, 0 failed.**

### 2.1 Name (VR-1)

| ID | Technique | Input | Expected | Actual | Status |
|---|---|---|---|---|---|
| BB-01 | EP valid | `John Smith` | Accepted | Accepted → `John Smith` | Pass |
| BB-02 | BVA min | `Jo` (2 chars) | Accepted | Accepted → `Jo` | Pass |
| BB-03 | BVA min−1 | `J` (1 char) | Rejected | Rejected: "Name must be 2-50 characters long." | Pass |
| BB-04 | BVA max | 50 × `A` | Accepted | Accepted | Pass |
| BB-05 | BVA max+1 | 51 × `A` | Rejected | Rejected: "Name must be 2-50 characters long." | Pass |
| BB-06 | EP invalid | `` (empty) | Rejected | Rejected: "Name is required." | Pass |
| BB-07 | EP invalid | `   ` (spaces) | Rejected | Rejected: "Name is required." | Pass |
| BB-08 | EP invalid | `null` | Rejected | Rejected: "Name is required." | Pass |
| BB-09 | EP invalid | `John3` | Rejected | Rejected: "Name may only contain letters…" | Pass |
| BB-10 | EP valid | `O'Brien-Smith` | Accepted | Accepted → `O'Brien-Smith` | Pass |
| BB-11 | EP invalid | `-John` | Rejected | Rejected: "…must start with a letter." | Pass |

### 2.2 Age (VR-2)

| ID | Technique | Input | Expected | Actual | Status |
|---|---|---|---|---|---|
| BB-12 | EP valid | `30` | Accepted | Accepted → 30 | Pass |
| BB-13 | BVA min | `18` | Accepted | Accepted → 18 | Pass |
| BB-14 | BVA min−1 | `17` | Rejected | Rejected: "Age must be a whole number between 18 and 65." | Pass |
| BB-15 | BVA max | `65` | Accepted | Accepted → 65 | Pass |
| BB-16 | BVA max+1 | `66` | Rejected | Rejected: "Age must be a whole number between 18 and 65." | Pass |
| BB-17 | EP invalid | `abc` | Rejected | Rejected: same message | Pass |
| BB-18 | EP invalid | `` (empty) | Rejected | Rejected: same message | Pass |
| BB-19 | EP invalid | `25.5` | Rejected | Rejected: same message | Pass |
| BB-20 | EP invalid | `-5` | Rejected | Rejected: same message | Pass |

### 2.3 Email (VR-3)

| ID | Technique | Input | Expected | Actual | Status |
|---|---|---|---|---|---|
| BB-21 | EP valid | `john.smith@company.com` | Accepted | Accepted | Pass |
| BB-22 | EP invalid | `johnsmith.com` (no @) | Rejected | Rejected: "Email format is invalid (expected name@domain.com)." | Pass |
| BB-23 | EP invalid | `john@` (no domain) | Rejected | Rejected: "Email format is invalid…" | Pass |
| BB-24 | EP invalid | `@company.com` (no local part) | Rejected | Rejected: "Email format is invalid…" | Pass |
| BB-25 | EP invalid | `john@company` (no TLD) | Rejected | Rejected: "Email format is invalid…" | Pass |
| BB-26 | EP invalid | `` (empty) | Rejected | Rejected: "Email is required." | Pass |
| BB-27 | EP invalid | `john smith@company.com` | Rejected | Rejected: "Email format is invalid…" | Pass |

### 2.4 Salary (VR-4)

| ID | Technique | Input | Expected | Actual | Status |
|---|---|---|---|---|---|
| BB-28 | EP valid | `5000.50` | Accepted | Accepted → 5000.50 | Pass |
| BB-29 | BVA min+ | `0.01` | Accepted | Accepted → 0.01 | Pass |
| BB-30 | BVA min | `0` | Rejected | Rejected: "Salary must be greater than 0 and at most 1000000." | Pass |
| BB-31 | EP invalid | `-100` | Rejected | Rejected: "Salary must be a number with at most 2 decimal places." | Pass |
| BB-32 | BVA max | `1000000` | Accepted | Accepted → 1000000.0 | Pass |
| BB-33 | BVA max+1 | `1000000.01` | Rejected | Rejected: "Salary must be greater than 0 and at most 1000000." | Pass |
| BB-34 | EP invalid | `abc` | Rejected | Rejected: "Salary must be a number…" | Pass |
| BB-35 | EP invalid | `` (empty) | Rejected | Rejected: "Salary must be a number…" | Pass |
| BB-36 | EP invalid | `5000.555` (3 decimals) | Rejected | Rejected: "…at most 2 decimal places." | Pass |

### 2.5 Whole employee and console behaviour (FR-01, FR-02)

| ID | Description | Input | Expected | Actual | Status |
|---|---|---|---|---|---|
| BB-37 | All four fields valid | `John Smith`, 30, `john@company.com`, 5000.50 | Employee created with the same values | Employee created, getters return the same values | Pass |
| BB-38 | One field invalid | valid name/email/salary, age 17 | Employee not created | Rejected with an "Age…" message | Pass |
| BB-39 | Console re-asks until valid | name `J`→`John Smith`; age `17`→`30`; email `john-at-company`→`john@company.com`; salary `0`→`5000.50` | Each bad entry shows an error and is asked again; final employee is correct | Errors shown ("Invalid input…"), final employee correct | Pass |

**Sample real run** (`java -cp target/classes employee.Employee`, input piped in; with a keyboard each prompt starts on its own line):

```
=== Employee registration ===
Full name:   Invalid input - Name must be 2-50 characters long.
Full name: Age (18-65):   Invalid input - Age must be a whole number between 18 and 65.
Age (18-65):   Invalid input - Age must be a whole number between 18 and 65.
Age (18-65): Email:   Invalid input - Email format is invalid (expected name@domain.com).
Email: Monthly salary:   Invalid input - Salary must be greater than 0 and at most 1000000.
Monthly salary:
Employee captured successfully:
Employee{name='John Smith', age=30, email='john@company.com', salary=5000.50}
```

---

## 3. White-box (structural) testing

**Technique.** The tests were designed by reading the code: **statement coverage**, **branch (decision) coverage**, **condition coverage** inside every `a || b || c` decision, **basis-path testing** (one test per independent path = cyclomatic complexity V(G)) and **loop testing** (0, 1 and many iterations of the retry loop). Coverage was measured with JaCoCo.

**Result: 34 run, 34 passed, 0 failed.**

### 3.1 Cyclomatic complexity and independent paths

| Method | Decisions | V(G) = paths | Tests |
|---|---|---|---|
| `validateName` | `name==null \|\| isBlank`, `len<MIN \|\| len>MAX`, `!pattern` | 6 | WB-01 … WB-06 |
| `validateAge` | `age<MIN \|\| age>MAX` | 3 | WB-07 … WB-09 |
| `parseAge` | `raw==null \|\| !pattern` (then `validateAge`) | 3 | WB-10 … WB-13 |
| `validateEmail` | `null \|\| isBlank`, `!pattern` | 4 | WB-14 … WB-17 |
| `validateSalary` | `isNaN \|\| <=0 \|\| >MAX` | 4 | WB-18 … WB-22 |
| `parseSalary` | `raw==null \|\| !pattern` (then `validateSalary`) | 3 | WB-23 … WB-26 |
| constructor / `readFromUser` / `prompt` / `toString` / `main` | sequence, retry loop | — | WB-27 … WB-34 |

### 3.2 White-box test cases

| ID | Method | Condition / path targeted | Input | Expected | Actual | Status |
|---|---|---|---|---|---|---|
| WB-01 | validateName | `name == null` true | `null` | Rejected "Name is required" | As expected | Pass |
| WB-02 | validateName | `isBlank()` true | `"   "` | Rejected "Name is required" | As expected | Pass |
| WB-03 | validateName | `length < MIN` true | `"A"` | Rejected "2-50 characters" | As expected | Pass |
| WB-04 | validateName | `length > MAX` true | 51 × `A` | Rejected "2-50 characters" | As expected | Pass |
| WB-05 | validateName | pattern mismatch | `"John3"` | Rejected "only contain letters" | As expected | Pass |
| WB-06 | validateName | all false; `trim()` runs | `"  Ann  "` | Returns `Ann` | As expected | Pass |
| WB-07 | validateAge | `age < MIN` true | 10 | Rejected | As expected | Pass |
| WB-08 | validateAge | `age > MAX` true | 70 | Rejected | As expected | Pass |
| WB-09 | validateAge | both false | 30 | Returns 30 | As expected | Pass |
| WB-10 | parseAge | `raw == null` true | `null` | Rejected "whole number" | As expected | Pass |
| WB-11 | parseAge | regex fails | `"abc"` | Rejected "whole number" | As expected | Pass |
| WB-12 | parseAge | regex passes, range check fails | `"200"` | Rejected "between 18 and 65" | As expected | Pass |
| WB-13 | parseAge | valid, `trim()` runs | `" 42 "` | Returns 42 | As expected | Pass |
| WB-14 | validateEmail | `email == null` true | `null` | Rejected "Email is required" | As expected | Pass |
| WB-15 | validateEmail | `isBlank()` true | `"  "` | Rejected "Email is required" | As expected | Pass |
| WB-16 | validateEmail | pattern mismatch | `"no-at-sign"` | Rejected "format is invalid" | As expected | Pass |
| WB-17 | validateEmail | valid, `trim()` runs | `" a@b.com "` | Returns `a@b.com` | As expected | Pass |
| WB-18 | validateSalary | `isNaN` true | `NaN` | Rejected | As expected | Pass |
| WB-19 | validateSalary | `<= 0` true | 0.0 | Rejected | As expected | Pass |
| WB-20 | validateSalary | `> MAX` true | 1000000.01 | Rejected | As expected | Pass |
| WB-21 | validateSalary | `> MAX` via infinity | `+Infinity` | Rejected | As expected | Pass |
| WB-22 | validateSalary | all false | 5000.0 | Returns 5000.0 | As expected | Pass |
| WB-23 | parseSalary | `raw == null` true | `null` | Rejected "Salary must be a number" | As expected | Pass |
| WB-24 | parseSalary | regex fails on the text `NaN` | `"NaN"` | Rejected "Salary must be a number" | As expected | Pass |
| WB-25 | parseSalary | regex passes, range check fails | `"0.00"` | Rejected "greater than 0" | As expected | Pass |
| WB-26 | parseSalary | valid, `trim()` runs | `" 5000.5 "` | Returns 5000.5 | As expected | Pass |
| WB-27 | constructor | validators run in order | `("", 5, "bad", -1)` | Name error reported first | As expected | Pass |
| WB-28 | constructor | reaches the last validator | valid name/age/email, salary 0 | Salary error | As expected | Pass |
| WB-29 | prompt loop | 0 retries | all four answers valid | No "Invalid input" printed | 0 error lines | Pass |
| WB-30 | prompt loop | 1 retry (`catch` runs once) | `J`, then valid answers | 1 error line | 1 error line | Pass |
| WB-31 | prompt loop | many retries on one field | age `10`, `99`, `abc`, then `30` | 3 error lines | 3 error lines | Pass |
| WB-32 | prompt loop | input ends mid-way | only a name is supplied | `NoSuchElementException` | As expected | Pass |
| WB-33 | toString | format with 2-decimal salary | `John Smith, 30, john@company.com, 5000.5` | `Employee{name='John Smith', age=30, email='john@company.com', salary=5000.50}` | As expected | Pass |
| WB-34 | main | end-to-end through `System.in` | four valid answers | Prints "Employee captured successfully" and the employee | As expected | Pass |

### 3.3 Coverage achieved (JaCoCo)

| Criterion | Black-box suite only (39 tests) | Black-box + white-box (73 tests) |
|---|---|---|
| Statement / line | 52 / 61 = **85.2 %** | 61 / 61 = **100 %** |
| Branch | 30 / 34 = **88.2 %** | 34 / 34 = **100 %** |
| Instruction | 230 / 279 = 82.4 % | 279 / 279 = 100 % |
| Method | 14 / 16 | 16 / 16 |

What the black-box tests alone missed, and the white-box test that closes it:

| Missed by black-box | Why a spec-only tester skips it | Closed by |
|---|---|---|
| `raw == null` in `parseAge` | Nothing in the rules mentions `null` | WB-10 |
| `email == null` in `validateEmail` | Same | WB-14 |
| `raw == null` in `parseSalary` | Same | WB-23 |
| `Double.isNaN` in `validateSalary` | `NaN` passes both `<= 0` and `> MAX`, so a range-only check lets it through | WB-18 |
| `toString()` | Output format is not a validation rule | WB-33 |
| `main()` | Entry point | WB-34 |

---

## 4. Summary and observations

| | Run | Passed | Failed |
|---|---|---|---|
| Black-box | 39 | 39 | 0 |
| White-box | 34 | 34 | 0 |
| **Total** | **73** | **73** | **0** |

- **No defects were found** in this iteration. Statement and branch coverage are both 100 %.
- 100 % coverage shows every line and decision was *executed*; it does not prove the assertions would catch every wrong behaviour. The next step for that is the fault-injection / mutation idea from the scenario (for example, change `<` to `<=`, or remove the `isNaN` check, and confirm a test fails).
- Negative salaries (`-100`) get the "must be a number…" message rather than "must be greater than 0", because the input pattern rejects the minus sign before the range check runs. The input is still rejected; only the wording could be friendlier.
- The `null` guards in `parseAge` / `parseSalary` / `validateEmail` cannot be reached from the console (`Scanner` never returns `null`), only when the methods are called from other code. They are kept as defensive checks.
- Salary is stored as `double`. That is fine for this iteration, but payment calculation (FR-06 / FR-07) should switch to `BigDecimal` to avoid rounding errors.

## 5. Next iteration

FR-03 to FR-05 (store, find, update, delete employees) → FR-06 to FR-08 (payments) → FR-09 to FR-11 (reports). Each iteration gets its own black-box and white-box suite, using the same structure as this report.
