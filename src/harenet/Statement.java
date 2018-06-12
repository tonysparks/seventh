package harenet;

public abstract class Statement {
    public void print() {
        printHeader();
        printBody();
        printFooter();
    }
    
    public void printHeader() {
        printLine();
        printHeaderContents();
        printLine();
    }
    
    public abstract void printBody();

    public void printFooter() {
        System.out.println();
        printLine();
    }

    private void printLine() {
        System.out.println("+--------------- ------------- ------- ------ --- -- -- - -- -- --");
    }

    protected abstract void printHeaderContents();
    
    protected void printBit(boolean isOne) {
        if (isOne) {
            System.out.print("1");
        }
        else {
            System.out.print("0");
        }
    }
    
    protected int countProcess(int count) {
        int tempCount = count;
        System.out.print(" ");
        tempCount++;
        if (tempCount == 12) {
            System.out.println();
            tempCount = 0;
        }
        return tempCount;
    }
}
