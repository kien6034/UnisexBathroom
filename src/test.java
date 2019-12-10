import java.util.concurrent.Semaphore;
public class test {
    public static void main(String[] args) throws InterruptedException {
        Semaphore sem = new Semaphore(1);
        sem.acquire();
        sem.release();
        System.out.println(sem.availablePermits());
    }
}
