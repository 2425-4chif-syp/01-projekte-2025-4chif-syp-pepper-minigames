package htl.leonding.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class TagAlongStory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Lob
    private byte[] storyIcon;
    @OneToMany(mappedBy = "tagAlongStory", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Step> steps;
    private int isEnabled;

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

    public byte[] getStoryIcon() {
        return storyIcon;
    }

    public void setStoryIcon(byte[] storyIcon) {
        this.storyIcon = storyIcon;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public int isEnabled() {
        return isEnabled;
    }

    public void setEnabled(int enabled) {
        isEnabled = enabled;
    }
    //endregion
}
