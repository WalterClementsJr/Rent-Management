package main.app.settings;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.ui.alert.CustomAlert;

public class Setting {

    public static void main(String[] args) {
        Setting s;
        s = Setting.getSetting();
//        s.setPwd("4444");
        System.out.println(s.checkPassword("4444"));
        Setting.writePreferenceToFile(s);
    }

    public static final String CONFIG_FILE = "qlnt.ini";
    public static MessageDigest DIGEST;
    
    private byte[] hash;

    public Setting() {
        try {
            DIGEST = MessageDigest.getInstance("SHA-256");
            hash = DIGEST.digest("".getBytes(StandardCharsets.UTF_16));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setPwd(String pwd) {
        this.hash = DIGEST.digest(pwd.getBytes(StandardCharsets.UTF_16));
    }

    public String getPwd() {
        return new String(hash);
    }

    public boolean checkPassword(String password) {
        return new String(
                DIGEST.digest(password.getBytes(StandardCharsets.UTF_16))).equals(getPwd());
    }
    
    public static void initSetting() {
        Writer writer = null;
        try {
            Setting setting = new Setting();
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(setting, writer);
        } catch (IOException ex) {
            Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * read setting from json file
     * @return 
     */
    public static Setting getSetting() {
        Gson gson = new Gson();
        Setting settings = new Setting();
        try {
            settings = gson.fromJson(new FileReader(CONFIG_FILE), Setting.class);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Setting.class.getName()).info("Config file is missing. Creating new one with default config");
            initSetting();
        }
        return settings;
    }

    public static void writePreferenceToFile(Setting setting) {
        Writer writer = null;
        try {
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(setting, writer);
            // TODO run this line only when in javafx.Application
//            CustomAlert.showSimpleAlert("wht", "ok");
            System.out.println("write to file success!");
        } catch (IOException ex) {
            Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("write to file faled!");
            //TODO same thing
            CustomAlert.showErrorMessage(ex, "Failed", "Cant save configuration file");
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
