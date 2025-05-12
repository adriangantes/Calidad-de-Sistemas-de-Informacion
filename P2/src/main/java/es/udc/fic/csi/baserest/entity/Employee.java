package es.udc.fic.csi.baserest.entity;

import java.util.Objects;
import javax.persistence.*;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Double salary;

    @Column(nullable = false, unique = true)
    private Integer nss;

    @Column(nullable = false)
    private String department;

    @ManyToOne(optional = true)
    @JoinColumn(name = "idSupervisor", referencedColumnName = "id")
    private Employee supervisor;

    public Employee() {}

    public Employee(String name, String address, Integer age, Double salary, Integer nss, String department, Employee supervisor) {
        this.name = name;
        this.address = address;
        this.age = age;
        this.salary = salary;
        this.nss = nss;
        this.department = department;
        this.supervisor = supervisor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Integer getNss() {
        return nss;
    }

    public void setNss(Integer nss) {
        this.nss = nss;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Employee getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Employee supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", age=" + age +
                ", salary=" + salary +
                ", nss=" + nss +
                ", department='" + department + '\'' +
                ", supervisor ='" + (supervisor != null ? supervisor.getId() : "null") + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, age, salary, nss, department, supervisor);
    }

}
