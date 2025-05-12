package es.udc.fic.csi.baserest.controller;

import es.udc.fic.csi.baserest.conversors.EmployeeConversors;
import es.udc.fic.csi.baserest.dto.EmployeeDto;
import es.udc.fic.csi.baserest.entity.Employee;
import es.udc.fic.csi.baserest.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * This controller handles HTTP requests related to the employee entity.
 * The {@link RequestMapping} annotation indicates the base path for all
 * requests handled by this controller. The endpoints defined in this class
 * will append their paths to the base path.
 *
 * Base path: `/employee`
 *
 * Example endpoints:
 * - `/employee/{id}` to get an employee by ID
 * - `/employee/new` to create a new employee
 * - `/employee/update/{id}` to update an existing employee
 * - `/employee?department={department}` to get employees by department
 *
 * @author angelotefic
 */

@RestController
@Transactional
@RequestMapping("employee")
public class EmployeeRestController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeRestController.class);

    @PersistenceContext
    private EntityManager em;

    private EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeRestController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Get a employee by ID
     *
     * This endpoint retrieves a Employee by their ID.
     *
     * HTTP Method: GET
     * Path: `/employee/{id}`
     *
     * If the employee is found, it returns a 200 OK response with the employee data.
     * If the employee is not found, it returns a 404 Not Found response.
     *
     * @param id the ID of the employee to retrieve
     * @return a ResponseEntity containing the employee data or a 404 response
     */

    @GetMapping(value = "{id}")
    public ResponseEntity<EmployeeDto> get(@PathVariable Long id) {
        logger.info("Fetching Employee with id: {}", id);
        var employee = employeeRepository.findById(id);

        if(employee.isPresent()) {
            Employee found = employee.get();
            logger.info("Employee found with id {}: {}", id, found);
            EmployeeDto employeeDto = EmployeeConversors.toEmployeeDto(found);
            return ResponseEntity.ok(employeeDto);
        } else {
            logger.warn("GET failed: Employee not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new employee
     *
     * This endpoint creates a new employee in the database.
     *
     * HTTP Method: POST
     * Path: `/employee/new`
     *
     * The employee data is provided in the request body as a JSON object.
     *
     * Example request body:
     * {
     *   "name": "Alice",
     *   "address": "123 Example Avenue",
     *   "age": 35,
     *   "salary": 42000.50,
     *   "nss": 987654321,
     *   "department": "Sales",
     *   "idSupervisor": 2
     * }
     *
     * @param employeeDto the new employee data as a employeeDto
     * @return the ID of the newly created employee
     */

    @PostMapping(value = "new")
    public Long create(@RequestBody EmployeeDto employeeDto) {
        logger.info("Creating new employee: {}", employeeDto);
        var newEmployee = em.merge(EmployeeConversors.toEmployee(employeeDto, em));
        logger.info("New employee created: {}", newEmployee);
        return newEmployee.getId();
    }

    /**
     * Update a employee
     *
     * This endpoint updates a employee in the database.
     *
     * HTTP Method: PUT
     * Path: `/employee/update/{id}`
     *
     * The new employee data is provided in the request body as a JSON object.
     *
     * Example request body:
     * {
     *   "name": "Alice",
     *   "address": "123 Example Avenue",
     *   "age": 35,
     *   "salary": 42000.50,
     *   "nss": 987654321,
     *   "department": "Sales",
     *   "idSupervisor": 2
     * }
     *
     * @param id the ID of the employee to update
     * @param employeeDto the new employee data as a employeeDto
     * @return a ResponseEntity containing the new employee data or a 404 response
     */

    @PutMapping(value = "update/{id}")
    public ResponseEntity<EmployeeDto> update(@PathVariable Long id, @RequestBody EmployeeDto employeeDto) {
        logger.info("Updating employee with id: {}", id);
        var exist = employeeRepository.findById(id);
        if(exist.isPresent()) {
            Employee employee = EmployeeConversors.toEmployee(employeeDto, em);
            employee.setId(id);
            var employeeUpdated = em.merge(employee);
            logger.info("Employee updated: {}", employeeUpdated);
            return ResponseEntity.ok(EmployeeConversors.toEmployeeDto(employeeUpdated));
        } else {
            logger.warn("UPDATE failed: Employee not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all employees from a specific department
     *
     * This endpoint retrieves all employees belonging to the given department.
     * It accepts a department name as a query parameter.
     *
     * HTTP Method: GET
     * Path: /employee?department={department}
     *
     * @param department the department name (e.g. "Sales", "IT")
     * @return list of EmployeeDto objects in that department
     */

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getEmployeesByDepartment(@RequestParam(required = false) String department) {
        if (department == null || department.trim().isEmpty()) {
            logger.info("Fetching all employees");
            List<Employee> employees = employeeRepository.findAll();
            List<EmployeeDto> employeeDtos = EmployeeConversors.toEmployeeDtoList(employees);
            logger.warn("No department provided.");
            return ResponseEntity.ok(employeeDtos);
        } else {
            logger.info("Fetching employees from department: {}", department);
            List<Employee> employees = employeeRepository.findByDepartment(department);

            if (employees.isEmpty()) {
                logger.warn("GET BY DEPARTMENT failed: No employees found in department: {}", department);
                return ResponseEntity.notFound().build();
            } else {
                List<EmployeeDto> employeeDtos = EmployeeConversors.toEmployeeDtoList(employees);
                logger.info("Found {} employees in department: {}", employeeDtos.size(), department);
                return ResponseEntity.ok(employeeDtos);
            }
        }
    }

}
