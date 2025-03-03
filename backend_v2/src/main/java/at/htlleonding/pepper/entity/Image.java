package at.htlleonding.pepper.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pe_image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "i_p_id")
    private Person person;

    @Lob
    @Column(name = "i_image")
    private byte[] image;

    @Column(name = "i_url")
    private String url;

    @Column(name = "i_description")
    private String description;

    public Image() {
    }

    public Image(Long id, Person person, byte[] image, String url, String description) {
        this.id = id;
        this.person = person;
        this.image = image;
        this.url = url;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
