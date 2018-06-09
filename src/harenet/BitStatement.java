package harenet;

public class BitStatement extends Statement {
    private int numBits;
    private BitArray data;
	
    public BitStatement(int number,BitArray bitData) {
	    numBits = number;
	    data = bitData;
	}
	
    protected void printHeaderContents() {
        System.out.println("| Dumping bitset, length: " + numBits);
    }
    
    public void printBody() {
    	int count = 0;

        for (int i = 0; i < numBits; i++) {
            printBit(data.getBit(i));
            if ((i != 0) && (i % 8 == 7)) {
                count = countProcess(count);
            }

        }
    }
    
}
