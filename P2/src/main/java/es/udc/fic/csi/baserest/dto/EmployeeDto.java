package es.udc.fic.csi.baserest.dto;

public record EmployeeDto( String name, String address, Integer age, Double salary, Integer nss, String department, Long idSupervisor) {
}
