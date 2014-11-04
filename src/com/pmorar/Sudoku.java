package com.pmorar;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Sudoku {

    public static Sudoku fromFile(final String filename) throws IOException {
        final BufferedReader in = new BufferedReader(new FileReader(filename));
        try {
            return new Sudoku(in);
        } finally {
            in.close();
        }
    }

    public static Sudoku fromReader(final Reader reader) throws IOException {
        final BufferedReader in = new BufferedReader(reader);
        try {
            return new Sudoku(in);
        } finally {
            in.close();
        }
    }

    public static Sudoku fromString(final String description) throws IOException {
        //a quick implementation, for performance it is better to use a separate parsing
        return fromReader(new StringReader(description));
    }

    public static Sudoku solve(final Sudoku sudoku) {
        if (sudoku.isSolved())
            return sudoku;
        final Position pos = sudoku.getNextAvailablePosition();
        if (pos == null)
            return null;
        for (final Integer action: pos.actions) {
            final Sudoku step = new Sudoku(sudoku);
            step.set(pos.i, pos.j, action);
            final Sudoku result = solve(step);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static final int N = 9;
    private static final String DELIMITER = ",";

    private final int[][] board = new int[N][N];

    private final Options[] rowOptions = new Options[N];
    private final Options[] columnOptions = new Options[N];
    private final Options[] cellOptions = new Options[N];

    public Sudoku(final int[][] board) {
        if (board.length != N) {
            throw new IllegalArgumentException("Illegal board dimension " + board.length);
        }
        for (int i = 0; i < N; i++) {
            if (board[i].length != N) {
                throw new IllegalArgumentException("Illegal row dimension " + board[i].length);
            }
            //defensive copying
            System.arraycopy(board[i], 0, this.board[i], 0, N);
        }
        calculateOptions();
    }

    public Sudoku(final Sudoku other) {
        for (int i = 0; i < N; i++) {
            System.arraycopy(other.board[i], 0, board[i], 0, N);
            rowOptions[i] = new Options(other.rowOptions[i]);//TODO optimize
            columnOptions[i] = new Options(other.columnOptions[i]);
            cellOptions[i] = new Options(other.cellOptions[i]);
        }
    }

    private Sudoku(final BufferedReader in) throws IOException {
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

    public boolean isSolved() {
        for (int i = 0; i < N; i++) {
            if (!(rowOptions[i].isLocked() && columnOptions[i].isLocked() && cellOptions[i].isLocked()))
                return false;
        }
        return true;
    }

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

        return true;
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    //------------------------------------------------------------------------------------------------------------------

    private static enum OptionType {
        Row,
        Column,
        Cell
    }

    private static class Options {
        static final Set<Integer> ALL_AVAILABLE = new HashSet<Integer>(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}));

        final OptionType type;
        final Set<Integer> available;// = new HashSet<Integer>();

        Options(final OptionType type) {
            this.type = type;
            available = new HashSet<Integer>(ALL_AVAILABLE);
        }

        Options(final Options other) {
            type = other.type;
            available = new HashSet<Integer>(other.available);
        }

        boolean isLocked() {
            return available.size() == 0;
        }
    }

    private void calculateOptions() {
        for (int i = 0; i < N; i++) {
            rowOptions[i] = new Options(OptionType.Row);
            columnOptions[i] = new Options(OptionType.Column);
            cellOptions[i] = new Options(OptionType.Cell);
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
        return cellOptions[(i / 3) * 3 + j / 3];
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

    private Position getNextAvailablePosition() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 0) {
                    final Set<Integer> actions = new HashSet<Integer>(rowOptions[i].available);
                    actions.retainAll(columnOptions[j].available);
                    actions.retainAll(getCellOptions(i, j).available);
                    if (actions.size() == 0)
                        return null;
                    return new Position(i, j, actions);
                }
            }
        }
        throw new AssertionError();
    }
}
