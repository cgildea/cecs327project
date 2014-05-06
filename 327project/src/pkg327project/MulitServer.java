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
                String[] splitted = message.split("\\s+");

                if (splitted[1].equalsIgnoreCase("CLIENTONE")) {
                    SpellCheck spellThread = new SpellCheck(splitted[0]);
                    spellThread.run();

                    if ((spellThread.getMessage().equals("Correct") || spellThread.getMessage().equals("Misspelled"))) {
                        AddToLinkedList addToList = new AddToLinkedList(spellThread.getWord());
                        addToList.run();
                    }
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(splitted[0] + " received from ClientOne" + spellThread.getMessage());

                    oos.close();
                    socket.close();

                } else if (splitted[1].equalsIgnoreCase("CLIENTTWO")) {
                    SearchLinkedList searchList = new SearchLinkedList(splitted[0]);
                    searchList.run();
                    
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(splitted[0] + " received from ClientTwo, found it " + searchList.getCounter() + " times in list");

                    oos.close();
                    socket.close();                   
                    
                } else {
                    System.out.println("CONNECTION HANDLER ERROR");
                }
                printList(serverList);
            }

            System.out.println("Waiting for client message...");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();

            // Technically we don't need this here.......... =]
            Iterator<String> listIterator = serverList.iterator();
            while (listIterator.hasNext()) {
                String x = listIterator.next();
                System.out.println(x + ", ");
            }

        }
    }

    public static class SpellCheck implements Runnable {

        private String word;
        private String message;

        public SpellCheck(String word) {
            this.word = word;
            Thread t = new Thread();
            t.start();
        }

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

    public static class AddToLinkedList {

        String str;
        boolean added;

        public AddToLinkedList(String str) {
            this.str = str;

            Thread t = new Thread();
            t.start();
        }

        public void run() {
            if (getLinkedList().isEmpty()) {
                getLinkedList().add(str);
            } else {
                for (int k = 0; k < getLinkedList().size(); k++) {
                    if (str.compareTo(getLinkedList().get(k)) <= 0) {
                        getLinkedList().add(k, str);
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    getLinkedList().add(str);
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

    public static void printList(LinkedList<String> list) {
        System.out.println("LIST:");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

}

class SearchLinkedList implements Runnable {

    String searchString;
    private int counter = 0;

    public SearchLinkedList(String searchString) {
        this.searchString = searchString;

        Thread t = new Thread();
        t.start();
    }

    public void run() {
        // In here, iterate through the linkedlist and return how many duplicates
        Iterator<String> listIterator = serverList.iterator();
        while (listIterator.hasNext()) {
            String x = listIterator.next();
            if (searchString.equals(x)) {
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