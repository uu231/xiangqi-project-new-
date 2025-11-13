package edu.sustech.xiangqi.model;

public class Move {
    private final AbstractPiece movedPiece;
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;
    private final AbstractPiece eatPiece;

    public Move(AbstractPiece movedPiece, int fromRow, int fromCol, int toRow, int toCol, AbstractPiece eatPiece) {
        this.movedPiece = movedPiece;
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.eatPiece = eatPiece;
    }

    public AbstractPiece getMovedPiece() {
        return movedPiece;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public AbstractPiece getEatPiece() {
        return eatPiece;
    }
}