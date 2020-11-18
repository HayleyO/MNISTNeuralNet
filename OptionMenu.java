/*   Name: Hayley Owens 
 *   Program: MNIST Neural Net
 *   Date: 10/14/2020  
 */

import java.util.Scanner;
import java.io.*; 
import java.util.*; 
public class OptionMenu
{
    private static List<Integer> choices = new ArrayList<Integer>();
    private static Scanner choiceScanner = new Scanner(System.in);
    public static void main(String[] args) 
    {
        System.out.println("Hello, and welcome to my MNIST Neural Network. Please type in an option from the menu below: ");
        //Open the "menu" aka just print option and then get input
        openMenu();
    }

    public static void openMenu()
    {        
        System.out.println("[1]  Train the network ");
        System.out.println("[2]  Load a pre-trained network ");
        System.out.println("[3]  Display network accuracy on TRAINING data ");
        System.out.println("[4]  Display network accuracy on TESTING data ");
        System.out.println("[5]  Save the network state to file ");
        System.out.println("[6]  Get visualizations for all images ");
        System.out.println("[7]  Get visualizations for incorrect images ");
        System.out.println("[0]  Exit ");
        getInput();
    }

    private static void getInput()
    {        
        String choice = choiceScanner.nextLine();
        Boolean recognizedInput = false;        
        while(!recognizedInput)
        {

            //Choice 1
            if(choice.trim().equals("1") || choice.trim().equals("[1]"))
            {
                // Train
                readTrainingData();
                NeuralNet.train();
                choices.add(1);
                break;                
            }
            //Choice 2
            else if(choice.trim().equals("2") || choice.trim().equals("[2]"))
            {
                loadNN();
                choices.add(2);
                break;
            }
            //Choice 3
            else if(choice.trim().equals("3") || choice.trim().equals("[3]"))
            {
                if(choices == null || (!choices.contains(1) && !choices.contains(2)))
                {
                    System.out.println("I'm sorry, but you currently have no trained/loaded network.");
                    recognizedInput = true;
                    break;
                }
                if(Images.trainingImages == null || Images.trainingImages.size() == 0)
                {
                    readTrainingData();
                }
                NeuralNet.eval(true);
                recognizedInput = true;
                break;
            }
            //Choice 4
            else if(choice.trim().equals("4") || choice.trim().equals("[4]"))
            {
                if(choices == null || (!choices.contains(1) && !choices.contains(2)))
                {
                    System.out.println("I'm sorry, but you currently have no trained/loaded network.");
                    break;
                }
                if(Images.testingImages == null || Images.testingImages.size() == 0)
                {
                    readTestingData();
                }
                NeuralNet.eval(false);
                recognizedInput = true;
                break;
            }
            //Choice 5
            else if(choice.trim().equals("5") || choice.trim().equals("[5]"))
            {
                saveNN();
                recognizedInput = true;
                break;
            }
            //Choice 6
            else if(choice.trim().equals("6") || choice.trim().equals("[6]"))
            {
                if(choices == null || (!choices.contains(1) && !choices.contains(2)))
                {
                    System.out.println("I'm sorry, but you currently have no trained/loaded network.");
                    break;
                }
                if(Images.trainingImages == null || Images.trainingImages.size() == 0)
                {
                    readTrainingData();
                }
                getAllTestingCasesVisualized();
                recognizedInput = true;
                break;
            }
            //Choice 7
            else if(choice.trim().equals("7") || choice.trim().equals("[7]"))
            {
                if(choices == null || (!choices.contains(1) && !choices.contains(2)))
                {
                    System.out.println("I'm sorry, but you currently have no trained/loaded network.");
                    break;
                }
                if(Images.trainingImages == null || Images.trainingImages.size() == 0)
                {
                    readTrainingData();
                }
                getAllIncorrectTestingCasesVisualized();
                recognizedInput = true;
                break;
            }
            //Choice 0, break
            else if(choice.trim().equals("0") || choice.trim().equals("[0]"))
            {
                choiceScanner.close();
                recognizedInput = true;
                choices.add(0);
                break;
            }
            if(!recognizedInput)
            {
                System.out.println("Unrecognized Input, try again. (Hint: Try simply typing a number, for example '1'.)");
            }
            if(choices!= null && !choices.contains(0))
            {
                choice = choiceScanner.nextLine();
            }
        }
        if(choices!= null && !choices.contains(0))
        {
            openMenu();
        }
    }

    private static void ImageToAscii(Matrix image)
    {
        int counter = 0;
        //These are 28 by 28 images so just print their values in nested for loops
        for(int row = 0; row < 28; row++)
        {
            String rowString = "";
            for(int col = 0; col < 28; col++)
            {
                double value = image.get(counter, 0);
                if(value == 0)
                {
                    rowString += " ";
                }
                else if(value >= 0.9)
                {
                    rowString += "#";
                }
                else if(value >= 0.8)
                {
                    rowString += "I";
                }
                else if(value >= 0.6)
                {
                    rowString += "i";
                }
                else if (value >= 0.4)
                {
                    rowString += ";";
                }
                else if(value >= 0.2)
                {
                    rowString += ":";
                }
                else if(value >= 0.1)
                {
                    rowString += ",";
                }
                else
                {
                    rowString += ".";
                }
                counter++;
            }
            System.out.println(rowString);
        }            
        
    }

    private static void readTrainingData()
    {
        //Set images and labels to empty
        Images.trainingImages = new ArrayList<Matrix>();
        Labels.trainingLabels = new ArrayList<Matrix>();        
        loadDataFromCSV(true);
    }    

    private static void readTestingData()
    {
        //Set images and labels to empty
        Images.testingImages = new ArrayList<Matrix>();
        Labels.testingLabels = new ArrayList<Matrix>();        
        loadDataFromCSV(false);
    }

    private static void loadDataFromCSV(boolean training)
    {
        String csvFile;
        if(training)
        {
            csvFile = "mnist_train.csv";
            //csvScanner = new Scanner(new File("mnist_train.csv")).useDelimiter(",");
        }
        else
        {
            csvFile = "mnist_test.csv";
            //csvScanner = new Scanner(new File("mnist_test.csv")).useDelimiter(",");
        }
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        try 
        {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) 
            {
                Matrix imageMatrix = new Matrix(NeuralNet.INPUTNODES, 1);
                // use comma as separator
                String[] image = line.split(cvsSplitBy);
                //The very first part of the image is the label
                if(training)
                {
                    Labels.trainingLabels.add(NeuralNet.hotVector(Integer.parseInt(image[0])));
                }
                else
                {
                    Labels.testingLabels.add(NeuralNet.hotVector(Integer.parseInt(image[0])));
                }
                //Every other part of the image is, like, actual values
                for(int i = 1; i < image.length; i++)
                {
                    imageMatrix.set(i-1, 0, ((double)Integer.parseInt(image[i])/255.0));
                }
                //After you have the whole length of the image, add it to the images.
                if(training)
                {
                    Images.trainingImages.add(imageMatrix);
                }
                else
                {
                    Images.testingImages.add(imageMatrix);
                } 
            }
            br.close();
        }
        catch(Exception e) 
        {
            System.out.println("An error occured while trying to open training data. Error: " + e);
        }        
    }

    private static void saveNN()
    {
        BufferedWriter bw = null;
        try
        {            
            if(NeuralNet.Weights != null && NeuralNet.Weights.size() !=0)
            {
                //Write weights file
                File newFile = new File("weights");
                FileWriter fw = new FileWriter(newFile);
                bw = new BufferedWriter(fw);
                for(int weight = 0; weight < NeuralNet.Weights.size(); weight++)
                {
                    bw.write("rows: "+NeuralNet.Weights.get(weight).rows+" columns: " +NeuralNet.Weights.get(weight).columns+" ");
                    bw.write(NeuralNet.Weights.get(weight).ToString());
                    bw.write(" stop ");
                }
                bw.close();
                //Write biases file
                newFile = new File("biases");
                fw = new FileWriter(newFile);
                bw = new BufferedWriter(fw);
                for(int bias = 0; bias < NeuralNet.Biases.size(); bias++)
                {
                    bw.write("rows: "+NeuralNet.Biases.get(bias).rows+" columns: " +NeuralNet.Biases.get(bias).columns+" ");
                    bw.write(NeuralNet.Biases.get(bias).ToString());
                    bw.write(" stop ");
                }
                bw.close();
            }
            else
            {
                System.out.println("Sorry, there is no network to currently save!");
            }      
        }
        catch(Exception e) 
        {
            System.out.println("An error occured while trying to save neural network. Error: " + e);
        } 
    }

    //This could be factured and done better but I've had a very long week and I'm tired so I'm just gonna get it done, okay.
    //We've all been here, don't judge, lol. 
    private static void loadNN()
    {
        try 
        {
            //These are kinda generic shared things between loading weights and biases
            String[] stuff;             
            int rows = 0;
            int columns = 0;
            List<String> content = new ArrayList<String>();
            //Load weight file
            BufferedReader br = new BufferedReader(new FileReader("weights"));
            String line = "";      
            if(NeuralNet.Weights == null)
            {
                NeuralNet.Weights = new ArrayList<Matrix>();
                NeuralNet.Biases = new ArrayList<Matrix>();
            }      
            while ((line = br.readLine()) != null) 
            {
                stuff = line.trim().split(" ");
                for(int i = 0; i < stuff.length; i++)
                {
                    if(stuff[i].equals("rows:"))
                    {
                        i++;
                        rows = Integer.parseInt(stuff[i]);
                    }
                    else if(stuff[i].equals("columns:"))
                    {
                        i++;
                        columns = Integer.parseInt(stuff[i]);
                    }
                    else if(stuff[i].equals("stop"))
                    {
                        Matrix newMatrix = new Matrix(rows, columns);
                        newMatrix.StringToMatrix(content);
                        NeuralNet.Weights.add(newMatrix);
                        content = new ArrayList<String>();                        
                    }
                    else
                    {
                        if(!stuff[i].equals(""))
                        {
                            content.add(stuff[i]);
                        }
                    }
                }
            }
            br.close();
            //Okay, now for the biases. 
            br = new BufferedReader(new FileReader("biases"));
            line = "";
            while ((line = br.readLine()) != null) 
            {
                stuff = line.split(" ");
                for(int i = 0; i < stuff.length; i++)
                {
                    if(stuff[i].equals("rows:"))
                    {
                        i++;
                        rows = Integer.parseInt(stuff[i]);
                    }
                    else if(stuff[i].equals("columns:"))
                    {
                        i++;
                        columns = Integer.parseInt(stuff[i]);
                    }
                    else if(stuff[i].equals("stop"))
                    {
                        Matrix newMatrix = new Matrix(rows, columns);
                        newMatrix.StringToMatrix(content);
                        NeuralNet.Biases.add(newMatrix);
                        content = new ArrayList<String>();                        
                    }
                    else
                    {
                        if(!stuff[i].equals(""))
                        {
                            content.add(stuff[i]);
                        }
                    }
                }
            }
            br.close();
        }
        catch(Exception e)
        {
            System.out.println("An error occured while trying to load neural network. Error: " + e);
        }
    }

    private static void getAllTestingCasesVisualized()
    {        
        //Go through the images, get the outputs 
        for(int image = 0; image < Images.trainingImages.size(); image++)
        {
            List<Matrix> outputs = NeuralNet.feedforward(Images.trainingImages.get(image));
            int highestIndexOutput = NeuralNet.getHighestIndex(outputs.get(outputs.size()-1));
            int actualValIndex = NeuralNet.getHighestIndex(Labels.trainingLabels.get(image));
            //If the outputs are correct, print correct, else print incorrect. I could do this cleaner, I just didn't because you know what we all get lazy sometimes.
            if(Labels.trainingLabels.get(image).get(highestIndexOutput, 0) == 1)
            {
                System.out.println("Testing case #" + image + " Correct classification = " + actualValIndex + " Network output = " + highestIndexOutput + " Correct");
            }
            else
            {
                System.out.println("Testing case #" + image + " Correct classification = " + actualValIndex + " Network output = " + highestIndexOutput + " Incorrect");
            }
            //Print image
            ImageToAscii(Images.trainingImages.get(image));
            //Keep going or nah
            System.out.println("Enter 1 to continue. All other values return to menu");
            String choice = choiceScanner.nextLine();
            if(!choice.trim().equals("1"))
            {
                break;
            }
        }
    }

    private static void getAllIncorrectTestingCasesVisualized()
    {
        //The same as the previous function except it only does things if the outputs are incorrect
        for(int image = 0; image < Images.trainingImages.size(); image++)
        {
            List<Matrix> outputs = NeuralNet.feedforward(Images.trainingImages.get(image));
            int highestIndexOutput = NeuralNet.getHighestIndex(outputs.get(outputs.size()-1));
            int actualValIndex = NeuralNet.getHighestIndex(Labels.trainingLabels.get(image));
            if(Labels.trainingLabels.get(image).get(highestIndexOutput, 0) != 1)
            {
                System.out.println("Testing case #" + image + " Correct classification = " + actualValIndex + " Network output = " + highestIndexOutput + " Incorrect");
                ImageToAscii(Images.trainingImages.get(image));
                System.out.println("Enter 1 to continue. All other values return to menu");
                String choice = choiceScanner.nextLine();
                if(!choice.trim().equals("1"))
                {
                    break;
                }
            }
            
        }
    }
}