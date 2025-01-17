package at.htlleonding.pepper.entity.dto;

import at.htlleonding.pepper.entity.Game;
import at.htlleonding.pepper.entity.Image;
import at.htlleonding.pepper.entity.Move;
import at.htlleonding.pepper.entity.Step;

public record StepDto(Game game, int index, Image image, Move move, String text) {
}

//public class StepDto {
//    private Game game;
//    private int index;
//    private Image image;
//    private Move move;
//    private String text;
//
//    public Game getGame() {
//        return game;
//    }
//
//    public int getIndex() {
//        return index;
//    }
//
//    public Image getImage() {
//        return image;
//    }
//
//    public Move getMove() {
//        return move;
//    }
//
//    public String getText() {
//        return text;
//    }
//
//    public void setGame(Game game) {
//        this.game = game;
//    }
//
//    public void setIndex(int index) {
//        this.index = index;
//    }
//
//    public void setImage(Image image) {
//        this.image = image;
//    }
//
//    public void setMove(Move move) {
//        this.move = move;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//}
