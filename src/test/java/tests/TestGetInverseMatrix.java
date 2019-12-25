package tests;

import lab4.DeterminantIsZero;
import lab4.Matrix;
import lab4.NotEqualLengthsOfMatrixException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestGetInverseMatrix {

    @Test
    void testGetInverseMatrix() {
        int n = 10;
        Matrix E = new Matrix(n);
        E.fillAsE();

        Matrix matrix = new Matrix(n);
        matrix.fillRandomValues();

        Matrix inverse = new Matrix(n);
        try {
            inverse = matrix.getInverse();
        } catch (DeterminantIsZero determinantIsZero) {
            determinantIsZero.printStackTrace();
        }

        Matrix multiply = new Matrix(n);
        try {
             multiply = inverse.multiply(matrix);
        } catch (NotEqualLengthsOfMatrixException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(E.equals(multiply));
    }
}
