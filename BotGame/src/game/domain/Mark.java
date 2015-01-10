package game.domain;

public enum Mark {
    X_MARK ('X'),
    O_MARK ('O'),
    EMPTY (' ');

    private final char symbol;

    private Mark(char symbol) {
        this.symbol = symbol;
    }

    public String toString() {
        return String.valueOf(symbol);
    }
}
