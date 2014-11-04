package com.pmorar;

import java.io.*;
import java.util.*;

/**
 * Represents a Sudoku game and allows to solve it.
 */
public class Sudoku {

    /**
     * Returns a Sudoku puzzle read from the file with the provided filename.
     */
    public static Sudoku fromFile(final String filename) throws IOException {
        final BufferedReader in = new BufferedReader(new FileReader(filename));
        try {
            return new Sudoku(in);
        } finally {
            in.close();
        }
    }

    /**
     * Returns a Sudoku puzzle read from the reader.
     */
    public static Sudoku fromReader(final Reader reader) throws IOException {
        final BufferedReader in = new BufferedReader(reader);
        try {
            return new Sudoku(in);
        } finally {
            in.close();
        }
    }

    /**
     * Returns a Sudoku puzzle read from the string.
     */
    public static Sudoku fromString(final String description) throws IOException {
        //a quick implementation, for performance it is better to use a separate parsing
        return fromReader(new StringReader(description));
    }

    /**
     * Returns the solution or null if could not find it.
     *
     * @param sudoku puzzle to solve.
     */
    public static Sudoku solve(final Sudoku sudoku) {
        //a recursive solution
        if (sudoku.isSolved())
            return sudoku;
        final Position pos = sudoku.getNextPosition();
        if (pos == null)
            return null;
        for (final Integer action : pos.actions) {
            final Sudoku result = solve(new Sudoku(sudoku, pos.i, pos.j, action));
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static final int N = 9;
    private static final String DELIMITER = ",";

    private final int[][] board;// = new int[N][];

    private final Options[] rowOptions = new Options[N];
    private final Options[] columnOptions = new Options[N];
    private final Options[] cellOptions = new Options[N];

    /**
     * Initialized using a copy of the provided board.
     *
     * @throws IllegalArgumentException if dimensions of the board are incorrect
     */
    public Sudoku(final int[][] board) {
        if (board.length != N) {
            throw new IllegalArgumentException("Illegal board dimension " + board.length);
        }
        this.board = new int[N][N];
        for (int i = 0; i < N; i++) {
            if (board[i].length != N) {
                throw new IllegalArgumentException("Illegal row dimension " + board[i].length);
            }
            //defensive copying
            System.arraycopy(board[i], 0, this.board[i], 0, N);
        }
        calculateOptions();
    }

    /**
     * A copy of other.
     */
    public Sudoku(final Sudoku other) {
        board = new int[N][N];
        for (int i = 0; i < N; i++) {
            System.arraycopy(other.board[i], 0, board[i], 0, N);
            rowOptions[i] = new Options(other.rowOptions[i]);
            columnOptions[i] = new Options(other.columnOptions[i]);
            cellOptions[i] = new Options(other.cellOptions[i]);
        }
    }

    /**
     * A shallow copy of other, which makes a game step making necessary deep copies.
     */
    private Sudoku(final Sudoku other, final int i, final int j, final int value) {
        this.board = new int[N][];
        for (int k = 0; k < N; k++) {
            if (k != i) {
                board[k] = other.board[k];
            } else {
                board[k] = new int[N];
                System.arraycopy(other.board[k], 0, board[k], 0, N);
            }
        }
        System.arraycopy(other.rowOptions, 0, rowOptions, 0, N);
        System.arraycopy(other.columnOptions, 0, columnOptions, 0, N);
        System.arraycopy(other.cellOptions, 0, cellOptions, 0, N);

        //step
        board[i][j] = value;
        rowOptions[i] = rowOptions[i].copyAndRemove(value);
        columnOptions[j] = columnOptions[j].copyAndRemove(value);
        final int k = getCellIndex(i, j);
        cellOptions[k] = cellOptions[k].copyAndRemove(value);
    }

    private Sudoku(final BufferedReader in) throws IOException {
        board = new int[N][N];
        for (int i = 0; i < N; i++) {
            final String line = in.readLine();
            if (line == null)
                throw new IOException("The input file has not enough lines!");
            final String[] tokens = line.split(DELIMITER);
            if (tokens.length != N)
                throw new IOException("The input file line is incorrect!");
            for (int j = 0; j < N; j++) {
                board[i][j] = Integer.parseInt(tokens[j]);
            }
        }
        calculateOptions();
    }

    //------------------------------------------------------------------------------------------------------------------

    public boolean isSolved() {
        for (int i = 0; i < N; i++) {
            if (!(rowOptions[i].isLocked() && columnOptions[i].isLocked() && cellOptions[i].isLocked()))
                return false;
        }
        return true;
    }

    /**
     * Returns true if we can make a step in the specified entry.
     */
    public boolean isAvailable(final int i, final int j) {
        return board[i][j] == 0;
    }

    public int get(final int i, final int j) {
        return board[i][j];
    }

    public void set(final int i, final int j, final int v) {
        board[i][j] = v;
        rowOptions[i].available.remove(v);
        columnOptions[j].available.remove(v);
        getCellOptions(i, j).available.remove(v);
    }


    public void writeToFile(final String filename) throws IOException {
        final PrintWriter out = new PrintWriter(filename);
        try {
            writeTo(out);
        } finally {
            out.close();
        }
    }

    public void writeTo(final PrintWriter out) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N - 1; j++) {
                out.print(board[i][j]);
                out.print(",");
            }
            out.print(board[i][N - 1]);
            if (i != N - 1)
                out.println();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N - 1; j++) {
                sb.append(board[i][j]);
                sb.append(',');
            }
            sb.append(board[i][N - 1]);
            if (i != N - 1)
                sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Sudoku)) return false;

        final Sudoku so = (Sudoku) o;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] != so.board[i][j])
                    return false;
            }
        }

        //TODO aux information equality

        return true;
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    //------------------------------------------------------------------------------------------------------------------


    private static class Options {
        static final Set<Integer> ALL_AVAILABLE = new HashSet<Integer>(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}));

        final Set<Integer> available;// = new HashSet<Integer>();

        Options() {
            available = new HashSet<Integer>(ALL_AVAILABLE);
        }

        Options(final Options other) {
            available = new HashSet<Integer>(other.available);
        }

        Options copyAndRemove(final Integer v) {
            final Options o = new Options(this);
            o.available.remove(v);
            return o;
        }

        boolean isLocked() {
            return available.size() == 0;
        }
    }

    private void calculateOptions() {
        for (int i = 0; i < N; i++) {
            rowOptions[i] = new Options();
            columnOptions[i] = new Options();
            cellOptions[i] = new Options();
        }
        for (int i = 0; i < N; i++) {
            final Options rowOption = rowOptions[i];
            for (int j = 0; j < N; j++) {
                final int e = board[i][j];
                rowOption.available.remove(e);
                columnOptions[j].available.remove(e);
                getCellOptions(i, j).available.remove(e);
            }
        }
    }

    private Options getCellOptions(final int i, final int j) {
        return cellOptions[getCellIndex(i, j)];
    }

    private int getCellIndex(final int i, final int j) {
        return (i / 3) * 3 + j / 3;
    }

    private static class Position {
        final int i;
        final int j;
        final Set<Integer> actions;

        private Position(final int i, final int j, final Set<Integer> actions) {
            this.i = i;
            this.j = j;
            this.actions = actions;
        }
    }

    private static final Comparator<Position> POSITION_COMPARATOR = new Comparator<Position>() {
        @Override
        public int compare(final Position position, final Position position2) {
            return position.actions.size() - position2.actions.size();
        }
    };

    private Position getNextPosition() {
        //get the entry with the lowest number of options available
        final Position[] positions = new Position[N * N];
        int n = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 0) {
                    final Set<Integer> actions = new HashSet<Integer>(rowOptions[i].available);
                    actions.retainAll(columnOptions[j].available);
                    actions.retainAll(getCellOptions(i, j).available);
                    if (actions.size() == 0)
                        return null;
                    positions[n++] = new Position(i, j, actions);
                }
            }
        }
        Arrays.sort(positions, 0, n, POSITION_COMPARATOR);

        return positions[0];
    }

    public static void main(final String[] args) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        final Sudoku sudoku = Sudoku.solve(Sudoku.fromReader(in));
        System.out.println(sudoku);
    }
}
