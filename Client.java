import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.TrayIcon.MessageType;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class Client extends JFrame {

    Socket socket;

    BufferedReader br;
    PrintWriter out;

    //*****GUI******
    //Declearing the components...

    private JLabel heading=new JLabel("Welcome to whatAp");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();
    private Font  font=new Font("Roboto",Font.BOLD,20);
    
    //constructer
    public Client(){
        try {
            System.out.println("Sending request to Server....");
            socket=new Socket("127.0.0.1",8888);
            System.out.println("Connection Done.");
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream());


            createGUI();
            handleEvents();

            startReading();
            // startWriting();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
                //  System.out.println("key relesed"+e.getKeyCode());
                if (e.getKeyCode()==10) {
                    // System.out.println("You have pressed Enter button");
                    String contentToSend=messageInput.getText();
                    messageArea.append("ME: "+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
            }
            
        });
    }


    private void createGUI()
    {
        //window title name...
        this.setTitle("CLIENT MESSANGER");
        // size of the Window
        this.setSize(600, 600);
        // creating the window into the middle
        this.setLocationRelativeTo(null);
        // this is to terminte the window by clicking on X
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

 
        //coding for the components....

        //Seting the font ...
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("pro.png"));
        //Puting the heading into the center..
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        // frame ka layout setting ...
        this.setLayout(new BorderLayout());

        //adding the components to the frame...
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jscrol=new JScrollPane(messageArea); 
        this.add(jscrol,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);





        this.setVisible(true);

    }


    public void startReading(){
        
        // THREAD -- used for reading the data...
        Runnable r1=()->
        {
        System.out.println("reader Started");

        try
        {
          while(!socket.isClosed()){
         String msg= br.readLine();
         if(msg.equals("exit"))
         {
               System.out.println("Server terminated the chat");
               JOptionPane.showMessageDialog(this, "Server Terminated the chat" );
               messageInput.setEnabled(false);
               socket.close();
               break;
         }
         //  System.out.println("Server : "+msg);

         messageArea.append("Server : "+msg+ "\n");
          }
      }catch(Exception e){
            
              // e.printStackTrace();
              System.out.println("Connection is closed");
           }
        

      };
      new Thread(r1).start();

  }
    
  public void startWriting(){
   
      // THREAD -- used for writing the data...
      System.out.println("Writer started...");
       Runnable r2=()->{
      
         try
          {
            while(!socket.isClosed()) {
                 
                BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));

                String content=br1.readLine();
                out.println(content);
                out.flush();

                if (content.equals("exit")) {
                  socket.close();
                  break;
                }
                 
              }
              } catch (Exception e) {
                  // e.printStackTrace();
                  System.out.println("Connection is closed");
              }
          };
        new Thread(r2).start();;
     
  }



    public static void main(String[] args) {
        System.out.println("This is Client....");
        new Client();

    }
}
