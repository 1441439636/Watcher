package tool;

/**
 * Created by 18754 on 2016/9/20.
 */
public class L {

    public static void println(String msg) {
        System.out.println(msg);
    }

    public static void d(String TAG, String msg) {
        println(TAG + "   " + msg);
    }

    public static void d(String msg) {
        d("==>", msg);
    }

    public static void pr(String[] msg) {
        for (String s : msg                ) {
            System.out.print(s+"   ");
        }
        System.out.println();
    }
}
