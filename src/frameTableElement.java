/**
 * Created by Hannah Pedersen on 4/23/17.
 */
public class frameTableElement {

    //if element is currently loaded
    private boolean isLoaded = false;
    private boolean isModified = false;
    private boolean isActive = false;

    private int processNum;
    private int pageNum;
    //the first time the element was loaded
    private int timeLoaded;
    private int indexInFrameTable;
    private int lastTimeReferenced;

    public frameTableElement(int processNum, int pageNum, int timeLoaded, int indexInFrameTable, boolean isActive){
        this.processNum = processNum;
        this.pageNum = pageNum;
        this.timeLoaded = timeLoaded;
        this.indexInFrameTable = indexInFrameTable;
        this.isActive = isActive;

        this.lastTimeReferenced = timeLoaded;


    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getProcessNum() {
        return processNum;
    }

    public void setProcessNum(int processNum) {
        this.processNum = processNum;
    }


    public int getTimeLoaded() {
        return timeLoaded;
    }

    public void setTimeLoaded(int timeLoaded) {
        this.timeLoaded = timeLoaded;
    }

    public int getIndexInFrameTable() {
        return indexInFrameTable;
    }

    public void setIndexInFrameTable(int indexInFrameTable) {
        this.indexInFrameTable = indexInFrameTable;
    }


    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getLastTimeReferenced() {
        return lastTimeReferenced;
    }

    public void setLastTimeReferenced(int lastTimeReferenced) {
        this.lastTimeReferenced = lastTimeReferenced;
    }
}
