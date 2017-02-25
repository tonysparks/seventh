/*
 * see license.txt 
 */
package seventh.shared;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;




/**
 * @author Tony
 *
 */
public class ConsoleFrame extends JFrame implements Logger {

    /**
     * SUID
     */
    private static final long serialVersionUID = 4136313198405978268L;

    private static final int NUMBER_OF_COLUMNS = 50;
    private static final int NUMBER_OF_ROWS = 40;
    
    static {
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());        
        } catch(Exception e) {}
    }
    
    private String welcomeBanner;
    private Console console;
    private JTextArea textArea;
    private JTextField textField;
    private Stack<String> cmdHistory;
    private int cmdHistoryIndex;
    
    /**
     * @param title
     * @param welcomeBanner
     * @param console
     */
    public ConsoleFrame(String title, String welcomeBanner, Console console) {
        this.welcomeBanner = welcomeBanner;
        this.console = console;
        this.console.addLogger(this);
        this.console.addCommand(new Command("clear"){
            /* (non-Javadoc)
             * @see shared.Command#execute(shared.Console, java.lang.String[])
             */
            @Override
            public void execute(Console console, String... args) {
                textArea.setText("");
            }
        });
        this.console.addCommand(new Command("echo") {
            /* (non-Javadoc)
             * @see shared.Command#execute(shared.Console, java.lang.String[])
             */
            @Override
            public void execute(Console console, String... args) {
                for(int i = 0; i < args.length; i++) {
                    if(i>0) console.print(" ");
                    console.print(args[i]);
                }
                console.print("\n");
            }
        });
        
        this.cmdHistory = new Stack<String>();
        this.cmdHistory.add("");
        
        this.cmdHistoryIndex = 0;
        
        setLocation(200, 300);
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        JPanel contentPane = new JPanel(new BorderLayout());

//        contentPane.setBorder(BorderLayout.);
        this.setJMenuBar(setupMenu());        
        this.textArea = setupTextArea(contentPane);                
        this.textField = setupTextField(contentPane, textArea);                
        setContentPane(contentPane);
        
        
        
        pack();
        setVisible(true);
        
        // enable TAB completion
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, new HashSet<AWTKeyStroke>());

    }
    
    /*
     * (non-Javadoc)
     * @see shared.Logger#println(java.lang.Object)
     */
    public void println(Object message) {
        this.textArea.append(message.toString() + "\n");
        this.textArea.select(this.textArea.getText().length(), this.textArea.getText().length());
    }
    
    /*
     * (non-Javadoc)
     * @see shared.Logger#printf(java.lang.Object, java.lang.Object[])
     */
    public void printf(Object message, Object ... args) {
        println(String.format(message.toString(), args));
    }
    
    /* (non-Javadoc)
     * @see shared.Logger#print(java.lang.Object)
     */
    public void print(Object msg) {
        this.textArea.append(msg.toString());
        this.textArea.select(this.textArea.getText().length(), this.textArea.getText().length());
    }
    
    private JMenuBar setupMenu() {
        JMenuBar menubar = new JMenuBar();
        
        JMenu file = new JMenu("File");
        
        JMenuItem exit = new JMenuItem("Exit", KeyEvent.VK_E);
        exit.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        file.add(exit);
        menubar.add(file);
        
        JMenu help = new JMenu("Help");
        
        JMenuItem about = new JMenuItem("About", KeyEvent.VK_A);
        exit.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
        help.add(about);
        menubar.add(help);                
        
        return menubar;
    }
    
    /**
     * @return the text area
     */
    private JTextArea setupTextArea(JPanel contentPane) {
        JPanel textAreaPanel = new JPanel(new BorderLayout());
        
        JTextArea text = new JTextArea(NUMBER_OF_ROWS, NUMBER_OF_COLUMNS);
        text.setText(this.welcomeBanner);
        
        text.setFont(new Font("Courier New", Font.PLAIN, 12));
        text.setLineWrap(true);
        text.setWrapStyleWord(true);        
        text.setEditable(false);
                
        text.setBackground(Color.BLUE);
        text.setForeground(Color.YELLOW);
        
        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);        
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        textAreaPanel.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(textAreaPanel);
        
        return text;
    }
    
    /**
     * @param contentPane
     * @param textArea
     * @return the input field area
     */
    private JTextField setupTextField(JPanel contentPane, final JTextArea textArea) {
        JPanel textInput = new JPanel(new BorderLayout());
                
        final JTextField textField = new JTextField(NUMBER_OF_COLUMNS);
        textField.requestFocus();
        
        textField.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                executeCommand();
            }
        });
        
        textField.getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke("TAB"), new AbstractAction() {
            
            /**
             */
            private static final long serialVersionUID = -5702217390878105195L;

            public void actionPerformed(ActionEvent e) {            
                List<String> cmdNames = console.find(textField.getText());
                if(cmdNames.isEmpty()) {
                    // do nothing
                }
                else if(cmdNames.size() == 1) {
                    textField.setText(cmdNames.get(0) + " ");
                }
                else {
                    console.println("");
                    
                    for(String cmd : cmdNames) {
                        console.println(cmd);
                    }
                    console.println("");
                }
                                            
            }
        });
        
        textField.getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke("UP"), new AbstractAction() {
            
            /**
             */
            private static final long serialVersionUID = -5702217390878105195L;

            public void actionPerformed(ActionEvent e) {            
                cmdHistoryIndex++;
                if(cmdHistoryIndex>=cmdHistory.size()) {
                    cmdHistoryIndex=0;
                }
                
                if(!cmdHistory.isEmpty()) {
                    textField.setText(cmdHistory.get(cmdHistoryIndex));
                }
            }
        });
        
        textField.getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke("DOWN"), new AbstractAction() {
            
            /**
             */
            private static final long serialVersionUID = -5702217390878105195L;

            public void actionPerformed(ActionEvent e) {            
                cmdHistoryIndex--;
                if(cmdHistoryIndex<0) {
                    cmdHistoryIndex=cmdHistory.size()-1;;
                }
                
                if(!cmdHistory.isEmpty()) {
                    textField.setText(cmdHistory.get(cmdHistoryIndex));
                }
            }
        });
        
        
        textInput.add(textField, BorderLayout.CENTER);
                
        final JButton sendBtn = new JButton("Send");
        sendBtn.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                executeCommand();
            }
        });
        textInput.add(sendBtn, BorderLayout.EAST);
        
        contentPane.add(textInput, BorderLayout.PAGE_END);
        return textField;
    }
    
    protected void executeCommand() {
        String text = textField.getText();
        //textArea.append(text + "\n");    
        textField.setText("");
        
        console.execute(text);
        this.cmdHistory.add(text);
        this.cmdHistoryIndex = 0;
    }
}
