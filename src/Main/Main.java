package Main;

import com.pi4j.io.gpio.*;
import com.pi4j.util.CommandArgumentParser;

import javax.swing.*;
import java.awt.*;

public class Main {
    //Variable Declaration
    private static final String TITLE ="Timer", VERID="0.3";
    private static final Color bgColor=Color.decode("#000000"), fgColor=Color.decode("#FFFFFF");
    //Main
    public static void main(String[] args) {
        //Set UI
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        PiTimer[] timers=new PiTimer[2];
        GpioController gpio;
        Pin[] pin=new Pin[timers.length];
        GpioPinDigitalOutput[] output=new GpioPinDigitalOutput[timers.length];
        try {
            gpio = GpioFactory.getInstance(); //Aquired code from: https://dzone.com/articles/simple-steps-to-develop-smart-light-java-amp-iot
            pin[0] = CommandArgumentParser.getPin(RaspiPin.class, RaspiPin.GPIO_04);
            pin[1] = CommandArgumentParser.getPin(RaspiPin.class, RaspiPin.GPIO_05);
            for(int i=0;i<output.length;i++) {
                output[i] = gpio.provisionDigitalOutputPin(pin[i], "My Output", PinState.HIGH);
            }
        } catch(Exception e) {
            gpio=null;
            pin=null;
            output=null;
        }
        for(int i=0;i<timers.length;i++) {
            timers[i]=new PiTimer(bgColor, fgColor);
            timers[i].setOutput(output[i]);
        }
        JPanel spacer=new JPanel();
        spacer.setFocusable(false);
        spacer.setBackground(Color.decode("#000000"));

        JFrame mainFrame=new JFrame();
        mainFrame.setTitle(TITLE+" "+VERID);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setUndecorated(true);
        mainFrame.setLayout(new GridLayout(1,3));
        mainFrame.add(timers[0]);
        mainFrame.add(spacer);
        mainFrame.add(timers[1]);
        mainFrame.setVisible(true);
    }
}
