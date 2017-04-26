import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Hannah Pedersen on 4/20/17.
 */
public class demandPaging {

    public final int QUANTUM = 3;
    public int totalEvictions = 0;
    public int currentTime = 1;

    public int machineSize;
    public int pageSize;
    public int processSize; //references to addresses are from 0 up until processSize-1
    public int numReferencesPerProcess;
    public int jobType; //either 1, 2, 3, or 4

    public int totalNumPages;
    public String replacementType;
    public ArrayList<process> processes;
    public frameTable frameTable;

    /**
     * Constructor to make a demandPaging simulator
     *
     * @param machineSize -> M
     * @param pageSize -> P
     * @param processSize -> S
     * @param numReferencesPerProcess -> N
     * @param jobType -> J
     * @param totalNumPages -> calculated by M/P
     * @param replacementType -> R
     */
    public demandPaging(int machineSize, int pageSize, int processSize, int numReferencesPerProcess, int jobType, int totalNumPages,
                        String replacementType){
        //set all args inputs
        this.machineSize = machineSize;
        this.pageSize = pageSize;
        this.processSize = processSize;
        this.numReferencesPerProcess = numReferencesPerProcess;
        this.jobType = jobType;
        this.totalNumPages = totalNumPages;
        this.replacementType = replacementType;

        //initialize processes and frameTable
        processes = new ArrayList<process>();
        frameTable = new frameTable(totalNumPages, totalNumPages);

        //fill processes array according to jobMix
        getJobMixAndMakeProcesses();


    }

    /**
     * Runs the simulation of demand paging. Uses round robin with quantum = 3 so for each process, 3 words can be
     * referenced. Checks for hits/misses and checks for full frame table. Calls helper methods based on outcome.
     */
    public void doSimulation(){

        File randomNumFile = new File("../src/random_nums.txt");

        try (Scanner inFile = new Scanner(randomNumFile)) {

            //
            while(!allProcessesDone()){

                //each process
                for(int i=0; i< processes.size(); i++){

                    //processes are numbered starting at 1, although they're indexed starting at 0 within the process array
                    process currentProcess = processes.get(i);
                    int processNum = currentProcess.getProcessNum();


                    for(int q=0; q<QUANTUM; q++){

                        if(currentProcess.isFinished()){
                            break;
                        }

                        int frameTableIndex = frameTable.checkForHit(processNum, currentProcess.getCurrentPage(pageSize));

                        if(frameTableIndex != -1){ //hit!

                            frameTableElement hitFrame = frameTable.getElement(frameTableIndex);
                            hitFrame.setLoaded(true);
                            hitFrame.setLastTimeReferenced(currentTime);


                        }else{ //miss

                            currentProcess.setNumPageFaults(currentProcess.getNumPageFaults() + 1);

                            if(frameTable.isFull()){ //need to evict an element
                                totalEvictions++;
                                frameTableElement evictedElement = null;

                                if(replacementType.equalsIgnoreCase("random")){
                                     evictedElement = findVictimRandom(inFile.nextInt());
                                }else{
                                     evictedElement = evictPage();
                                }
                                if(evictedElement == null){
                                    System.out.println("ERROR FINDING VICTIM!");
                                }


                                int correspondingProcessNum = evictedElement.getProcessNum();
                                int correspondingFrameIndex = evictedElement.getIndexInFrameTable();
                                int correspondingProcessIndex = correspondingProcessNum - 1;

                                //average residency time is said to be the time that the page was evicted minus the time it was loaded
                                processes.get(correspondingProcessIndex).setTotalResidencyTime(processes.get(correspondingProcessIndex).getTotalResidencyTime()
                                    + (currentTime - evictedElement.getTimeLoaded()));

                                //increment number of evictions
                                processes.get(correspondingProcessIndex).setNumEvictions(processes.get(correspondingProcessIndex).getNumEvictions() + 1);

                                //set new element
                                frameTableElement newElement = new frameTableElement(processNum, currentProcess.getCurrentPage(pageSize), currentTime, correspondingFrameIndex, true);
                                frameTable.setElement(correspondingFrameIndex, newElement);

                            }else{
                                //place the page in the highest numbered free frame based on lab instructions
                                frameTableElement highestFreeFrame = null;
                                int freeFrameIndex = -1;
                                for(int frame = frameTable.getSize()- 1; frame>=0; frame--){
                                    if(!frameTable.getElement(frame).isActive()){
                                        highestFreeFrame = frameTable.getElement(frame);
                                        freeFrameIndex = frame;
                                        break;
                                    }
                                }
                                if(highestFreeFrame == null || freeFrameIndex == -1){
                                    System.out.println("error finding free frame");
                                }
                                frameTableElement newElement = new frameTableElement(processNum, currentProcess.getCurrentPage(pageSize), currentTime, freeFrameIndex, true );
                                frameTable.setElement(freeFrameIndex, newElement);
                            }

                        }

                        //calculate next current reference word for next quantum turn
                        processes.get(i).setNextCurrentReferenceWord(inFile, processSize, numReferencesPerProcess);

                        currentTime++;
                    }


                }

            }//end of simulation
            printFinishInfo();

        }
        catch (Exception e){
            System.out.println(e);
        }

    }

    /**
     * @return true or false depending on if all processes are marked as done
     */
    public boolean allProcessesDone(){
        for(process p: processes){
            if(!p.isFinished()){
                return false;

            }
        }
        return true;
    }

    /**
     * prints ending information
     */
    public void printFinishInfo(){
        int totalNumFaults = 0;
        double totalResidencyTime = 0;

        for(process p: processes){
            System.out.printf("process %d - page faults: %d ", p.getProcessNum(), p.getNumPageFaults());
            totalNumFaults += p.getNumPageFaults();
            if(p.getNumEvictions() == 0){
                System.out.println("    With no evictions, the average residence is undefined.");
                continue;
            }
            double averageResidency = (double) p.getTotalResidencyTime() / p.getNumEvictions();
            System.out.println("average residency : " + averageResidency);
            totalResidencyTime += p.getTotalResidencyTime();

        }
        System.out.print("TOTALS - page faults: " + totalNumFaults);
        if(totalResidencyTime != 0){
            double averageTotal = (double)totalResidencyTime / totalEvictions;
            System.out.println(", average residency : " + averageTotal);

        }else{
            System.out.println("    No evictions, so no average residency time");
        }


    }

    /**
     * helper function to choose which eviction method should be called (excluding random)
     * @return a frameTableElement
     */
    public frameTableElement evictPage(){


        if(replacementType.equalsIgnoreCase("FIFO")){

            return findVictimFIFO();
        }
        else{

            return findVictimLRU();
        }

    }


    /**
     * finds the FTE that was loaded first
     * @return the frameTableElement to be evicted
     */
    public frameTableElement findVictimFIFO(){
        int smallestLoadTime = Integer.MAX_VALUE;
        frameTableElement correspondingElement = null;

        for(int i=0; i< frameTable.getSize(); i++){
            frameTableElement currElement = frameTable.getElement(i);
            if(currElement.getTimeLoaded() < smallestLoadTime){
                smallestLoadTime = currElement.getTimeLoaded();
                correspondingElement = currElement;
            }
        }

        return correspondingElement;

    }

    /**
     * finds a random FTE to evict
     * @return the frameTableElement to be evicted
     */
    public frameTableElement findVictimRandom(int randomNum){
        return frameTable.getElement(randomNum % totalNumPages);

    }
    /**
     * finds the FTE that was references longest ago
     * @return the frameTableElement to be evicted
     */
    public frameTableElement findVictimLRU(){
        int smallestLastReferenceTime = Integer.MAX_VALUE;
        frameTableElement correspondingElement = null;

        for(int i=0; i< frameTable.getSize(); i++){
            frameTableElement currElement = frameTable.getElement(i);
            if(currElement.getLastTimeReferenced() < smallestLastReferenceTime){
                smallestLastReferenceTime = currElement.getLastTimeReferenced();
                correspondingElement = currElement;
            }
        }

        return correspondingElement;
    }
    /**
     * looks at the job type and produces processes based on the job mix
     * by lab definition, the currentWord of the process starts as (111*proccessNum) % processSize
     */
    public void getJobMixAndMakeProcesses(){
        if(jobType == 1){
            //type 1: there is only one process, with A=1 and B=C=0
            //since there is only one process, no the currentWord is 111%processSize
            processes.add(new process(1, 1, 0, 0, 111 % processSize));

        }else if(jobType ==2){
            //type 2: there are four processes, each with A=1, B=C=0
            for(int i=1; i<5; i++){
                processes.add(new process(i, 1, 0, 0, (111*i) % processSize));
            }


        }else if(jobType ==3){
            //type 3: there are four processes, each with A=B=C=0
            for(int i=1; i<5; i++){
                processes.add(new process(i,0, 0, 0, (111*i) % processSize));
            }

        }else{
            System.out.println("job type 4!!");
            //process 1 with A=0.75, B=0.25, C=0
            processes.add(new process(1, 0.75, 0.25, 0, (111) % processSize));
            //process 2 with A=0.75, B= 0, C=0.25
            processes.add(new process(2, 0.75, 0, 0.25, (111*2) % processSize));
            //process 3 with A=0.75, B=0.125, C=0.125
            processes.add(new process(3, 0.75, 0.125, 0.125, (111*3) % processSize));
            //process 4 with A=0.5, B=0.125, C=0.125
            processes.add(new process(4, 0.5, 0.125, 0.125, (111*4) % processSize));

        }

    }


    public static void main(String[]args){
        //split up the 6 command line arguments
        int machineSize = Integer.parseInt(args[0]);
        int pageSize = Integer.parseInt(args[1]);
        int processSize = Integer.parseInt(args[2]);
        int jobType = Integer.parseInt(args[3]);
        int numReferencesPerProcess = Integer.parseInt(args[4]);
        String replacementType = args[5];

        int totalNumPages = (int) Math.ceil((double)machineSize/pageSize);

        demandPaging demandPagingTest = new demandPaging(machineSize, pageSize, processSize, numReferencesPerProcess, jobType, totalNumPages, replacementType);
        demandPagingTest.doSimulation();



    }

}
