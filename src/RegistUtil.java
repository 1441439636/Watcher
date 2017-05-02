import java.util.prefs.Preferences;
public class RegistUtil {
    private static final String dbName = "autoDatabase";
    private static Preferences preferences = Preferences.systemRoot().node(dbName);
    private static final String[] key = {"dbType", "name", "password", "ip", "databasename"};

    public static void write(String[] val) {
        for (int i = 0; i < val.length; i++) {
            preferences.put(key[i], val[i]);
        }
    }

    public static String[] read() {
        String[] val = new String[5];
        for (int i = 0; i < val.length; i++) {
            val[i] = preferences.get(key[i], "");
        }
        return val;
    }
}
