package harenet;

public class ByteStatement extends Statement {
    private byte[] value;
	
    public ByteStatement(byte[] byteValue) {
        value = byteValue;
    }

    protected void printHeaderContents() {
        System.out.println("| Dumping bytes, length: " + (value.length * 8) + " (" + value.length + " byte(s))");
    }

    public void printBody() {
        int count = 0;
        for (int j = 0; j < value.length; j++) {

            byte v = value[j];

            for (int i = 0; i < Byte.SIZE; i++) {
                printBit(((v >> i) & 1) == 1);
            }

            count = countProcess(count);

        }
    }
}
