/*    */ package adbot;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import java.awt.GridLayout;
/*    */ import java.awt.MouseInfo;
/*    */ import java.awt.Point;
/*    */ import java.awt.PointerInfo;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.Robot;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.image.BufferedImage;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class SetUpGUI extends JFrame
/*    */ {
/* 19 */   private ImagePane xImg = new ImagePane();
/*    */ 
/* 21 */   private ImagePane clickImg = new ImagePane();
/*    */   private Point closeLoc;
/* 25 */   private JButton setClick = new JButton("Set click location");
/*    */ 
/* 27 */   private JButton setX = new JButton("Set close location");
/*    */ 
/* 29 */   private JButton cont = new JButton("Continue");
/*    */ 
/* 31 */   private Point clickLoc = new Point(0, 0);
/*    */ 
/*    */   public SetUpGUI() {
/* 34 */     this.cont.setEnabled(false);
/* 35 */     JPanel setup = new JPanel();
/* 36 */     setup.setLayout(new GridLayout(2, 2));
/* 37 */     setup.add(this.setX);
/* 38 */     setup.add(this.xImg);
/* 39 */     setup.add(this.setClick);
/* 40 */     setup.add(this.clickImg);
/* 41 */     this.xImg.setSize(new Dimension(6, 6));
/* 42 */     this.clickImg.setSize(new Dimension(100, 100));
/* 43 */     add(setup, "Center");
/* 44 */     add(this.cont, "South");
/* 45 */     pack();
/* 46 */     setDefaultCloseOperation(3);
/* 47 */     this.setX.addActionListener(new ActionListener()
/*    */     {
/*    */       public void actionPerformed(ActionEvent arg0)
/*    */       {
/*    */         try {
/* 52 */           Thread.sleep(1000L);
/*    */         } catch (InterruptedException e) {
/* 54 */           e.printStackTrace();
/*    */         }
/*    */         try {
/* 57 */           Point p = MouseInfo.getPointerInfo().getLocation();
/* 58 */           SetUpGUI.this.xImg.setImage(new Robot().createScreenCapture(new Rectangle(p.x - 5, p.y - 5, 10, 10)));
/* 59 */           SetUpGUI.this.closeLoc = new Point(p.x, p.y);
/* 60 */           if ((SetUpGUI.this.clickLoc.x != 0) && (SetUpGUI.this.clickLoc.y != 0))
/* 61 */             SetUpGUI.this.cont.setEnabled(true);
/*    */         } catch (Exception ex) {
/* 63 */           ex.printStackTrace();
/*    */         }
/*    */       }
/*    */     });
/* 67 */     this.setClick.addActionListener(new ActionListener()
/*    */     {
/*    */       public void actionPerformed(ActionEvent arg0)
/*    */       {
/*    */         try {
/* 72 */           Thread.sleep(1000L);
/*    */         } catch (InterruptedException e) {
/* 74 */           e.printStackTrace();
/*    */         }
/*    */         try {
/* 77 */           Point p = MouseInfo.getPointerInfo().getLocation();
/* 78 */           SetUpGUI.this.clickLoc = new Point(p.x, p.y);
/* 79 */           SetUpGUI.this.clickImg.setImage(new Robot().createScreenCapture(new Rectangle(p.x, p.y, 100, 100)));
/* 80 */           if (SetUpGUI.this.xImg.getImage().getWidth() != 1)
/* 81 */             SetUpGUI.this.cont.setEnabled(true);
/*    */         } catch (Exception ex) {
/* 83 */           ex.printStackTrace();
/*    */         }
/*    */       }
/*    */     });
/* 87 */     this.cont.addActionListener(new ActionListener()
/*    */     {
/*    */       public void actionPerformed(ActionEvent arg0)
/*    */       {
/* 91 */         new MsgGUI(SetUpGUI.this.clickLoc, SetUpGUI.this.closeLoc).setVisible(true);
/* 92 */         SetUpGUI.this.dispose();
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           C:\Users\ramos\Downloads\AdBot4.jar
 * Qualified Name:     adbot.SetUpGUI
 * JD-Core Version:    0.6.0
 */