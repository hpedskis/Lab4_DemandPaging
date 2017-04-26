import java.util.ArrayList;

/**
 * Created by Hannah Pedersen on 4/23/17.
 */
public class frameTable {

    public ArrayList<frameTableElement> frameTable;
    public int capacity;

    public frameTable(int numProcesses, int capacity){
        this.capacity = capacity;
        frameTable = new ArrayList<frameTableElement>();

        //initialize frameTable with empty frameTableElements
        for(int i=0; i< numProcesses; i++){
            frameTable.add(new frameTableElement(-1, -1, -1, i, false));


        }
    }

    /**
     * @param processNum the process that is currently referencing a word
     * @param pageNum the page that the process is referencing
     * @return the frameTable index or -1 if there's no hit
     */
    public int checkForHit(int processNum, int pageNum){
        for(int i=0; i< frameTable.size(); i++){

            frameTableElement currElement = frameTable.get(i);
            if(currElement.isActive() && currElement.getProcessNum() == processNum && currElement.getPageNum() == pageNum){
                return i;
            }
        }

        return -1;

    }

    public frameTableElement getElement(int index){
        return frameTable.get(index);
    }

    public void setElement(int index, frameTableElement element){
        frameTable.set(index, element);
    }

    public boolean isFull(){
        //System.out.println("size is " + frameTable.size());
        //System.out.println("capacity is " + capacity);
        int activePages = 0;
        for(frameTableElement fte: frameTable){
            if(fte.isActive()){
                activePages++;
            }
        }
        if(activePages == capacity){
            return true;
        }else{
            return false;
        }

    }

    public int getSize(){
        return frameTable.size();
    }
}
