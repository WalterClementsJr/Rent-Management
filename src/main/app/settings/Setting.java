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
import main.util.Util;

public final class Setting {

    public static void main(String[] args) {
    }

    private static Setting APP_SETTING = null;

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
        if (APP_SETTING == null) {
            APP_SETTING = readSettting();
        }
        if (APP_SETTING == null) {
            initSetting();
        }

        System.out.println(APP_SETTING.getSTYLE_SHEET());
        System.out.println(APP_SETTING.getPassword());

        return APP_SETTING;
    }

    /**
     * create default setting object
     */
    public Setting() {
        this.STYLE_SHEET = Util.BOOTSTRAP_STYLE_SHEET_LOCATION;
        setPassword("");
    }

//    public void hashPassword(String pwd) {
//        this.hash = DIGEST.digest(pwd.getBytes(StandardCharsets.UTF_16));
//    }
    public void setPassword(String pwd) {
        this.hash = DIGEST.digest(pwd.getBytes(StandardCharsets.UTF_16));
        writeSetting(APP_SETTING);
    }

    public String getPassword() {
        return new String(hash);
    }

    /**
     * check password
     *
     * @param password
     * @return true if equals with user password
     */
    public boolean checkPassword(String password) {
        return new String(
                DIGEST.digest(password.getBytes(StandardCharsets.UTF_16))).equals(getPassword());
    }

    /**
     * create .ini file with default settings use when can't find available
     * setting file
     */
    public static void initSetting() {
        Writer writer;
        try {
            APP_SETTING = new Setting();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(APP_SETTING, writer);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * read setting from .ini file
     *
     * @return Setting object
     */
    public static Setting readSettting() {
        try {
            return gson.fromJson(new FileReader(CONFIG_FILE), Setting.class);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Setting.class.getName()).info(
                    "Config file is missing.");
            return null;
        }
    }

    /**
     * write Setting object to json
     *
     * @param setting
     */
    public static void writeSetting(Setting setting) {
        Writer writer = null;
        try {
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(setting, writer);
            // TODO run this line only when in javafx.Application
//            CustomAlert.showSimpleAlert("Thành công", "Đã lưu thông tin");
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
            //TODO run this line only when in javafx.Application
//            CustomAlert.showErrorMessage(
//                    "Thất bại", "Không thể lưu thông tin");
        }
    }

    public String getSTYLE_SHEET() {
        return STYLE_SHEET;
    }

    public void setSTYLE_SHEET(String STYLE_SHEET) {
        this.STYLE_SHEET = STYLE_SHEET;
        writeSetting(APP_SETTING);
    }
}
