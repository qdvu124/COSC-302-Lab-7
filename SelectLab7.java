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
    int q = -1;

    // TODO: Choose x = A[r]. Swap elements so they are in order
    // first elements < x, then x, then elements > x
	int x = A[r];
	q = p;
	int temp;
    for(int i = p; i < r - 1; i++) {
    	if(A[i] < x) {
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

    if(i == q)
    	return A[q];
    if(i < q)
    	return Select(A, p, q, i, partitioner);
	return Select(A, q + 1, r, i - q, partitioner);
    // TODO: implement recursive Select.
}

static boolean TestSelect()
{
    System.out.println("Running Select tester");
    // TODO: implement tester for Select, use it to test both random, linear, and bad pivot selection.
    // Call select with Partitioner.RANDOM
    // Call select with Partitioner.LINEAR
    // Call select with Partitioner.BAD

    return false;
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
    
    int testPartition[] = {1, 2, 4, 3 , 9, 7};
    System.out.println(RandomPartition(testPartition, 0, 5));
    for(int k: testPartition)
    	System.out.print(k + " ");
    System.out.println();

    if (args.length > 0) {
        if (args[0].equals("--test")) {
            runRandomizedSelect = false;
            boolean testPassed = TestSelect();
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
