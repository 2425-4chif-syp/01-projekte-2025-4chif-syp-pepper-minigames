package at.htlleonding.pepper.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "pe_person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id")
    private Long id;
    @Column(name = "p_first_name")
    private String firstName;
    @Column(name = "p_last_name")
    private String lastName;
    @Column(name = "p_dob")
    private LocalDate dob;
    @Column(name = "p_room_no")
    private String roomNo;
    @Column(name = "p_isWorker")
    private Boolean isWorker;
    @Column(name = "p_password")
    private String password;
    // true is male, false is female
    @Column(name = "p_gender")
    private boolean gender;

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public Boolean getWorker() {
        return isWorker;
    }

    public void setWorker(Boolean worker) {
        isWorker = worker;
    }

    public Person() {
    }

    public Person(String firstName, String lastName, LocalDate dob, String roomNo, Boolean isWorker,String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.roomNo = roomNo;
        this.isWorker = isWorker;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public Boolean getIsWorker() { return isWorker; }

    public void setIsWorker(Boolean isWorker) { this.isWorker = isWorker; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }


}
