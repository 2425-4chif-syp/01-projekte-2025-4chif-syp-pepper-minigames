package at.htlleonding.pepper.entity.dto;

import at.htlleonding.pepper.entity.GameType;

public record GameDto(String name, String icon, GameType gameType, boolean isEnabled){

}

//public class GameDto {
//    private String name;
//    private String icon;
//    private boolean isEnabled;
//
//    public GameDto(String name, String icon, boolean isEnabled) {
//        this.name = name;
//        this.icon = icon;
//        this.isEnabled = isEnabled;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getIcon() {
//        return icon;
//    }
//
//    public boolean isEnabled() {
//        return isEnabled;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setIcon(String icon) {
//        this.icon = icon;
//    }
//
//    public void setEnabled(boolean enabled) {
//        isEnabled = enabled;
//    }
//}
