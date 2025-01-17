package htl.leonding.DTOs;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TagAlongStoryDTO {
    private Long id;
    private String name;
    private String storyIcon;
    @JsonProperty("isEnabled")
    private String isEnabled;

    //region Getters_Setters
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

    public String getStoryIcon() {
        return storyIcon;
    }

    public void setStoryIcon(String storyIcon) {
        this.storyIcon = storyIcon;
    }

    public String isEnabled() {
        return isEnabled;
    }

    public void setEnabled(String enabled) {
        isEnabled = enabled;
    }
    //endregion

    @Override
    public String toString() {
        boolean storyIconExsist = false;
        if (this.storyIcon != null) {
            storyIconExsist = true;
        }
        return "===TagAlongStoryDTO===\n" +
                "ID: " + id + "\n"+
                "NAME:'" + name + '\n' +
                "STORY_ICON: " + storyIconExsist + '\n' +
                "IS_ENABLED: " + isEnabled;
    }
}
