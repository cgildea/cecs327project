/**
 * @author Cody Gildea <cgildea@gmail.com>
 * @autoor Patrick Khensovan <patrick@patrickkhensovan.com>
 * @author Eddie Zamora <edzamora22@yahoo.com>
 * CECS 327, Spring 2014
 * Assignment 8
 * Objective: Network computers to communicate in a client-server-type fashion.
 *   This particular program sends spawns clients that generates a random word
 *   which may be added to the server's linked list if it is correct or misspelled.
 * file: ClientOne.java
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

public class ClientOne {

    /**
     * Runs the main method and spawns 50 threads
     */
    public static void main(String[] args) {
        ClientOneRun threads[] = new ClientOneRun[50];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new ClientOneRun();
            threads[i].run();
        }
    }

    /**
     * The child thread that sends words to the server which may be added.
     */
    public static class ClientOneRun implements Runnable {

        /**
         * Runs the child thread process
         */
        public ClientOneRun() {
            Thread t = new Thread();
            t.start();
        }
        
        /**
         * The client creates a string of characters that gets sent to the server.
         * If the word has all upper case letters, it is a correct word. If there
         * are any lower case letters, then it is misspelled. Any words with symbols
         * are considered invalid.
         */
        @Override
        public void run() {
            try {
                // Clearly define our ilst of allowed characters
                String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!)&@%";
                String word = "";
                SecureRandom random = new SecureRandom();

                // Create a connection to our server                
                // INSERT THE CORRECT IP IN THE STRING
//                InetAddress host = InetAddress.getLocalHost();
//                Socket socket = new Socket("xx.xxx.xx.xxx", 7777); // Usage -- new Socket("THE.IP", portNumber);

                InetAddress host = InetAddress.getLocalHost();
                Socket socket = new Socket(host, 7777);
                
                // Generates the randomly generated word
                int wordLength = random.nextInt(10) + 1;
                for (int j = 0; j < wordLength; j++) {
                    word += allowedChars.charAt(random.nextInt(allowedChars.length()));
                }
                // Appends so the server knows which client is communicating
                word += " CLIENTONE";
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(word);

                // Reads and displays the response message sent by the server
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