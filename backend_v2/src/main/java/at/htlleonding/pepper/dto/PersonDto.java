package at.htlleonding.pepper.dto;

import java.time.LocalDate;

public class PersonDto{
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String roomNo;
    private Boolean isWorker;

    public PersonDto() {
    }

    public PersonDto(Long id, String firstName, String lastName, LocalDate dob, String roomNo, Boolean isWorker) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.roomNo = roomNo;
        this.isWorker = isWorker;
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

    public Boolean getIsWorker() {
        return isWorker;
    }

    public void setIsWorker(Boolean isWorker) {
        this.isWorker = isWorker;
    }
}
