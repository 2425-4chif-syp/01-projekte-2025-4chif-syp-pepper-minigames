package at.htlleonding.pepper.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pe_game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "g_id")
    private Long id;

    @Column(name = "g_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "g_story_icon")
    private Image storyIcon;

    @Lob
    @Column(name = "g_story_icon_binary", columnDefinition = "OID")
    private byte[] storyIconBinary;

    @Column(name = "g_story_icon_type")
    private String storyIconType;

    @Column(name = "g_is_enabled")
    private boolean isEnabled;

    @ManyToOne
    @JoinColumn(name = "g_gt_id")
    private GameType gameType;


    //region constructors
    public Game() {
    }

    public Game(String name, byte[] storyIconBinary, boolean isEnabled, GameType gameType) {
        this.name = name;
        this.storyIconBinary = storyIconBinary;
        this.isEnabled = isEnabled;
        this.gameType = gameType;
    }

    public Game(String name, String storyIconBase64, boolean isEnabled, GameType gameType) {
        this.name = name;
        setStoryIconBase64(storyIconBase64);
        this.isEnabled = isEnabled;
        this.gameType = gameType;
    }
    //endregion

    /**
     * Gibt das Story-Icon als Base64-String zurück
     *
     * @return Base64-String des Icons
     */
    public String getStoryIconBase64() {
        if (storyIconBinary == null) {
            return null;
        }
        String base64 = "data:"
                + getStoryIconType()
                + ";base64,"
                + java.util.Base64.getEncoder().encodeToString(storyIconBinary);

        //System.out.println(base64);

        return base64;
    }

    /**
     * Setzt das Story-Icon als Base64-String und aktualisiert storyIconType
     *
     * @param iconBase64 Base64-String des Icons
     */
    public void setStoryIconBase64(String iconBase64) {
        String base64Part;
        if (iconBase64.contains(";base64,")) {
            base64Part = iconBase64.substring(iconBase64.indexOf(";base64,")
                    + ";base64,".length());
        } else {
            base64Part = iconBase64; // Falls bereits ein reiner Base64-String
        }

        setStoryIconType(iconBase64.substring(iconBase64.indexOf("data:")
                + "data:".length(), iconBase64.indexOf(";base64,")));
        this.storyIconBinary = java.util.Base64.getDecoder().decode(base64Part);
    }




    //region getter and setter
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

    public Image getStoryIcon() {
        return storyIcon;
    }

    public void setStoryIcon(Image storyIcon) {
        this.storyIcon = storyIcon;
    }

    public byte[] getStoryIconBinary() {
        return storyIconBinary;
    }

    public void setStoryIconBinary(byte[] icon) {
        this.storyIconBinary = icon;
    }

    public String getStoryIconType() {
        return storyIconType;
    }

    public void setStoryIconType(String storyIconType) {
        this.storyIconType = storyIconType;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

//endregion
}
