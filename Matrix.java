import java.util.*;

public class Matrix
{
    private double[][] matrix;
    public int columns;
    public int rows;

    //I could have made this a generic class but I didn't. Why didn't I? Too much thinking... 
    //I would potentially do it in the future, but there are already super optimized matrices out there
    //The world doesn't need my attempt at a matrix class.
    public Matrix(int rows, int columns) 
    {
        this.columns = columns;
        this.rows = rows;
        this.matrix = new double[rows][columns];
        this.InitializeToZero();
    }
    
    public void set(int rowposition , int columnPosition, double value)
    {
        this.matrix[rowposition][columnPosition] = value;
    }

    public double get(int rowposition, int columnPosition)
    {
        return this.matrix[rowposition][columnPosition];
    }

    //Set this up to work for all types maybe in the future?

    public static Matrix MatrixMultiply(Matrix a, Matrix b) 
    {
        //If both a and b are both normal matrixes, do normal matrix multiplication
        if(a.columns == b.rows)
        {
            //Make new matrix with the same amount of columns as shape B and the same amount of rows as A
            Matrix newMatrix = new Matrix(a.rows, b.columns);
            //Multiply the matrices
            for (int colB = 0; colB < b.columns; colB++)
            {
                for(int rowA = 0; rowA < a.rows; rowA++)
                {
                    double sum = 0.0;
                    for(int colA = 0; colA < a.columns; colA++)
                    {
                        double multiple = (double)a.get(rowA, colA) * (double)b.get(colA, colB);
                        sum += multiple;
                    }
                    newMatrix.set(rowA, colB, sum);
                }
            }
            //Return the new matrix
            return newMatrix;
        }
        return null;           
    }

    public static Matrix MultiplyScalar(Matrix a, double scalar) 
    {
        //Just take a scalar value and multiply it to the matrix
        Matrix newMatrix = new Matrix(a.rows, a.columns);
        for (int row = 0; row < a.rows; row++)
        {
            for(int col = 0; col < a.columns; col++)
            {
                newMatrix.set(row, col, (a.get(row, col)*(double)scalar));
            }
        }
        return newMatrix;
    }

    public static Matrix VectorMultiply(Matrix a, Matrix b)
    {
        //If both a and b are both vectors, take the dot product but don't add them up cause that would mess us up
        //Think this is called inner product. It only works when it's two vectors.
        //Luckily I control when it's used so I don't have to focus too much on "what if they're not vectors?"
        if(a.rows == b.rows)
        {
            Matrix newMatrix = new Matrix(a.rows, a.columns);
            for (int row = 0; row < a.rows; row++)
            {
                newMatrix.set(row, 0, (double)b.get(row, 0)*(double)a.get(row, 0));
            }
            return newMatrix;
        }
        return null;
    }

    public static Matrix Transpose(Matrix a)
    {
        Matrix newMatrix = new Matrix(a.columns, a.rows);
        for (int col = 0; col < a.columns; col++)
        {
            for (int row = 0; row < a.rows; row++)
            {
                //Just go on and flip it around.
                // 1 [0][0]     
                // 1 [1][0]        [ 1, 1, 1, 1]
                // 1 [2][0]   [0][0] [0][1] [0][2] [0][4] 
                // 1 [3][0]
                newMatrix.set(col, row, (double)a.get(row, col));
            }
        }
        return newMatrix;
    }
    
    public static Matrix Add(Matrix a, Matrix b)
    {
        //Bro, what are you trying here? Smh, not gonna happen
        if(a.rows != b.rows || a.columns != b.columns)
        {
            return null;
        }
        //Just adds corresponding values
        Matrix newMatrix = new Matrix(a.rows, b.columns);
        for (int row = 0; row < a.rows; row++)
        {
            for(int col = 0; col < a.columns; col++)
            {
                newMatrix.set(row, col, ((double)a.get(row, col)+ (double)b.get(row, col)));
            }
        }
        return newMatrix;
    }

    public static Matrix Subtract(Matrix a, Matrix b)
    {
        //Bro, what are you trying here? That won't work
        if(a.rows != b.rows || a.columns != b.columns)
        {
            return null;
        }
        // Real simple, just subtracts values
        Matrix newMatrix = new Matrix(a.rows, b.columns);
        for (int row = 0; row < a.rows; row++)
        {
            for(int col = 0; col < a.columns; col++)
            {
                newMatrix.set(row, col, ((double)a.get(row, col) - (double)b.get(row, col)));
            }
        }
        return newMatrix;
    }

    //This creates an rowsize x 1 vector 
    public static Matrix Vector(int rowsize)
    {
        Matrix newMatrix = new Matrix(rowsize, 1);
        for(int row = 0; row < rowsize; row++)
        {
            newMatrix.set(row, 0, 1.00);
        }
        return newMatrix;
    }
    
    //Prints the matrix
    public void Print() 
    {
        for (double[] row : this.matrix) 
  
            // converting each row as string 
            // and then printing in a separate line 
            System.out.println(Arrays.toString(row)); 
    }

    //Sets everything to zero for addition and such to avoid null references
    public void InitializeToZero()
    {
        for(int row = 0; row < this.rows; row++)
        {
            for(int col = 0; col < this.columns; col++)
            {
                this.set(row, col, 0);
            }
        }
    }

    //Create a matrix equal to an array. I used this when testing stuff with excel
    public void Equal(double[][] array)
    {
        for(int row = 0; row < this.rows; row++)
        {
            for (int col = 0; col < this.columns; col++)
            {
                this.matrix[row][col] = array[row][col];
            }
        }
    }

    //Use this to save my weights
    public String ToString()
    {
        String tempString = "";
        for(int row = 0; row < this.rows; row++)
        {
            for (int col = 0; col < this.columns; col++)
            {
                tempString += " " + this.get(row, col)+ " ";
            }
        }
        return tempString;
    }

    //Use this to load my weights
    public void StringToMatrix(List<String> string)
    {
        int counter = 0;
        for(int row = 0; row < this.rows; row++)
        {
            for (int col = 0; col < this.columns; col++)
            {
                this.set(row, col, Double.parseDouble(string.get(counter)));
                counter++;
            }
        }
    }
}
