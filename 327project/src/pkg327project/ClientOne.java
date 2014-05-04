/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg327project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;

public class ClientOne {

    public static void main(String[] args) {
        ClientOneRun threads[] = new ClientOneRun[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new ClientOneRun();
            threads[i].run();
        }
    }

    public static class ClientOneRun implements Runnable {

        public ClientOneRun() {
            Thread t = new Thread();
            t.start();
        }
        
        @Override
        public void run() {
            try {
                String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!)&@%";
                String word = "";
                SecureRandom random = new SecureRandom();
                boolean added = false;
                //
                // Create a connection to the server socket on the server application
                //
                
                /*THIS DOESN'T WORK FOR SOME REASON!!!*/
////                InetAddress host = InetAddress.getLocalHost();
//                Socket socket = new Socket("75.142.123.113", 7777);

                InetAddress host = InetAddress.getLocalHost();
                Socket socket = new Socket(host, 7777);
                
                //
                // Send a message to the client application
                //
                int wordLength = random.nextInt(10) + 1;
                for (int j = 0; j < wordLength; j++) {
                    word += allowedChars.charAt(random.nextInt(allowedChars.length()));
                }
                System.out.println("OUT OF FOR LOOP WORD: " + word);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(word);

                //
                // Read and display the response message sent by server application
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

//
//import java.io.DataInputStream;
//import java.io.PrintStream;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.IOException;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.security.SecureRandom;
//
//public class ClientOne implements Runnable {
//
//    // The client socket
//    private static Socket clientSocket = null;
//    // The output stream
//    private static PrintStream os = null;
//    // The input stream
//    private static DataInputStream is = null;
//
//    private static BufferedReader inputLine = null;
//    private static boolean closed = false;
//
//    public static void main(String[] args) {
//
//        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!)&@%";
//        String word = "";
//        SecureRandom random = new SecureRandom();
//        boolean added = false;
//        // The default port.
//        int portNumber = 2222;
//        // The default host.
//        String host = "localhost";
//
//        if (args.length < 2) {
//            System.out
//                    .println("Usage: java MultiThreadChatClient <host> <portNumber>\n"
//                            + "Now using host=" + host + ", portNumber=" + portNumber);
//        } else {
//            host = args[0];
//            portNumber = Integer.valueOf(args[1]).intValue();
//        }
//
//        /*
//         * Open a socket on a given host and port. Open input and output streams.
//         */
//        try {
//            clientSocket = new Socket(host, portNumber);
//            inputLine = new BufferedReader(new InputStreamReader(System.in));
//            os = new PrintStream(clientSocket.getOutputStream());
//            is = new DataInputStream(clientSocket.getInputStream());
//        } catch (UnknownHostException e) {
//            System.err.println("Don't know about host " + host);
//        } catch (IOException e) {
//            System.err.println("Couldn't get I/O for the connection to the host "
//                    + host);
//        }
//
//        /*
//         * If everything has been initialized then we want to write some data to the
//         * socket we have opened a connection to on the port portNumber.
//         */
//        if (clientSocket != null && os != null && is != null) {
//            try {
//
//                /* Create a thread to read from the server. */
//                new Thread(new ClientOne()).start();
//                while (!closed) {
//                    int wordLength = random.nextInt(10) + 1;
//                    for (int j = 0; j < wordLength; j++) {
//                        word += allowedChars.charAt(random.nextInt(allowedChars.length()));
//                    }
//                    os.println(inputLine.readLine().trim());
//                }
//                /*
//                 * Close the output stream, close the input stream, close the socket.
//                 */
//                os.close();
//                is.close();
//                clientSocket.close();
//            } catch (IOException e) {
//                System.err.println("IOException:  " + e);
//            }
//        }
//    }
//
//    /*
//     * Create a thread to read from the server. (non-Javadoc)
//     * 
//     * @see java.lang.Runnable#run()
//     */
//    public void run() {
//
//        /*
//         * Keep on reading from the socket till we receive "Bye" from the
//         * server. Once we received that then we want to break.
//         */
//        String responseLine;
//        try {
//            while ((responseLine = is.readLine()) != null) {
//                System.out.println(responseLine);
//                if (responseLine.indexOf("*** Bye") != -1) {
//                    break;
//                }
//            }
//            closed = true;
//        } catch (IOException e) {
//            System.err.println("IOException:  " + e);
//        }
//    }
//}
