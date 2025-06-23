package cn.maxpixel.mods.wuziqi.board;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Board {
    public static final String PIECES_KEY = "Pieces";
    public static final int BOARD_SIZE = 15;

    private final Runnable setChanged;
    private final PieceType[][] pieces;

    public Board(Runnable setChanged) {
        this(setChanged, new PieceType[BOARD_SIZE][BOARD_SIZE]);
    }

    public Board(Runnable setChanged, PieceType[][] pieces) {
        this.setChanged = setChanged;
        this.pieces = Objects.requireNonNull(pieces);
    }

    private boolean checkPieceBounds(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    public boolean hasPiece(int x, int y) {
        return checkPieceBounds(x, y) && pieces[x][y] != null;
    }

    public boolean putPiece(int x, int y, @Nullable PieceType type) {
        if (hasPiece(x, y) || !checkPieceBounds(x, y)) return false;
        pieces[x][y] = type;
        setChanged.run();
        return true;
    }

    public @Nullable PieceType getPiece(int x, int y) {
        return checkPieceBounds(x, y) ? pieces[x][y] : null;
    }

    public boolean checkWin(int x, int y) {
        var type = getPiece(x, y);
        return type != null && (checkX(x, y, type) || checkY(x, y, type) || checkLeft(x, y, type) || checkRight(x, y, type));
    }

    private boolean checkX(int x, int y, PieceType type) {
        int count = 1;
        for (int i = x - 1; i > x - 5; i--) {
            if (getPiece(i, y) == type) count++;
            else break;
        }
        for (int i = x + 1; i < x + 5; i++) {
            if (getPiece(i, y) == type) count++;
            else break;
        }
        return count >= 5;
    }

    private boolean checkY(int x, int y, PieceType type) {
        int count = 1;
        for (int i = y - 1; i > y - 5; i--) {
            if (getPiece(x, i) == type) count++;
            else break;
        }
        for (int i = y + 1; i < y + 5; i++) {
            if (getPiece(x, i) == type) count++;
            else break;
        }
        return count >= 5;
    }

    private boolean checkLeft(int x, int y, PieceType type) {
        int count = 1;
        for (int i = -1; i > -5; i--) {
            if (getPiece(x + i, y - i) == type) count++;
            else break;
        }
        for (int i = 1; i < 5; i++) {
            if (getPiece(x + i, y - i) == type) count++;
            else break;
        }
        return count >= 5;
    }

    private boolean checkRight(int x, int y, PieceType type) {
        int count = 1;
        for (int i = -1; i > -5; i--) {
            if (getPiece(x + i, y + i) == type) count++;
            else break;
        }
        for (int i = 1; i < 5; i++) {
            if (getPiece(x + i, y + i) == type) count++;
            else break;
        }
        return count >= 5;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        byte[] pieceArr = new byte[BOARD_SIZE * BOARD_SIZE];
        int i = 0;
        for (PieceType[] piece : pieces) {
            for (PieceType p : piece) {
                pieceArr[i++] = p == null ? 0 : p.code;
            }
        }
        tag.putByteArray(PIECES_KEY, pieceArr);

        return tag;
    }

    public byte[][] getPieces() {
        byte[][] arr = new byte[BOARD_SIZE][BOARD_SIZE];
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                arr[x][y] = pieces[x][y] == null ? 0 : pieces[x][y].code;
            }
        }
        return arr;
    }

    public static Board load(Runnable setChanged, CompoundTag tag) {
        PieceType[][] pieces = new PieceType[BOARD_SIZE][BOARD_SIZE];
        if (tag.contains(PIECES_KEY, Tag.TAG_BYTE_ARRAY)) {
            byte[] arr = tag.getByteArray(PIECES_KEY);
            for (int i = 0; i < arr.length; i++) {
                var type = PieceType.getFromCode(arr[i]);
                pieces[i / BOARD_SIZE][i % BOARD_SIZE] = type;
            }
        }
        return new Board(setChanged, pieces);
    }
}