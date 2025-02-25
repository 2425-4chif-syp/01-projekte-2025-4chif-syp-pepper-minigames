package at.htlleonding.pepper.entity.dto;

import at.htlleonding.pepper.entity.GameType;

public record GameDto(String name, String icon, GameType gameType, boolean isEnabled){
}