package lab4;

public class App {
    
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("Main thread started...");
        
        long[][] A = {{2, 5, 7},
                {6, 3, 4},
                {5, -2, -3}};
        
        long[][] B = {{10, 11, 34, 55},
                {33, 45, 17, 81},
                {45, 63, 12, 16}};
        
        Matrix matrix = new Matrix(10);
        matrix.fillRandomValues();
        matrix.displayMatrix();
        System.out.println(matrix.calculateDet());
        Matrix inverseMatrix = matrix.getInverse();
        System.out.println(inverseMatrix.getMatrixKoef());
        inverseMatrix.displayDoubleMatrix();
        inverseMatrix.displayMatrix();
        Matrix mulMatrix = new Matrix(0);
        try {
            mulMatrix = inverseMatrix.multiply(matrix);
        } catch (NotEqualLengthsOfMatrixException e) {
            e.printStackTrace();
        }
        mulMatrix.displayDoubleMatrix();
        mulMatrix.displayMatrix();
        System.out.println("Main thread finished...");
        long timeSpent = System.currentTimeMillis() - startTime;
        System.out.println("программа выполнялась " + timeSpent + " миллисекунд");
    }
}
