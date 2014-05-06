/*
 * The other multi-threaded client program houses client threads that produce 
 * its own (correctly spelled) words to send to the server program to be 
 * searched, gets & outputs the reply, then terminates. 
 */
package pkg327project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;

public class ClientTwo {

    public static void main(String[] args) {
        ClientTwoRun threads[] = new ClientTwoRun[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new ClientTwoRun();
            threads[i].run();
        }
    }

    public static class ClientTwoRun implements Runnable {

        public ClientTwoRun() {
            Thread t = new Thread();
            t.start();
        }
        
        @Override
        public void run() {
            try {
                // Allow the characters to only be upper case letters
                String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                String word = "";
                SecureRandom random = new SecureRandom();
                boolean added = false;
                //
                // Create a connection to the server socket on the server application
                //
                InetAddress host = InetAddress.getLocalHost();
                Socket socket = new Socket(host.getHostName(), 4444);

                //
                // Send a message to the client application
                //
                int wordLength = random.nextInt(10) + 1;
                for (int j = 0; j < wordLength; j++) {
                    word += allowedChars.charAt(random.nextInt(allowedChars.length()));
                }
                word += " CLIENTTWO";
                System.out.println("OUT OF FOR LOOP WORD: " + word);

                // The server will get our word, look for it in the LL, then replies
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(word);

                //
                // Server should output if our word is found in LL and how many duplicates exist
                //
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String message = (String) ois.readObject();
                System.out.println("Message: " + message);

                ois.close();
                oos.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
