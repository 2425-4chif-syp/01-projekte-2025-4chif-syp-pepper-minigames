package at.htlleonding.pepper.entity;

import jakarta.persistence.*;

@Entity
@Table(name="pe_game_type")
public class GameType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gt_id")
    private Long id;

    @Column(name = "gt_name")
    private String name;

    //region Constructors
    public GameType() {
    }

    public GameType(String name) {
        this.name = name;
    }
    //endregion

    //region getter and setter
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
    //endregion


    @Override
    public String toString() {
        return "GameType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
