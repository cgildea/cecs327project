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

    public MulitServer() {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MulitServer example = new MulitServer();
        example.handleConnection();
    }

    public void handleConnection() {
        System.out.println("Waiting for client message...");

        //
        // The server do a loop here to accept all connection initiated by the
        // client application.
        //
        while (true) {
            try {
                Socket socket = server.accept();

                new ConnectionHandler(socket);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ConnectionHandler implements Runnable {
    private Socket socket;
    ReentrantLock lock = new ReentrantLock();
    static LinkedList<String> serverList = new LinkedList<String>(); // Introduce the static LinkedList

    public ConnectionHandler(Socket socket) {
        this.socket = socket;

        Thread t = new Thread(this);
        t.start();

    }

    @Override
    public void run() {
        lock.lock();
        try {
            ObjectOutputStream oos;
            try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                String message = (String) ois.readObject();
                System.out.println("Message Received: " + message);
                //message = SpellCheck(message);
//                SpellCheck spell = new SpellCheck(message);
//                System.out.println("\nMessage Spell Checked: " + spell.getMessage());

                SpellCheck spellThread = new SpellCheck(message);
                spellThread.run();

                System.out.println("\nMessage Spell Checked: " + spellThread.getMessage());
                if (spellThread.getMessage() != "0") {
                    AddToLinkedList addToList = new AddToLinkedList(message); // ADDS IT TO THE LINKED LIST
                    addToList.run();
                }

                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("Hi...");
            }
            oos.close();
            socket.close();

            System.out.println("Waiting for client message...");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();

            // Technically we don't need this here.......... =]
            Iterator<String> listIterator = serverList.iterator();
            while(listIterator.hasNext()) {
                String x = listIterator.next();
                System.out.println(x + ", ");
            }
            
        }
    }

    public static class SpellCheck implements Runnable {

        private String message;

        public SpellCheck(String message) {
            this.message = message;
            Thread t = new Thread();
            t.start();
        }

        public static String spellCheck(String w) {
            if (!w.contains("!") && !w.contains(")") && !w.contains("&") && !w.contains("@") && !w.contains("%")) {
                return w.toUpperCase();
            } else {
                return null;
            }
        }

        /**
         * @return the message
         */
        public String getMessage() {
            return spellCheck(message);
        }

        /**
         * @param message the message to set
         */
        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            if (!message.contains("!") && !message.contains(")") && !message.contains("&") && !message.contains("@") && !message.contains("%")) {
                System.out.println(message.toUpperCase());
                setMessage(message);
            } else {
                System.out.println("Nada");
                setMessage("0");
            }

        }

    }

    public static class AddToLinkedList {
        String str;
        boolean addedString = false;

        public AddToLinkedList(String str) {
            this.str = str;

            Thread t = new Thread();
            t.start();
        }

        public void run() {
            if (serverList.isEmpty())
                serverList.add(str);
            else {
                Iterator<String> listIterator = serverList.iterator();
                while (listIterator.hasNext()) {
                    String x = listIterator.next();
                    if (str.compareTo(x) <= 0) {
                        serverList.add(str);
                        addedString = true;
                        break;
                    } // end compareTo 'if' 
                    if (!addedString) {
                        serverList.add(str);
                    } // end !addedString 'if'
                    addedString = false;
                } // end while
//            serverList.add(str); // Adds it to the end of the linked list
            }
        }
    }
}

class SearchLinkedList implements Runnable {
    String searchString;
    int counter = 0;
    
    public SearchLinkedList(String searchString) {
        this.searchString = searchString;
        
        Thread t = new Thread();
        t.start();
    }
    
    public void run() {
        // In here, iterate through the linkedlist and return how many duplicates
        Iterator<String> listIterator = serverList.iterator();
        while(listIterator.hasNext()) {
            String x = listIterator.next();
            if(searchString.equals(x)) {
                counter++;                
            }
        }
    }
}