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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import static java.lang.Thread.sleep;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 *
 * @author Rodolfo Navarro
 */
public class JDSettings extends javax.swing.JDialog {

    /**
     * Creates new form NewJDialog
     */
    private final Timer mtcycle;
    private int indT=0;
    private int set_op=0;
    private String jpFocus;
    
    public JDSettings(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        mtcycle = new Timer();
        mtcycle.scheduleAtFixedRate(timerTask, 0, 500);
    }
    
    TimerTask timerTask = new TimerTask() 
     { 
         public void run()  
         {
            Border mbf;
            if (indT==0) {
                mbf=BorderFactory.createMatteBorder(3, 3, 3, 3, Color.yellow);
            } else {
                mbf=BorderFactory.createEmptyBorder();
            }

            jpCycleUsedW();
         } 
    };
    private void jpCycleUsedW() {
        for (int i=1;i<4;i++) {
            if (i!=2){
                jpFocus="jpS"+ i;
                JPanel jp =Awt1.getComponentByName(this, jpFocus);
                jp.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.yellow));

                int tms=1500;
                try {
                    sleep(tms);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KeyboardK.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(indT==1){
                    for(Component component : jp.getComponents()) {
                        JTextField key = (JTextField)component;
                        set_op=i;
                        indT=0;
                        ExecSO();
                    }
                }
                jp.setBorder(BorderFactory.createEmptyBorder());
            }
        }
        set_op=0;
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jpS1 = new javax.swing.JPanel();
        jTextField3 = new javax.swing.JTextField();
        jpS2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jpS3 = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Settings");
        setIconImages(null);
        setLocation(new java.awt.Point(0, 0));
        setPreferredSize(new java.awt.Dimension(200, 200));
        setResizable(false);

        jPanel1.setLayout(new java.awt.GridLayout(3, 0));

        jpS1.setLayout(new java.awt.GridLayout(1, 0));

        jTextField3.setEditable(false);
        jTextField3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTextField3.setText("< Volver");
        jTextField3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField3MouseClicked(evt);
            }
        });
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeyPressedF12(evt);
            }
        });
        jpS1.add(jTextField3);

        jPanel1.add(jpS1);

        jpS2.setLayout(new java.awt.GridLayout(1, 0));

        jTextField1.setEditable(false);
        jTextField1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTextField1.setText("Idioma");
        jTextField1.setEnabled(false);
        jTextField1.setFocusable(false);
        jpS2.add(jTextField1);

        jPanel1.add(jpS2);

        jpS3.setLayout(new java.awt.GridLayout(1, 0));

        jTextField2.setEditable(false);
        jTextField2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTextField2.setText("Salir");
        jTextField2.setToolTipText("");
        jTextField2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField2MouseClicked(evt);
            }
        });
        jpS3.add(jTextField2);

        jPanel1.add(jpS3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 84, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleParent(this);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void KeyPressedF12(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeyPressedF12
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_F12) {
            indT=1;
        }
    }//GEN-LAST:event_KeyPressedF12

    private void jTextField3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField3MouseClicked
        // TODO add your handling code here:
        set_op=1;
        ExecSO();
    }//GEN-LAST:event_jTextField3MouseClicked

    private void jTextField2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField2MouseClicked
        // TODO add your handling code here:
        set_op=3;
        ExecSO();
    }//GEN-LAST:event_jTextField2MouseClicked

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void ExecSO(){
        mtcycle.cancel();
        if (set_op==1){
            this.dispose();
        } else if (set_op==3){
            System.exit(0);
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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDSettings dialog = new JDSettings(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JPanel jpS1;
    private javax.swing.JPanel jpS2;
    private javax.swing.JPanel jpS3;
    // End of variables declaration//GEN-END:variables
}
