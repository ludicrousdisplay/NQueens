package org.dot;


import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {


    @Test
    public void arrange8Queens() {
        int[] queens = Main.arrangeNQueens(8);

        assertTrue(checkRows(queens));
        assertTrue(checkDiagonals(queens));
        assertTrue(checkLines(queens));
    }

    @Test
    public void arrange17Queens() {
        int[] queens = Main.arrangeNQueens(17);

        assertTrue(checkRows(queens));
        assertTrue(checkDiagonals(queens));
        assertTrue(checkLines(queens));
    }

    @Test
    public void arrange29Queens() {
        int[] queens = Main.arrangeNQueens(29);

        assertTrue(checkRows(queens));
        assertTrue(checkDiagonals(queens));
        assertTrue(checkLines(queens));
    }

    @Test
    public void testFailures() {

        assertFalse(checkRows(new int[] {6, 8, 0, 2, 7, 5, 1, 9, 4, 10, -1}));
        assertFalse(checkRows(new int[] {6, 8, 0, 2, 7, 5, 1, 9, 4, 10, 11}));
        assertFalse(checkRows(new int[] {6, 8, 0, 2, 7, 5, 1, 9, 6, 10, 3}));

        assertFalse(checkDiagonals(new int[] {6, 0, 8, 2, 7, 5, 1, 9, 4, 10, 3}));
        assertFalse(checkDiagonals(new int[] {6, 8, 0, 2, 1, 7, 5, 9, 4, 10, 3}));
        assertFalse(checkDiagonals(new int[] {6, 8, 0, 7, 2, 5, 1, 9, 4, 10, 3}));

        assertFalse(checkLines(new int[] {6, 8, 10, 2, 7, 5, 1, 9, 4, 0, 3}));
        assertFalse(checkLines(new int[] {6, 8, 0, 2, 9, 5, 1, 7, 4, 10, 3}));

        // good positions
        assertTrue(checkRows(new int[] {6, 8, 0, 2, 7, 5, 1, 9, 4, 10, 3}));
        assertTrue(checkDiagonals(new int[] {6, 8, 0, 2, 7, 5, 1, 9, 4, 10, 3}));
        assertTrue(checkLines(new int[] {6, 8, 0, 2, 7, 5, 1, 9, 4, 10, 3}));

    }

    public boolean checkRows(int[] positions) {
        boolean ok = true;

        final int n = positions.length;

        // check for row duplicates and out of bounds rows
        boolean[] rows = new boolean[n];
        for (int i = 0; i < n && ok; i++) {

            if (positions[i] < 0 || positions[i] >= n || rows[positions[i]]) {
                ok = false;
            } else {
                rows[positions[i]] = true;
            }
        }

        return ok;
    }

    public boolean checkDiagonals(int[] positions) {
        boolean ok = true;

        final int n = positions.length;

        // check diagonals
        for (int i = 0; i < n && ok; i++) {
            final int iRow = positions[i];

            for (int j = i + 1; j < n && ok; j++) {
                final int jRow = positions[j];

                if ((j - i) == Math.abs(iRow - jRow)) {
                    // Q i and Q j are on same diagonal
                    ok = false;
                }
            }
        }

        return ok;
    }

    public boolean checkLines(int[] positions) {
        boolean ok = true;

        final int n = positions.length;

        // check for lines of 3 or more
        for (int i = 0; i < n; i++) {
            final int iRow = positions[i];

            for (int j = i + 1; j < n; j++) {
                int jRow = positions[j];

                int cStep = j - i;
                int rStep = jRow - iRow;

                // reduce lines to smaller segments so we don't skip over any points
                boolean dvp = true;
                while (dvp) {
                    dvp = false;
                    for (int p = 0; p < Main.primes.length; p++) {
                        if (cStep % Main.primes[p] == 0 && rStep % Main.primes[p] == 0) {
                            cStep /= Main.primes[p];
                            rStep /= Main.primes[p];
                            dvp = true;
                        }
                    }
                }

                int k = j;
                int kRow = jRow;

                // move right from k and look for row and column matches
                while ((k < n) && ok) {
                    k += cStep;
                    kRow += rStep;

                    if (k < n && positions[k] == kRow) {
                        ok = false;
                    }
                }

                k = j;
                kRow = jRow;
                // move left from k and look for row and column matches (other than 'i')
                while ((k > -1) && ok) {
                    k -= cStep;
                    kRow -= rStep;

                    if (k > -1 && positions[k] == kRow) {
                        if (k != i) {
                            ok = false;
                        }
                    }
                }
            }
        }
        return ok;
    }

}