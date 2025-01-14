package at.htlleonding.pepper.entity;

import jakarta.persistence.*;

@Entity
@Table(name="pe_game_type")
public class GameType {
    @Id
    @GeneratedValue
    @Column(name = "gt_id")
    private Long id;

    @Column(name = "gt_name")
    private String name;
}
