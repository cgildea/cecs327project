/**
 * @author Cody Gildea <cgildea@gmail.com>
 * @autoor Patrick Khensovan <patrick@patrickkhensovan.com>
 * @author Eddie Zamora <edzamora22@yahoo.com>
 * CECS 327, Spring 2014
 * Assignment 8
 * Objective: Network computers to communicate in a client-server-type fashion.
 *   This program accepts connections from the two clients and spawns off in 
 *   different threads depending on the message it receives. It will first check
 *   the spelling of the word & add it to the linked list, or search the linked
 *   list for the client and give a response.
 * file: MulitServer.java
 */
// Comment out the next line if not using NetBeans
package pkg327project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import static pkg327project.ConnectionHandler.serverList;

public class MulitServer {

    private ServerSocket server;
    private int port = 7777;

    /**
     * Constructor for our MulitServer. Tries to connect to the port in mind.
     */
    public MulitServer() {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Our main method that initiates our mulitserver
     */
    public static void main(String[] args) {
        MulitServer theMulitServer = new MulitServer();
        theMulitServer.handleConnection();
    }

    /**
     * Here we will actually wait for the connections from our clients
     */
    public void handleConnection() {
        System.out.println("Waiting for client message...");

        // The server loops here to accept all connections created by the clients.
        while (true) {
            try {
                Socket socket = server.accept(); // accept the connection
                new ConnectionHandler(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * ConnectionHandler does all the routing for the two different clients.
 */
class ConnectionHandler implements Runnable {
    private Socket socket;
    ReentrantLock lock = new ReentrantLock();

    // Introduce the static LinkedList
    static LinkedList<String> serverList = new LinkedList<String>();

    /**
     * ConnectionHandler constructor initiates the handler and starts our thread.
     * @param socket is the socket to be set.
     */
    public ConnectionHandler(Socket socket) {
        this.socket = socket;
        // Runs our client thread
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * The server takes in the message from the client and decides which thread
     * to send it to. 
     */
    @Override
    public void run() {
        // Locks down our thread so our list doesn't get corrupted
        lock.lock();
        try {
            ObjectOutputStream oos;
            
            // Tries to make sure that our streams are working
            try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                // Receives the message from the user before 
                String message = (String) ois.readObject();
                System.out.println("Message Received: " + message);

                // This splits our message and sends it to the appropriate thread
                String[] splitted = message.split("\\s+");

                // This does the spell check. If words are correct or misspelled, then they will be added to the linked list
                if (splitted[1].equalsIgnoreCase("CLIENTONE")) {
                    SpellCheck spellThread = new SpellCheck(splitted[0]);
                    spellThread.run();

                    // Our check for the message from the therad. Runs the thread that adds it to the linked list
                    if ((spellThread.getMessage().equals("Correct") || spellThread.getMessage().equals("Misspelled"))) {
                        AddToLinkedList addToList = new AddToLinkedList(spellThread.getWord());
                        addToList.run();
                    }
                    // Responds to the client with the spell-checked message
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(splitted[0] + " received from ClientOne, word is " + spellThread.getMessage());

                    // Closes the stream & socket
                    oos.close();
                    socket.close();
                } 
                // This searches the linked list for instances of the word sent from client two
                else if (splitted[1].equalsIgnoreCase("CLIENTTWO")) {
                    SearchLinkedList searchList = new SearchLinkedList(splitted[0]);
                    searchList.run();
                    
                    // Responds to the client with the number of times the word is found
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(splitted[0] + " received from ClientTwo, found it " + searchList.getCounter() + " times in list");

                    // Closes the stream & socket
                    oos.close();
                    socket.close();
                } else {
                    System.out.println("CONNECTION HANDLER ERROR");
                }
            }
            System.out.println("Waiting for client message...");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * The SpellCheck thread simply checks to make sure our randomly created word
     * is valid. If it is all upper-case, it is correct. If the word contains any
     * lower-case characters, it is misspelled. However, if there are any symbols
     * then it is considered invalid.
     */
    public static class SpellCheck implements Runnable {
        private String word;
        private String message;

        /**
         * SpellCheck constructor that runs the thread.
         * @param word is the word to be set
         */
        public SpellCheck(String word) {
            this.word = word;
            Thread t = new Thread();
            t.start();
        }

        /**
         * Checks the word and sets the message to be sent back to the server.
         */
        @Override
        public void run() {
            if (!word.contains("!") && !word.contains(")") && !word.contains("&") && !word.contains("@") && !word.contains("%")) {
                System.out.println(getWord().toUpperCase()); // NO.
                if (getWord().equals(getWord().toUpperCase())) //
                {
                    setMessage("Correct");
                } else {
                    setMessage("Misspelled");
                }
                // If it's valid, we will keep the word and set it to be added to our linked list
                setWord(word.toUpperCase());
            } else {
                setMessage("Invalid");
            }
        }

        /**
         * @return the word
         */
        public String getWord() {
            return word;
        }

        /**
         * @param word the word to set
         */
        public void setWord(String word) {
            this.word = word;
        }

        /**
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @param message the message to set
         */
        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * The thread that adds our word to our linked list. Our linked list is sorted
     * alphabetically for easier duplicates search time.
     */
    public static class AddToLinkedList {
        String word;
        boolean added;

        /**
         * AddToLinkedList constructor that runs the thread.
         * @param str is the word to be set
         */
        public AddToLinkedList(String word) {
            this.word = word;

            Thread t = new Thread();
            t.start();
        }

        /**
         * Adds the word to the linked list alphabetically.
         */
        public void run() {
            // If we have an empty list, then add it to the front of the list
            if (getLinkedList().isEmpty()) {
                getLinkedList().add(word);
            } else {
                // Traverses through the entire linked list.
                for (int k = 0; k < getLinkedList().size(); k++) {
                    // We compare letters to see where we could insert without having to traverse through the whole thing
                    if (word.compareTo(getLinkedList().get(k)) <= 0) {
                        getLinkedList().add(k, word);
                        added = true;
                        break; // break out of the loop to save us time & avoid unnecessary duplicates
                    }
                }
                if (!added) {
                    getLinkedList().add(word);
                }
                added = false;
            }
        }

        /**
         * @return the linkedList
         */
        public LinkedList<String> getLinkedList() {
            return serverList;
        }
    }

    /**
     * The thread that searches our linked list.
     */    
    public static class SearchLinkedList implements Runnable {
        String searchString;
        private int counter = 0;

        /**
         * SearchLinkedList constructor that runs the thread.
         * @param searchString is the word to be searched
         */
        public SearchLinkedList(String searchString) {
            this.searchString = searchString;

            Thread t = new Thread();
            t.start();
        }

        /**
         * Iterates through the linked list and increments our counter every time
         * a duplicate entry is found.
         */
        public void run() {
            Iterator<String> listIterator = serverList.iterator();
            while (listIterator.hasNext()) {
                String x = listIterator.next();
                if (searchString.equals(x)) { // we have found a match
                    setCounter(getCounter() + 1);
                }
            }
        }

        /**
         * @return the counter
         */
        public int getCounter() {
            return counter;
        }

        /**
         * @param counter the counter to set
         */
        public void setCounter(int counter) {
            this.counter = counter;
        }
    }
}