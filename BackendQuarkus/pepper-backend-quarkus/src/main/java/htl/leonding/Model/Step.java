package htl.leonding.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
@Entity
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int duration;
    private String moveNameAndDuration;
    private String text;
    @Lob
    private byte[] image;
    private int stepIndex;
    @ManyToOne
    @JsonBackReference
    private TagAlongStory tagAlongStory;

    //region Getters_Setters
    public TagAlongStory getTagAlongStory() {
        return tagAlongStory;
    }

    public void setTagAlongStory(TagAlongStory tagAlongStory) {
        this.tagAlongStory = tagAlongStory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getMoveNameAndDuration() {
        return moveNameAndDuration;
    }

    public void setMoveNameAndDuration(String moveNameAndDuration) {
        this.moveNameAndDuration = moveNameAndDuration;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getIndex() {
        return stepIndex;
    }

    public void setIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }
    //endregion
}
