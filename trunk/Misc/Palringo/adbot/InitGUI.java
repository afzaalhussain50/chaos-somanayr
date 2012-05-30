/*    */ package adbot;
/*    */ 
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.GridLayout;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.io.PrintStream;
/*    */ import java.util.ArrayList;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class InitGUI extends JFrame
/*    */ {
/* 23 */   private JButton agree = new JButton("Agree");
/*    */ 
/* 25 */   private JButton cancel = new JButton("Cancel");
/*    */ 
/*    */   public InitGUI()
/*    */   {
/* 29 */     setVisible(true);
/* 30 */     JPanel textPanel = new JPanel();
/* 31 */     JLabel[] list = wrapped("\"You\" is defined as the user. \"I\" is defined as the programmer. By clicking the agree button below, you agree that this program is provided on an as-is basis and that I am not responsible for anything you decide to do with this program.", getGraphics(), 400);
/* 32 */     textPanel.setLayout(new GridLayout(list.length, 1));
/* 33 */     for (JLabel l : list) {
/* 34 */       textPanel.add(l);
/*    */     }
/* 36 */     JPanel buttonPanel = new JPanel();
/* 37 */     buttonPanel.setLayout(new GridLayout(1, 2));
/* 38 */     buttonPanel.add(this.agree);
/* 39 */     buttonPanel.add(this.cancel);
/* 40 */     add(textPanel, "Center");
/* 41 */     add(buttonPanel, "South");
/* 42 */     setDefaultCloseOperation(3);
/* 43 */     pack();
/* 44 */     this.agree.addActionListener(new ActionListener()
/*    */     {
/*    */       public void actionPerformed(ActionEvent arg0)
/*    */       {
/* 48 */         new SetUpGUI().setVisible(true);
/* 49 */         InitGUI.this.dispose();
/*    */       }
/*    */     });
/* 52 */     this.cancel.addActionListener(new ActionListener()
/*    */     {
/*    */       public void actionPerformed(ActionEvent e)
/*    */       {
/* 56 */         System.exit(0);
/*    */       } } );
/*    */   }
/*    */ 
/*    */   public JLabel[] wrapped(String str, Graphics g, int maxW) {
/* 62 */     FontMetrics m = g.getFontMetrics();
/* 63 */     String[] words = str.split(" ");
/* 64 */     StringBuffer line = new StringBuffer();
/* 65 */     int offset = 0;
/* 66 */     ArrayList labels = new ArrayList();
/* 67 */     for (String s : words) {
/* 68 */       s = s + " ";
/* 69 */       if (m.stringWidth(line.toString() + s) > maxW) {
/* 70 */         System.out.println(line.toString());
/* 71 */         labels.add(new JLabel(line.toString()));
/* 72 */         line = new StringBuffer();
/*    */       }
/* 74 */       line.append(s);
/*    */     }
/* 76 */     return (JLabel[])labels.toArray(new JLabel[labels.size()]);
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 80 */     new InitGUI();
/*    */   }
/*    */ }

/* Location:           C:\Users\ramos\Downloads\AdBot4.jar
 * Qualified Name:     adbot.InitGUI
 * JD-Core Version:    0.6.0
 */