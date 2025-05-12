package es.udc.fic.csi.baserest.controller;

import es.udc.fic.csi.baserest.dto.EmployeeDto;
import es.udc.fic.csi.baserest.repository.EmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    private void initBaseUrl() {
        baseUrl = "http://localhost:" + port + "/employee";
    }

    @BeforeEach
    @AfterEach
    private void resetEmployees() {
        employeeRepository.deleteAll();
    }


    @Test
    public void createAndUpdateEmployeeTest() {
        //Create supervisor
        var supervisor = new EmployeeDto("thiago", "avenida croquetas", 2, 7000.0, 98765432, "Sales", null);
        var createResponseSupervisor = restTemplate.postForEntity(baseUrl + "/new", supervisor, Long.class);
        assertThat(createResponseSupervisor.getStatusCode()).matches(HttpStatus::is2xxSuccessful);
        var idSupervisor = createResponseSupervisor.getBody();
        var findResponseSupervisor = restTemplate.getForObject(baseUrl + "/" + idSupervisor, EmployeeDto.class);
        assertThat(findResponseSupervisor).isEqualTo(supervisor);

        //Create employee
        var employee = new EmployeeDto("adrian", "avenida ejemplo", 20, 4000000.0, 987654321, "Sales", idSupervisor);
        var createResponse = restTemplate.postForEntity(baseUrl + "/new", employee, Long.class);
        assertThat(createResponse.getStatusCode()).matches(HttpStatus::is2xxSuccessful);

        var id = createResponse.getBody();
        var findResponse = restTemplate.getForObject(baseUrl + "/" + id, EmployeeDto.class);
        assertThat(findResponse).isEqualTo(employee);

        //Update employee
        var employeeUpdate = new EmployeeDto("adrian", "calle ejemplo", 33, 40000.0, 987654321, "I+D", idSupervisor);
        restTemplate.put(baseUrl + "/update/" + id, employeeUpdate);
        var findResponseUpdate = restTemplate.getForEntity(baseUrl + "/" + id, EmployeeDto.class);
        assertThat(findResponseUpdate.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(findResponseUpdate.getBody(), employeeUpdate);
    }


    @Test
    public void updateNotFoundEmployeeTest() {
        var id = 10L;

        //Update employee
        var employeeUpdate = new EmployeeDto("adrian", "calle ejemplo", 33, 40000.0, 987654321, "I+D", null);
        restTemplate.put(baseUrl + "/update/" + id, employeeUpdate);
        var findResponseUpdate = restTemplate.getForEntity(baseUrl + "/" + id, EmployeeDto.class);
        assertThat(findResponseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertNull(findResponseUpdate.getBody());
    }

    @Test
    public void getEmployeeByIdTest() {
        // Create employee
        var employee = new EmployeeDto("adrian", "avenida ejemplo", 20, 4000000.0, 987654321, "Sales", null);
        var createResponse = restTemplate.postForEntity(baseUrl + "/new", employee, Long.class);
        assertThat(createResponse.getStatusCode()).matches(HttpStatus::is2xxSuccessful);
        var id = createResponse.getBody();

        // Get employee
        var findResponse = restTemplate.getForEntity(baseUrl + "/" + id, EmployeeDto.class);
        assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(findResponse.getBody(), employee);
    }

    @Test
    void getEmployeeNotFoundTest() {
        var nonExistingId = 1000L;
        var response = restTemplate.getForEntity(baseUrl + "/" + nonExistingId, EmployeeDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void getDepartmentEmployeesTest() {

        // Create supervisors
        var supervisorSales = new EmployeeDto("John", "Avenida Ejemplo 7", 40, 90000.0, 111223344, "Sales", null);
        var supervisorIT = new EmployeeDto("Eva", "Calle Ejemplo 8", 38, 85000.0, 445566778, "IT", null);

        restTemplate.postForEntity(baseUrl + "/new", supervisorSales, Long.class);
        restTemplate.postForEntity(baseUrl + "/new", supervisorIT, Long.class);

        // Create employees
        var employee1 = new EmployeeDto("Adrian", "Avenida Ejemplo 1", 25, 50000.0, 123456789, "Sales", 1L);
        var employee2 = new EmployeeDto("Carlos", "Avenida Ejemplo 2", 30, 60000.0, 987654321, "Sales", 2L);
        var employee3 = new EmployeeDto("Laura", "Calle Ejemplo 3", 28, 45000.0, 123987654, "IT", 3L);
        var employee4 = new EmployeeDto("Maria", "Calle Ejemplo 4", 35, 75000.0, 456789123, "HR", null);
        var employee5 = new EmployeeDto("Juan", "Calle Ejemplo 5", 22, 40000.0, 789654123, "Sales", 5L);
        var employee6 = new EmployeeDto("Ana", "Avenida Ejemplo 6", 29, 55000.0, 321654987, "IT", 6L);

        restTemplate.postForEntity(baseUrl + "/new", employee1, Long.class);
        restTemplate.postForEntity(baseUrl + "/new", employee2, Long.class);
        restTemplate.postForEntity(baseUrl + "/new", employee3, Long.class);
        restTemplate.postForEntity(baseUrl + "/new", employee4, Long.class);
        restTemplate.postForEntity(baseUrl + "/new", employee5, Long.class);
        restTemplate.postForEntity(baseUrl + "/new", employee6, Long.class);


        // Department with 2 employees + 1 supervisor: IT
        var responseIT = restTemplate.exchange(baseUrl + "?department=" + "IT", HttpMethod.GET, null, new ParameterizedTypeReference<List<EmployeeDto>>() {});
        assertThat(responseIT.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<EmployeeDto> employeesIT = responseIT.getBody();
        assertThat(employeesIT).hasSize(3);
        assertThat(employeesIT).containsExactly(supervisorIT, employee3, employee6);

        // Department with 3 employees + 1 supervisor: Sales
        var responseSales = restTemplate.exchange(baseUrl + "?department=" + "Sales", HttpMethod.GET, null, new ParameterizedTypeReference<List<EmployeeDto>>() {});
        assertThat(responseSales.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<EmployeeDto> employeesSales = responseSales.getBody();
        assertThat(employeesSales).hasSize(4);
        assertThat(employeesSales).containsExactly(supervisorSales, employee1, employee2, employee5);

        // Department with 1 employee: HR
        var responseHR = restTemplate.exchange(baseUrl + "?department=" + "HR", HttpMethod.GET, null, new ParameterizedTypeReference<List<EmployeeDto>>() {});
        assertThat(responseHR.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<EmployeeDto> employeesHR = responseHR.getBody();
        assertThat(employeesHR).hasSize(1);
        assertThat(employeesHR).containsExactly(employee4);

        // Department not found
        var responseNotFound = restTemplate.exchange(baseUrl + "?department=" + "NotFound", HttpMethod.GET, null, new ParameterizedTypeReference<List<EmployeeDto>>() {});
        assertThat(responseNotFound.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseNotFound.getBody()).isNull();

        // No department provided - /employee?department=
        var responseNoDepartmentProvided  = restTemplate.exchange(baseUrl + "?department=", HttpMethod.GET, null, new ParameterizedTypeReference<List<EmployeeDto>>() {});
        assertThat(responseNoDepartmentProvided.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<EmployeeDto> employeesNotProvided = responseNoDepartmentProvided.getBody();
        assertThat(employeesNotProvided).isNotNull();
        assertThat(employeesNotProvided).hasSize(8);
        assertThat(employeesNotProvided).containsExactly(supervisorSales, supervisorIT, employee1, employee2, employee3, employee4, employee5, employee6);

        // No department provided - /employee
        var responseEmpty = restTemplate.exchange(baseUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<EmployeeDto>>() {});
        assertThat(responseEmpty.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<EmployeeDto> employeesEmpty = responseEmpty.getBody();
        assertThat(employeesEmpty).isNotNull();
        assertThat(employeesEmpty).hasSize(8);
        assertThat(employeesEmpty).containsExactly(supervisorSales, supervisorIT, employee1, employee2, employee3, employee4, employee5, employee6);
    }




}

