package lab4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Matrix {

    private int nThreads;

    private int n;
    private long[][] mainMatrix;
    private double matrixKoef;
    private double[][] doubleMatrix;

    public Matrix(int n) {
        this.n = n;
        nThreads = 5;
        this.mainMatrix = new long[this.n][this.n];
        doubleMatrix = new double[n][n];
        matrixKoef = 1;
    }

    public Matrix(long[][] paramMatrix) {
        this.n = paramMatrix.length;
        nThreads = n;
        this.mainMatrix = paramMatrix;
        doubleMatrix = new double[n][n];
        matrixKoef = 1;
    }

    public long getElement(int n, int m) {
        return this.mainMatrix[n][m];
    }

    public void setElement(int n, int m, long value) {
        this.mainMatrix[n][m] = value;
    }

    public int getN() {
        return this.n;
    }

    public double getMatrixKoef() {
        return matrixKoef;
    }

    public void fillRandomValues() {
        Random rand = new Random();

        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.n; j++) {
                this.mainMatrix[i][j] = rand.nextInt(7) - 3;
            }
        }
    }

    public void fillAsE() {
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.n; j++) {
                if (i == j) {
                    mainMatrix[i][j] = 1;
                    doubleMatrix[i][j] = 1;
                } else {
                    mainMatrix[i][j] = 0;
                    doubleMatrix[i][j] = 0;
                }
            }
        }
    }

    public boolean equals(Matrix matrix) {
        if (n != matrix.n)
            return false;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (mainMatrix[i][j] != matrix.mainMatrix[i][j])
                    return false;
            }
        }
        return true;
    }

    public void displayMatrix() {
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.n; j++) {
                System.out.print(this.mainMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void displayDoubleMatrix() {
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.n; j++) {
                System.out.print(this.doubleMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public Matrix transpone() {
        Matrix transponeMatrix = new Matrix(n);
        final ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        final int range = n / nThreads;
        for (int count = 0; count < nThreads; ++count) {
            final int startAt = count * range;
            final int endAt = startAt + range;
            executorService.submit(() -> {
                for (int i = startAt; i < endAt; i++) {
                    transponeMatrix.transponeParallelFunc(i, this);
                }
            });
        }
        awaitTerminationAfterShutdown(executorService);

        return transponeMatrix;
    }

    private void transponeParallelFunc(int i, Matrix matrix) {
        final ExecutorService executorService2 = Executors.newFixedThreadPool(nThreads);
        final int range2 = n / nThreads;
        for (int count2 = 0; count2 < nThreads; ++count2) {
            final int startAt2 = count2 * range2;
            final int endAt2 = startAt2 + range2;
            executorService2.submit(() -> {
                for (int j = startAt2; j < endAt2; j++) {
                    setElement(i, j, matrix.getElement(j, i));
                }
            });
        }
        awaitTerminationAfterShutdown(executorService2);
    }

    public long calculateDet() {
        long det = 0;
        if (n == 2) {
            det = mainMatrix[0][0] * mainMatrix[1][1] - mainMatrix[1][0] * mainMatrix[0][1];
        } else {
            int koef;
            for (int i = 0; i < n; i++) {
                if (i % 2 == 1) {
                    koef = -1;
                } else {
                    koef = 1;
                }
                det += koef * mainMatrix[0][i] * getMinor(0, i).calculateDet();
            }
        }
        return det;
    }

    private Matrix getMinor(int row, int column) {
        int minorLength = n - 1;
        Matrix minor = new Matrix(minorLength);
        int dI = 0;
        int dJ = 0;
        for (int i = 0; i <= minorLength; i++) {
            dJ = 0;
            for (int j = 0; j <= minorLength; j++) {
                if (i == row) {
                    dI = 1;
                } else {
                    if (j == column) {
                        dJ = 1;
                    } else {
                        minor.setElement(i - dI, j - dJ, mainMatrix[i][j]);
                    }
                }
            }
        }
        return minor;

    }

    public Matrix getComplement() {
        Matrix complementMatrix = new Matrix(n);
        final ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        final int range = n / nThreads;
        for (int count = 0; count < nThreads; ++count) {
            final int startAt = count * range;
            final int endAt = startAt + range;
            executorService.submit(() -> {
                for (int i = startAt; i < endAt; i++) {
                    complementMatrix.complementParallelFunc(i, this);
                }
            });
        }
        awaitTerminationAfterShutdown(executorService);
        return complementMatrix;
    }

    private void complementParallelFunc(int i, Matrix matrix) {
        final ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        final int range = n / nThreads;
        for (int count = 0; count < nThreads; ++count) {
            final int startAt = count * range;
            final int endAt = startAt + range;
            executorService.submit(() -> {
                for (int j = startAt; j < endAt; j++) {
                    int koef;
                    if ((i + j) % 2 == 1) {
                        koef = -1;
                    } else {
                        koef = 1;
                    }
                    this.setElement(i, j, koef * matrix.getMinor(i, j).calculateDet());
                }
            });
        }
        awaitTerminationAfterShutdown(executorService);
    }

    public static void awaitTerminationAfterShutdown(final ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (final InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public Matrix getInverse() throws DeterminantIsZero {
        long det = calculateDet();
        if (det != 0) {
            Matrix inverseMatrix = getComplement().transpone();
            inverseMatrix.matrixKoef = 1.0 / det;
            final ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
            final int range = n / nThreads;
            for (int count = 0; count < nThreads; ++count) {
                final int startAt = count * range;
                final int endAt = startAt + range;
                executorService.submit(() -> {
                    for (int i = startAt; i < endAt; i++) {
                        inverseMatrix.inverseParallelFunc(i);
                    }
                });
            }
            awaitTerminationAfterShutdown(executorService);

            return inverseMatrix;
        }
        throw new DeterminantIsZero();
    }

    private void inverseParallelFunc(int i) {
        final ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        final int range = n / nThreads;
        for (int count = 0; count < nThreads; ++count) {
            final int startAt = count * range;
            final int endAt = startAt + range;
            executorService.submit(() -> {
                for (int j = startAt; j < endAt; j++) {
                    doubleMatrix[i][j] = matrixKoef * mainMatrix[i][j];
                }
            });
        }
        awaitTerminationAfterShutdown(executorService);
    }

    public Matrix multiply(Matrix second) throws NotEqualLengthsOfMatrixException {
        if (n != second.n)
            throw new NotEqualLengthsOfMatrixException();
        else {
            Matrix tmpMatrix = new Matrix(n);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < n; k++) {
                        tmpMatrix.doubleMatrix[i][j] += doubleMatrix[i][k] * second.getElement(k, j);
                        tmpMatrix.mainMatrix[i][j] = (int) (tmpMatrix.doubleMatrix[i][j] + 0.01);
                    }
                }
            }
            return tmpMatrix;
        }
    }
}

