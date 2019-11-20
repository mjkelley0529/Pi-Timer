package PiTimer.Main;

import com.pi4j.io.gpio.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PiTimer extends JPanel implements ActionListener {
    //Logic Variables
    private int[] times={0,0,0},
            timeMax={200,59,59},
            savedTimes={0,0,0},
            lastTimes={0,0,0};
    private long startTime=-1;
    private long elapsedTime=0;
    private Timer timer;
    //Interface Variables
    private JLabel[] timeDisplays =new JLabel[times.length];
    private JButton[] timeAdjusters =new JButton[timeDisplays.length*2],
            interfaceButtons =new JButton[3];
    private String[] iButtonStrings ={"Clear","Reset","Start","Stop"};
    private JPanel[] containerPanes =new JPanel[4];
    //Aesthetic Variables
    private Font labelFont=new Font("Courier new", Font.BOLD, 32),
            buttonFont=new Font(labelFont.getName(), labelFont.getStyle(), 20);
    private Color backgroundColor=Color.decode("#000000"),
            foregroundColor=Color.decode("#FFFFFF"),
            transparent=new Color(0,0,0,0),
            startColor=Color.decode("#009900"),
            stopColor=Color.decode("#990000");
    private int gap=10;
    //Pi Variables
    private GpioPinDigitalOutput output=null;
    //Constructor
    PiTimer() {
        frameSetup();
    }
    PiTimer(Color backgroundColor, Color foregroundColor, Color startColor, Color stopColor, Font font) {
        this.backgroundColor=backgroundColor;
        this.foregroundColor=foregroundColor;
        this.startColor=startColor;
        this.stopColor=stopColor;
        labelFont=font;
        buttonFont=new Font(labelFont.getName(), labelFont.getStyle(), labelFont.getSize()/8*5);
        frameSetup();
    }
    private void frameSetup() {
        //Initialize Variables
        for(int i=0;i<timeDisplays.length;i++) {
            timeDisplays[i]=new JLabel(String.valueOf(times[i]));
            timeDisplays[i].setFocusable(false);
            timeDisplays[i].setFont(labelFont);
            timeDisplays[i].setHorizontalAlignment(JTextField.CENTER);
            timeDisplays[i].setBackground(backgroundColor);
            timeDisplays[i].setForeground(foregroundColor);
        }
        for(int i=0;i<timeAdjusters.length;i++) {
            timeAdjusters[i]=new JButton();
            if(i<timeAdjusters.length/2) {
                timeAdjusters[i].setIcon(new ImageIcon("src/PiTimer/Resources/arrowUp.png"));
            } else {
                timeAdjusters[i].setIcon(new ImageIcon("src/PiTimer/Resources/arrowDown.png"));
            }
            timeAdjusters[i].addActionListener(this);
            timeAdjusters[i].setFocusable(false);
            timeAdjusters[i].setFont(buttonFont);
            timeAdjusters[i].setBackground(backgroundColor);
            timeAdjusters[i].setForeground(foregroundColor);
            timeAdjusters[i].setBorder(BorderFactory.createRaisedBevelBorder());
        }
        for(int i=0;i<interfaceButtons.length;i++) {
            interfaceButtons[i]=new JButton(iButtonStrings[i]);
            interfaceButtons[i].addActionListener(this);
            interfaceButtons[i].setFocusable(false);
            interfaceButtons[i].setFont(buttonFont);
            if(i==2) {
                interfaceButtons[i].setBackground(startColor);
                interfaceButtons[i].setForeground(Color.getHSBColor(startColor.getRed(), startColor.getGreen(), startColor.getBlue()));
            } else {
                interfaceButtons[i].setBackground(backgroundColor);
                interfaceButtons[i].setForeground(foregroundColor);
            }
        }
        GridLayout[] containerLay = new GridLayout[containerPanes.length];
        containerLay[0]=new GridLayout(3, timeDisplays.length+2, gap,gap);
        containerLay[1]=new GridLayout(2, 1,gap,gap);
        containerLay[2]=new GridLayout(1,2,gap,gap);
        containerLay[3]=new GridLayout(1,1,gap,gap);
        for(int i=0;i<containerPanes.length;i++) {
            containerPanes[i]=new JPanel();
            containerPanes[i].setLayout(containerLay[i]);
            containerPanes[i].setFocusable(false);
            containerPanes[i].setBackground(transparent);
        }
        //Frame Setup
        setBackground(backgroundColor);
        GridLayout frameLay = new GridLayout(2, 1, gap, gap);
        setLayout(frameLay);

        for(int i=0;i<timeDisplays.length;i++) {
            containerPanes[0].add(timeAdjusters[i]);
        }
        for(int i=0;i<timeDisplays.length;i++) {
            containerPanes[0].add(timeDisplays[i]);
        }
        for(int i=timeDisplays.length;i<timeDisplays.length*2;i++) {
            containerPanes[0].add(timeAdjusters[i]);
        }
        containerPanes[2].add(interfaceButtons[0]);
        containerPanes[2].add(interfaceButtons[1]);
        containerPanes[3].add(interfaceButtons[2]);
        for(int i=2;i<containerPanes.length;i++) {
            containerPanes[1].add(containerPanes[i]);
        }
        for(int i=0;i<2;i++) {
            add(containerPanes[i]);
        }
    }
    //Utility Methods
    private void updateTimes() {
        for(int i=0;i<times.length;i++) {
            if (times[i]<0) {
                times[i]=timeMax[i];
            }
            if(times[i]>timeMax[i]) {
                times[i]=0;
            }
        }
    }
    private void updateDisplays() {
        updateTimes();
        for(int i=0;i<timeDisplays.length;i++) {
            timeDisplays[i].setText(String.valueOf(times[i]));
            timeDisplays[i].repaint();
        }
        repaint();
    }
    //Inherited Methods
    @Override
    public void actionPerformed(ActionEvent e) {
        Object s=e.getSource();
        boolean clickable;
        try {
            clickable=!timer.isRunning();
        } catch (NullPointerException npe) {
            clickable=true;
        }
        for(int i=0;i<timeAdjusters.length;i++) {
            if (s.equals(timeAdjusters[i])&&clickable) {
                if(i>=timeDisplays.length) {
                    times[i-timeDisplays.length]--;
                } else {
                    times[i]++;
                }
                updateDisplays();
            }
        }
        if(s.equals(interfaceButtons[0])) {
            if(!clickable) {
                stop();
            }
            for(int i=0;i<times.length;i++) {
                times[i]=0;
            }
            updateDisplays();
        }
        if(s.equals(interfaceButtons[1])) {
            System.arraycopy(savedTimes, 0, times, 0, savedTimes.length);
            updateDisplays();
            if(!clickable) {
                stop();
            }
        }
        if(s.equals(interfaceButtons[2])) {
            if (clickable) {
                start();
            } else if (!clickable) {
                stop();
            }
        }
    }
    public void start() {
        if(isRunnable()) {
            interfaceButtons[2].setText(iButtonStrings[3]);
            interfaceButtons[2].setBackground(stopColor);
            interfaceButtons[2].setForeground(Color.getHSBColor(stopColor.getRed(), stopColor.getGreen(), stopColor.getBlue()));
            System.arraycopy(times, 0, savedTimes, 0, times.length);
            timer = new Timer(0, e -> {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis();
                }
                elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime % 1000 == 0) {
                    times[times.length - 1]--;
                    updateTimes();
                    for (int i = times.length - 1; i > 0; i--) {
                        if ((times[i] == timeMax[i]) && (lastTimes[i] == 0)) {
                            times[i - 1]--;
                            updateTimes();
                        }
                    }
                    updateDisplays();
                }
                boolean done = true;
                for (int i : times) {
                    if (i != 0) {
                        done = false;
                        break;
                    }
                }
                if (done) {
                    stop();
                }
                System.arraycopy(times, 0, lastTimes, 0, times.length);
            });
            timer.start();
            try {
                output.low();
            } catch (Exception e) {
            }
        }
    }
    public void stop() {
        interfaceButtons[2].setText(iButtonStrings[2]);
        interfaceButtons[2].setBackground(startColor);
        interfaceButtons[2].setForeground(Color.getHSBColor(startColor.getRed(), startColor.getGreen(), startColor.getBlue()));
        try {
            timer.stop();
            output.high();
        } catch(Exception e) {}
    }
    public boolean isRunnable() {
        boolean runnable=false;
        for(int i:times) {
            if (i != 0) {
                runnable = true;
                break;
            }
        }
        return runnable;
    }
    public boolean isRunning() {
        return interfaceButtons[2].getText().equals(iButtonStrings[3]);
    }
    public void setOutput(GpioPinDigitalOutput output) {
        this.output=output;
    }
}