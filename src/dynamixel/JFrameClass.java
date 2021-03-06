/*

started to refactor the code at 9:20PM on Dec 13, 2021
https://stackoverflow.com/questions/26265002/pause-and-resume-swingworker-doinbackground

 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamixel;

import com.sun.jna.Native;
import java.awt.Color;
import static java.lang.Math.abs;
import static java.lang.Thread.sleep;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class JFrameClass extends javax.swing.JFrame {

    public String Batch_time_stamp_into_mysql = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
    String time_stamp_into_mysql = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
    Dynamixel dynamixel = new Dynamixel();
    String DEVICENAME                   = "COM3";   
    
    short position1 = 0;
    short position2 = 0;
    short position3 = 0;
    short position4 = 0;
    Double ChangeBand = 0.01; // if the change in position is within this % range a new position is not printed to screen to reduce printouts
    
    private String scenarioSaveName = "initialized";
    private int recordPositions = 0; // 0 means do not record; 1 means yes to record
    private int recordOnce = 0; // records one record to mySQL and then stops recording.
    int TargetPosMtr0 = 200;
    int TargetPosMtr1 = 850;
    int TargetPosMtr2 = 500;
    int TargetPosMtr3 = 500;
    int port_num = 0;
    int load1 = 0;
    int load2 = 0;
    int load3 = 0;
    int load4 = 0;
    int mGoalPosLabelMotor1             = 0;
    int PROTOCOL_VERSION                = 1;
    int group_num = dynamixel.groupBulkRead(port_num, PROTOCOL_VERSION);
    byte DXL_ID                         = 6;    
    short ADDR_MX_TORQUE_ENABLE         = 24;
    short ADDR_MX_GOAL_POSITION         = 30;
    byte TORQUE_ENABLE                  = 1;
    int motorNumber                     = 0;
    short dxl_goal_position             = 300;
    Boolean FollowDriverFlag = false;
    MotorClass motor1 = new MotorClass();
    MotorClass motor2 = new MotorClass();
    MotorClass motor3 = new MotorClass();
    MotorClass motor4 = new MotorClass();
    
    public JFrameClass() {
        initComponents();
        final PausableSwingWorker<Void, String> worker = new PausableSwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() {
                    System.out.println("here4?");
                    System.out.println("Make sure power is connected and working. Can I automate this validation step?");
                    while (true) {
                       System.out.println("here2?");
                        
                    Native.setProtected(true);
                    port_num = dynamixel.portHandler(DEVICENAME);
                    dynamixel.packetHandler();
                    dynamixel.closePort(port_num);         
                    dynamixel.openPort(port_num); 
                    System.err.println("BaudRate: "+1000000+"; motorNumber: "+motorNumber);
                    dynamixel.setBaudRate(port_num, 1000000);
                    if(dynamixel.openPort(port_num)){
                        System.err.println("Port Open: "+port_num);
                    }
                    else
                    {
                      System.err.println("Failed to open the port!");
                    }
                    //mReadPositionsLoadEtc.execute();
                    

                    motorNumber = 0;
                    motor1.setBatchTime(Batch_time_stamp_into_mysql);
                    motor1.makeDynamix(dynamixel, motorNumber);
                    motor1.setMovingSpeed();
                    motor1.setTorque();

                    motorNumber = 1;
                    motor2.setBatchTime(Batch_time_stamp_into_mysql);
                    motor2.makeDynamix(dynamixel, motorNumber);
                    motor2.setMovingSpeed();
                    motor2.setTorque();

                    motorNumber = 2; // wrist
                    motor3.setBatchTime(Batch_time_stamp_into_mysql);
                    motor3.makeDynamix(dynamixel, motorNumber);
                    motor3.setMovingSpeed();
                    motor3.setTorque();
                    System.err.println("motor3 pos: "+motor3.readPosition());

                    try {
                        sleep(25);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MotorClass.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    motorNumber = 3; // shoulder
                    motor4.setBatchTime(Batch_time_stamp_into_mysql);
                    motor4.makeDynamix(dynamixel, motorNumber);
                    motor4.setMovingSpeed();
                    motor4.setTorque();
                    System.err.println("motor4 pos: "+motor4.readPosition());

                    motorNumber = 4; // shoulder on control arm (this is the arm to manually control)

                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MotorClass.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    dxl_goal_position = 200;
                    motor1.moveMotor(dxl_goal_position);

                    dxl_goal_position = 700;
                    motor2.moveMotor(dxl_goal_position);

                    dxl_goal_position = 508;
                    motor3.moveMotor(dxl_goal_position);

                    dxl_goal_position = 520;
                    motor4.moveMotor(dxl_goal_position); // shoulder
//                    try {
//                        sleep(1500);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(MotorClass.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//
//                    dxl_goal_position = (short) TargetPosMtr0; // end effector
//                    motor1.moveMotor(dxl_goal_position);
//
//                    dxl_goal_position = (short) TargetPosMtr1; // elbow
//                    motor2.moveMotor(dxl_goal_position);
//
//                    dxl_goal_position = (short) TargetPosMtr2; // wrist
//                    motor3.moveMotor(dxl_goal_position);
//
//                    dxl_goal_position = (short) TargetPosMtr3; // shoulder
//                    motor4.moveMotor(dxl_goal_position);

                    System.out.println("------------------------------------------------------------------ end of jclass");
                    jSlider1_ActualTemp.setOpaque(true);
                    jSlider2_ActualTemp.setOpaque(true);
                    jSlider3_ActualTemp.setOpaque(true);
                    jSlider4_ActualTemp.setOpaque(true);

                    int x1 = 0;

                    while(true){
                        x1 = x1 + 1;
                        try {
                            System.err.println("made it here");
                            sleep(4500); // give motors time to connect before reading from them
                        } catch (InterruptedException ex) {
                            Logger.getLogger(JFrameClass.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        int x = 0;
                        while(true) {
                            x = x + 1;
                            try {    
                                if (FollowDriverFlag) { 
                                    // if this flag is true then arm 2 will follow the manual movement of arm 1

                                    // this section is designed to prevent extreme motions based on potentially erroneous readings
//                                        if (position5 < oldPosition5 *.8 && position5 > 0) {
//                                            position5 = (short) (oldPosition5 * 0.8);
//                                            //System.out.println("POSITION5 " + position5);
//                                        }
//                                        if (position6 < oldPosition6 *.8 && position6 > 0 ) {
//                                            position6 = (short) (oldPosition6 * 0.8);
//                                            //System.out.println("POSITION6 " + position6);
//                                        }
//                                        if (position7 < oldPosition7 *.8 && position7 > 0 ) {
//                                            position7 = (short) (oldPosition7 * 0.8);
//                                        }
//                                        if (position8 < oldPosition8 *.8 && position8 > 0 ) {
//                                            position8 = (short) (oldPosition8 * 0.8);
//                                        }
//
//                                        if (position5 > oldPosition5 *1.2 && position5 > 0) {
//                                            position5 = (short) (oldPosition5 * 1.2);
//                                            //System.out.println("POSITION5 TWO " + position5);
//                                        }
//                                        if (position6 > oldPosition6 *1.2 && position6 > 0) {
//                                            position6 = (short) (oldPosition6 * 1.2);
//                                        }
//                                        if (position7 > oldPosition7 *1.2 && position7 > 0) {
//                                            position7 = (short) (oldPosition7 * 1.2);
//                                        }
//                                        if (position8 > oldPosition8 *1.2 && position8 > 0) {
//                                            position8 = (short) (oldPosition8 * 1.2);
//                                        }
//                                        if (position5 == 0) {
//                                            position5 = (short) (oldPosition5);
//                                            //System.out.println("POSITION5 Three " + position5);
//                                        }
//                                        if (position6 == 0) {
//                                            position6 = (short) (oldPosition6);
//                                            //System.out.println("POSITION5 Three " + position5);
//                                        }
//                                        if (position7 == 0) {
//                                            position7 = (short) (oldPosition7);
//                                            //System.out.println("POSITION5 Three " + position5);
//                                        }
//                                        if (position8 == 0) {
//                                            position8 = (short) (oldPosition8);
//                                            //System.out.println("POSITION5 Three " + position5);
//                                        }
//
//                                        oldPosition5 = position5;
//                                        oldPosition6 = position6;
//                                        oldPosition7 = position7;
//                                        oldPosition8 = position8;
//
//                                    String time_stamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
//                                    System.out.println(time_stamp);
//                                    System.out.print("move motor 5 ");
//                                    motor1.moveMotor((short)(position5));
//                                    time_stamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
//                                    System.out.println(time_stamp);
//                                    System.out.print("move motor 6 ");
//                                    motor2.moveMotor((short)(position6));
//                                    time_stamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
//                                    System.out.println(time_stamp);
//                                    System.out.print("move motor 7 ");
//                                    motor3.moveMotor((short)(position7));
//                                    time_stamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
//                                    System.out.println(time_stamp);
//                                    System.out.print("move motor 8 ");
//                                    motor4.moveMotor((short)(position8));
//                                    time_stamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
//                                    System.out.println("Last time_stamp " + time_stamp);
                                    }
                                
                                    position1 = motor1.readPosition(); // error here may mean this is starting before devices are connected.
                                    jTextField_PositionMtr0.setText(position1+"");
                                    sleep(1);
                                    short load1 = motor1.readLoad();
                                    //System.out.println("Load1 " + load1 + " Position motor1 "+position1);
                                    jTextField_Load_1.setText(load1 + "");
                                    if (load1 == 16959){
                                        jLabelMotor1.setOpaque(true);
                                        jLabelMotor1.setBackground(Color.red);
                                    }
                                    else {
                                        jLabelMotor1.setBackground(Color.green);
                                    }
                                    int Temp1C = motor1.readPresentTemperature(); // this also has read errors that appear to be high temps
                                    int Temp1F = Temp1C*9/5+32;
                                    jSlider1_ActualTemp.setValue(Temp1F);
                                    if (Temp1F>140) {
                                        //System.err.println("Fahrenheit: " + Temp1F + " motor1 Temperature is high " + Temp1C);
                                            jSlider1_ActualTemp.setBackground(Color.red);
                                        }
                                        else {
                                            jSlider1_ActualTemp.setBackground(Color.green);
                                        }
                                    sleep(2); // RxTx takes 100 ms? need to reread documentation.
                                    position2 = motor2.readPosition();	
                                    jTextField_PositionMtr1.setText(position2+"");	
                                    int load2 = motor2.readLoad();
                                    
                                    jTextField_Load_2.setText(load2 + "");
                                    
                                    if (load2 == 999999){	
                                        jLabelMotor2.setOpaque(true);
                                        jLabelMotor2.setBackground(Color.red);	
                                    }	
                                    else {	
                                        jLabelMotor2.setOpaque(true);
                                        jLabelMotor2.setBackground(Color.green);	
                                    }	
                                    int Temp2C = motor2.readPresentTemperature(); // this also has read errors that appear to be high temps
                                    int Temp2F = Temp2C*9/5+32;
                                    jSlider2_ActualTemp.setValue(Temp2F);
                                    if (Temp2F>140) {
                                        //System.err.println("Fahrenheit: " + Temp2F + " motor2 Temperature is high " + Temp2C);
                                            jSlider2_ActualTemp.setBackground(Color.RED);
                                        }
                                        else {
                                            jSlider2_ActualTemp.setBackground(Color.GREEN);
                                        }
                                    sleep(2); // RxTx takes 100 ms? need to reread documentation.	
                                    position3 = motor3.readPosition();	
                                    jTextField_PositionMtr2.setText(position3+"");	
                                    int load3 = motor3.readLoad();
                                    
                                    jTextField_Load_3.setText(load3 + "");
                                    
                                    if (load3 == 999999){
                                        jLabelMotor3.setOpaque(true);
                                        jLabelMotor3.setBackground(Color.RED);	
                                    }	
                                    else {
                                        jLabelMotor3.setOpaque(true);
                                        jLabelMotor3.setBackground(Color.GREEN);	
                                    }
                                    int Temp3C = motor3.readPresentTemperature(); // this also has read errors that appear to be high temps
                                    int Temp3F = Temp3C*9/5+32;
                                    jSlider3_ActualTemp.setValue(Temp3F);
                                    if (Temp3F>140) {
                                        //System.err.println("Fahrenheit: " + Temp3F + " motor3 Temperature is high " + Temp3C);
                                            jSlider3_ActualTemp.setBackground(Color.RED);
                                        }
                                        else {
                                            jSlider3_ActualTemp.setBackground(Color.GREEN);
                                        }
                                    sleep(2); // RxTx takes 100 ms? need to reread documentation.
                                    position4 = motor4.readPosition();	
                                    jTextField_PositionMtr3.setText(position4+"");	
                                    jScrollBarMtr4_Actual.setValue(position4);
                                    int load4 = motor4.readLoad();	
                                    jTextField_LoadEndEffector.setText(load4 + "");
                                    if (load4 == 999999){
                                        jLabelMotor4.setOpaque(true);
                                        jLabelMotor4.setBackground(Color.red);	
                                    }	
                                    else {	
                                        jLabelMotor4.setOpaque(true);
                                        jLabelMotor4.setBackground(Color.green);	
                                    }	
                                    int Temp4C = motor4.readPresentTemperature(); // this also has read errors that appear to be high temps
                                    int Temp4F = Temp4C*9/5+32;
                                    jSlider4_ActualTemp.setValue(Temp4F);
                                    if (Temp4F>140) {
                                        //System.err.println("Fahrenheit: " + Temp4F + " motor4 Temperature is high " + Temp4C);
                                            jSlider4_ActualTemp.setBackground(Color.RED);
                                        }
                                        else {
                                            jSlider4_ActualTemp.setBackground(Color.GREEN);
                                        }

                                time_stamp_into_mysql = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
                                scenarioSaveName = jTextFieldRecordScenarioName.getText();

                                if(position1!=0 && position2!=0 && position3!=0 && position4!=0){ // only record when positions are not zero	
                                     if(recordPositions==1 || recordOnce == 1){	
                                        recordOnce = 0;	
                                    }	
                                }                    

                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(JFrameClass.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } catch (InterruptedException ex) {
                                Logger.getLogger(JFrameClass.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        };    
      worker.execute();
    }
            
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonMoveMtrToPresetHigh = new javax.swing.JButton();
        jButtonMoveMtr0ToPresetLow = new javax.swing.JButton();
        jScrollBarMtr0 = new javax.swing.JScrollBar();
        jLabelMotor1 = new javax.swing.JLabel();
        jScrollBarMtr1 = new javax.swing.JScrollBar();
        jScrollBarMtr2 = new javax.swing.JScrollBar();
        jScrollBarMtr4_Target = new javax.swing.JScrollBar();
        jLabelMotor2 = new javax.swing.JLabel();
        jButtonStepUpMtr0 = new javax.swing.JButton();
        jButtonStepDownMtr0 = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jToggleButtonMtr0 = new javax.swing.JToggleButton();
        jButtonResetTorqueMtr0 = new javax.swing.JButton();
        jButtonMoveMtr1ToPresetHigh = new javax.swing.JButton();
        jButtonStepUpMtr1 = new javax.swing.JButton();
        jButtonStepDownMtr1 = new javax.swing.JButton();
        jButtonMoveMtr1ToPresetLow = new javax.swing.JButton();
        jToggleButtonMtr1 = new javax.swing.JToggleButton();
        jButtonResetTorqueMtr1 = new javax.swing.JButton();
        jLabelMotor3 = new javax.swing.JLabel();
        jLabelMotor4 = new javax.swing.JLabel();
        jButtonMoveMtr2ToPresetHigh = new javax.swing.JButton();
        jButtonStepUpMtr2 = new javax.swing.JButton();
        jButtonStepDownMtr2 = new javax.swing.JButton();
        jButtonMoveMtr2ToPresetLow = new javax.swing.JButton();
        jToggleButtonMtr2 = new javax.swing.JToggleButton();
        jButtonResetTorqueMtr2 = new javax.swing.JButton();
        jButtonMoveMtr3ToPresetHigh = new javax.swing.JButton();
        jButtonStepUpMtr3 = new javax.swing.JButton();
        jButtonStepDownMtr3 = new javax.swing.JButton();
        jButtonMoveMtr3ToPresetLow = new javax.swing.JButton();
        jToggleButtonMtr3 = new javax.swing.JToggleButton();
        jButtonResetTorqueMtr3 = new javax.swing.JButton();
        jButtonEnableTorqueAllMotors = new javax.swing.JButton();
        jButtonDisableTorqueAllMotors = new javax.swing.JButton();
        jToggleButtonRecorYorN = new javax.swing.JToggleButton();
        jButtonRecord1Position = new javax.swing.JButton();
        jButtonRunASequence1 = new javax.swing.JButton();
        jTextFieldRecordScenarioName = new javax.swing.JTextField();
        jButtonRunASequence2 = new javax.swing.JButton();
        jTextFieldSpeedMotor1 = new javax.swing.JTextField();
        jTextFieldSpeedMotor2 = new javax.swing.JTextField();
        jTextFieldSpeedMotor3 = new javax.swing.JTextField();
        jTextFieldSpeedMotor4 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextField_IncrementMtr0 = new javax.swing.JTextField();
        jTextField_IncrementMtr1 = new javax.swing.JTextField();
        jTextField_IncrementMtr2 = new javax.swing.JTextField();
        jTextField_IncrementMtr3 = new javax.swing.JTextField();
        jLabel_Increment = new javax.swing.JLabel();
        jTextField_PositionMtr3 = new javax.swing.JTextField();
        jTextField_PositionMtr0 = new javax.swing.JTextField();
        jTextField_PositionMtr1 = new javax.swing.JTextField();
        jTextField_PositionMtr2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jToggleButtonPrintDebugging = new javax.swing.JToggleButton();
        jLabel4 = new javax.swing.JLabel();
        jToggleButton_FollowDriverButton = new javax.swing.JToggleButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField_LoadEndEffector = new javax.swing.JTextField();
        jTextField_Load_1 = new javax.swing.JTextField();
        jTextField_Load_3 = new javax.swing.JTextField();
        jTextField_Load_2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jScrollBarMtr4_Actual = new javax.swing.JScrollBar();
        jSlider4_ActualTemp = new javax.swing.JSlider();
        jSlider1_ActualTemp = new javax.swing.JSlider();
        jSlider2_ActualTemp = new javax.swing.JSlider();
        jSlider3_ActualTemp = new javax.swing.JSlider();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonMoveMtrToPresetHigh.setText("Max Up  ");
        jButtonMoveMtrToPresetHigh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoveMtrToPresetHighActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMoveMtrToPresetHigh, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 350, 83, 58));

        jButtonMoveMtr0ToPresetLow.setText("Max Down");
        jButtonMoveMtr0ToPresetLow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoveMtr0ToPresetLowActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMoveMtr0ToPresetLow, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 470, 83, 51));

        jScrollBarMtr0.setBlockIncrement(20);
        jScrollBarMtr0.setMaximum(1000);
        jScrollBarMtr0.setValue(390);
        jScrollBarMtr0.setVisibleAmount(20);
        jScrollBarMtr0.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBarMtr0AdjustmentValueChanged(evt);
            }
        });
        getContentPane().add(jScrollBarMtr0, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 53, 30, 130));

        jLabelMotor1.setText("Position");
        getContentPane().add(jLabelMotor1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 280, 57, 30));

        jScrollBarMtr1.setBlockIncrement(20);
        jScrollBarMtr1.setMaximum(1000);
        jScrollBarMtr1.setMinimum(700);
        jScrollBarMtr1.setValue(850);
        jScrollBarMtr1.setVisibleAmount(20);
        jScrollBarMtr1.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBarMtr1AdjustmentValueChanged(evt);
            }
        });
        getContentPane().add(jScrollBarMtr1, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 53, 30, 130));

        jScrollBarMtr2.setBlockIncrement(20);
        jScrollBarMtr2.setMaximum(720);
        jScrollBarMtr2.setMinimum(340);
        jScrollBarMtr2.setValue(500);
        jScrollBarMtr2.setVisibleAmount(20);
        jScrollBarMtr2.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBarMtr2AdjustmentValueChanged(evt);
            }
        });
        getContentPane().add(jScrollBarMtr2, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 53, 33, 130));

        jScrollBarMtr4_Target.setBlockIncrement(20);
        jScrollBarMtr4_Target.setMaximum(700);
        jScrollBarMtr4_Target.setMinimum(385);
        jScrollBarMtr4_Target.setValue(500);
        jScrollBarMtr4_Target.setVisibleAmount(20);
        jScrollBarMtr4_Target.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBarMtr4_TargetAdjustmentValueChanged(evt);
            }
        });
        getContentPane().add(jScrollBarMtr4_Target, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 53, 36, 130));

        jLabelMotor2.setText("Position");
        getContentPane().add(jLabelMotor2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 280, 67, 32));

        jButtonStepUpMtr0.setText("step up");
        jButtonStepUpMtr0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStepUpMtr0ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonStepUpMtr0, new org.netbeans.lib.awtextra.AbsoluteConstraints(195, 410, -1, -1));

        jButtonStepDownMtr0.setText("step down");
        jButtonStepDownMtr0.setPreferredSize(new java.awt.Dimension(72, 22));
        jButtonStepDownMtr0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStepDownMtr0ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonStepDownMtr0, new org.netbeans.lib.awtextra.AbsoluteConstraints(195, 440, -1, -1));

        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 610, 175, 55));

        jToggleButtonMtr0.setText("Lower Torque");
        jToggleButtonMtr0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonMtr0ActionPerformed(evt);
            }
        });
        getContentPane().add(jToggleButtonMtr0, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 530, 110, -1));

        jButtonResetTorqueMtr0.setText("Reset Torque to Normal");
        jButtonResetTorqueMtr0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetTorqueMtr0ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonResetTorqueMtr0, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 570, 100, -1));

        jButtonMoveMtr1ToPresetHigh.setText("Max Up  ");
        jButtonMoveMtr1ToPresetHigh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoveMtr1ToPresetHighActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMoveMtr1ToPresetHigh, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 350, 83, 58));

        jButtonStepUpMtr1.setText("step up");
        jButtonStepUpMtr1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStepUpMtr1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonStepUpMtr1, new org.netbeans.lib.awtextra.AbsoluteConstraints(335, 410, -1, -1));

        jButtonStepDownMtr1.setText("step down");
        jButtonStepDownMtr1.setPreferredSize(new java.awt.Dimension(72, 22));
        jButtonStepDownMtr1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStepDownMtr1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonStepDownMtr1, new org.netbeans.lib.awtextra.AbsoluteConstraints(335, 440, -1, -1));

        jButtonMoveMtr1ToPresetLow.setText("Max Down");
        jButtonMoveMtr1ToPresetLow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoveMtr1ToPresetLowActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMoveMtr1ToPresetLow, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 470, 83, 51));

        jToggleButtonMtr1.setText("Lower Torque");
        jToggleButtonMtr1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonMtr1ActionPerformed(evt);
            }
        });
        getContentPane().add(jToggleButtonMtr1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 530, 114, -1));

        jButtonResetTorqueMtr1.setText("Reset Torque to Normal");
        jButtonResetTorqueMtr1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetTorqueMtr1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonResetTorqueMtr1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 570, 110, -1));

        jLabelMotor3.setText("Position");
        getContentPane().add(jLabelMotor3, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 280, 67, 32));

        jLabelMotor4.setText("Position");
        getContentPane().add(jLabelMotor4, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 280, 67, 32));

        jButtonMoveMtr2ToPresetHigh.setText("Max Up  ");
        jButtonMoveMtr2ToPresetHigh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoveMtr2ToPresetHighActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMoveMtr2ToPresetHigh, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 350, 83, 58));

        jButtonStepUpMtr2.setText("step up");
        jButtonStepUpMtr2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStepUpMtr2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonStepUpMtr2, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 410, -1, -1));

        jButtonStepDownMtr2.setText("step down");
        jButtonStepDownMtr2.setPreferredSize(new java.awt.Dimension(72, 22));
        jButtonStepDownMtr2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStepDownMtr2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonStepDownMtr2, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 440, -1, -1));

        jButtonMoveMtr2ToPresetLow.setText("Max Down");
        jButtonMoveMtr2ToPresetLow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoveMtr2ToPresetLowActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMoveMtr2ToPresetLow, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 470, 83, 51));

        jToggleButtonMtr2.setText("Lower Torque");
        jToggleButtonMtr2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonMtr2ActionPerformed(evt);
            }
        });
        getContentPane().add(jToggleButtonMtr2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 530, -1, -1));

        jButtonResetTorqueMtr2.setText("Reset Torque to Normal");
        jButtonResetTorqueMtr2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetTorqueMtr2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonResetTorqueMtr2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 570, 100, -1));

        jButtonMoveMtr3ToPresetHigh.setText("Max Up  ");
        jButtonMoveMtr3ToPresetHigh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoveMtr3ToPresetHighActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMoveMtr3ToPresetHigh, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 350, 83, 58));

        jButtonStepUpMtr3.setText("Left");
        jButtonStepUpMtr3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStepUpMtr3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonStepUpMtr3, new org.netbeans.lib.awtextra.AbsoluteConstraints(615, 410, -1, -1));

        jButtonStepDownMtr3.setText("Right");
        jButtonStepDownMtr3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStepDownMtr3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonStepDownMtr3, new org.netbeans.lib.awtextra.AbsoluteConstraints(615, 440, -1, -1));

        jButtonMoveMtr3ToPresetLow.setText("Max Down");
        jButtonMoveMtr3ToPresetLow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoveMtr3ToPresetLowActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMoveMtr3ToPresetLow, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 470, 83, 51));

        jToggleButtonMtr3.setText("Lower Torque");
        jToggleButtonMtr3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonMtr3ActionPerformed(evt);
            }
        });
        getContentPane().add(jToggleButtonMtr3, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 530, -1, -1));

        jButtonResetTorqueMtr3.setText("Reset Torque to Normal");
        jButtonResetTorqueMtr3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetTorqueMtr3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonResetTorqueMtr3, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 570, 100, -1));

        jButtonEnableTorqueAllMotors.setText("enable all torque");
        jButtonEnableTorqueAllMotors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEnableTorqueAllMotorsActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonEnableTorqueAllMotors, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 640, -1, -1));

        jButtonDisableTorqueAllMotors.setText("diable all torque");
        jButtonDisableTorqueAllMotors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDisableTorqueAllMotorsActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDisableTorqueAllMotors, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 610, 120, -1));

        jToggleButtonRecorYorN.setText("Record Positions");
        jToggleButtonRecorYorN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonRecorYorNActionPerformed(evt);
            }
        });
        getContentPane().add(jToggleButtonRecorYorN, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        jButtonRecord1Position.setText("Record 1 Position");
        jButtonRecord1Position.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRecord1PositionActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonRecord1Position, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        jButtonRunASequence1.setText("run ex1 sequence");
        jButtonRunASequence1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunASequence1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonRunASequence1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 145, -1));

        jTextFieldRecordScenarioName.setText("record_scenario_name");
        getContentPane().add(jTextFieldRecordScenarioName, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 156, -1));

        jButtonRunASequence2.setText("run ex2 sequence");
        jButtonRunASequence2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunASequence2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonRunASequence2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 145, -1));

        jTextFieldSpeedMotor1.setText("150");
        jTextFieldSpeedMotor1.setMinimumSize(new java.awt.Dimension(12, 25));
        jTextFieldSpeedMotor1.setPreferredSize(new java.awt.Dimension(25, 28));
        jTextFieldSpeedMotor1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSpeedMotor1ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldSpeedMotor1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 190, 50, -1));

        jTextFieldSpeedMotor2.setText("20");
        jTextFieldSpeedMotor2.setMinimumSize(new java.awt.Dimension(12, 25));
        jTextFieldSpeedMotor2.setPreferredSize(new java.awt.Dimension(25, 28));
        jTextFieldSpeedMotor2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSpeedMotor2ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldSpeedMotor2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 190, 50, -1));

        jTextFieldSpeedMotor3.setText("15");
        jTextFieldSpeedMotor3.setMinimumSize(new java.awt.Dimension(12, 25));
        jTextFieldSpeedMotor3.setPreferredSize(new java.awt.Dimension(25, 28));
        jTextFieldSpeedMotor3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSpeedMotor3ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldSpeedMotor3, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 190, 50, -1));

        jTextFieldSpeedMotor4.setText("8");
        jTextFieldSpeedMotor4.setMinimumSize(new java.awt.Dimension(12, 25));
        jTextFieldSpeedMotor4.setPreferredSize(new java.awt.Dimension(25, 28));
        jTextFieldSpeedMotor4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSpeedMotor4ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldSpeedMotor4, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 190, 50, -1));

        jLabel1.setText("Speed");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 200, -1, -1));

        jTextField_IncrementMtr0.setText("250");
        jTextField_IncrementMtr0.setMinimumSize(new java.awt.Dimension(12, 25));
        jTextField_IncrementMtr0.setPreferredSize(new java.awt.Dimension(25, 28));
        getContentPane().add(jTextField_IncrementMtr0, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 220, 50, -1));

        jTextField_IncrementMtr1.setText("25");
        jTextField_IncrementMtr1.setMinimumSize(new java.awt.Dimension(12, 25));
        jTextField_IncrementMtr1.setPreferredSize(new java.awt.Dimension(25, 28));
        getContentPane().add(jTextField_IncrementMtr1, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 220, 50, -1));

        jTextField_IncrementMtr2.setText("25");
        jTextField_IncrementMtr2.setMinimumSize(new java.awt.Dimension(12, 25));
        jTextField_IncrementMtr2.setPreferredSize(new java.awt.Dimension(25, 28));
        getContentPane().add(jTextField_IncrementMtr2, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 220, 50, -1));

        jTextField_IncrementMtr3.setText("10");
        jTextField_IncrementMtr3.setMinimumSize(new java.awt.Dimension(12, 25));
        jTextField_IncrementMtr3.setPreferredSize(new java.awt.Dimension(25, 28));
        getContentPane().add(jTextField_IncrementMtr3, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 220, 50, -1));

        jLabel_Increment.setText("Increment");
        jLabel_Increment.setToolTipText("How much does the motor move on one click.");
        getContentPane().add(jLabel_Increment, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 230, 70, -1));

        jTextField_PositionMtr3.setText("Position");
        getContentPane().add(jTextField_PositionMtr3, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 250, 50, -1));

        jTextField_PositionMtr0.setText("Position");
        getContentPane().add(jTextField_PositionMtr0, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 250, 50, -1));

        jTextField_PositionMtr1.setText("Position");
        getContentPane().add(jTextField_PositionMtr1, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 250, 50, -1));

        jTextField_PositionMtr2.setText("Position");
        getContentPane().add(jTextField_PositionMtr2, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, 50, -1));

        jLabel2.setText("Pos Read");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 260, -1, -1));

        jLabel3.setText("Target Position");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 280, -1, 30));

        jToggleButtonPrintDebugging.setText("Print Debugging");
        jToggleButtonPrintDebugging.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonPrintDebuggingActionPerformed(evt);
            }
        });
        getContentPane().add(jToggleButtonPrintDebugging, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 540, -1, -1));

        jLabel4.setText("Elbow");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 10, 50, 30));

        jToggleButton_FollowDriverButton.setText("Follow Driver Arm");
        jToggleButton_FollowDriverButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton_FollowDriverButtonActionPerformed(evt);
            }
        });
        getContentPane().add(jToggleButton_FollowDriverButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 40, -1, -1));

        jLabel5.setText("Load");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 310, 70, 20));

        jLabel6.setText("End Effector");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, 60, 30));

        jLabel7.setText("Sholder");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(635, 10, 60, 30));

        jTextField_LoadEndEffector.setText("Load");
        jTextField_LoadEndEffector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_LoadEndEffectorActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField_LoadEndEffector, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 310, -1, -1));

        jTextField_Load_1.setText("Load");
        jTextField_Load_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_Load_1ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField_Load_1, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 310, -1, -1));

        jTextField_Load_3.setText("Load");
        jTextField_Load_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_Load_3ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField_Load_3, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 310, -1, -1));

        jTextField_Load_2.setText("Load");
        jTextField_Load_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_Load_2ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField_Load_2, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 310, -1, -1));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Wrist");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, 70, 30));

        jScrollBarMtr4_Actual.setBlockIncrement(20);
        jScrollBarMtr4_Actual.setMaximum(700);
        jScrollBarMtr4_Actual.setMinimum(385);
        jScrollBarMtr4_Actual.setValue(500);
        jScrollBarMtr4_Actual.setFocusable(false);
        jScrollBarMtr4_Actual.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBarMtr4_ActualAdjustmentValueChanged(evt);
            }
        });
        getContentPane().add(jScrollBarMtr4_Actual, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 53, 36, 130));

        jSlider4_ActualTemp.setMaximum(160);
        jSlider4_ActualTemp.setMinimum(110);
        jSlider4_ActualTemp.setMinorTickSpacing(10);
        jSlider4_ActualTemp.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider4_ActualTemp.setPaintLabels(true);
        jSlider4_ActualTemp.setPaintTicks(true);
        getContentPane().add(jSlider4_ActualTemp, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 100, 20, -1));

        jSlider1_ActualTemp.setMaximum(160);
        jSlider1_ActualTemp.setMinimum(110);
        jSlider1_ActualTemp.setMinorTickSpacing(10);
        jSlider1_ActualTemp.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider1_ActualTemp.setPaintLabels(true);
        jSlider1_ActualTemp.setPaintTicks(true);
        jSlider1_ActualTemp.setToolTipText("");
        getContentPane().add(jSlider1_ActualTemp, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 100, 20, -1));

        jSlider2_ActualTemp.setMaximum(160);
        jSlider2_ActualTemp.setMinimum(110);
        jSlider2_ActualTemp.setMinorTickSpacing(10);
        jSlider2_ActualTemp.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider2_ActualTemp.setPaintLabels(true);
        jSlider2_ActualTemp.setPaintTicks(true);
        getContentPane().add(jSlider2_ActualTemp, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 100, 20, -1));

        jSlider3_ActualTemp.setMaximum(160);
        jSlider3_ActualTemp.setMinimum(110);
        jSlider3_ActualTemp.setMinorTickSpacing(10);
        jSlider3_ActualTemp.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider3_ActualTemp.setPaintLabels(true);
        jSlider3_ActualTemp.setPaintTicks(true);
        getContentPane().add(jSlider3_ActualTemp, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 100, 20, -1));

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Temperature Sensors. \nShutdown occurs at 158 F (70 C).");
        jTextArea1.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 310, 230, 100));

        jTextField2.setText("140 F");
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 170, -1, -1));

        jTextField3.setText("110 F");
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 275, -1, -1));

        jTextField4.setText("160 F");
        getContentPane().add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 100, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonMoveMtrToPresetHighActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMoveMtrToPresetHighActionPerformed
        motor1.moveMotor((short)400);
        jLabelMotor1.setText(""+400);
    }//GEN-LAST:event_jButtonMoveMtrToPresetHighActionPerformed

    private void jButtonMoveMtr0ToPresetLowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMoveMtr0ToPresetLowActionPerformed
        motor1.moveMotor((short)150);
        jLabelMotor1.setText(""+150);
    }//GEN-LAST:event_jButtonMoveMtr0ToPresetLowActionPerformed

    private void jScrollBarMtr0AdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBarMtr0AdjustmentValueChanged
        short j = (short)jScrollBarMtr0.getValue();
        motor1.moveMotor(j);
        TargetPosMtr0 = (int)j;
        jLabelMotor1.setText(""+j);
    }//GEN-LAST:event_jScrollBarMtr0AdjustmentValueChanged

    private void jScrollBarMtr1AdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBarMtr1AdjustmentValueChanged
        short j = (short)jScrollBarMtr1.getValue();
        motor2.moveMotor(j);
        TargetPosMtr1 = (int)j;
        jLabelMotor2.setText(""+j);
    }//GEN-LAST:event_jScrollBarMtr1AdjustmentValueChanged

    private void jScrollBarMtr2AdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBarMtr2AdjustmentValueChanged
        short j = (short)jScrollBarMtr2.getValue();
        motor3.moveMotor(j);
        TargetPosMtr2 = (int)j;
        jLabelMotor3.setText(""+j);
    }//GEN-LAST:event_jScrollBarMtr2AdjustmentValueChanged

    private void jScrollBarMtr4_TargetAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBarMtr4_TargetAdjustmentValueChanged
        short j = (short)jScrollBarMtr4_Target.getValue();
        motor4.moveMotor(j);
        TargetPosMtr3 = (int)j;
        jLabelMotor4.setText(""+j);
    }//GEN-LAST:event_jScrollBarMtr4_TargetAdjustmentValueChanged

    private void jButtonStepUpMtr0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStepUpMtr0ActionPerformed
        int val = Integer.valueOf(jTextFieldSpeedMotor1.getText());
        motor1.setMovingSpeed(val);
        
        int moveIncrement = -100;
        moveIncrement = -Integer.parseInt(jTextField_IncrementMtr0.getText());
        //short currentPos = motor1.readPosition(); // currentPos is a local reading - testing to see if using the more global variable is better
        System.out.println("currentPosXXXXX "+ position1 + "; moveIncrement "+moveIncrement +" TargetPosMtr0 "+TargetPosMtr0 );
        TargetPosMtr0 = TargetPosMtr0 + moveIncrement;
        jScrollBarMtr0.setValue(TargetPosMtr0);
        motor1.moveMotor((short)(TargetPosMtr0));
         try {
             sleep(1);
         } catch (InterruptedException ex) {
             Logger.getLogger(JFrameClass.class.getName()).log(Level.SEVERE, null, ex);
         }
        jLabelMotor1.setText(TargetPosMtr0+"");
    }//GEN-LAST:event_jButtonStepUpMtr0ActionPerformed

    private void jButtonStepDownMtr0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStepDownMtr0ActionPerformed
        int val = Integer.valueOf(jTextFieldSpeedMotor1.getText());
        motor1.setMovingSpeed(val);
        
        int moveIncrement = 100;
        moveIncrement = Integer.parseInt(jTextField_IncrementMtr0.getText());
        short currentPos = motor1.readPosition();
        System.out.println("currentPos "+ currentPos + "; moveIncrement "+moveIncrement +" TargetPosMtr0 "+TargetPosMtr0 );
        TargetPosMtr0 = TargetPosMtr0 + moveIncrement;
        jScrollBarMtr0.setValue(TargetPosMtr0);
        motor1.moveMotor((short)(TargetPosMtr0));
         try {
             sleep(1);
         } catch (InterruptedException ex) {
             Logger.getLogger(JFrameClass.class.getName()).log(Level.SEVERE, null, ex);
         }
        jLabelMotor1.setText(TargetPosMtr0+"");
    }//GEN-LAST:event_jButtonStepDownMtr0ActionPerformed

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        dynamixel.closePort(port_num);
        try {
            sleep(750);
        } catch (InterruptedException ex) {
            Logger.getLogger(JFrameClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(0);
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jToggleButtonMtr0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonMtr0ActionPerformed
        motor1.setTorque(0);
        motor1.disableTorque();
    }//GEN-LAST:event_jToggleButtonMtr0ActionPerformed

    private void jButtonResetTorqueMtr0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetTorqueMtr0ActionPerformed
        motor1.enableTorque();
        motor1.setTorque();
    }//GEN-LAST:event_jButtonResetTorqueMtr0ActionPerformed

    private void jButtonMoveMtr1ToPresetHighActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMoveMtr1ToPresetHighActionPerformed
        motor2.moveMotor((short)400);
        jLabelMotor2.setText(""+400);
    }//GEN-LAST:event_jButtonMoveMtr1ToPresetHighActionPerformed

    private void jButtonStepUpMtr1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStepUpMtr1ActionPerformed
        int val = Integer.valueOf(jTextFieldSpeedMotor2.getText());
        motor2.setMovingSpeed(val);
        
        int move_direction = -1; // set this to 1 or -1 to switch direction
        int moveIncrement = 25;
        moveIncrement = move_direction * Integer.parseInt(jTextField_IncrementMtr1.getText());
        short currentPos = motor2.readPosition();
        System.out.println("currentPos "+ currentPos + "; moveIncrement "+moveIncrement +" TargetPosMtr1 "+TargetPosMtr1 );
        TargetPosMtr1 = TargetPosMtr1 + moveIncrement;
        jScrollBarMtr1.setValue(TargetPosMtr1);
        motor2.moveMotor((short)(TargetPosMtr1));
         try {
             sleep(1);
         } catch (InterruptedException ex) {
             Logger.getLogger(JFrameClass.class.getName()).log(Level.SEVERE, null, ex);
         }
        jLabelMotor2.setText(TargetPosMtr1+"");
    }//GEN-LAST:event_jButtonStepUpMtr1ActionPerformed

    private void jButtonStepDownMtr1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStepDownMtr1ActionPerformed
        int val = Integer.valueOf(jTextFieldSpeedMotor2.getText());
        motor2.setMovingSpeed(val);
        
        int move_direction = 1; // set this to 1 or -1 to switch direction
        int moveIncrement = 25;
        moveIncrement = move_direction * Integer.parseInt(jTextField_IncrementMtr1.getText());
        short currentPos = motor2.readPosition();
        System.out.println("currentPos "+ currentPos + "; moveIncrement "+moveIncrement +" TargetPosMtr1 "+TargetPosMtr1 );
        TargetPosMtr1 = TargetPosMtr1 + moveIncrement;
        jScrollBarMtr1.setValue(TargetPosMtr1);
        motor2.moveMotor((short)(TargetPosMtr1));
         try {
             sleep(1);
         } catch (InterruptedException ex) {
             Logger.getLogger(JFrameClass.class.getName()).log(Level.SEVERE, null, ex);
         }
        jLabelMotor2.setText(TargetPosMtr1+"");
    }//GEN-LAST:event_jButtonStepDownMtr1ActionPerformed

    private void jButtonMoveMtr1ToPresetLowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMoveMtr1ToPresetLowActionPerformed
        motor2.moveMotor((short)150);
        jLabelMotor2.setText(""+150);
    }//GEN-LAST:event_jButtonMoveMtr1ToPresetLowActionPerformed

    private void jToggleButtonMtr1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonMtr1ActionPerformed
        motor2.setTorque(0);
        motor2.disableTorque();
    }//GEN-LAST:event_jToggleButtonMtr1ActionPerformed

    private void jButtonResetTorqueMtr1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetTorqueMtr1ActionPerformed
        motor2.enableTorque();
        motor2.setTorque();
    }//GEN-LAST:event_jButtonResetTorqueMtr1ActionPerformed

    private void jButtonMoveMtr2ToPresetHighActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMoveMtr2ToPresetHighActionPerformed
        motor3.moveMotor((short)340);
        jLabelMotor3.setText(""+340);
    }//GEN-LAST:event_jButtonMoveMtr2ToPresetHighActionPerformed

    private void jButtonStepUpMtr2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStepUpMtr2ActionPerformed
        int val = Integer.valueOf(jTextFieldSpeedMotor3.getText());
        motor3.setMovingSpeed(val);
        
        int move_direction = -1; // set this to 1 or -1 to switch direction
        int moveIncrement = 25;
        moveIncrement = move_direction * Integer.parseInt(jTextField_IncrementMtr2.getText());
        short currentPos = motor3.readPosition();
        System.out.println("currentPos "+ currentPos + "; moveIncrement "+moveIncrement +" TargetPosMtr2 "+TargetPosMtr2 );
        TargetPosMtr2 = TargetPosMtr2 + moveIncrement;
        jScrollBarMtr2.setValue(TargetPosMtr2);
        motor3.moveMotor((short)(TargetPosMtr2));
         try {
             sleep(1);
         } catch (InterruptedException ex) {
             Logger.getLogger(JFrameClass.class.getName()).log(Level.SEVERE, null, ex);
         }
        jLabelMotor3.setText(TargetPosMtr2+"");
    }//GEN-LAST:event_jButtonStepUpMtr2ActionPerformed

    private void jButtonStepDownMtr2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStepDownMtr2ActionPerformed
        int val = Integer.valueOf(jTextFieldSpeedMotor3.getText());
        motor3.setMovingSpeed(val);
        
        int move_direction = 1; // set this to 1 or -1 to switch direction
        int moveIncrement = 25;
        moveIncrement = move_direction * Integer.parseInt(jTextField_IncrementMtr2.getText());
        short currentPos = motor3.readPosition();
        System.out.println("currentPos "+ currentPos + "; moveIncrement "+moveIncrement +" TargetPosMtr2 "+TargetPosMtr2 );
        TargetPosMtr2 = TargetPosMtr2 + moveIncrement;
        jScrollBarMtr2.setValue(TargetPosMtr2);
        motor3.moveMotor((short)(TargetPosMtr2));
         try {
             sleep(1);
         } catch (InterruptedException ex) {
             Logger.getLogger(JFrameClass.class.getName()).log(Level.SEVERE, null, ex);
         }
        jLabelMotor3.setText(TargetPosMtr2+"");
    }//GEN-LAST:event_jButtonStepDownMtr2ActionPerformed

    private void jButtonMoveMtr2ToPresetLowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMoveMtr2ToPresetLowActionPerformed
        motor3.moveMotor((short)720);
        jLabelMotor3.setText(""+720);
    }//GEN-LAST:event_jButtonMoveMtr2ToPresetLowActionPerformed

    private void jToggleButtonMtr2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonMtr2ActionPerformed
        motor3.setTorque(0);
        motor3.disableTorque();
    }//GEN-LAST:event_jToggleButtonMtr2ActionPerformed

    private void jButtonResetTorqueMtr2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetTorqueMtr2ActionPerformed
        motor3.enableTorque();
        motor3.setTorque();
    }//GEN-LAST:event_jButtonResetTorqueMtr2ActionPerformed

    private void jButtonMoveMtr3ToPresetHighActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMoveMtr3ToPresetHighActionPerformed
        motor4.moveMotor((short)700);
        jLabelMotor4.setText(""+700);
    }//GEN-LAST:event_jButtonMoveMtr3ToPresetHighActionPerformed

    private void jButtonStepUpMtr3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStepUpMtr3ActionPerformed
        int val = Integer.valueOf(jTextFieldSpeedMotor4.getText());
        motor4.setMovingSpeed(val);
        
        int moveIncrement = -25;
        moveIncrement = -Integer.parseInt(jTextField_IncrementMtr3.getText());
        short currentPos = motor4.readPosition();
        System.out.println("currentPos "+ currentPos + "; moveIncrement "+moveIncrement +" TargetPosMtr3 "+TargetPosMtr3 );
        TargetPosMtr3 = TargetPosMtr3 + moveIncrement;
        jScrollBarMtr4_Target.setValue(TargetPosMtr3);
        motor4.moveMotor((short)(TargetPosMtr3));
         try {
             sleep(1);
         } catch (InterruptedException ex) {
             Logger.getLogger(JFrameClass.class.getName()).log(Level.SEVERE, null, ex);
         }
        jLabelMotor4.setText(TargetPosMtr3+"");
        
        jScrollBarMtr4_Target.setValue(TargetPosMtr3);
    }//GEN-LAST:event_jButtonStepUpMtr3ActionPerformed

    private void jButtonStepDownMtr3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStepDownMtr3ActionPerformed
        int val = Integer.valueOf(jTextFieldSpeedMotor4.getText());
        motor4.setMovingSpeed(val);
        
        int moveIncrement = 25;
        moveIncrement = Integer.parseInt(jTextField_IncrementMtr3.getText());
        short currentPos = motor4.readPosition();
        System.out.println("currentPos "+ currentPos + "; moveIncrement "+moveIncrement +" TargetPosMtr3 "+TargetPosMtr3 );
        TargetPosMtr3 = TargetPosMtr3 + moveIncrement;
        jScrollBarMtr4_Target.setValue(TargetPosMtr3);
        motor4.moveMotor((short)(TargetPosMtr3));
         try {
             sleep(1);
         } catch (InterruptedException ex) {
             Logger.getLogger(JFrameClass.class.getName()).log(Level.SEVERE, null, ex);
         }
        jLabelMotor4.setText(TargetPosMtr3+"");
        
        jScrollBarMtr4_Target.setValue(TargetPosMtr3);
    }//GEN-LAST:event_jButtonStepDownMtr3ActionPerformed

    private void jButtonMoveMtr3ToPresetLowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMoveMtr3ToPresetLowActionPerformed
        motor4.moveMotor((short)250);
        jLabelMotor4.setText(""+250);
    }//GEN-LAST:event_jButtonMoveMtr3ToPresetLowActionPerformed

    private void jToggleButtonMtr3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonMtr3ActionPerformed
        motor4.setTorque(0);
        motor4.disableTorque();
    }//GEN-LAST:event_jToggleButtonMtr3ActionPerformed

    private void jButtonResetTorqueMtr3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetTorqueMtr3ActionPerformed
        motor4.enableTorque();
        motor4.setTorque();
    }//GEN-LAST:event_jButtonResetTorqueMtr3ActionPerformed

    private void jButtonEnableTorqueAllMotorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEnableTorqueAllMotorsActionPerformed
        motor1.enableTorque();
        motor2.enableTorque();
        motor3.enableTorque();
        motor4.enableTorque();
    }//GEN-LAST:event_jButtonEnableTorqueAllMotorsActionPerformed

    private void jButtonDisableTorqueAllMotorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDisableTorqueAllMotorsActionPerformed
        
        motor1.disableTorque();
        motor2.disableTorque();
        motor3.disableTorque();
        motor4.disableTorque();
    }//GEN-LAST:event_jButtonDisableTorqueAllMotorsActionPerformed

    private void jToggleButtonRecorYorNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonRecorYorNActionPerformed
        if(jToggleButtonRecorYorN.isSelected()){
            recordPositions = 1;
        }
        if(!jToggleButtonRecorYorN.isSelected()){
            recordPositions = 0;
        }
    }//GEN-LAST:event_jToggleButtonRecorYorNActionPerformed

    private void jButtonRecord1PositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRecord1PositionActionPerformed
        recordOnce = 1;
    }//GEN-LAST:event_jButtonRecord1PositionActionPerformed

    private void jButtonRunASequence1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunASequence1ActionPerformed
        String scenarioReadName = "a2";
        int loop = 0;
        

    }//GEN-LAST:event_jButtonRunASequence1ActionPerformed

    private void jButtonRunASequence2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunASequence2ActionPerformed
        String scenarioReadName = "example2";
        
    }//GEN-LAST:event_jButtonRunASequence2ActionPerformed

    private void jTextFieldSpeedMotor1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSpeedMotor1ActionPerformed
        int val = Integer.valueOf(jTextFieldSpeedMotor1.getText());
        System.out.println("speed set to: "+val);
        motor1.setMovingSpeed(val);
    }//GEN-LAST:event_jTextFieldSpeedMotor1ActionPerformed

    private void jTextFieldSpeedMotor2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSpeedMotor2ActionPerformed
        int val = Integer.valueOf(jTextFieldSpeedMotor2.getText());
        System.out.println("speed set to: "+val);
        motor2.setMovingSpeed(val);
    }//GEN-LAST:event_jTextFieldSpeedMotor2ActionPerformed

    private void jTextFieldSpeedMotor3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSpeedMotor3ActionPerformed
        int val = Integer.valueOf(jTextFieldSpeedMotor3.getText());
        System.out.println("speed set to: "+val);
        motor3.setMovingSpeed(val);
    }//GEN-LAST:event_jTextFieldSpeedMotor3ActionPerformed

    private void jTextFieldSpeedMotor4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSpeedMotor4ActionPerformed
        int val = Integer.valueOf(jTextFieldSpeedMotor4.getText());
        System.out.println("speed set to: "+val);
        motor4.setMovingSpeed(val);
    }//GEN-LAST:event_jTextFieldSpeedMotor4ActionPerformed

    private void jToggleButtonPrintDebuggingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonPrintDebuggingActionPerformed
        boolean togglevalue = jToggleButtonPrintDebugging.isSelected();
        motor1.printDebugging(togglevalue);
        motor2.printDebugging(togglevalue);
        motor3.printDebugging(togglevalue);
        motor4.printDebugging(togglevalue);
    }//GEN-LAST:event_jToggleButtonPrintDebuggingActionPerformed

    private void jToggleButton_FollowDriverButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton_FollowDriverButtonActionPerformed
        if (FollowDriverFlag){
            FollowDriverFlag = false;
            jToggleButton_FollowDriverButton.setText("Not Following Arm");
            jToggleButton_FollowDriverButton.setOpaque(true);
            jToggleButton_FollowDriverButton.setBackground(Color.green);
        }
        else {
            FollowDriverFlag = true;
            System.out.println("");
            System.out.println("------------ Begin to follow other arm -----------------");
            jToggleButton_FollowDriverButton.setText("Following Arm");
            jToggleButton_FollowDriverButton.setOpaque(true);
            jToggleButton_FollowDriverButton.setBackground(Color.red);
        }
    }//GEN-LAST:event_jToggleButton_FollowDriverButtonActionPerformed

    private void jTextField_LoadEndEffectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_LoadEndEffectorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_LoadEndEffectorActionPerformed

    private void jTextField_Load_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_Load_1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_Load_1ActionPerformed

    private void jTextField_Load_3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_Load_3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_Load_3ActionPerformed

    private void jTextField_Load_2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_Load_2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_Load_2ActionPerformed

    private void jScrollBarMtr4_ActualAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBarMtr4_ActualAdjustmentValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollBarMtr4_ActualAdjustmentValueChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrameClass.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrameClass.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrameClass.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrameClass.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        
    
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrameClass().setVisible(true);
        
            }
        });
        
    }

    abstract class PausableSwingWorker<K, V> extends SwingWorker<K, V> {
        private volatile boolean isPaused;
        public final void pause() {
            if (!isPaused() && !isDone()) {
                isPaused = true;
                firePropertyChange("paused", false, true);
            }
        }

        public final void resume23() {
            if (isPaused() && !isDone()) {
                isPaused = false;
                firePropertyChange("paused", true, false);
            }
        }

        public final boolean isPaused() {
            return isPaused;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDisableTorqueAllMotors;
    private javax.swing.JButton jButtonEnableTorqueAllMotors;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonMoveMtr0ToPresetLow;
    private javax.swing.JButton jButtonMoveMtr1ToPresetHigh;
    private javax.swing.JButton jButtonMoveMtr1ToPresetLow;
    private javax.swing.JButton jButtonMoveMtr2ToPresetHigh;
    private javax.swing.JButton jButtonMoveMtr2ToPresetLow;
    private javax.swing.JButton jButtonMoveMtr3ToPresetHigh;
    private javax.swing.JButton jButtonMoveMtr3ToPresetLow;
    private javax.swing.JButton jButtonMoveMtrToPresetHigh;
    private javax.swing.JButton jButtonRecord1Position;
    private javax.swing.JButton jButtonResetTorqueMtr0;
    private javax.swing.JButton jButtonResetTorqueMtr1;
    private javax.swing.JButton jButtonResetTorqueMtr2;
    private javax.swing.JButton jButtonResetTorqueMtr3;
    private javax.swing.JButton jButtonRunASequence1;
    private javax.swing.JButton jButtonRunASequence2;
    private javax.swing.JButton jButtonStepDownMtr0;
    private javax.swing.JButton jButtonStepDownMtr1;
    private javax.swing.JButton jButtonStepDownMtr2;
    private javax.swing.JButton jButtonStepDownMtr3;
    private javax.swing.JButton jButtonStepUpMtr0;
    private javax.swing.JButton jButtonStepUpMtr1;
    private javax.swing.JButton jButtonStepUpMtr2;
    private javax.swing.JButton jButtonStepUpMtr3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelMotor1;
    private javax.swing.JLabel jLabelMotor2;
    private javax.swing.JLabel jLabelMotor3;
    private javax.swing.JLabel jLabelMotor4;
    private javax.swing.JLabel jLabel_Increment;
    private javax.swing.JScrollBar jScrollBarMtr0;
    private javax.swing.JScrollBar jScrollBarMtr1;
    private javax.swing.JScrollBar jScrollBarMtr2;
    private javax.swing.JScrollBar jScrollBarMtr4_Actual;
    private javax.swing.JScrollBar jScrollBarMtr4_Target;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSlider jSlider1_ActualTemp;
    private javax.swing.JSlider jSlider2_ActualTemp;
    private javax.swing.JSlider jSlider3_ActualTemp;
    private javax.swing.JSlider jSlider4_ActualTemp;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextFieldRecordScenarioName;
    private javax.swing.JTextField jTextFieldSpeedMotor1;
    private javax.swing.JTextField jTextFieldSpeedMotor2;
    private javax.swing.JTextField jTextFieldSpeedMotor3;
    private javax.swing.JTextField jTextFieldSpeedMotor4;
    private javax.swing.JTextField jTextField_IncrementMtr0;
    private javax.swing.JTextField jTextField_IncrementMtr1;
    private javax.swing.JTextField jTextField_IncrementMtr2;
    private javax.swing.JTextField jTextField_IncrementMtr3;
    private javax.swing.JTextField jTextField_LoadEndEffector;
    private javax.swing.JTextField jTextField_Load_1;
    private javax.swing.JTextField jTextField_Load_2;
    private javax.swing.JTextField jTextField_Load_3;
    private javax.swing.JTextField jTextField_PositionMtr0;
    private javax.swing.JTextField jTextField_PositionMtr1;
    private javax.swing.JTextField jTextField_PositionMtr2;
    private javax.swing.JTextField jTextField_PositionMtr3;
    private javax.swing.JToggleButton jToggleButtonMtr0;
    private javax.swing.JToggleButton jToggleButtonMtr1;
    private javax.swing.JToggleButton jToggleButtonMtr2;
    private javax.swing.JToggleButton jToggleButtonMtr3;
    private javax.swing.JToggleButton jToggleButtonPrintDebugging;
    private javax.swing.JToggleButton jToggleButtonRecorYorN;
    private javax.swing.JToggleButton jToggleButton_FollowDriverButton;
    // End of variables declaration//GEN-END:variables
}
