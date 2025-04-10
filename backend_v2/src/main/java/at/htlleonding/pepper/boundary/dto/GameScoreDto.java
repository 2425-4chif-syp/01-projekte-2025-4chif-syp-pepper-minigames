package at.htlleonding.pepper.boundary.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public record GameScoreDto (
        String comment,
        LocalDateTime dateTime,
        int elapsedTime,
        int score,
        Long person_id,
        Long game_id
) { }