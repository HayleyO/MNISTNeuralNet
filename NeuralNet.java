import java.util.*; 

public class NeuralNet
{
    //I'll probably make this cleaner and more object oriented in the future.
    public static final int LAYERS = 3;
    public static final int INPUTNODES = 784;
    public static final int OUTPUTNODES = 10;
    public static final int MINIBATCHES = 10;
    public static final int EPOCHS = 30;
    public static final double LEARNING_RATE = 3.0; 
    public static int hiddenLayers = LAYERS-2;
    //To get equivalent layer, hiddenLayer-1;
    public static int[] hiddenNodes = {30};
    //[0] is the for between input and hidden layer, you will always have +1 more than HiddenLayers 
    public static List<Matrix> Weights;
    public static List<Matrix> Biases;     

    public static void train()
    {
        //Initialize weights and biases
        generateRandomWeightsAndBiases();
        //Stochastic Gradient Descent
        SGD();
    }

    public static Matrix hotVector(int val)
    {
        // 0 = 1 0 0 0 0 0 0 0 0
        // 1 = 0 1 0 0 0 0 0 0 0 etc
        Matrix temp = new Matrix(10, 1);
        for (int i = 0; i < OUTPUTNODES; i++)
        {
            if(val == i)
            {
                temp.set(i,0,1.00);
            }
            else
            {
                temp.set(i,0,0.00);
            }
        }
        return temp;
    }   

    public static List<Matrix> feedforward(Matrix input)
    {
        List<Matrix> outputs = new ArrayList<Matrix>();
        for(int layer = 0; layer <= hiddenLayers; layer++)
        {
            //w x x
            // (w x x) + b
            // sigma ((wxx)+b)
            Matrix WeightsxInput = Matrix.MatrixMultiply(Weights.get(layer), input);
            Matrix WeightsxInputPlusBias = Matrix.Add(WeightsxInput, Biases.get(layer));
            Matrix SigmoidedWeightsxInputPlusBias = sigmoid(WeightsxInputPlusBias);
            outputs.add(SigmoidedWeightsxInputPlusBias);
            //the last output is the new input to the next layer
            input = SigmoidedWeightsxInputPlusBias;
        }
        return outputs;
    }

    private static Matrix sigmoid(Matrix outputPreSigmoided)
    {
        Matrix outputPostSigmoided = new Matrix(outputPreSigmoided.rows, outputPreSigmoided.columns);
        for(int row = 0; row < outputPreSigmoided.rows; row++)
        {
            for(int col = 0; col < outputPreSigmoided.columns; col++)
            {
                double newValue =  1.0/(1.0+Math.exp(-(outputPreSigmoided.get(row, col))));
                outputPostSigmoided.set(row, col, newValue);
            }
        }
        return outputPostSigmoided;
    }
    
    private static void generateRandomWeightsAndBiases()
    {
        Weights = new ArrayList<Matrix>();
        Biases = new ArrayList<Matrix>();
        //Layer 0 and layer 2 are important for three layer network otherwise it bases it off previous nodes
        for (int layer = 0; layer < hiddenLayers+1; layer++)
        {      
            Matrix biases;      
            Matrix weights;
            //Determine what size to initialize the weights
            if(layer == 0)
            {
                weights = new Matrix(hiddenNodes[layer], INPUTNODES);
                biases = new Matrix(hiddenNodes[layer], 1);
            }
            else if(layer == hiddenLayers)
            {
                weights = new Matrix(OUTPUTNODES, hiddenNodes[hiddenLayers-1]);
                biases = new Matrix(OUTPUTNODES, 1);
            }
            else
            {
                weights = new Matrix(hiddenNodes[layer], hiddenNodes[layer-1]);
                biases = new Matrix(hiddenNodes[layer], 1);
            }
            //Fill the columns up
            for (int row = 0; row < weights.rows; row++)
            {
                for(int col = 0; col < weights.columns; col++)
                {
                    Random r = new Random();
                    double random_double_weight = (-1.0) + (1.0 - (-1.0)) * r.nextDouble();
                    weights.set(row, col, random_double_weight);
                }
                Random r = new Random();
                double random_double_bias = (-1.0) + (1.0 - (-1.0)) * r.nextDouble();
                biases.set(row, 0, random_double_bias);
            }
            Weights.add(weights);
            Biases.add(biases);
        }
    }

    private static List<List<List<Matrix>>> getMiniBatches()
    {
        //Minibatchsize of 10
        List<List<List<Matrix>>> results = new ArrayList<List<List<Matrix>>>();
        //Do the cupid shuffle.
        long seed = System.nanoTime();
        Collections.shuffle(Images.trainingImages, new Random(seed));
        Collections.shuffle(Labels.trainingLabels, new Random(seed));
        List<List<Matrix>> MiniBatches = new ArrayList<List<Matrix>>();
        List<List<Matrix>> MiniBatchLabels = new ArrayList<List<Matrix>>();
        int numberOfMiniBatches = Images.trainingImages.size()/MINIBATCHES;
        int counter = 0;
        //Just go through and put images into minibatches.
        for(int i = 0; i < numberOfMiniBatches; i++)
        {
            List<Matrix> newMiniBatch = new ArrayList<Matrix>();
            List<Matrix> newLabel = new ArrayList<Matrix>();
            for(int j = 0; j < MINIBATCHES; j++)
            {
                newMiniBatch.add(Images.trainingImages.get(counter));
                newLabel.add(Labels.trainingLabels.get(counter));
                counter++;
            }
            MiniBatches.add(newMiniBatch);
            MiniBatchLabels.add(newLabel);
        }
        results.add(MiniBatches);
        results.add(MiniBatchLabels);
        return results;
    }

    private static List<Matrix[]> createBiasGradients()
    {
        //Make appropriate sized biases and weights
        List<Matrix[]> results = new ArrayList<Matrix[]>();
        Matrix[] biases = new Matrix[hiddenLayers+1];      
        Matrix[] weights = new Matrix[hiddenLayers+1];
        for (int layer = 0; layer < hiddenLayers+1; layer++)
        {    
            if(layer == 0)
            {
                weights[layer] = new Matrix(hiddenNodes[layer], INPUTNODES);
                biases[layer] = new Matrix(hiddenNodes[layer], 1);
            }
            else if(layer == hiddenLayers)
            {
                weights[layer] = new Matrix(OUTPUTNODES, hiddenNodes[hiddenLayers-1]);
                biases[layer] = new Matrix(OUTPUTNODES, 1);
            }
            else
            {
                weights[layer] = new Matrix(hiddenNodes[layer], hiddenNodes[layer-1]);
                biases[layer] = new Matrix(hiddenNodes[layer], 1);
            }
        }
        //Should return two arrays with the length of 2 that have matrixes = 0 
        results.add(biases);
        results.add(weights);
        return results;
    }

    private static void SGD()
    {
        List<List<List<Matrix>>> resultsFromGettingMiniBatches = getMiniBatches();
        List<List<Matrix>> miniBatches = resultsFromGettingMiniBatches.get(0);
        List<List<Matrix>> miniBatchLabels = resultsFromGettingMiniBatches.get(1);
        //For 30 epochs
        for (int epoch = 0; epoch < EPOCHS; epoch++)
        {     
            //Like, 6000 minibatches with 10 items each       
            for (int miniBatch = 0; miniBatch < miniBatches.size(); miniBatch++)
            {
                updateMiniBatch(miniBatches.get(miniBatch), miniBatchLabels.get(miniBatch));
            }
            printMiniBatchAccuracy(miniBatches, miniBatchLabels);
        }
    }

    private static void updateMiniBatch(List<Matrix> minibatch, List<Matrix> minibatchlabels)
    {
        //Get empty bias gradient/ weight gradients
        List<Matrix[]> results = createBiasGradients();
        Matrix[] BiasGradient = results.get(0);
        Matrix [] WeightGradient = results.get(1);
        for (int image = 0; image < minibatch.size(); image++)
        {
            //Preform backprop (aka get bias gradient for this minibatch)
            List<Matrix[]> resultsBackProp = backpropogation(minibatch.get(image), minibatchlabels.get(image));
            Matrix[] addBg = resultsBackProp.get(0);
            Matrix[] addWg = resultsBackProp.get(1);
            //Add all the results biasgradients and weight gradients up and set them equal to biasgradient
            for (int i = 0; i < BiasGradient.length; i++)
            {
                BiasGradient[i] = Matrix.Add(BiasGradient[i], addBg[i]);
                WeightGradient[i] = Matrix.Add(WeightGradient[i], addWg[i]);
            }
        }
        //Update weights and biases
        for(int layer = 0; layer <= hiddenLayers; layer++)
        {
            Biases.set(layer, Matrix.Subtract(Biases.get(layer),Matrix.MultiplyScalar(BiasGradient[layer],((double)LEARNING_RATE/(double)MINIBATCHES))));
            //Weights.get(layer).Print();
            Weights.set(layer, Matrix.Subtract(Weights.get(layer),Matrix.MultiplyScalar(WeightGradient[layer],((double)LEARNING_RATE/(double)MINIBATCHES))));
            //Weights.get(layer).Print();
        }    
    }

    private static List<Matrix[]> backpropogation(Matrix image, Matrix label)
    {
        List<Matrix[]> results = createBiasGradients();
        Matrix[] BiasGradient = results.get(0);
        Matrix [] WeightGradient = results.get(1);
        Matrix input = unchangingMatrix(image);
        //a is our outputs per last layer
        List<Matrix> a = new ArrayList<Matrix>();
        a.add(image);
        //outputs prior to last layer
        List<Matrix> outputs = feedforward(input);
        for(int output = 0; output < outputs.size(); output++)
        {
            a.add(outputs.get(output));
        }
        //calculate last layer bias gradient
        // ((a-y)*a)*(1-a)
        Matrix biasGradient = Matrix.VectorMultiply(costFunctionDerivative(a.get(a.size()-1), label), activationFunctionDerivative(a.get(a.size()-1)));
        //Set the last layer to the last bias gradient
        //10 x 1
        BiasGradient[hiddenLayers] = biasGradient;
        //Get weight gradient (a^L-1) * bg
        //10 x 1 *  (30 x 1 -> 1 x 30 ) = 10 x 30 which is the size of our weights so we're good : ) 
        WeightGradient[hiddenLayers] = Matrix.MatrixMultiply(biasGradient, Matrix.Transpose(a.get(a.size()-2)));
        //layer = 0 is between 0 and 1 and layer = 1 is between 1 and 2
        //Three a values input, a1, a2
        for(int layer = hiddenLayers-1; layer >= 0; layer--)
        {   
            //-1 because of size being + 1 more than array and minus another 1 because we are exluding the very last answer
            Matrix currentOuput = a.get((a.size()-2)-layer);
            biasGradient = Matrix.VectorMultiply(Matrix.MatrixMultiply(Matrix.Transpose(Weights.get(layer+1)), biasGradient), activationFunctionDerivative(currentOuput));
            BiasGradient[layer] = biasGradient;
            Matrix lastOutput = a.get((a.size()-3)-layer);
            WeightGradient[layer] = Matrix.MatrixMultiply(biasGradient, Matrix.Transpose(lastOutput));
        }
        List<Matrix[]> returnGradients = new ArrayList<Matrix[]>();
        returnGradients.add(BiasGradient);
        returnGradients.add(WeightGradient);
        return returnGradients;
    }

    private static Matrix costFunctionDerivative(Matrix a, Matrix label)
    {
        // a - y
        return Matrix.Subtract(a, label);
    }

    private static Matrix activationFunctionDerivative(Matrix a)
    {
        //a * (1-a)
        return Matrix.VectorMultiply(a, Matrix.Subtract(Matrix.Vector(a.rows),a));
    }

    private static Matrix unchangingMatrix(Matrix input)
    {
        //I'm just spooked by object references so I'm making this just in case
        Matrix newMatrix = new Matrix(input.rows, input.columns);
        for (int row = 0; row < input.rows; row++)
        {
            for(int col = 0; col < input.columns; col++)
            {
                newMatrix.set(row, col, input.get(row, col));
            }
        }
        return newMatrix;
    }    

    private static void printMiniBatchAccuracy(List<List<Matrix>> miniBatches, List<List<Matrix>> miniBatchLabels)
    {
        int[] accurate = new int[OUTPUTNODES];
        int [] actualAmount = new int[OUTPUTNODES];
        Arrays.fill(accurate, 0);
        for (int miniBatch = 0; miniBatch < miniBatches.size(); miniBatch++)
        {
            for (int image = 0; image < miniBatches.get(miniBatch).size(); image++)
            {
                Matrix input = unchangingMatrix(miniBatches.get(miniBatch).get(image));
                List<Matrix> outputs = feedforward(input);
                int highestIndexOutput = getHighestIndex(outputs.get(outputs.size()-1));
                if(miniBatchLabels.get(miniBatch).get(image).get(highestIndexOutput, 0) == 1)
                {
                    //If it's correct add it to the number correct place (remember how hot vectors were made, it should be easy)
                    accurate[highestIndexOutput]++;
                }
                int highestIndex = getHighestIndex(miniBatchLabels.get(miniBatch).get(image));
                actualAmount[highestIndex] ++;
            }   
        }
        String output = "";
        int totalAccuracy = 0;
        //Go through and just print accuracy
        for(int currentOutputNode = 0; currentOutputNode < OUTPUTNODES; currentOutputNode++)
        {
            output += currentOutputNode + " = " + accurate[currentOutputNode] + "/" + actualAmount[currentOutputNode] + " ";
            totalAccuracy+=accurate[currentOutputNode];
        }
        output += "Accuracy = " + totalAccuracy + "/" + Images.trainingImages.size() + " = " + (totalAccuracy/((double)Images.trainingImages.size()))*(100) + "%";
        System.out.println(output);
    }
    
    public static int getHighestIndex(Matrix output)
    {
        //Find the highest int in the matrix
        int highestIndex = -1;
        double highestVal = Double.MIN_VALUE;
        for(int i = 0; i < output.rows; i++)
        {
            if (output.get(i, 0) >= highestVal)
            {
                highestIndex = i;
                highestVal = output.get(i,0);
            }
        }
        return highestIndex;
    }

    public static void eval(Boolean training)
    {
        //Kinda works like getting minibatch accuracy, but it is actually just going through all images
        int accurate = 0;
        if(training)
        {
            for(int image = 0; image < Images.trainingImages.size(); image++)
            {
                List<Matrix> outputs = feedforward(Images.trainingImages.get(image));
                int highestIndexOutput = getHighestIndex(outputs.get(outputs.size()-1));
                if(Labels.trainingLabels.get(image).get(highestIndexOutput, 0) == 1)
                {
                    accurate++;
                }
            }
            System.out.println(accurate+"/"+Images.trainingImages.size()+" Accuracy: " + (accurate/((double)Images.trainingImages.size()))*(100) + "%");
        }
        else
        {
            for(int image = 0; image < Images.testingImages.size(); image++)
            {
                List<Matrix> outputs = feedforward(Images.testingImages.get(image));
                int highestIndexOutput = getHighestIndex(outputs.get(outputs.size()-1));
                if(Labels.testingLabels.get(image).get(highestIndexOutput, 0) == 1)
                {
                    accurate++;
                }
            }
            System.out.println(accurate+"/"+Images.testingImages.size()+" Accuracy: " + (accurate/((double)Images.testingImages.size()))*(100) + "%");
        }
    }    
}
