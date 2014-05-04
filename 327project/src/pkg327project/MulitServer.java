package pkg327project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulitServer {

    private ServerSocket server1;
    private ServerSocket server2;
    private int port1 = 7777;
    private int port2 = 4444;

    public MulitServer() {
        try {
            server1 = new ServerSocket(port1);
            server2 = new ServerSocket(port2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        MulitServer example = new MulitServer();
        example.handleConnection();
    }

    public void handleConnection() throws ClassNotFoundException {
        System.out.println("Waiting for client message...");

        //
        // The server do a loop here to accept all connection initiated by the
        // client application.
        //
        
        
        
        while (true) {
            try {
   //                Socket socket = server.accept();
   //
   //                new TestConnection(socket);
                if(port1 == 7777) {
                   System.out.println("Port is 7777");

                   Socket socket = server1.accept();
                   new ConnectionHandler(socket);
   //           THE BOTTOM WON'T WORK.
//                   ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//                   String input = (String) ois.readObject();
//                   System.out.println(input+"##################");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }   

            try {
                if (port2 == 4444) {
                    System.out.println("Port is NOT 7777");

                    Socket socket = server2.accept();                
    //                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
    //                String input = (String) ois.readObject();
    //                System.out.println(input+"##################");
                    new ConnectionHandler(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }            
        }
    }


    public static class TestConnection implements Runnable {
        Socket socket;
        ReentrantLock lock = new ReentrantLock();
        
        public TestConnection(Socket socket) {
            this.socket = socket;
            
            Thread t = new Thread(this);
            t.start();
        }
        
        @Override
        public void run() {
            lock.lock();
            try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
//                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String input = (String) ois.readObject();
                System.out.println(input+"##################");
//                ConnectionHandler connectionHandler = new ConnectionHandler(socket);
//                connectionHandler.startHandler();

                ois.close();
                socket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }            
        }
    }


}


class ConnectionHandler implements Runnable {

    private Socket socket;
    ReentrantLock lock = new ReentrantLock();
    LinkedList<String> serverList = new LinkedList<String>();

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
                    System.out.println("Message was null after if check");
                    AddToLinkedList addToList = new AddToLinkedList(message, serverList);
                    addToList.run();
                    System.out.println(addToList.getLinkedList());
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
        private LinkedList<String> linkedList;

        public AddToLinkedList(String str, LinkedList<String> linkedList) {
            this.str = str;
            this.linkedList = linkedList;

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
//                            added = true;
                        break;
                    }
                }
//                    if(!added)
//                    {
//                        linkedList.add(str);
//                    }
//                    added = false;
            }
            setLinkedList(linkedList);
        }

        /**
         * @return the linkedList
         */
        public LinkedList<String> getLinkedList() {
            return linkedList;
        }

        /**
         * @param linkedList the linkedList to set
         */
        public void setLinkedList(LinkedList<String> linkedList) {
            this.linkedList = linkedList;
        }

    }
}