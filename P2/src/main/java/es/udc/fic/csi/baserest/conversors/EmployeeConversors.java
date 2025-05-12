package es.udc.fic.csi.baserest.conversors;

import es.udc.fic.csi.baserest.dto.EmployeeDto;
import es.udc.fic.csi.baserest.entity.Employee;

import javax.persistence.EntityManager;
import java.util.List;

public class EmployeeConversors {
    private EmployeeConversors() {
    }

    public static EmployeeDto toEmployeeDto(Employee employee) {
        Long supervisorId = (employee.getSupervisor() != null) ? employee.getSupervisor().getId() : null;

        return new EmployeeDto(
                employee.getName(),
                employee.getAddress(),
                employee.getAge(),
                employee.getSalary(),
                employee.getNss(),
                employee.getDepartment(),
                supervisorId
        );
    }

    public static List<EmployeeDto> toEmployeeDtoList(List<Employee> Employees) {
        return Employees.stream().map(EmployeeConversors::toEmployeeDto).toList();
    }

    public static Employee toEmployee(EmployeeDto employeeDto, EntityManager em) {
        Employee supervisor = null;
        if (employeeDto.idSupervisor() != null) {
            supervisor = em.find(Employee.class, employeeDto.idSupervisor());
        }

        return new Employee(employeeDto.name(), employeeDto.address(), employeeDto.age(), employeeDto.salary(), employeeDto.nss(), employeeDto.department(), supervisor);
    }
}
