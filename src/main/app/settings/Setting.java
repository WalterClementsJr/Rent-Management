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

public final class Setting {

    public static void main(String[] args) {
        Setting s;
        s = Setting.readSettting();
//        s.setPwd("4444");
        System.out.println(s.checkPassword("4444"));
        Setting.writeSetting(s);
    }

    private static Setting APP_SETTING = null;
    public static String SETTING_STYLE_SHEET;
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
     * read setting from json
     * @return Setting object
     */
    public static Setting readSettting() {
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

    /**
     * write Setting object to json
     * @param setting 
     */
    public static void writeSetting(Setting setting) {
        Writer writer = null;
        try {
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(setting, writer);
            // TODO run this line only when in javafx.Application
//            CustomAlert.showSimpleAlert("Thành công", "Đã lưu thông tin mới");
            System.out.println("write to file success!");
        } catch (IOException ex) {
            Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("write to file faled!");
            //TODO run this line only when in javafx.Application
//            CustomAlert.showErrorMessage("Failed", "Cant save configuration file");
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
