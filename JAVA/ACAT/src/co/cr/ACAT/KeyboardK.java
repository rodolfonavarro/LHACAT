/*
 * Copyright (C) 2019 Rodolfo Navarro B.
 *
 * This program is free software: you can redistribute it and/or modify
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 */
package co.cr.ACAT;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import static java.lang.Thread.sleep;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



/**
 *
 * @author Rodolfo Navarro
 */


public class KeyboardK extends javax.swing.JFrame  {

    /**
     * Creates new form keyboard
     * Variables definitions
     */
    private final static Color DEFAULT_KEY_COLOR = new Color(-13421773);
    private final static Color SELECTED_COLOR = new Color(-15304792); //Dark Sky blue
    private final ArrayList<JButton> buttons;
    private final ArrayList<JTextField> jtDW;
    private javax.swing.JTextArea editText;
    private static Robot robot = null;
    static { 
        try {
            robot = new Robot();
        }catch(AWTException ex) { }
    }
    private final int ind_number = 0;
    private String ktype="kn3";
    private final String syl_span="aeioun";
    private Boolean Ind_Caps=true;
    private Boolean paused=false;
    private final Timer mtcycle;
    private String jpFocus;
    private String jpFocus1;
    private String jpFocus2;
    private String jpFocus3;
    private int indT=0;
    private JButton keySel;
    private final String[] jpnames ={"FixedWords","DictWords","Keyboard"};
    private List<String> modelstr;
    private String lastword=" ";
    private Process p;
    
    public KeyboardK() {
        
        initComponents();
        
        setLocationToBottom();
        
        buttons =  new ArrayList<>();
        jtDW= new ArrayList<>();
        
        setIconImage((new ImageIcon(getClass().getResource("/co/cr/ACAT/Images/ic_launcher.png"))).getImage());
        InitButtons();

        mtcycle = new Timer();
        mtcycle.scheduleAtFixedRate(timerTask, 0, 500);
        /*Init Festival for express de words*/
        try {
            this.p = Runtime.getRuntime().exec("festival");
        } catch (IOException ex) {
            Logger.getLogger(KeyboardK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void InitButtons(){
        /*Store buttons and textfield in keyboard*/
        for(Component component : this.jpKB1.getComponents()) {
            if (component instanceof JButton){
                JButton myb =(JButton) component;
                if (myb.getText().length()==1){
                    buttons.add(myb);
                }
            }
        }
        for(Component component : this.jpKB2.getComponents()) {
            if (component instanceof JButton){
                JButton myb =(JButton) component;
                if (myb.getText().length()==1){
                    buttons.add(myb);
                }
            }
        }
        for(Component component : this.jpKB3.getComponents()) {
            if (component instanceof JButton){
                JButton myb =(JButton) component;
                if (myb.getText().length()==1){
                    buttons.add(myb);
                }
            }
        }
        for (Component component : this.jpDictWords.getComponents()){
            if (component instanceof JPanel){
                for (Component componentint : ((JPanel) component).getComponents()){
                    if (componentint instanceof JTextField){
                        JTextField myb =(JTextField) componentint;
                        jtDW.add(myb);
                    }
                }
            }
            
        }
        String lw=LastWord();
        pressageWord(lw);

    }
    
    private void jpCycleUsedW() {
        /*Cycle of the words used*/
        jpFixedWords.setBorder(BorderFactory.createEmptyBorder());
        try {
                sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(KeyboardK.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        for (int i=1;i<6;i++) {
            if (indT==1){
                jpFocus1="jpFW"+ i;
                JPanel jp =Awt1.getComponentByName(this,jpFocus1);
                jp.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.yellow));

                int tms=1500;
                try {
                    sleep(tms);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KeyboardK.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(indT==11){
                    for(Component component : jp.getComponents()) {
                        JTextField key = (JTextField)component;
                        SpeakTxt(key.getText());
                        //System.out.println(key.getText());
                    }
                }
                jp.setBorder(BorderFactory.createEmptyBorder());
            }
        } 
        indT=0;
    }
    
    private void jpCyclePredictW() {
        /*Cycle for words of dictionary*/
        JPanel jpm =Awt1.getComponentByName(this,"jpDictWords");
        jpm.setBorder(BorderFactory.createEmptyBorder());
        for (int i=1;i<11;i++) {
            if (indT==2){
                jpFocus2="jpDW"+ i;
                JPanel jp =Awt1.getComponentByName(this,jpFocus2);
                jp.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.yellow));
                int tms=1500;
                try {
                    sleep(tms);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KeyboardK.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (indT==22){
                    for(Component component : jp.getComponents()) {
                        JTextField key = (JTextField)component;
                        addWord(key.getText());
                    }
                }
                jp.setBorder(BorderFactory.createEmptyBorder());
            }
        }
        indT=0;
    }
    
    private void jpCycleAlpha() {
        /*Cycle of buttons in keyboard*/
        JPanel jpm =Awt1.getComponentByName(this,"jpKeyboard");
        jpm.setBorder(BorderFactory.createEmptyBorder());
        for (int i=1;i<5;i++) {
            if (indT==3){
                jpFocus3="jpKB"+ i;
                JPanel jp =Awt1.getComponentByName(this,jpFocus3);
                jp.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.yellow));
                int tms=1500;
                try {
                    sleep(tms);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KeyboardK.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(indT==33){
                    for(Component component : jp.getComponents()) {
                        JButton key = (JButton)component;
                        if (indT==33){
                            keySel=key;
                            key.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.yellow));
                            try {
                                sleep(tms);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(KeyboardK.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if(indT==333){
                                String bname=keySel.getName();
                                int bval=Integer.parseInt( bname.substring(5));
                                if (bval>26 || bval==20){
                                    switch(bval){
                                        case 20:
                                            if ("kn3".equals(ktype)){
                                                changeCapLock();
                                            } else {
                                                if ("kn1".equals(ktype)){
                                                    ktype="kn2";
                                                } else {
                                                   ktype="kn1"; 
                                                }
                                                changeKeyboard();
                                            }
                                            break;
                                        case 28:
                                            backChar();
                                            break;
                                        case 29:
                                            changeNumKey();
                                            break;
                                        case 30:
                                            armSpeak();
                                            break;
                                        case 31:
                                            deleteWord();
                                            break;
                                        case 32:
                                            arrowLeft();
                                            break;
                                        case 33:
                                            addSpace();
                                            break;
                                        case 34:
                                            arrowRight();
                                            break;
                                        case 35:
                                            addCharKey(keySel.getText());
                                            break;
                                        case 36:
                                            //Settings
                                            ShowSetting();
                                            break;
                                        case 37:
                                            jtaSpeak.setText("");
                                            break;
                                    }
                                } else {
                                    String stkey=keySel.getText();
                                    if (syl_span.contains(stkey)){
                                        CallJDSyl(stkey);
                                    } else {
                                        addCharKey(keySel.getText());
                                    }
                                }
                                indT=0;
                            }
                            key.setBorder(BorderFactory.createEmptyBorder());
                        } 
                    }
                    indT=0;
                }
                jp.setBorder(BorderFactory.createEmptyBorder());
            }    
        }
        indT=0;
    }
    
    private void CallJDSyl(String stkey){
        /*Call jdialog with the letters option*/
        paused=true;
        JDSyl SylFormdialog= new JDSyl(this,true);
        SylFormdialog.ReceiveLetter(stkey);
        SylFormdialog.setLocationRelativeTo(this);
        SylFormdialog.setVisible(true);
        addCharKey(SylFormdialog.kdef);
        paused=false;
    }
    
    private void addCharKey(String btxt){
        /*Add a char in jtextfield for speak*/
        String mtx=jtaSpeak.getText();
        if (mtx.length()>0){
            mtx= mtx +(btxt);
        } else {
            mtx=btxt;
        }
        
        jtaSpeak.setText(mtx);
        jtaSpeak.requestFocusInWindow();
        String lw=LastWord();
        pressageWord(lw);
    }
    
    private String LastWord(){
        /*Return the last word in jtextfield for speak*/
        String oriContentw = jtaSpeak.getText();
        String lwrd="";
        String[] multi_line=oriContentw.split("\n");
        if (multi_line.length>0){
            String[] lw=multi_line[multi_line.length-1].split(" ");
            if (lw.length>0){
                lwrd=lw[lw.length-1];
            }
        }
        return lwrd;
    }
    
    private void pressageWord(String word){
        /*Search words in dictionary*/
        if (lastword == null ? word != null : !lastword.equals(word)){
            lastword=word;
            String whereCond="";
            if (word.trim().length()!=0){
                whereCond=" where lower(word) like '" + word.trim() +"%' ";
            }
            try {
                SQLite_Mantain mr = new SQLite_Mantain();
                JSONArray ja_data = new JSONArray();
                ja_data=mr.execute_sql("SELECT DISTINCT ValNG1,lower(word) wordpres FROM ngram1 " + whereCond +" ORDER BY ngram1.ValNG1 ASC limit 15");
                int iw=0;
                for (int i =0;i<ja_data.size();i++){
                    JSONObject row = new JSONObject();
                    row=(JSONObject)ja_data.get(i);
                    String result_1 = row.get("wordpres").toString();
                    if (iw<10){
                        boolean exw=false;
                        for (int xi=0;xi<=iw;xi++){
                            String mv=jtDW.get(xi).getText();
                            if (mv.compareTo(result_1.trim())==0){
                                exw=true;
                            }
                        }
                        if (exw==false) {
                            jtDW.get(iw).setText(result_1);
                            iw+=1;
                        }
                    }
                }
            } catch (SAXException | SQLException | IOException | ClassNotFoundException ex) {
                Logger.getLogger(KeyboardK.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void changeCapLock(){
        /*Change letters uppercase or lowercase */
        if(Ind_Caps) {
            buttons.forEach((key) -> {
                key.setText(key.getText().toUpperCase());
            });
            Ind_Caps=false;
        }
        else {
            buttons.forEach((key) -> {
                key.setText(key.getText().toLowerCase());
            });
            Ind_Caps=true;
        }
        jtaSpeak.requestFocusInWindow();
    }
    
    private void changeNumKey(){
        /*Change keyboard from letters to number and symbols*/
        if ("kn3".equals(ktype)){
            jbKey29.setText("ABC");
            jbKey20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/co/cr/Keyboard/Images/ic_others.png")));
            ktype="kn1";
        } else if(!"kn3".equals(ktype)){
            ktype="kn3";
            jbKey29.setText("123");
            jbKey20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/co/cr/Keyboard/Images/ic_caplock.png")));
        }
        changeKeyboard();
    }
    
    private void backChar(){
        /*Delete a char in jtextfield to speak*/
        if (this.jtaSpeak.getText().length()>0){
            String oriContent = jtaSpeak.getText();
            int startb = jtaSpeak.getSelectionStart();
            int endb = jtaSpeak.getSelectionEnd();
            int index = jtaSpeak.getSelectionStart() >= 0 ? jtaSpeak.getSelectionStart() : 0;
            StringBuilder builder = new StringBuilder(oriContent);
            if (index>0){
                builder.delete(index-1, endb);
                jtaSpeak.setText(builder.toString());
                jtaSpeak.setSelectionStart(index-1 );
                jtaSpeak.setSelectionEnd(index-1 );
            }
        }
        jtaSpeak.requestFocusInWindow();
        String lw=LastWord();
        pressageWord(lw);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpMain = new javax.swing.JPanel();
        jpSpeak = new javax.swing.JPanel();
        jspSpeak = new javax.swing.JScrollPane();
        jtaSpeak = new javax.swing.JTextArea();
        jpFixedWords = new javax.swing.JPanel();
        jpFW1 = new javax.swing.JPanel();
        jtWU1 = new javax.swing.JTextField();
        jpFW2 = new javax.swing.JPanel();
        jtWU2 = new javax.swing.JTextField();
        jpFW3 = new javax.swing.JPanel();
        jtWU3 = new javax.swing.JTextField();
        jpFW4 = new javax.swing.JPanel();
        jtWU4 = new javax.swing.JTextField();
        jpFW5 = new javax.swing.JPanel();
        jtWU5 = new javax.swing.JTextField();
        jpDictWords = new javax.swing.JPanel();
        jpDW1 = new javax.swing.JPanel();
        jtfDW1 = new javax.swing.JTextField();
        jpDW2 = new javax.swing.JPanel();
        jtfDW2 = new javax.swing.JTextField();
        jpDW3 = new javax.swing.JPanel();
        jtfDW3 = new javax.swing.JTextField();
        jpDW4 = new javax.swing.JPanel();
        jtfDW4 = new javax.swing.JTextField();
        jpDW5 = new javax.swing.JPanel();
        jtfDW5 = new javax.swing.JTextField();
        jpDW6 = new javax.swing.JPanel();
        jtfDW6 = new javax.swing.JTextField();
        jpDW7 = new javax.swing.JPanel();
        jtfDW7 = new javax.swing.JTextField();
        jpDW8 = new javax.swing.JPanel();
        jtfDW8 = new javax.swing.JTextField();
        jpDW9 = new javax.swing.JPanel();
        jtfDW9 = new javax.swing.JTextField();
        jpDW10 = new javax.swing.JPanel();
        jtfDW10 = new javax.swing.JTextField();
        jpKeyboard = new javax.swing.JPanel();
        jpKB1 = new javax.swing.JPanel();
        jbKey1 = new javax.swing.JButton();
        jbKey2 = new javax.swing.JButton();
        jbKey3 = new javax.swing.JButton();
        jbKey4 = new javax.swing.JButton();
        jbKey5 = new javax.swing.JButton();
        jbKey6 = new javax.swing.JButton();
        jbKey7 = new javax.swing.JButton();
        jbKey8 = new javax.swing.JButton();
        jbKey9 = new javax.swing.JButton();
        jbKey10 = new javax.swing.JButton();
        jpKB2 = new javax.swing.JPanel();
        jbKey11 = new javax.swing.JButton();
        jbKey12 = new javax.swing.JButton();
        jbKey13 = new javax.swing.JButton();
        jbKey14 = new javax.swing.JButton();
        jbKey15 = new javax.swing.JButton();
        jbKey16 = new javax.swing.JButton();
        jbKey17 = new javax.swing.JButton();
        jbKey18 = new javax.swing.JButton();
        jbKey19 = new javax.swing.JButton();
        jpKB3 = new javax.swing.JPanel();
        jbKey20 = new javax.swing.JButton();
        jbKey21 = new javax.swing.JButton();
        jbKey22 = new javax.swing.JButton();
        jbKey23 = new javax.swing.JButton();
        jbKey24 = new javax.swing.JButton();
        jbKey25 = new javax.swing.JButton();
        jbKey26 = new javax.swing.JButton();
        jbKey27 = new javax.swing.JButton();
        jbKey28 = new javax.swing.JButton();
        jpKB4 = new javax.swing.JPanel();
        jbKey29 = new javax.swing.JButton();
        jbKey30 = new javax.swing.JButton();
        jbKey31 = new javax.swing.JButton();
        jbKey32 = new javax.swing.JButton();
        jbKey33 = new javax.swing.JButton();
        jbKey34 = new javax.swing.JButton();
        jbKey35 = new javax.swing.JButton();
        jbKey36 = new javax.swing.JButton();
        jbKey37 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ACAT");
        setAlwaysOnTop(true);
        setBounds(new java.awt.Rectangle(0, 0, 130, 130));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().setLayout(new javax.swing.OverlayLayout(getContentPane()));

        jpMain.setLayout(new java.awt.GridLayout(2, 2));

        jpSpeak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpSpeak.setLayout(new javax.swing.OverlayLayout(jpSpeak));

        jtaSpeak.setColumns(20);
        jtaSpeak.setRows(5);
        jtaSpeak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jspSpeak.setViewportView(jtaSpeak);

        jpSpeak.add(jspSpeak);

        jpMain.add(jpSpeak);

        jpFixedWords.setLayout(new java.awt.GridLayout(5, 1));

        jpFW1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpFW1.setLayout(new javax.swing.OverlayLayout(jpFW1));

        jtWU1.setEditable(false);
        jtWU1.setText("Si");
        jtWU1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SpeakWU(evt);
            }
        });
        jtWU1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpFW1.add(jtWU1);

        jpFixedWords.add(jpFW1);

        jpFW2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpFW2.setLayout(new javax.swing.OverlayLayout(jpFW2));

        jtWU2.setEditable(false);
        jtWU2.setText("No");
        jtWU2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SpeakWU(evt);
            }
        });
        jtWU2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpFW2.add(jtWU2);

        jpFixedWords.add(jpFW2);

        jpFW3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpFW3.setLayout(new javax.swing.OverlayLayout(jpFW3));

        jtWU3.setEditable(false);
        jtWU3.setText("Hola");
        jtWU3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SpeakWU(evt);
            }
        });
        jtWU3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpFW3.add(jtWU3);

        jpFixedWords.add(jpFW3);

        jpFW4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpFW4.setLayout(new javax.swing.OverlayLayout(jpFW4));

        jtWU4.setEditable(false);
        jtWU4.setText("Adios");
        jtWU4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SpeakWU(evt);
            }
        });
        jtWU4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpFW4.add(jtWU4);

        jpFixedWords.add(jpFW4);

        jpFW5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpFW5.setLayout(new javax.swing.OverlayLayout(jpFW5));

        jtWU5.setEditable(false);
        jtWU5.setText("Gracias");
        jtWU5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SpeakWU(evt);
            }
        });
        jtWU5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpFW5.add(jtWU5);

        jpFixedWords.add(jpFW5);

        jpMain.add(jpFixedWords);

        jpDictWords.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDictWords.setLayout(new java.awt.GridLayout(5, 2));

        jpDW1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW1.setLayout(new javax.swing.OverlayLayout(jpDW1));

        jtfDW1.setEditable(false);
        jtfDW1.setText("jTextField6");
        jtfDW1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addPredictW(evt);
            }
        });
        jtfDW1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW1.add(jtfDW1);

        jpDictWords.add(jpDW1);

        jpDW2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW2.setLayout(new javax.swing.OverlayLayout(jpDW2));

        jtfDW2.setEditable(false);
        jtfDW2.setText("jTextField7");
        jtfDW2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addPredictW(evt);
            }
        });
        jtfDW2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW2.add(jtfDW2);

        jpDictWords.add(jpDW2);

        jpDW3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW3.setLayout(new javax.swing.OverlayLayout(jpDW3));

        jtfDW3.setEditable(false);
        jtfDW3.setText("jTextField8");
        jtfDW3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addPredictW(evt);
            }
        });
        jtfDW3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW3.add(jtfDW3);

        jpDictWords.add(jpDW3);

        jpDW4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW4.setLayout(new javax.swing.OverlayLayout(jpDW4));

        jtfDW4.setEditable(false);
        jtfDW4.setText("jTextField9");
        jtfDW4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addPredictW(evt);
            }
        });
        jtfDW4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW4.add(jtfDW4);

        jpDictWords.add(jpDW4);

        jpDW5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW5.setLayout(new javax.swing.OverlayLayout(jpDW5));

        jtfDW5.setEditable(false);
        jtfDW5.setText("jTextField10");
        jtfDW5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addPredictW(evt);
            }
        });
        jtfDW5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW5.add(jtfDW5);

        jpDictWords.add(jpDW5);

        jpDW6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW6.setLayout(new javax.swing.OverlayLayout(jpDW6));

        jtfDW6.setEditable(false);
        jtfDW6.setText("jTextField11");
        jtfDW6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addPredictW(evt);
            }
        });
        jtfDW6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW6.add(jtfDW6);

        jpDictWords.add(jpDW6);

        jpDW7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW7.setLayout(new javax.swing.OverlayLayout(jpDW7));

        jtfDW7.setEditable(false);
        jtfDW7.setText("jTextField12");
        jtfDW7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addPredictW(evt);
            }
        });
        jtfDW7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW7.add(jtfDW7);

        jpDictWords.add(jpDW7);

        jpDW8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW8.setLayout(new javax.swing.OverlayLayout(jpDW8));

        jtfDW8.setEditable(false);
        jtfDW8.setText("jTextField13");
        jtfDW8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addPredictW(evt);
            }
        });
        jtfDW8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW8.add(jtfDW8);

        jpDictWords.add(jpDW8);

        jpDW9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW9.setLayout(new javax.swing.OverlayLayout(jpDW9));

        jtfDW9.setEditable(false);
        jtfDW9.setText("jTextField14");
        jtfDW9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addPredictW(evt);
            }
        });
        jtfDW9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW9.add(jtfDW9);

        jpDictWords.add(jpDW9);

        jpDW10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW10.setLayout(new javax.swing.OverlayLayout(jpDW10));

        jtfDW10.setEditable(false);
        jtfDW10.setText("jTextField15");
        jtfDW10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addPredictW(evt);
            }
        });
        jtfDW10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpDW10.add(jtfDW10);

        jpDictWords.add(jpDW10);

        jpMain.add(jpDictWords);

        jpKeyboard.setName(""); // NOI18N
        jpKeyboard.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKeyboard.setLayout(new java.awt.GridLayout(4, 1));

        jpKB1.setLayout(new java.awt.GridLayout(1, 10));

        jbKey1.setBackground(DEFAULT_KEY_COLOR);
        jbKey1.setForeground(new java.awt.Color(255, 255, 255));
        jbKey1.setText("q");
        jbKey1.setName("jbKey1"); // NOI18N
        jbKey1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB1.add(jbKey1);

        jbKey2.setBackground(DEFAULT_KEY_COLOR);
        jbKey2.setForeground(new java.awt.Color(255, 255, 255));
        jbKey2.setText("w");
        jbKey2.setName("jbKey2"); // NOI18N
        jbKey2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB1.add(jbKey2);

        jbKey3.setBackground(DEFAULT_KEY_COLOR);
        jbKey3.setForeground(new java.awt.Color(255, 255, 255));
        jbKey3.setText("e");
        jbKey3.setName("jbKey3"); // NOI18N
        jbKey3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB1.add(jbKey3);

        jbKey4.setBackground(DEFAULT_KEY_COLOR);
        jbKey4.setForeground(new java.awt.Color(255, 255, 255));
        jbKey4.setText("r");
        jbKey4.setName("jbKey4"); // NOI18N
        jbKey4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB1.add(jbKey4);

        jbKey5.setBackground(DEFAULT_KEY_COLOR);
        jbKey5.setForeground(new java.awt.Color(255, 255, 255));
        jbKey5.setText("t");
        jbKey5.setName("jbKey5"); // NOI18N
        jbKey5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB1.add(jbKey5);

        jbKey6.setBackground(DEFAULT_KEY_COLOR);
        jbKey6.setForeground(new java.awt.Color(255, 255, 255));
        jbKey6.setText("y");
        jbKey6.setName("jbKey6"); // NOI18N
        jbKey6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB1.add(jbKey6);

        jbKey7.setBackground(DEFAULT_KEY_COLOR);
        jbKey7.setForeground(new java.awt.Color(255, 255, 255));
        jbKey7.setText("u");
        jbKey7.setName("jbKey7"); // NOI18N
        jbKey7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB1.add(jbKey7);

        jbKey8.setBackground(DEFAULT_KEY_COLOR);
        jbKey8.setForeground(new java.awt.Color(255, 255, 255));
        jbKey8.setText("i");
        jbKey8.setName("jbKey8"); // NOI18N
        jbKey8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB1.add(jbKey8);

        jbKey9.setBackground(DEFAULT_KEY_COLOR);
        jbKey9.setForeground(new java.awt.Color(255, 255, 255));
        jbKey9.setText("o");
        jbKey9.setName("jbKey9"); // NOI18N
        jbKey9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB1.add(jbKey9);

        jbKey10.setBackground(DEFAULT_KEY_COLOR);
        jbKey10.setForeground(new java.awt.Color(255, 255, 255));
        jbKey10.setText("p");
        jbKey10.setName("jbKey10"); // NOI18N
        jbKey10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB1.add(jbKey10);

        jpKeyboard.add(jpKB1);

        jpKB2.setLayout(new java.awt.GridLayout(1, 9));

        jbKey11.setBackground(DEFAULT_KEY_COLOR);
        jbKey11.setForeground(new java.awt.Color(255, 255, 255));
        jbKey11.setText("a");
        jbKey11.setName("jbKey11"); // NOI18N
        jbKey11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB2.add(jbKey11);

        jbKey12.setBackground(DEFAULT_KEY_COLOR);
        jbKey12.setForeground(new java.awt.Color(255, 255, 255));
        jbKey12.setText("s");
        jbKey12.setName("jbKey12"); // NOI18N
        jbKey12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey12.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB2.add(jbKey12);

        jbKey13.setBackground(DEFAULT_KEY_COLOR);
        jbKey13.setForeground(new java.awt.Color(255, 255, 255));
        jbKey13.setText("d");
        jbKey13.setName("jbKey13"); // NOI18N
        jbKey13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey13.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB2.add(jbKey13);

        jbKey14.setBackground(DEFAULT_KEY_COLOR);
        jbKey14.setForeground(new java.awt.Color(255, 255, 255));
        jbKey14.setText("f");
        jbKey14.setName("jbKey14"); // NOI18N
        jbKey14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey14.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB2.add(jbKey14);

        jbKey15.setBackground(DEFAULT_KEY_COLOR);
        jbKey15.setForeground(new java.awt.Color(255, 255, 255));
        jbKey15.setText("g");
        jbKey15.setName("jbKey15"); // NOI18N
        jbKey15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey15.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB2.add(jbKey15);

        jbKey16.setBackground(DEFAULT_KEY_COLOR);
        jbKey16.setForeground(new java.awt.Color(255, 255, 255));
        jbKey16.setText("h");
        jbKey16.setName("jbKey16"); // NOI18N
        jbKey16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey16.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB2.add(jbKey16);

        jbKey17.setBackground(DEFAULT_KEY_COLOR);
        jbKey17.setForeground(new java.awt.Color(255, 255, 255));
        jbKey17.setText("j");
        jbKey17.setName("jbKey17"); // NOI18N
        jbKey17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey17.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB2.add(jbKey17);

        jbKey18.setBackground(DEFAULT_KEY_COLOR);
        jbKey18.setForeground(new java.awt.Color(255, 255, 255));
        jbKey18.setText("k");
        jbKey18.setName("jbKey18"); // NOI18N
        jbKey18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey18.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB2.add(jbKey18);

        jbKey19.setBackground(DEFAULT_KEY_COLOR);
        jbKey19.setForeground(new java.awt.Color(255, 255, 255));
        jbKey19.setText("l");
        jbKey19.setName("jbKey19"); // NOI18N
        jbKey19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey19.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB2.add(jbKey19);

        jpKeyboard.add(jpKB2);

        jpKB3.setLayout(new java.awt.GridLayout(1, 9));

        jbKey20.setBackground(DEFAULT_KEY_COLOR);
        jbKey20.setForeground(new java.awt.Color(255, 255, 255));
        jbKey20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/co/cr/ACAT/Images/ic_caplock.png"))); // NOI18N
        jbKey20.setName("jbKey20"); // NOI18N
        jbKey20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbKey20MouseClicked(evt);
            }
        });
        jbKey20.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB3.add(jbKey20);

        jbKey21.setBackground(DEFAULT_KEY_COLOR);
        jbKey21.setForeground(new java.awt.Color(255, 255, 255));
        jbKey21.setText("z");
        jbKey21.setName("jbKey21"); // NOI18N
        jbKey21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey21.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB3.add(jbKey21);

        jbKey22.setBackground(DEFAULT_KEY_COLOR);
        jbKey22.setForeground(new java.awt.Color(255, 255, 255));
        jbKey22.setText("x");
        jbKey22.setName("jbKey22"); // NOI18N
        jbKey22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey22.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB3.add(jbKey22);

        jbKey23.setBackground(DEFAULT_KEY_COLOR);
        jbKey23.setForeground(new java.awt.Color(255, 255, 255));
        jbKey23.setText("c");
        jbKey23.setName("jbKey23"); // NOI18N
        jbKey23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey23.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB3.add(jbKey23);

        jbKey24.setBackground(DEFAULT_KEY_COLOR);
        jbKey24.setForeground(new java.awt.Color(255, 255, 255));
        jbKey24.setText("v");
        jbKey24.setName("jbKey24"); // NOI18N
        jbKey24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey24.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB3.add(jbKey24);

        jbKey25.setBackground(DEFAULT_KEY_COLOR);
        jbKey25.setForeground(new java.awt.Color(255, 255, 255));
        jbKey25.setText("b");
        jbKey25.setName("jbKey25"); // NOI18N
        jbKey25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey25.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB3.add(jbKey25);

        jbKey26.setBackground(DEFAULT_KEY_COLOR);
        jbKey26.setForeground(new java.awt.Color(255, 255, 255));
        jbKey26.setText("n");
        jbKey26.setName("jbKey26"); // NOI18N
        jbKey26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey26.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB3.add(jbKey26);

        jbKey27.setBackground(DEFAULT_KEY_COLOR);
        jbKey27.setForeground(new java.awt.Color(255, 255, 255));
        jbKey27.setText("m");
        jbKey27.setName("jbKey27"); // NOI18N
        jbKey27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyClicked(evt);
            }
        });
        jbKey27.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB3.add(jbKey27);

        jbKey28.setBackground(DEFAULT_KEY_COLOR);
        jbKey28.setForeground(new java.awt.Color(255, 255, 255));
        jbKey28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/co/cr/ACAT/Images/ic_back.png"))); // NOI18N
        jbKey28.setName("jbKey28"); // NOI18N
        jbKey28.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbKey28MouseClicked(evt);
            }
        });
        jbKey28.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB3.add(jbKey28);

        jpKeyboard.add(jpKB3);

        jpKB4.setLayout(new java.awt.GridLayout(1, 9));

        jbKey29.setBackground(DEFAULT_KEY_COLOR);
        jbKey29.setForeground(new java.awt.Color(255, 255, 255));
        jbKey29.setText("123");
        jbKey29.setBorder(null);
        jbKey29.setName("jbKey29"); // NOI18N
        jbKey29.setRolloverEnabled(false);
        jbKey29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbKey29MouseClicked(evt);
            }
        });
        jbKey29.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB4.add(jbKey29);

        jbKey30.setBackground(DEFAULT_KEY_COLOR);
        jbKey30.setForeground(new java.awt.Color(255, 255, 255));
        jbKey30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/co/cr/ACAT/Images/ic_microphonew.png"))); // NOI18N
        jbKey30.setBorder(null);
        jbKey30.setName("jbKey30"); // NOI18N
        jbKey30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbKey30MouseClicked(evt);
            }
        });
        jbKey30.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB4.add(jbKey30);

        jbKey31.setBackground(DEFAULT_KEY_COLOR);
        jbKey31.setForeground(new java.awt.Color(255, 255, 255));
        jbKey31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/co/cr/ACAT/Images/ic_backword.png"))); // NOI18N
        jbKey31.setName("jbKey31"); // NOI18N
        jbKey31.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbKey31MouseClicked(evt);
            }
        });
        jbKey31.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB4.add(jbKey31);

        jbKey32.setBackground(DEFAULT_KEY_COLOR);
        jbKey32.setForeground(new java.awt.Color(255, 255, 255));
        jbKey32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/co/cr/ACAT/Images/ic_arrowleft.png"))); // NOI18N
        jbKey32.setName("jbKey32"); // NOI18N
        jbKey32.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbKey32MouseClicked(evt);
            }
        });
        jbKey32.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB4.add(jbKey32);

        jbKey33.setBackground(DEFAULT_KEY_COLOR);
        jbKey33.setForeground(new java.awt.Color(255, 255, 255));
        jbKey33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/co/cr/ACAT/Images/ic_spacebar.png"))); // NOI18N
        jbKey33.setName("jbKey33"); // NOI18N
        jbKey33.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbKey33MouseClicked(evt);
            }
        });
        jbKey33.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB4.add(jbKey33);

        jbKey34.setBackground(DEFAULT_KEY_COLOR);
        jbKey34.setForeground(new java.awt.Color(255, 255, 255));
        jbKey34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/co/cr/ACAT/Images/ic_arrowright.png"))); // NOI18N
        jbKey34.setName("jbKey34"); // NOI18N
        jbKey34.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbKey34MouseClicked(evt);
            }
        });
        jbKey34.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB4.add(jbKey34);

        jbKey35.setBackground(DEFAULT_KEY_COLOR);
        jbKey35.setForeground(new java.awt.Color(255, 255, 255));
        jbKey35.setText(".");
        jbKey35.setName("jbKey35"); // NOI18N
        jbKey35.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB4.add(jbKey35);

        jbKey36.setBackground(DEFAULT_KEY_COLOR);
        jbKey36.setForeground(new java.awt.Color(255, 255, 255));
        jbKey36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/co/cr/ACAT/Images/ic_settings.png"))); // NOI18N
        jbKey36.setName("jbKey36"); // NOI18N
        jbKey36.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbKey36MouseClicked(evt);
            }
        });
        jbKey36.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB4.add(jbKey36);

        jbKey37.setBackground(DEFAULT_KEY_COLOR);
        jbKey37.setForeground(new java.awt.Color(255, 255, 255));
        jbKey37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/co/cr/ACAT/Images/Borrador.png"))); // NOI18N
        jbKey37.setName("jbKey37"); // NOI18N
        jbKey37.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbKey37MouseClicked(evt);
            }
        });
        jbKey37.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpKB4.add(jbKey37);

        jpKeyboard.add(jpKB4);

        jpMain.add(jpKeyboard);

        getContentPane().add(jpMain);

        getAccessibleContext().setAccessibleDescription("");

        setBounds(150, 200, 916, 600);
    }// </editor-fold>//GEN-END:initComponents

    private void keyClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_keyClicked
        // When used click whith mouse
        JButton bt = (JButton)evt.getSource();
        String btxt = bt.getText();
        if (syl_span.contains(btxt)){
            CallJDSyl(btxt);
        } else {
            addCharKey(btxt);
        }
    }//GEN-LAST:event_keyClicked

    private void jbKey20MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbKey20MouseClicked
        // Show more symbols in keyboard
        if ("kn3".equals(ktype)){
            changeCapLock();
        } else {
            if ("kn1".equals(ktype)){
                ktype="kn2";
            } else {
               ktype="kn1"; 
            }
            changeKeyboard();
        }
    }//GEN-LAST:event_jbKey20MouseClicked

    private void jbKey28MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbKey28MouseClicked
        // TODO add your handling code here:
        backChar();
    }//GEN-LAST:event_jbKey28MouseClicked

    private void jbKey32MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbKey32MouseClicked
        // TODO add your handling code here:
        arrowLeft();
    }//GEN-LAST:event_jbKey32MouseClicked

    private void jbKey34MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbKey34MouseClicked
        // TODO add your handling code here:
        arrowRight();
    }//GEN-LAST:event_jbKey34MouseClicked

    private void jbKey33MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbKey33MouseClicked
        // TODO add your handling code here:
        addSpace();
    }//GEN-LAST:event_jbKey33MouseClicked

    private void jbKey37MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbKey37MouseClicked
        // TODO add your handling code here:
        jtaSpeak.setText("");
        jtaSpeak.requestFocusInWindow();
    }//GEN-LAST:event_jbKey37MouseClicked

    private void jbKey30MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbKey30MouseClicked
        // TODO add your handling code here:
        armSpeak();
    }//GEN-LAST:event_jbKey30MouseClicked

    private void jbKey31MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbKey31MouseClicked
        // TODO add your handling code here:
        deleteWord();
    }//GEN-LAST:event_jbKey31MouseClicked

    private void KeyPressedF12(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeyPressedF12
        if (evt.getKeyCode() == KeyEvent.VK_F12) {
            switch (indT) {
                case 0:
                    indT=1;
                    break;
                case 1:
                    indT=11;
                    break;
                case 2:
                    indT=22;
                    break;
                case 3:
                    indT=33;
                    break;
                case 33:
                    indT=333;
                    break;
                default:
                    break;
            }
        }
    }//GEN-LAST:event_KeyPressedF12

    private void SpeakWU(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SpeakWU
        // TODO add your handling code here:
        JTextField mtfld = (JTextField)evt.getSource();
        SpeakTxt(mtfld.getText());
    }//GEN-LAST:event_SpeakWU

    private void ShowSetting(){
        /*Show form with options of settings*/
        paused=true;
        JDSettings SeetingFrmdialog=new JDSettings(this,true);
        SeetingFrmdialog.setLocationRelativeTo(this);
        SeetingFrmdialog.setVisible(true);
        indT=0;
        paused=false;
    }
    
    private void addWord(String wordpw){
        /*Add a word select for dictionary*/
        String wordsp=this.jtaSpeak.getText();
        if (wordsp.length()>0){
            if(!" ".equals(wordsp.substring(wordsp.length()))){
                int ix=wordsp.lastIndexOf(" ");
                wordpw=wordpw.substring(wordsp.substring(ix+1,wordsp.length()).length(),wordpw.length())+ " ";
            } else {
                wordpw=wordpw+" ";
            }
        } else {
            wordpw=wordpw+" ";
        }
        addCharKey(wordpw);
    }
    
    private void addPredictW(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addPredictW
        // TODO add your handling code here:
        JTextField mtfld = (JTextField)evt.getSource();
        String wordpw=mtfld.getText();
        addWord(wordpw);
    }//GEN-LAST:event_addPredictW

    private void jbKey29MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbKey29MouseClicked
        // TODO add your handling code here:
        changeNumKey();
    }//GEN-LAST:event_jbKey29MouseClicked

    private void jbKey36MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbKey36MouseClicked
        // TODO add your handling code here:
        ShowSetting();
    }//GEN-LAST:event_jbKey36MouseClicked

    private void jButtonClick(String btxt){
        String mtx=jtaSpeak.getText();
        
        int start = jtaSpeak.getSelectionStart();
	int end = jtaSpeak.getSelectionEnd();
        String oriContent = jtaSpeak.getText();
        if (start >= 0 && end > 0 && start != end) {
            StringBuilder builder = new StringBuilder(oriContent);
            builder.replace(start, end, mtx);
            jtaSpeak.setText(builder.toString());
        } else {
            int index = jtaSpeak.getSelectionStart() >= 0 ? jtaSpeak.getSelectionStart() : 0;
            StringBuilder builder = new StringBuilder(oriContent);
            builder.insert(index, mtx);
            jtaSpeak.setText(builder.toString());
        }   
    }
    
    private void SpeakTxt(String txtSpeak){
        /*Send Festival speak*/
        try {
            
            Writer w = new OutputStreamWriter(p.getOutputStream());
            w.append("(SayText \"" + txtSpeak +"\")");
            w.flush();
            jtaSpeak.requestFocusInWindow();
        }catch (IOException ioe) {
            Logger.getLogger(KeyboardK.class.getName()).log(Level.SEVERE, null, ioe);
        }
    }
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(KeyboardK.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new KeyboardK().setVisible(true);
            }
        });
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jbKey1;
    private javax.swing.JButton jbKey10;
    private javax.swing.JButton jbKey11;
    private javax.swing.JButton jbKey12;
    private javax.swing.JButton jbKey13;
    private javax.swing.JButton jbKey14;
    private javax.swing.JButton jbKey15;
    private javax.swing.JButton jbKey16;
    private javax.swing.JButton jbKey17;
    private javax.swing.JButton jbKey18;
    private javax.swing.JButton jbKey19;
    private javax.swing.JButton jbKey2;
    private javax.swing.JButton jbKey20;
    private javax.swing.JButton jbKey21;
    private javax.swing.JButton jbKey22;
    private javax.swing.JButton jbKey23;
    private javax.swing.JButton jbKey24;
    private javax.swing.JButton jbKey25;
    private javax.swing.JButton jbKey26;
    private javax.swing.JButton jbKey27;
    private javax.swing.JButton jbKey28;
    private javax.swing.JButton jbKey29;
    private javax.swing.JButton jbKey3;
    private javax.swing.JButton jbKey30;
    private javax.swing.JButton jbKey31;
    private javax.swing.JButton jbKey32;
    private javax.swing.JButton jbKey33;
    private javax.swing.JButton jbKey34;
    private javax.swing.JButton jbKey35;
    private javax.swing.JButton jbKey36;
    private javax.swing.JButton jbKey37;
    private javax.swing.JButton jbKey4;
    private javax.swing.JButton jbKey5;
    private javax.swing.JButton jbKey6;
    private javax.swing.JButton jbKey7;
    private javax.swing.JButton jbKey8;
    private javax.swing.JButton jbKey9;
    private javax.swing.JPanel jpDW1;
    private javax.swing.JPanel jpDW10;
    private javax.swing.JPanel jpDW2;
    private javax.swing.JPanel jpDW3;
    private javax.swing.JPanel jpDW4;
    private javax.swing.JPanel jpDW5;
    private javax.swing.JPanel jpDW6;
    private javax.swing.JPanel jpDW7;
    private javax.swing.JPanel jpDW8;
    private javax.swing.JPanel jpDW9;
    private javax.swing.JPanel jpDictWords;
    private javax.swing.JPanel jpFW1;
    private javax.swing.JPanel jpFW2;
    private javax.swing.JPanel jpFW3;
    private javax.swing.JPanel jpFW4;
    private javax.swing.JPanel jpFW5;
    private javax.swing.JPanel jpFixedWords;
    private javax.swing.JPanel jpKB1;
    private javax.swing.JPanel jpKB2;
    private javax.swing.JPanel jpKB3;
    private javax.swing.JPanel jpKB4;
    private javax.swing.JPanel jpKeyboard;
    private javax.swing.JPanel jpMain;
    private javax.swing.JPanel jpSpeak;
    private javax.swing.JScrollPane jspSpeak;
    private javax.swing.JTextField jtWU1;
    private javax.swing.JTextField jtWU2;
    private javax.swing.JTextField jtWU3;
    private javax.swing.JTextField jtWU4;
    private javax.swing.JTextField jtWU5;
    private javax.swing.JTextArea jtaSpeak;
    private javax.swing.JTextField jtfDW1;
    private javax.swing.JTextField jtfDW10;
    private javax.swing.JTextField jtfDW2;
    private javax.swing.JTextField jtfDW3;
    private javax.swing.JTextField jtfDW4;
    private javax.swing.JTextField jtfDW5;
    private javax.swing.JTextField jtfDW6;
    private javax.swing.JTextField jtfDW7;
    private javax.swing.JTextField jtfDW8;
    private javax.swing.JTextField jtfDW9;
    // End of variables declaration//GEN-END:variables

    private JPanel jpMT(String mpname){
        
        return Awt1.getComponentByName(this,mpname);
    }
    
    /*Timer change panels and buttons for selected*/
    TimerTask timerTask = new TimerTask() 
     { 
         public void run()  
         {
             if (paused==false){
                Border mbf;
                if (indT==0) {
                    mbf=BorderFactory.createMatteBorder(3, 3, 3, 3, Color.yellow);
                } else {
                    mbf=BorderFactory.createEmptyBorder();
                }

                for (int i=0;i<3;i++) {
                    if (indT==0){
                        jpFocus="jp"+ jpnames[i];
                        JPanel jp=jpMT(jpFocus);
                        jp.setBorder(mbf);
                        int tms=2000;
                        try {
                            sleep(tms);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(KeyboardK.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        jp.setBorder(BorderFactory.createEmptyBorder());
                    } else {
                        switch(jpFocus){
                            case "jpFixedWords":
                                indT=1;
                                jpCycleUsedW();
                                break;
                            case "jpDictWords":
                                indT=2;
                                jpCyclePredictW();
                                break;
                            case "jpKeyboard":
                                indT=3;
                                jpCycleAlpha();
                                break;
                        }
                    }           
                } 
             }
            
         } 
    };

    private void arrowLeft() {
        int keycode = Integer.decode("0x25");
        this.jtaSpeak.requestFocusInWindow();
        robot.keyPress(keycode);
        jtaSpeak.requestFocusInWindow();
    }

    private void addSpace() {
        String mtx=jtaSpeak.getText();
        mtx=mtx+" ";
        jtaSpeak.setText(mtx);
        jtaSpeak.requestFocusInWindow();
    }

    private void armSpeak() {
        String multiLines = jtaSpeak.getText();
        String[] txtspk;
        String delimiter = "\n";

        txtspk = multiLines.split(delimiter);
        if (txtspk.length>0){
            SpeakTxt( txtspk[txtspk.length-1] );
            jtaSpeak.append("\n");
        }
    }

    private void deleteWord() {
        String mdw;
        String oriContentw = jtaSpeak.getText();
        int startw = jtaSpeak.getSelectionStart();
        int endw = jtaSpeak.getSelectionEnd();

        if (startw >= 0 && endw > 0 && startw != endw) {
        } else {
            int x=-1;
            if (startw < oriContentw.length()){
                /*char[] ascii2 = oriContentw.substring(startw, startw +1).toCharArray();*/
                if (oriContentw.substring(startw,startw+1).charAt(0) != 32) {
                    x=oriContentw.substring(startw).indexOf(" ");
                    if (x != -1) {
                        startw=startw+x;
                    }
                }
            }
            int vc=0;
            int starts =startw;
            do {
                x=oriContentw.substring(0,starts).lastIndexOf(" ");
                if (x != -1) {
                    /*char[] ascii2 = oriContentw.substring(x).toCharArray();*/
                    if (oriContentw.substring(x).charAt(0) == 32 && oriContentw.substring(x).length() == 1) {
                            starts = starts-1;
                            vc = vc +1;
                    } else {
                            vc = 4;
                    }
                } else {
                        vc = 4;
                }
            } while (vc <4);
//
            if (x != -1) {
                String finalString= oriContentw.substring(0,x) + oriContentw.substring(startw,oriContentw.length());
                jtaSpeak.setText(finalString);
                jtaSpeak.setSelectionStart(x);
                jtaSpeak.setSelectionEnd(x);
            } else if (oriContentw.length()>0){
                String finalString=  oriContentw.substring(startw,oriContentw.length());
                jtaSpeak.setText(finalString);
                jtaSpeak.setSelectionStart(0);
                jtaSpeak.setSelectionEnd(0);
            }
        }
        jtaSpeak.requestFocusInWindow();
        String lw=LastWord();
        pressageWord(lw);
    }

    private void arrowRight() {
        int keycode = Integer.decode("0x27");
        this.jtaSpeak.requestFocusInWindow();
        robot.keyPress(keycode);
        jtaSpeak.requestFocusInWindow();
    }
    
    private void setLocationToBottom() {
        /*Location form size screen*/
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets inset = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        
        int workingScreen = screenSize.height - inset.bottom;
        int width = (screenSize.width - getWidth()) / 2; //Positions horizontally to middle of screen
        int height = workingScreen - getHeight(); //Positions vertically just above the taskbar
        setLocation(width, height);
    }

    private void changeKeyboard(){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File("string.xml"));
            document.getDocumentElement().normalize();
            NodeList listfields;
            listfields = document.getElementsByTagName(ktype);
            int nbt=0;

            for (int temp = 0; temp < listfields.getLength(); temp++) {
                Node nodo = listfields.item(temp);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nodo;
                    NodeList fstNmElmntLst = element.getElementsByTagName("item");
                    Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
                    NodeList fstNm = fstNmElmnt.getChildNodes();
                    buttons.get(nbt).setText(((Node) fstNm.item(0)).getNodeValue());
                    nbt +=1;
                }
            }
            if (Ind_Caps==false && "kn3".equals(ktype)){
                Ind_Caps=true;
                changeCapLock();
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(KeyboardK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
