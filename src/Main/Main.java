package Main;

import com.pi4j.io.gpio.*;
import com.pi4j.util.CommandArgumentParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame implements ActionListener {
    //Variable Declaration
    private static final String TITLE ="Timer", VERID="0.3";
    //UI Variables
    PiTimer[] timers=new PiTimer[2];
    JPanel spacer=new JPanel(), containerPane=new JPanel(), framePane=new JPanel();
    JToggleButton start=new JToggleButton("Start");
    //Aesthetic Variables
    private Color bgColor=Color.decode("#000000"),
            fgColor=Color.decode("#FFFFFF"),
            startColor=Color.decode("#009900"),
            stopColor=Color.decode("#990000");
    private Font font=new Font("Arial", Font.BOLD, 32);
    Main() {
        for(int i=0;i<timers.length;i++) {
            timers[i]=new PiTimer(bgColor, fgColor,startColor,stopColor,font);
        }
        //Pi Interfacing
        /*GpioController gpio;
        Pin[] pin=new Pin[timers.length];
        GpioPinDigitalOutput[] output=new GpioPinDigitalOutput[timers.length];
        try {
            gpio = GpioFactory.getInstance(); //Aquired code from: https://dzone.com/articles/simple-steps-to-develop-smart-light-java-amp-iot
            pin[0] = CommandArgumentParser.getPin(RaspiPin.class, RaspiPin.GPIO_04);
            pin[1] = CommandArgumentParser.getPin(RaspiPin.class, RaspiPin.GPIO_05);
            for(int i=0;i<output.length;i++) {
                output[i] = gpio.provisionDigitalOutputPin(pin[i], "My Output", PinState.HIGH);
                timers[i].setOutput(output[i]);
            }
        } catch(Exception e) {
            gpio=null;
            pin=null;
            output=null;
        }*/
        spacer.setFocusable(false);
        spacer.setBackground(bgColor);

        start.setFocusable(false);
        start.addActionListener(this);
        start.setBackground(startColor);
        start.setFont(font);

        containerPane.setLayout(new GridLayout(3,1,5,5));
        containerPane.setFocusable(false);
        containerPane.setBackground(bgColor);
        containerPane.add(new JLabel(new ImageIcon("/src/Resources/logo.png")));
        containerPane.add(spacer);
        containerPane.add(start);

        framePane.setLayout(new GridLayout(1,3,10,10));
        framePane.setBackground(bgColor);
        framePane.setFocusable(false);
        framePane.add(timers[0]);
        framePane.add(containerPane);
        framePane.add(timers[1]);

        setTitle(TITLE+" "+VERID);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setLayout(new GridLayout(1,1));
        setBackground(bgColor);
        setForeground(fgColor);
        add(framePane);
        setVisible(true);
    }
    private void setTimers(boolean started) {
        if(started) {
            for(int i=0;i<timers.length;i++) {
                if(timers[i].isRunnable()) {
                    timers[i].start();
                } else {
                    start.setSelected(false);
                    setTimers(false);
                }
            }
        } else {
            for (int i = 0; i < timers.length; i++) {
                timers[i].stop();
            }
        }
    }
    public void actionPerformed(ActionEvent evt) {
        if(evt.getSource()==start) {
            if(start.isSelected()) {
                setTimers(true);
                start.setText("Stop");
                start.setBackground(stopColor);
            } else {
                setTimers(false);
                start.setText("Start");
                start.setBackground(startColor);
            }
        }
    }
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
        new Main();
    }
}
