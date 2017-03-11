/*  COSC 302 Spring 2017, Lab 7: Selection

    Name:
*/

import java.util.Random;

class SelectLab7 {

static Random rand = new Random();

public enum Partitioner {
    RANDOM, BAD, LINEAR
}

// choose x = A[r] and partition array A
// so that all values < x come before x,
// all values > x come after x.
// return q such that x = A[q]
static int Partition(int A[], int p, int r)
{
    int q = p;

    // TODO: Choose x = A[r]. Swap elements so they are in order
    // first elements < x, then x, then elements > x

    // Assigning variable for pivot for convenience
  	int pivot = A[r];
  	int temp;

    // Parition algorithm as discussed in class
    for(int i = p; i <= r - 1; i++) {
    	if(A[i] < pivot) {
    		temp = A[q];
    		A[q] = A[i];
    		A[i] =  temp;
    		q++;
    	}
    }
    temp =  A[r];
    A[r] = A[q];
    A[q] =  temp;
    return q;
}
// take a random element x = A[i] and swap it with A[r]
static int RandomPartition(int A[], int p, int r)
{
    // a random index between p and r (inclusive)
    int i = p + rand.nextInt(r+1-p);

    // TODO: swap A[i] with A[r]

    int temp = A[i];
    A[i] =  A[r];
    A[r] = temp;

    // Testing if partition was done correctly

    // for(int k: A)
    //   System.out.print(k + " ");
    // System.out.println();

    return Partition(A,p,r);
}

// Compute the median of medians and swap it with A[r]
static int LinearPartition(int[] A, int p, int r)
{
    // TODO: compute median of medians, swap with A[r]

    // Recommendation: sort sequences of 5 with insertion sort
    // or use Arrays.sort(A, start, end)
    return Partition(A,p,r);
}

// compute the max element x and swap it with A[r]
static int BadPartition(int[] A, int p, int r)
{
    int x = -1;
    int maxIndex = 0;
    // TODO: select x to be the maximum element in A[p..r] and swap with A[r]
    for(int i = p; i <= r; i++) {
    	if(A[i] > x) {
    		x = A[i];
    		maxIndex = i;
    	}
    }
    A[maxIndex] = A[r];
    A[r] =  x;

    return Partition(A,p,r);
}

// Returns the i-th smallest element in array A,
// between p and r. That is, if A were sorted,
// this function would return A[a+i]

// This algorithm runs in O(n) expected time, where
// n is the number of integers in A.
static int Select(int[] A, int p, int r, int i, Partitioner partitioner)
{
    int q = -1;
    if (partitioner == Partitioner.BAD) {
        q = BadPartition(A, p, r);
    } else if (partitioner == Partitioner.RANDOM) {
        q = RandomPartition(A, p, r);
    } else { // LINEAR
        q = LinearPartition(A, p, r);
    }
    // TODO: implement recursive Select.
    // If q equals i, we have found the element
    if(i == q)
    	return A[q];
    // Else if the array contains only one element, this element must be what we are looking for
    if (p ==  r)
      return A[p];
    // Else search the left half of the new array for the ith smallest element
    if(i < q)
    	return Select(A, p, q - 1, i, partitioner);
    // Else search the right half of the new array for the (i - q)th smallest element. However this is still indexed relative to the full array, we look for ith smallest
  	return Select(A, q + 1, r, i, partitioner);
}

static boolean TestSelect()
{
    System.out.println("Running Select tester");
    // TODO: implement tester for Select, use it to test both random, linear, and bad pivot selection.
    static final int SIZE = 100;
    // Random length to ensure correctness. Bounds within 100 to ensure reasonable computation time
    
    int length = rand.nextInt(SIZE);
    int[] testArray = GenerateA(length);
    // Random index to search for
    int index = rand.nextInt(length);
    int result;

    // Call select with Partitioner.RANDOM
    result = Select(testArray, 0, length - 1, index, Partitioner.RANDOM);
    if(result != index)
      return false;

    // Call select with Partitioner.LINEAR
    // RandomlyPermute(testArray);
    // index = Select(0, 20, 9, Partitioner.LINEAR);
    // if(index != 9)
    //   return false;

    // Call select with Partitioner.BAD
    RandomlyPermute(testArray);
    index = Select(testArray, 0, length - 1, index, Partitioner.BAD);
    if(result != index)
      return false;
    return true;
}

// helper function to random permute array A
static void RandomlyPermute(int[] A)
{
    //Random rand = new Random();
    for (int i = 0; i < A.length; i++)
    {
        int randInt = rand.nextInt(A.length-i) + i;
        int tmp = A[i];
        A[i] = A[randInt];
        A[randInt] = tmp;
    }
}

static int[] GenerateA(int n)
{
    int[] A = new int[n];

    for (int i = 0; i < n; i++)
    {
        A[i] = i;
    }

    RandomlyPermute(A);
    return A;
}

static void RunSelectOnce(int[] A, Partitioner partitioner)
{
    int i = rand.nextInt(A.length);
    int ithSmallest = Select(A, 0 , A.length-1, i, partitioner);
}

static double SelectionExperiment(int[] A, Partitioner partitioner)
{
    int numRuns = 5;
    long totalMillis = 0;
    int [] CopyA = new int[A.length];
    for (int i = 0; i < numRuns; ++i)
    {
        System.arraycopy(A, 0, CopyA, 0, A.length);
        long startMillis = System.currentTimeMillis();
        RunSelectOnce(CopyA, partitioner);
        totalMillis += (System.currentTimeMillis() - startMillis);
    }

    return (((float)totalMillis)/numRuns)/1000.0;
}

static void RunRandomizedSelectOnce(int n)
{
    System.out.println("Running Randomized-Select once");

    int[] A = GenerateA(n);
    for(int k: A)
    	System.out.print(k + " ");
    System.out.println();
    int i = rand.nextInt(A.length);
    int ithSmallest = Select(A, 0 , A.length-1, i, Partitioner.RANDOM);
    System.out.print("The ");
    System.out.print (i);
    System.out.print("th smallest integer is ");
    System.out.println(ithSmallest);
}

static void RunTimeSelect(boolean noBad)
{
    System.out.println("Timing Select");
    if (!noBad) {
        System.out.printf("%15s %8s %8s %8s%n", "n", "Random", "Linear", "Bad");
    } else {
        System.out.printf("%15s %8s %8s%n", "n", "Random", "Linear");
    }
    for (int i = 1; i > 0 && i < 200000000; i=i*2) {
        int[] A = GenerateA(i);
        System.out.printf("%15d ", i);
        double timeRandom = SelectionExperiment(A, Partitioner.RANDOM);
        double timeLinear = SelectionExperiment(A, Partitioner.LINEAR);
        System.out.printf("%8.2f %8.2f ", timeRandom, timeLinear);
        if (!noBad) {
            double timeBad = SelectionExperiment(A, Partitioner.BAD);
            System.out.printf("%8.2f%n", timeBad);
        } else {
            System.out.printf("%n");
        }
    }
}

public static void main(String[] args)
{
    boolean runRandomizedSelect = true;

    int testPartition[] = {1, 2, 4, 3 , 7, 9};
    System.out.println(Partition(testPartition, 0, 5));
    for(int k: testPartition)
    	System.out.print(k + " ");
    System.out.println();

    if (args.length > 0) {
        if (args[0].equals("--test")) {
            runRandomizedSelect = false;
            boolean testPassed = TestSelect();
            if(testPassed)
              System.out.println("All tests passed!");
        } else if (args[0].equals("--time=nobad")) {
            RunTimeSelect(true /* exclude bad partition from experiments */);
            runRandomizedSelect = false;
        } else if (args[0].equals("--time=all")) {
            RunTimeSelect(false /* include bad partition in experiments */);
            runRandomizedSelect = false;
        } else {
            System.out.print("Unrecognized command: ");
            System.out.println(args[0]);
        }
    }

    if (runRandomizedSelect) {
        RunRandomizedSelectOnce(20);
    }
}

};
