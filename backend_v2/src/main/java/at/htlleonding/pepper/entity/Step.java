package at.htlleonding.pepper.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pe_step")
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "st_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "st_g_id")
    private Game game;

    @Column(name = "st_index")
    private int index;

    @ManyToOne
    @JoinColumn(name = "st_i_id")
    private Image image;

    @Lob
    @Column(name = "st_image_binary", columnDefinition = "OID")
    private byte[] imageBinary;

    @Column(name = "st_image_type")
    private String imageType;

    @ManyToOne
    @JoinColumn(name = "st_m_id")
    private Move move;

    @Column(name = "st_text")
    private String text;

    @Column(name = "st_duration_in_sec")
    private int durationInSeconds;

    public Step() {
    }

    //region constructors
    public Step(
            Long id,
            Game game,
            int index,
            Image image,
            byte[] imageBinary,
            Move move,
            String text,
            int durationInSeconds) {
        this.id = id;
        this.game = game;
        this.index = index;
        this.image = image;
        this.move = move;
        this.text = text;
        this.durationInSeconds = durationInSeconds;
    }

    public Step(
            Long id,
            Game game,
            int index,
            Image image,
            String imageBase64,
            Move move,
            String text,
            int durationInSeconds) {
        this.id = id;
        this.game = game;
        this.index = index;
        setImageBase64(imageBase64);
        this.move = move;
        this.text = text;
        this.durationInSeconds = durationInSeconds;
    }
    //endregion

    public void setImageBase64(String imageBase64) {
        String base64Part;
        if (imageBase64.contains(";base64,")) {
            base64Part = imageBase64.substring(imageBase64.indexOf(";base64,") + ";base64,".length());
        } else {
            base64Part = imageBase64; // Falls bereits ein reiner Base64-String
        }
        this.setImageType(imageBase64.substring(imageBase64.indexOf("data:")
                + "data:".length(), imageBase64.indexOf(";base64,")));

        this.setImageBinary(java.util.Base64.getDecoder().decode(base64Part));
    }

    public String getImageBase64() {
        if (this.getImageBinary() == null) {
            return null;
        }

        return "data:"
                + this.getImageType()
                + ";base64,"
                + java.util.Base64.getEncoder().encodeToString(this.getImageBinary());
    }

    //region getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public byte[] getImageBinary() {
        return imageBinary;
    }

    public void setImageBinary(byte[] imageBinary) {
        this.imageBinary = imageBinary;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }
    //endregion
}
