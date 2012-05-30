/*    */ package adbot;
/*    */ 
/*    */ import java.awt.Graphics;
/*    */ import java.awt.image.BufferedImage;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class ImagePane extends JComponent
/*    */ {
/* 10 */   private BufferedImage img = null;
/*    */ 
/*    */   public ImagePane() {
/* 13 */     this.img = new BufferedImage(1, 1, 1);
/*    */   }
/*    */ 
/*    */   protected void paintComponent(Graphics g)
/*    */   {
/* 18 */     super.paintComponent(g);
/* 19 */     g.drawImage(this.img, 0, 0, this);
/*    */   }
/*    */ 
/*    */   public void setImage(BufferedImage img) {
/* 23 */     this.img = img;
/* 24 */     repaint();
/*    */   }
/*    */ 
/*    */   public BufferedImage getImage() {
/* 28 */     return this.img;
/*    */   }
/*    */ }

/* Location:           C:\Users\ramos\Downloads\AdBot4.jar
 * Qualified Name:     adbot.ImagePane
 * JD-Core Version:    0.6.0
 */