package main.app.settings;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.util.Util;

public final class Setting {

    public static void main(String[] args) {
    }

    private static Setting APP_SETTING;

    public static boolean IS_VERIFIED = false;
    public static final String CONFIG_FILE = "qlnt.ini";
    public static MessageDigest DIGEST = null;
    public static Gson gson = new Gson();

    private String STYLE_SHEET;
    private byte[] hash;

    public static Setting getInstance() {
        if (DIGEST == null) {
            try {
                DIGEST = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (APP_SETTING != null) {
            return APP_SETTING;
        }

        if (!readSettting()) {
            APP_SETTING = new Setting();
        }

        if (APP_SETTING == null) {
            APP_SETTING = new Setting();
        }

        // to counter a bug when reading from a file, its content will now be "null"
        // write this object to file to prevent data loss when file is loaded but
        // not modified
        writeSetting(APP_SETTING);
        return APP_SETTING;
    }

    /**
     * create default Setting object
     */
    public Setting() {
        this.STYLE_SHEET = Util.Themes.BOOTSTRAP.getLocation();
        setPassword("");
    }

    /**
     * read setting from .ini file
     *
     * @return Setting object
     */
    public static boolean readSettting() {
        Reader reader;
        try {
            reader = new FileReader(CONFIG_FILE);
            APP_SETTING = gson.fromJson(reader, Setting.class);
            reader.close();
            return true;
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * write Setting object to json
     *
     * @param setting
     */
    public static void writeSetting(Setting setting) {
        Writer writer;
        try {
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(setting, writer);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setPassword(String password) {
        this.hash = DIGEST.digest(
                password.getBytes(StandardCharsets.UTF_16));
        writeSetting(APP_SETTING);
    }

    public String getPassword() {
        return new String(APP_SETTING.hash);
    }

    public byte[] getHash() {
        return APP_SETTING.hash;
    }

    /**
     * check password
     *
     * @param password
     * @return true if equals with user password
     */
    public boolean checkPassword(String password) {
        return new String(DIGEST.digest(
                password.getBytes(StandardCharsets.UTF_16)))
                .equals(APP_SETTING.getPassword());
    }

    public String getSTYLE_SHEET() {
        return APP_SETTING.STYLE_SHEET;
    }

    public void setSTYLE_SHEET(String STYLE_SHEET) {
        APP_SETTING.STYLE_SHEET = STYLE_SHEET;
        writeSetting(APP_SETTING);
    }
}
