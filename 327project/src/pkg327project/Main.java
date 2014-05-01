/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pkg327project;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author akshayvangari
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!)&@%";
        String word = "";
        SecureRandom random = new SecureRandom();
        LinkedList<String> list = new LinkedList();
        boolean added = false;
        for(int i = 0; i < 100; i++)
        {
            int wordLength = random.nextInt(10) + 1;
            for(int j = 0; j < wordLength; j++)
            {
                word += allowedChars.charAt(random.nextInt(allowedChars.length()));
            }
            System.out.println(word);
            word = spellCheck(word);
            
            if(word != null){
                if(list.isEmpty())
                    list.add(word);
                else
                {
                    for(int k=0; k < list.size(); k++)
                    {
                        if(word.compareTo(list.get(k)) <= 0)
                        {
                            list.add(k, word);
                            added = true;
                            break;
                        }
                    }
                    if(!added)
                    {
                        list.add(word);
                    }
                    added = false;
                }
            }
            word = "";
        }
        printList(list);
    }
    
    public static String spellCheck(String w)
    {
        if(!w.contains("!") && !w.contains(")") && !w.contains("&") && !w.contains("@") && !w.contains("%"))
        {
            return w.toUpperCase();
        }
        else
        {
            return null;
        }
    }
    
    public static void printList(LinkedList<String> list)
    {
        System.out.println("LIST:");
        for(int i = 0; i<list.size(); i++)
        {
            System.out.println(list.get(i));
        }
    }
}
