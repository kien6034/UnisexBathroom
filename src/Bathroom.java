import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bathroom{
    static Semaphore bathroom;
    static Semaphore men_only;
    static Semaphore women_only;
    public static void main(String[] args){
        //import the number of men and women
        Scanner scIn = new Scanner(System.in);
        int totMen = 0;
        int totWomen = 0;
        String Line;
        System.out.print("Total number of men:");
        totMen = scIn.nextInt();
        System.out.print("Total number of women:");
        totWomen = scIn.nextInt();

        Line = Rall(totMen,totWomen);
        bathroom= new Semaphore(1);
        men_only= new Semaphore(1);
        women_only= new Semaphore(1);
        System.out.println("m"+Bathroom.men_only.availablePermits()+
                "f"+Bathroom.women_only.availablePermits()+
                "b"+Bathroom.bathroom.availablePermits());
        System.out.println("Working\t\tEntering\tIn Bathroom\tLeaving");
        System.out.println("----------------------------------------------------------");

        Thread[] men = new Thread[Line.length()];
        Thread[] women = new Thread[Line.length()];
        for (int i = 0; i < Line.length(); i++) {
            if(Line.charAt(i)=='M'){
                men[i] = new ManThread(i);
                men[i].start();
            }
            if(Line.charAt(i)=='F'){
                women[i] = new WomanThread(i);
                women[i].start();
            }
        }

        for (int i = 1; i < Line.length(); i++) {
            if(Line.charAt(i)!=Line.charAt(i-1)){
                if(Line.charAt(i)=='M'){
                    try {
                        men[i].join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Bathroom.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }else{
                    try {women[i].join();
                    } catch (InterruptedException ex)
                    {
                        Logger.getLogger(Bathroom.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        System.exit(0);
    }

    private static String Rall(int x, int y){ // create a string MFFM...
        String Output = "";
        Random rnd = new Random();
        int male=0;
        int female=0;
        while(male!=x||female!=y){
            int R=rnd.nextInt(2); // get a random int number between 0 and 2
            switch(R){
                case 0:
                    if(male<x){
                        Output=Output.concat("M"); // add characters after "Output" Characters.
                        male++;}
                    break;
                case 1:
                    if(female<y){
                        Output=Output.concat("F");
                        female++;}
                    break;
            }
        }
        return Output;
    }

    public static void randomSleep(int max) {
        Random rnd = new Random();
        int x = 1+rnd.nextInt(2);
        try {
            Thread.sleep((int) (x* max));
        }
        catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    private static class ManThread extends Thread {
        private int id;

        public ManThread(int id) {
            this.id = id;
        }

        public void run() {
            doWork();
            if(Bathroom.men_only.availablePermits()==0){
                useBathroom();
            }//if
            else{
                try{
                    Bathroom.bathroom.acquire();  // wait --
                    Bathroom.men_only.acquire();
                }catch(InterruptedException e){
                    System.out.println(e);
                    System.exit(-1);
                }
                try{
                    useBathroom();
                }finally{  //whenever the try statement is called, the finally statement will be called consequently.
                    Bathroom.men_only.release();  //signal
                    Bathroom.bathroom.release();
                }//else
                System.out.print("E");doWork();
            }
        }//run

        private void doWork() {
            System.out.println("Man " + id);
            Bathroom.randomSleep(10000);    // do work randomly amongst 10 secs
        }

        private void useBathroom() {
            System.out.println("\t\t\tMan " + id );
            Bathroom.randomSleep(1000);
            System.out.println("\t\t\t\t\t\tMan " + id );
            Bathroom.randomSleep(3000);
            System.out.println("\t\t\t\t\t\t\t\t\tMan " + id );
            Bathroom.randomSleep(1000);
        }
    }

    private static class WomanThread extends Thread {
        private int id;

        public WomanThread(int id) {
            this.id = id;
        }

        public void run() {
            doWork();
            if(Bathroom.women_only.availablePermits()==0){
                useBathroom();
            }
            else{
                try{
                    Bathroom.bathroom.acquire();
                    Bathroom.women_only.acquire();
                }catch(InterruptedException e){
                    System.out.println(e);
                    System.exit(-1);
                }
                try{
                    useBathroom();
                }finally{
                    Bathroom.women_only.release();
                    Bathroom.bathroom.release();
                }//else
                System.out.print("E");doWork();
            }
        }//run

        private void doWork() {
            System.out.println("Woman " + id);
            Bathroom.randomSleep(10000);
        }

        private void useBathroom() {
            System.out.println("\t\t\tWoman " + id );
            Bathroom.randomSleep(2000);
            System.out.println("\t\t\t\t\t\tWoman " + id );
            Bathroom.randomSleep(5000);
            System.out.println("\t\t\t\t\t\t\t\t\tWoman " + id );
            Bathroom.randomSleep(2000);
        }
    }
}