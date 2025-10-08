package at.htlleonding.pepper.dto;

import at.htlleonding.pepper.domain.GameType;

public record GameDto(String name, String icon, GameType gameType, boolean isEnabled){
}