import java.util.Scanner;

/**
 * Created by Hannah Pedersen on 4/20/17.
 */
public class process {
    private int processNum;
    private double probabilityA;
    private double probabilityB;
    private double probabilityC;

    private int currentReferenceWord;
    private int currentReferenceNum = 1;

    private int numPageFaults = 0;
    private int numEvictions = 0;
    private int totalResidencyTime = 0;

    private boolean isFinished = false;


    public process(int processNum, double A, double B, double C, int currentReferenceWord){
        this.processNum = processNum;
        this.probabilityA = A;
        this.probabilityB = B;
        this.probabilityC = C;

        //currentReferenceWord is 111*k mod S
        this.currentReferenceWord = currentReferenceWord;

    }

    public int getProcessNum() {
        return processNum;
    }

    public void setProcessNum(int processNum) {
        this.processNum = processNum;
    }

    public double getProbabilityA() {
        return probabilityA;
    }

    public void setProbabilityA(double probabilityA) {
        this.probabilityA = probabilityA;
    }

    public double getProbabilityB() {
        return probabilityB;
    }

    public void setProbabilityB(double probabilityB) {
        this.probabilityB = probabilityB;
    }

    public double getProbabilityC() {
        return probabilityC;
    }

    public void setProbabilityC(double probabilityC) {
        this.probabilityC = probabilityC;
    }

    public int getNumPageFaults() {
        return numPageFaults;
    }

    public void setNumPageFaults(int numPageFaults) {
        this.numPageFaults = numPageFaults;
    }

    public int getTotalResidencyTime() {
        return totalResidencyTime;
    }

    public void setTotalResidencyTime(int totalResidencyTime) {
        this.totalResidencyTime = totalResidencyTime;
    }

    public int getCurrentReferenceWord() {
        return currentReferenceWord;
    }

    public void setCurrentReferenceWord(int currentWord) {
        this.currentReferenceWord = currentWord;
    }

    public void setNextCurrentReferenceWord(Scanner randomNumScanner, int ProccessSize, int numReferencesPerProcess){
        double probabilityA = getProbabilityA();
        double probabilityB = getProbabilityB();
        double probabilityC = getProbabilityC();

        double y = randomNumScanner.nextInt()/(Integer.MAX_VALUE + 1d);

        int currentReferenceWord = getCurrentReferenceWord();

        //case 1 with probability A
        if(y < probabilityA){
            int newReferenceWord = (currentReferenceWord + 1) % ProccessSize;
            setCurrentReferenceWord(newReferenceWord);
        }
        //case 2 with probability B
        else if(y < (probabilityA + probabilityB)){
            int newReferenceWord = (currentReferenceWord - 5 + ProccessSize) % ProccessSize;
            setCurrentReferenceWord(newReferenceWord);
        }
        //case 3 with probability C
        else if(y < (probabilityA + probabilityB + probabilityC)){
            int newReferenceWord = (currentReferenceWord + 4) % ProccessSize;
            setCurrentReferenceWord(newReferenceWord);
        }
        //case 4, with probability 1-A-B-C
        else if (y >= (probabilityA + probabilityB + probabilityC)){ //
            int secondRandom = randomNumScanner.nextInt();
            setCurrentReferenceWord(secondRandom % ProccessSize);

        }else{
            System.out.println("error");
        }

        int newReferenceNum = getCurrentReferenceNum()+1;
        if(newReferenceNum > numReferencesPerProcess){
            isFinished = true;
        }

        setCurrentReferenceNum(newReferenceNum);

    }

    public int getCurrentReferenceNum() {
        return currentReferenceNum;
    }

    public void setCurrentReferenceNum(int currentReferenceNum) {
        this.currentReferenceNum = currentReferenceNum;
    }

    public int getCurrentPage(int pageSize){
        return (getCurrentReferenceWord() / pageSize);
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getNumEvictions() {
        return numEvictions;
    }

    public void setNumEvictions(int numEvictions) {
        this.numEvictions = numEvictions;
    }
}
