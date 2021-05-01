/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.app;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 *
 * @author walker
 */
public class Demo {
    public static void main(String[] args) {
        BigDecimal d = new BigDecimal(1200000);
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setDecimalSeparator('.');
        s.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("###.###",s);
        formatter.setDecimalFormatSymbols(s);
        
        System.out.println(formatter.format(d));
    }
}
