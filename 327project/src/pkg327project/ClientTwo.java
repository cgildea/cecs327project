/**
 * @author Cody Gildea <cgildea@gmail.com>
 * @autoor Patrick Khensovan <patrick@patrickkhensovan.com>
 * @author Eddie Zamora <edzamora22@yahoo.com>
 * CECS 327, Spring 2014
 * Assignment 8
 * Objective: Network computers to communicate in a client-server-type fashion.
 *   This particular program sends spawns clients that generates a random word
 *   which will be searched in the server's linked list for entries & number of
 *   duplicates that exist.
 * file: ClientTwo.java
 */
// Comment out the next line if not using NetBeans
package pkg327project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;

public class ClientTwo {

    /**
     * Runs the main method and spawns 50 threads
     */
    public static void main(String[] args) {
        ClientTwoRun threads[] = new ClientTwoRun[50];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new ClientTwoRun();
            threads[i].run();
        }
    }

    /**
     * The child thread that sends words to the server which may be added.
     */
    public static class ClientTwoRun implements Runnable {

       /**
         * Runs the child thread process
         */
        public ClientTwoRun() {
            Thread t = new Thread();
            t.start();
        }
        
        /**
         * The client creates a string of upper-case characters that gets sent 
         * to the server to be searched in the linked list. The server responds
         * with the number of times that word is found in the linked list.
         */
        @Override
        public void run() {
            try {
                // Allow the word to only include upper-case letters
                String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                String word = "";
                SecureRandom random = new SecureRandom();

                // Create a connection to our server
                // INSERT THE CORRECT IP IN THE STRING
//                InetAddress host = InetAddress.getLocalHost();
//                Socket socket = new Socket("xx.xxx.xx.xxx", 7777); // Usage -- new Socket("THE.IP", portNumber);

                InetAddress host = InetAddress.getLocalHost();
                Socket socket = new Socket(host.getHostName(), 7777);

                // Generates the randomly generated word
                int wordLength = random.nextInt(10) + 1;
                for (int j = 0; j < wordLength; j++) {
                    word += allowedChars.charAt(random.nextInt(allowedChars.length()));
                }
                // Appends so the server knows which client is communicating
                word += " CLIENTTWO";

                // Sends the word to our server to be searched in the linked list
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(word);

                // Outputs the number of times our word is found in the linked list
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String message = (String) ois.readObject();
                System.out.println("Message: " + message);

                // Closes our streams
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
