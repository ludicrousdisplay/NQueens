package org.dot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static boolean VERBOSE = false;

    protected static final int[] primes = new int[]{31, 29, 23, 19, 17, 13, 11, 7, 5, 3, 2};

    public static void main(String[] args) {

        final int nR;

        int n = -1;
        if (args.length > 0) {
            n = Integer.parseInt(args[0]);
        }
        if (n > 7) {
            if( n > 64 ) {
                n = 64;
                System.out.println("limiting N to 64 for now, this may take about ten minutes...");
            }
            nR = n;
        } else {
            System.out.println("no solution possible for N less than 8 ");
            nR = 16;
        }
        System.out.println("Setting N to " + nR + ", positioning queens...\n");

        int[] positions = arrangeNQueens(nR);
        printBoard(positions);

    }

    public static int[] arrangeNQueens(final int n) {

        int[] positions = new int[n];
        List<int[]> badPaths = new ArrayList<int[]>();
        int[] columnIteration = new int[n];

        // create an array of column ids that defines the order in which columns are filled
        int mid = (n - 1) / 2;
        int ctr = 0;
        for (int i = 1; i < n; i += 2) {
            columnIteration[i - 1] = mid - ctr;
            ctr += 1;
            columnIteration[i] = mid + ctr;
        }

        long startTime = System.currentTimeMillis();

        int ci = 0;
        while (ci < n) {
            ci = placeQueenInColumn(ci, columnIteration, positions, badPaths);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println(n + " queens positioned in approximately " + (elapsedTime / 1000) + " seconds");

        return positions;
    }


    private static int placeQueenInColumn(final int ci, int[] colItr, int[] positions, List<int[]> badPaths) {
        final int retCI;

        final int nR = positions.length;
        final int cCol = colItr[ci];

        for (int c = ci; c < nR; c++) {
            positions[colItr[c]] = -1;
        }

        // the claimed array stores positions that are 'used' by positioned queens
        // and therefore not valid for later queen positions
        boolean[] claimed = new boolean[nR];
        Arrays.fill(claimed, false);

        for (int i = 0; i < ci; i++) {
            final int iCol = colItr[i];

            // claim row
            final int iRow = positions[iCol];
            claimed[iRow] = true;

            // claim diagonal overlaps with current column
            int cDelta = Math.abs(iCol - cCol);
            if ((iRow - cDelta) > -1) {
                claimed[iRow - cDelta] = true;
            }
            if ((iRow + cDelta) < nR) {
                claimed[iRow + cDelta] = true;
            }
        }

        // claim line overlaps with current column
        // for each pair of queens continue the line to the current column
        for (int i = 0; i < ci; i++) {

            for (int j = i + 1; j < ci; j++) {

                final int iCol;
                final int jCol;

                if (colItr[i] > colItr[j]) {
                    iCol = colItr[j];
                    jCol = colItr[i];
                } else {
                    iCol = colItr[i];
                    jCol = colItr[j];
                }
                final int iRow = positions[iCol];
                final int jRow = positions[jCol];

                int cStep = jCol - iCol;
                int rStep = jRow - iRow;

                // reduce lines to smaller segments so we don't skip over any points
                // such as when positioned queens are far apart
                boolean dvp = true;
                while (dvp) {
                    dvp = false;
                    for (int p = 0; p < primes.length; p++) {
                        if (cStep % primes[p] == 0 && rStep % primes[p] == 0) {
                            cStep /= primes[p];
                            rStep /= primes[p];
                            dvp = true;
                        }
                    }
                }
                int kCol = jCol;
                int kRow = jRow;

                if (kCol < cCol) {
                    while (kCol < cCol) {
                        kCol += cStep;
                        kRow += rStep;
                    }
                } else if (kCol > cCol) {
                    while (kCol > cCol) {
                        kCol -= cStep;
                        kRow -= rStep;
                    }
                }
                if (kCol == cCol && kRow > -1 && kRow < nR) {
                    claimed[kRow] = true;
                }
            }
        }

        // record path positions for this branch in case we need to exclude it in later iterations
        final int[] pathPositions = new int[ci];
        for (int i = 0; i < ci; i++) {
            int iCol = colItr[i];
            pathPositions[i] = positions[iCol];
        }

        // make sure we don't go down previously traversed bad paths so add those to the claimed array...
        for (int[] path : badPaths) {

            boolean samePath = true;

            for (int i = 0; i < path.length && i < ci && samePath; i++) {
                if (path[i] != pathPositions[i]) {
                    samePath = false;
                }
            }

            if (samePath && path.length >= (ci + 1)) {
                claimed[path[ci]] = true;
            }
        }

        if (VERBOSE) {
            // print out the positioned queens and the claimed array side by side
            printProgress(nR, positions, claimed);
        }

        boolean freeSpace = false;

        for (int i = 0; i < nR && !freeSpace; i++) {
            freeSpace = !claimed[i];
        }

        if (freeSpace) {

            int[] span = new int[]{-1, -1};
            int[] bestSpan = new int[]{1, -1};

            for (int i = 0; i < nR; i++) {
                if (!claimed[i]) {
                    if (span[0] < 0) {
                        span[0] = i;
                    }
                    span[1] = i;
                } else { // if claimed
                    if (span[1] > -1) {
                        if ((span[1] - span[0]) > (bestSpan[1] - bestSpan[0])) {
                            bestSpan[0] = span[0];
                            bestSpan[1] = span[1];
                        }
                    }
                    span[0] = span[1] = -1;
                }
            }
            if (span[1] > -1) {

                if ((span[1] - span[0]) > (bestSpan[1] - bestSpan[0])) {
                    bestSpan[0] = span[0];
                    bestSpan[1] = span[1];
                }
            }

            if (bestSpan[1] > -1) {

                final int cRow = bestSpan[0] + (bestSpan[1] - bestSpan[0]) / 2; // pick midpoint of span

                positions[cCol] = cRow;

                if (VERBOSE) {
                    System.out.println(Arrays.toString(positions));
                }
            }
            retCI = ci + 1;
        } else {
            // need to backtrack as there are no unclaimed spaces

            // look for existing bad paths that match the current pathPositions and remove them
            for (int i = badPaths.size() - 1; i > -1; i--) {
                int[] bp = badPaths.get(i);
                boolean samePath = true;
                for (int j = 0; j < ci && j < bp.length && samePath; j++) {
                    if (pathPositions[j] != bp[j]) {
                        samePath = false;
                    }
                }
                if (samePath) {
                    badPaths.remove(i);
                }
            }
            // add current pathPositions to list of bad paths
            badPaths.add(pathPositions);

            retCI = ci - 1;
        }
        return retCI;
    }

    private static void printBoard(int[] positions) {

        char QUEEN = 'Q';
        char BOX = '.';

        final int nR = positions.length;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < nR; i++) {
            sb.setLength(0);
            for (int j = 0; j < nR; j++) {
                if (positions[j] == i) {
                    sb.append(QUEEN);
                } else {
                    sb.append(BOX);
                }
                sb.append(' ');
            }
            System.out.println(sb.toString());
        }
        System.out.println();
        System.out.println(Arrays.toString(positions));

        System.out.flush();
    }

    private static void printProgress(int c, int[] positions, boolean[] claimed) {

        char QUEEN = 'Q';
        char BOX = '.';

        final int nR = positions.length;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < nR; i++) {
            sb.setLength(0);
            for (int j = 0; j < c; j++) {
                if (positions[j] == i) {
                    sb.append(QUEEN);
                } else {
                    sb.append(BOX);
                }
                sb.append(' ');
            }
            if (claimed[i]) {
                sb.append('X');
            } else {
                sb.append('O');
            }
            System.out.println(sb.toString());
        }
        System.out.flush();
    }

}
