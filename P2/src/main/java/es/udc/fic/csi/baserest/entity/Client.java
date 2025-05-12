package es.udc.fic.csi.baserest.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Client {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @ElementCollection
    @CollectionTable(name = "pay_methods", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "pay_method_id", nullable = false)
    private List<Long> payMethods;

    public Client() {}

    public Client(String name, String surname, String email, String phone, String address, List<Long> payMethods) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.payMethods = payMethods;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Long> getPayMethods() {
        return payMethods;
    }

    public void setPayMethods(List<Long> payMethods) {
        this.payMethods = payMethods;
    }

    @Override
    public String toString() {
        return "Client[id=" + id + ", name=" + name + ", surname=" + surname + ", email=" + email
                + ", phone=" + phone + ", address=" + address + ", payMethod=" + payMethods + "]";
    }
}
