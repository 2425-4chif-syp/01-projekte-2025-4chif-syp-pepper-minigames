package htl.leonding.DTOs;
import com.fasterxml.jackson.annotation.JsonProperty;
import htl.leonding.Model.TagAlongStory;

public class StepDTO {
    private Long id;
    private int duration;
    private String moveNameAndDuration;
    private String text;
    private String image;
    @JsonProperty("stepIndex")
    private String stepIndex;
    private TagAlongStory tagAlongStory;

    //region Getters_Setters
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIndex() {
        return stepIndex;
    }

    public void setIndex(String stepIndex) {
        this.stepIndex = stepIndex;
    }

    public void setTagAlongStory(TagAlongStory tagAlongStory) {
        //setze tagalongstory_id zu id von tagalongstory
        this.tagAlongStory = tagAlongStory;
    }

    public TagAlongStory getTagAlongStory() {
        return tagAlongStory;
    }

    //endregion

    @Override
    public String toString() {
        return "StepDTO{" +
                "id=" + id +
                ", duration=" + duration +
                ", moveNameAndDuration='" + moveNameAndDuration + '\'' +
                ", text='" + text + '\'' +
                ", image='" + image + '\'' +
                ", stepIndex=" + stepIndex +
                '}';
    }


}
