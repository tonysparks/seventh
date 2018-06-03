/*
**************************************************************************************
*Myriad Engine                                                                       *
*Copyright (C) 2006-2007, 5d Studios (www.5d-Studios.com)                            *
*                                                                                    *
*This library is free software; you can redistribute it and/or                       *
*modify it under the terms of the GNU Lesser General Public                          *
*License as published by the Free Software Foundation; either                        *
*version 2.1 of the License, or (at your option) any later version.                  *
*                                                                                    *
*This library is distributed in the hope that it will be useful,                     *
*but WITHOUT ANY WARRANTY; without even the implied warranty of                      *
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU                   *
*Lesser General Public License for more details.                                     *
*                                                                                    *
*You should have received a copy of the GNU Lesser General Public                    *
*License along with this library; if not, write to the Free Software                 *
*Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA      *
**************************************************************************************
*/
package seventh.ui;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import seventh.shared.TimeStep;

/**
 * Displays informative messages to the user.  The messages are placed on 
 * a queue and displayed one message at a time.  A message is popped off the
 * queue once it expires.
 * 
 * @author Tony
 *
 */
public class MessageBoard extends Widget {

    /**
     * Messages on the board
     */
    private Queue<Message> messages;
    
    /**
     * Text Size
     */
    private float textSize;
    
    /**
     * Cache messages
     */
//    private ObjectPool<Message> msgPool;

    
    /**
     * Message
     * 
     * @author Tony
     *
     */
    public static class Message {
        
        public static final int FLICKER_EFFECT = (1 << 1);
        public static final int FADE_EFFECT    = (1 << 2);
        
        String message;
        long expire;
        int effect;        
        
        void reset(String message, long expire, int effect) {
            this.message = message;
            this.expire = expire;
            this.effect = effect;
        }

        /**
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @return the expire
         */
        public long getExpire() {
            return expire;
        }            
        
        /**
         * @return the effect
         */
        public int getEffect() {
            return effect;
        }
    }
    
    /**
     * Size of the message board
     * @param messageBoardSize
     */
    public MessageBoard(int messageBoardSize) {
        this.messages = new LinkedList<Message>();
        this.textSize = 12.0f;
//        this.msgPool = new ObjectPool<Message>(new ObjectFactory<Message>() {            
//            public Message create() {
//                return new Message();
//            }
//        }, messageBoardSize);
        
        this.setForegroundColor(Styling.BLACK);
        
    }
    
    /**
     * Updates the message board, removes any expired messages.
     * 
     * @param timeStep
     */
    public void update(TimeStep timeStep) {
        if ( !this.messages.isEmpty() ) {
            
            // determine if the message has expired.
            Message msg = this.messages.peek();
            msg.expire -= timeStep.getDeltaTime();
            if ( msg.expire <= 0) {
                this.messages.poll();                
//                this.msgPool.destroy(msg);
            }
        
        }
    }
    
    
    
    /**
     * @return the textSize
     */
    public float getTextSize() {
        return textSize;
    }

    /**
     * @param textSize the textSize to set
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    /**
     * Clears the message board
     */
    public void clearMessages() {
//        while(!this.messages.isEmpty()) {
//            Message msg = this.messages.poll();
//            this.msgPool.destroy(msg);
//        }        

        this.messages.clear();
    }
    
    /**
     * Adds a message to the board, which will expire. 
     * 
     * @param message
     * @param expire 
     */
    public void addMessage(String message, TimeUnit expire) {
        addMessage(message, expire);
    }
    
    /**
     * Adds a message to the board, which will expire. 
     * 
     * @param message
     * @param expire
     * @param effect 
     */
    public void addMessage(String message, long expire, int effect) {        
        Message msg = new Message(); //this.msgPool.create();        
        if ( msg != null ) {
            msg.reset(message, expire, effect);
            
            this.messages.add(msg);
        }        
    }
    
    /**
     * Gets the messages
     * 
     * @return maybe null if no message is posted
     */
    public Message getCurrentMessage() {
        return this.messages.peek();
    }
    
}
