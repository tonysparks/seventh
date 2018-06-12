package harenet;

public class StatementFactory {
    public Statement getInstance(byte[] value) {
        return new ByteStatement(value);
    }
    public Statement getInstance(int numBits, BitArray data) {
        return new BitStatement(numBits,data);
    }
}