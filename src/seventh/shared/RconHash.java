/*
 * see license.txt 
 */
package seventh.shared;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Tony
 *
 */
public class RconHash {

    private long token;
    /**
     * 
     */
    public RconHash(long token) {
        this.token = token;
    }

    /**
     * @param value
     * @return the hashed version of the value
     */
    public String hash(String value) {
        String hashedValue = value;
        try {
            byte[] hv = hashImpl(value);            
            hashedValue = Base64.encodeBytes(hv); 
        }
        catch(NoSuchAlgorithmException e) {
            Cons.println("*** No hashing algorithm defined on the JVM" + e);
        }
        
        return hashedValue;
    }
    
    
    protected byte[] hashImpl(String password) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");        
        byte[] passBytes = (password + Long.toString(token)).getBytes();        
        byte[] passHash = sha256.digest(passBytes);
        return passHash;
    }
}
