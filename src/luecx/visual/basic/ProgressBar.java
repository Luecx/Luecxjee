package luecx.visual.basic;

public class ProgressBar {

    public static void update(String name, int v, int total, int detail){
        System.out.print("\r" + name + " [");
        for(int i = 0; i <  detail * ((double) v / (double) total) - 1; i++){
            System.out.print("=");
        }
        System.out.print(">");
        for(int i = 0; i <  0.001 + detail - detail * ((double) v / (double) total); i++){
            System.out.print(" ");
        }
        System.out.print("] " + v + " / " + total);
    }

    public static void update(String name, int v, int total, int detail, String addition){
        System.out.print("\r" + name + " [");
        for(int i = 0; i <  detail * ((double) v / (double) total) - 1; i++){
            System.out.print("=");
        }
        System.out.print(">");
        for(int i = 0; i <  0.001 + detail - detail * ((double) v / (double) total); i++){
            System.out.print(" ");
        }
        System.out.print("] " + v + " / " + total + "  " + addition);
    }

    public static void main(String[] args) throws InterruptedException {
        for(int i = 0; i < 100; i++){
            ProgressBar.update("Test", i * 3, 300, 100);
            Thread.sleep(100);
        }
    }
}
