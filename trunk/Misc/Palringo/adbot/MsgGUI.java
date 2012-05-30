/*    */ package adbot;
/*    */ 
/*    */ import java.awt.AWTException;
/*    */ import java.awt.GridLayout;
/*    */ import java.awt.HeadlessException;
/*    */ import java.awt.Point;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.image.BufferedImage;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JSpinner;
/*    */ import javax.swing.JTextArea;
/*    */ import javax.swing.JTextField;
/*    */ 
/*    */ public class MsgGUI extends JFrame
/*    */ {
/* 26 */   JTextArea message = new JTextArea("", 5, 20);
/*    */   Point clickLoc;
/*    */   Point closeLoc;
/*    */   BufferedImage xImg;
/* 34 */   JButton start = new JButton("Start");
/*    */ 
/* 36 */   JSpinner spin = new JSpinner();
/*    */ 
/* 38 */   JTextField sleep = new JTextField();
/*    */ 
/*    */   public MsgGUI(Point p, Point p2)
/*    */   {
/* 42 */     this.clickLoc = p;
/* 43 */     this.closeLoc = p2;
/* 44 */     add(this.message, "North");
/* 45 */     JPanel numSend = new JPanel(new GridLayout(2, 2));
/* 46 */     numSend.add(new JLabel("Number of messages:"));
/* 47 */     numSend.add(this.spin);
/* 48 */     numSend.add(new JLabel("Sleep time (ms):"));
/* 49 */     numSend.add(this.sleep);
/* 50 */     add(numSend, "Center");
/* 51 */     add(this.start, "South");
/* 52 */     pack();
/* 53 */     setDefaultCloseOperation(3);
/* 54 */     this.start.addActionListener(new ActionListener()
/*    */     {
/*    */       public void actionPerformed(ActionEvent arg0)
/*    */       {
/*    */         try {
/* 59 */           new Advertiser(MsgGUI.this.clickLoc, MsgGUI.this.closeLoc, MsgGUI.this.message.getText(), Integer.parseInt(MsgGUI.this.sleep.getText())).sendPMs(((Integer)MsgGUI.this.spin.getValue()).intValue());
/*    */         } catch (HeadlessException e) {
/* 61 */           e.printStackTrace();
/*    */         } catch (AWTException e) {
/* 63 */           e.printStackTrace();
/*    */         }
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           C:\Users\ramos\Downloads\AdBot4.jar
 * Qualified Name:     adbot.MsgGUI
 * JD-Core Version:    0.6.0
 */