package at.htlleonding.pepper.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.json.bind.annotation.JsonbDateFormat;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PersonDto(
        Long id,
        String firstName,
        String lastName,
        @JsonbDateFormat("yyyy-MM-dd")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate dob,
        String roomNo,
        Boolean isWorker,
        String gender
) {}
