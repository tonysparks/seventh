package test.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.shared.Broadcaster;

public class BroadcasterTest {

    /*
     * purpose : test Broadcaster function in Broadcaster class.
     * input : mtu:20, groupAddress:192.168.25.53, port:80
     * expected output : X, cause the function is only made for creation.
     */
    @Test
    public void testBroadcaster() {
        try {
            Broadcaster test = new Broadcaster(20,"192.168.25.53",80);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
        
    }

    /*
     * purpose : test BroadcastMessage function in Broadcaster class.
     * input : mtu:20, groupAddress:192.168.25.53, port:80
     * expected output : X, cause the function is only made for creation.
     */
    @Test
    public void testBroadcastMessage() {
        try {
            Broadcaster test = new Broadcaster(5,"192.168.25.53",80);            
            test.broadcastMessage("Hello");
            test.broadcastMessage("HelloHello");
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        }
        
    }

    /*
     * purpose : test close function in Broadcaster class.
     * input : mtu:20, groupAddress:192.168.25.53, port:80
     * expected output : X, cause the function is only made for close socket.
     */
    @Test
    public void testClose() {
        try {
            Broadcaster test = new Broadcaster(5,"192.168.25.53",80);
            test.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}
