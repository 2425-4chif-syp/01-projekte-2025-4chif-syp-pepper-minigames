package at.htlleonding.pepper.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@IdClass(GameScoreId.class)
@Table(name = "pe_game_score")
public class GameScore {

    @Id
    @ManyToOne
    @JoinColumn(name = "gs_g_id")
    private Game game;

    @Id
    @ManyToOne
    @JoinColumn(name = "gs_p_id")
    private Person person;

    @Column(name = "gs_date_time")
    private LocalDateTime dateTime;

    @Column(name = "gs_score", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private int score;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
