package com.pmorar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;


@RunWith(JUnit4.class)
public class SudokuTestCase {

    private static final String[] SAMPLE_BOARD_STRINGS = {
            "1,3,5,2,9,7,8,6,4\n" +
                    "9,8,2,4,1,6,7,5,3\n" +
                    "7,6,4,3,8,5,1,9,2\n" +
                    "2,1,8,7,3,9,6,4,5\n" +
                    "5,9,7,8,6,4,2,3,1\n" +
                    "6,4,3,1,5,2,9,7,8\n" +
                    "4,2,6,5,7,1,3,8,9\n" +
                    "3,5,9,6,2,8,4,0,7\n" +
                    "8,7,1,9,4,3,5,2,6",
            "0,3,5,2,9,0,8,6,4\n" +
                    "0,8,2,4,1,0,7,0,3\n" +
                    "7,6,4,3,8,0,0,9,0\n" +
                    "2,1,8,7,3,9,0,4,0\n" +
                    "0,0,0,8,0,4,2,3,0\n" +
                    "0,4,3,0,5,2,9,7,0\n" +
                    "4,0,6,5,7,1,0,0,9\n" +
                    "3,5,9,0,2,8,4,1,7\n" +
                    "8,0,0,9,0,0,5,2,6",
            "0,0,0,7,8,6,0,0,0\n" +
                    "0,0,7,0,0,0,4,0,0\n" +
                    "1,0,8,0,0,0,7,0,9\n" +
                    "3,0,0,1,0,2,0,0,8\n" +
                    "0,2,0,0,7,0,0,9,0\n" +
                    "5,0,0,8,0,3,0,0,7\n" +
                    "7,0,3,0,0,0,9,0,2\n" +
                    "0,0,9,0,0,0,3,0,0\n" +
                    "0,0,0,3,6,9,0,0,0",
    };

    private static final int[][][] SAMPLE_BOARDS = {
            {{1, 3, 5, 2, 9, 7, 8, 6, 4},
                    {9, 8, 2, 4, 1, 6, 7, 5, 3},
                    {7, 6, 4, 3, 8, 5, 1, 9, 2},
                    {2, 1, 8, 7, 3, 9, 6, 4, 5},
                    {5, 9, 7, 8, 6, 4, 2, 3, 1},
                    {6, 4, 3, 1, 5, 2, 9, 7, 8},
                    {4, 2, 6, 5, 7, 1, 3, 8, 9},
                    {3, 5, 9, 6, 2, 8, 4, 0, 7},
                    {8, 7, 1, 9, 4, 3, 5, 2, 6}},
            {{0, 3, 5, 2, 9, 0, 8, 6, 4},
                    {0, 8, 2, 4, 1, 0, 7, 0, 3},
                    {7, 6, 4, 3, 8, 0, 0, 9, 0},
                    {2, 1, 8, 7, 3, 9, 0, 4, 0},
                    {0, 0, 0, 8, 0, 4, 2, 3, 0},
                    {0, 4, 3, 0, 5, 2, 9, 7, 0},
                    {4, 0, 6, 5, 7, 1, 0, 0, 9},
                    {3, 5, 9, 0, 2, 8, 4, 1, 7},
                    {8, 0, 0, 9, 0, 0, 5, 2, 6}},
            {{0, 0, 0, 7, 8, 6, 0, 0, 0},
                    {0, 0, 7, 0, 0, 0, 4, 0, 0},
                    {1, 0, 8, 0, 0, 0, 7, 0, 9},
                    {3, 0, 0, 1, 0, 2, 0, 0, 8},
                    {0, 2, 0, 0, 7, 0, 0, 9, 0},
                    {5, 0, 0, 8, 0, 3, 0, 0, 7},
                    {7, 0, 3, 0, 0, 0, 9, 0, 2},
                    {0, 0, 9, 0, 0, 0, 3, 0, 0},
                    {0, 0, 0, 3, 6, 9, 0, 0, 0}},
            {{3, 1, 0, 0, 7, 0, 0, 0, 0},
                    {9, 0, 0, 0, 0, 4, 0, 0, 0},
                    {0, 0, 0, 0, 8, 0, 6, 0, 0},
                    {0, 0, 0, 3, 0, 0, 0, 2, 0},
                    {7, 0, 6, 0, 0, 0, 1, 0, 4},
                    {0, 2, 0, 0, 0, 5, 0, 0, 0},
                    {0, 0, 9, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 2, 0, 0, 0, 0, 7},
                    {0, 0, 0, 0, 9, 0, 0, 5, 3}},
    };

    private static final int[][][] SAMPLE_SOLUTIONS = {
            {{1, 3, 5, 2, 9, 7, 8, 6, 4},
                    {9, 8, 2, 4, 1, 6, 7, 5, 3},
                    {7, 6, 4, 3, 8, 5, 1, 9, 2},
                    {2, 1, 8, 7, 3, 9, 6, 4, 5},
                    {5, 9, 7, 8, 6, 4, 2, 3, 1},
                    {6, 4, 3, 1, 5, 2, 9, 7, 8},
                    {4, 2, 6, 5, 7, 1, 3, 8, 9},
                    {3, 5, 9, 6, 2, 8, 4, 1, 7},
                    {8, 7, 1, 9, 4, 3, 5, 2, 6}},
            {{1, 3, 5, 2, 9, 7, 8, 6, 4},
                    {9, 8, 2, 4, 1, 6, 7, 5, 3},
                    {7, 6, 4, 3, 8, 5, 1, 9, 2},
                    {2, 1, 8, 7, 3, 9, 6, 4, 5},
                    {5, 9, 7, 8, 6, 4, 2, 3, 1},
                    {6, 4, 3, 1, 5, 2, 9, 7, 8},
                    {4, 2, 6, 5, 7, 1, 3, 8, 9},
                    {3, 5, 9, 6, 2, 8, 4, 1, 7},
                    {8, 7, 1, 9, 4, 3, 5, 2, 6}},
            {{9, 4, 5, 7, 8, 6, 2, 3, 1},
                    {2, 3, 7, 9, 5, 1, 4, 8, 6},
                    {1, 6, 8, 2, 3, 4, 7, 5, 9},
                    {3, 7, 6, 1, 9, 2, 5, 4, 8},
                    {8, 2, 4, 6, 7, 5, 1, 9, 3},
                    {5, 9, 1, 8, 4, 3, 6, 2, 7},
                    {7, 5, 3, 4, 1, 8, 9, 6, 2},
                    {6, 8, 9, 5, 2, 7, 3, 1, 4},
                    {4, 1, 2, 3, 6, 9, 8, 7, 5}},
            {{3, 1, 8, 6, 7, 2, 5, 4, 9},
                    {9, 6, 7, 1, 5, 4, 3, 8, 2},
                    {5, 4, 2, 9, 8, 3, 6, 7, 1},
                    {8, 9, 4, 3, 6, 1, 7, 2, 5},
                    {7, 5, 6, 8, 2, 9, 1, 3, 4},
                    {1, 2, 3, 7, 4, 5, 8, 9, 6},
                    {2, 3, 9, 5, 1, 7, 4, 6, 8},
                    {4, 8, 5, 2, 3, 6, 9, 1, 7},
                    {6, 7, 1, 4, 9, 8, 2, 5, 3}},
    };

    private final static int SAMPLE_STRINGS_NUM = SAMPLE_BOARD_STRINGS.length;
    private final static int SAMPLE_BOARDS_NUM = SAMPLE_BOARDS.length;

    private static Sudoku getSampleBoardString(final int i) throws IOException {
        return Sudoku.fromString(SAMPLE_BOARD_STRINGS[i]);
    }

    private static Sudoku getSampleBoard(final int i) throws IOException {
        return new Sudoku(SAMPLE_BOARDS[i]);
    }

    private static Sudoku getSampleSolution(final int i) throws IOException {
        return new Sudoku(SAMPLE_SOLUTIONS[i]);
    }

    @Test
    public void testLoadFromStringAndToString() {
        for (int i = 0; i < SAMPLE_STRINGS_NUM; i++) {
            try {
                final Sudoku sudoku = getSampleBoardString(i);
                Assert.assertEquals(SAMPLE_BOARD_STRINGS[i], sudoku.toString());
            } catch (IOException e) {
                Assert.assertTrue(e.toString(), false);
            }
        }
    }

    @Test
    public void testReadFromFile() {
        for (int i = 0; i < SAMPLE_STRINGS_NUM; i++) {
            try {
                final Sudoku sudoku = Sudoku.fromFile("sudoku_sample.txt");
                Assert.assertEquals(SAMPLE_BOARD_STRINGS[1], sudoku.toString());
            } catch (IOException e) {
                Assert.assertTrue(e.toString(), false);
            }
        }
    }

    @Test
    public void testArrayConstructorConsistence() {
        for (int i = 0; i < SAMPLE_STRINGS_NUM; i++) {
            try {
                final Sudoku sudoku = getSampleBoard(i);
                Assert.assertEquals(SAMPLE_BOARD_STRINGS[i], sudoku.toString());
            } catch (IOException e) {
                Assert.assertTrue(e.toString(), false);
            }
        }
    }

    @Test
    public void testGet() {
        final int[][] array = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                array[i][j] = (i + j) % 9;
            }
        }
        final Sudoku sudoku = new Sudoku(array);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Assert.assertEquals(array[i][j], sudoku.get(i, j));
            }
        }
    }

    @Test
    public void testArrayConstructorIndependence() {
        final int[][] array = new int[9][9];
        final Sudoku sudoku = new Sudoku(array);
        array[0][0] = 1;
        Assert.assertEquals(0, sudoku.get(0, 0));
    }

    @Test
    public void testNotEqualNull() throws IOException {
        for (int i = 0; i < SAMPLE_STRINGS_NUM; i++) {
            final Sudoku s = getSampleBoard(i);
            Assert.assertNotEquals(s, null);
        }
    }

    @Test
    public void testNotEqualObject() throws IOException {
        for (int i = 0; i < SAMPLE_STRINGS_NUM; i++) {
            final Sudoku s = getSampleBoard(i);
            Assert.assertNotEquals(s, new Object());
        }
    }

    @Test
    public void testNotEqual() throws IOException {
        final Sudoku s = getSampleBoard(0);
        final Sudoku s1 = getSampleBoard(1);
        Assert.assertNotEquals(s, s1);
    }

    @Test
    public void testEqual() throws IOException {
        for (int i = 0; i < SAMPLE_STRINGS_NUM; i++) {
            final Sudoku s = getSampleBoard(i);
            final Sudoku s1 = getSampleBoard(i);
            Assert.assertEquals(s, s1);
        }
    }

    @Test
    public void testCopy() throws IOException {
        for (int i = 0; i < SAMPLE_STRINGS_NUM; i++) {
            final Sudoku s = getSampleBoard(i);
            final Sudoku s1 = new Sudoku(s);
            Assert.assertEquals(s, s1);
        }
    }

    @Test
    public void testSolve() throws IOException {
        for (int i = 0; i < SAMPLE_BOARDS_NUM; i++) {
            final Sudoku s = getSampleBoard(i);
            final long start = System.nanoTime();
            final Sudoku result = Sudoku.solve(s);
            System.out.println((System.nanoTime() - start) / 1000000);
            Assert.assertEquals(getSampleSolution(i), result);
        }
    }
//    1
//    10
//            220
//            2192


}
