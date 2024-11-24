package htl.leonding.Service;

import htl.leonding.DTOs.StepDTO;
import htl.leonding.DTOs.TagAlongStoryDTO;
import htl.leonding.Model.Step;
import htl.leonding.Model.TagAlongStory;

import java.util.Base64;
import java.util.Objects;

public class Converter {
    public static TagAlongStory ConvertToTagAlongStory(TagAlongStoryDTO tagAlongStoryDTO) {
        int isEnabled = 0;
        TagAlongStory tagAlongStory = new TagAlongStory();
        tagAlongStory.setName(tagAlongStoryDTO.getName());
        tagAlongStory.setStoryIcon(Base64.getDecoder().decode(tagAlongStoryDTO.getStoryIcon()));
        if(Objects.equals(tagAlongStoryDTO.isEnabled(), "true"))
            isEnabled = 1;
        tagAlongStory.setEnabled(isEnabled);
        return tagAlongStory;
    }

    public static Step ConvertToStep(StepDTO stepDTO) {
        Step step = new Step();
        step.setDuration(stepDTO.getDuration());
        step.setImage(Base64.getDecoder().decode(stepDTO.getImage()));
        step.setIndex(Integer.parseInt(stepDTO.getIndex()));
        step.setText(stepDTO.getText());
        step.setMoveNameAndDuration(stepDTO.getMoveNameAndDuration());
        step.setTagAlongStory(stepDTO.getTagAlongStory());
        return step;
    }
}
