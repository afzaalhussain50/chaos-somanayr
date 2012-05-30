/*     */ package adbot;
/*     */ 
/*     */ import java.awt.AWTException;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Robot;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Field;
/*     */ 
/*     */ public class Advertiser extends Robot
/*     */ {
/*     */   private BufferedImage x;
/*     */   private Rectangle zone;
/*     */   String msg;
/*     */   private Point firstClick;
/*     */   private Point closeClick;
/*     */   private int extra;
/*     */ 
/*     */   public Advertiser(Point firstClick, BufferedImage x, Rectangle zone, String msg, int extraSleep)
/*     */     throws AWTException
/*     */   {
/*  25 */     this.x = x;
/*  26 */     this.zone = zone;
/*  27 */     this.firstClick = firstClick;
/*  28 */     this.msg = msg;
/*  29 */     this.extra = extraSleep;
/*     */   }
/*     */ 
/*     */   public Advertiser(Point firstClick, Point closeClick, String msg, int extraSleep) throws AWTException
/*     */   {
/*  34 */     this.firstClick = firstClick;
/*  35 */     this.closeClick = closeClick;
/*  36 */     this.msg = msg;
/*  37 */     this.extra = extraSleep;
/*     */   }
/*     */ 
/*     */   public void sendPMs(int pms) {
/*  41 */     clickMouse(this.firstClick.x, this.firstClick.y, 16);
/*  42 */     clickMouse(this.firstClick.x, this.firstClick.y, 16);
/*     */     try {
/*  44 */       Thread.sleep(500L);
/*     */     } catch (InterruptedException e) {
/*  46 */       e.printStackTrace();
/*     */     }
/*  48 */     for (int i = 0; i < pms; i++)
/*  49 */       advertise();
/*     */   }
/*     */ 
/*     */   private void advertise()
/*     */   {
/*  55 */     keyPress(10);
/*  56 */     keyRelease(10);
/*     */     try {
/*  58 */       Thread.sleep(300L);
/*     */     } catch (InterruptedException e) {
/*  60 */       Thread.interrupted();
/*  61 */       throw new RuntimeException(e);
/*     */     }
/*  63 */     typeMessage();
/*  64 */     keyPress(10);
/*  65 */     keyRelease(10);
/*     */     try {
/*  67 */       Thread.sleep(300L);
/*     */     } catch (InterruptedException e) {
/*  69 */       Thread.interrupted();
/*  70 */       throw new RuntimeException(e);
/*     */     }
/*  72 */     clickX();
/*     */     try {
/*  74 */       Thread.sleep(200L);
/*     */     } catch (InterruptedException e) {
/*  76 */       e.printStackTrace();
/*     */     }
/*  78 */     typeKey(40, false);
/*     */     try
/*     */     {
/*  81 */       Thread.sleep(this.extra + 200);
/*     */     } catch (InterruptedException e) {
/*  83 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void typeMessage() {
/*  88 */     if ((this.msg == null) || (this.msg.length() == 0)) {
/*  89 */       keyPress(17);
/*  90 */       keyPress(86);
/*  91 */       keyRelease(86);
/*  92 */       keyRelease(17);
/*     */     } else {
/*  94 */       for (char c : this.msg.toCharArray())
/*  95 */         typeKey(c);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void typeKey(char c)
/*     */   {
/* 101 */     if ((c >= 'a') && (c <= 'z'))
/* 102 */       typeKey(fetchKey(c), false);
/* 103 */     else if ((c >= 'A') && (c <= 'Z'))
/* 104 */       typeKey(fetchKey(c), true);
/* 105 */     else if ((c >= '0') && (c <= '9'))
/* 106 */       typeKey(fetchKey(c), false);
/* 107 */     else if (c == '!')
/* 108 */       typeKey(49, true);
/* 109 */     else if (c == '@')
/* 110 */       typeKey(50, true);
/* 111 */     else if (c == '#')
/* 112 */       typeKey(51, true);
/* 113 */     else if (c == '$')
/* 114 */       typeKey(52, true);
/* 115 */     else if (c == '%')
/* 116 */       typeKey(53, true);
/* 117 */     else if (c == '^')
/* 118 */       typeKey(54, true);
/* 119 */     else if (c == '&')
/* 120 */       typeKey(55, true);
/* 121 */     else if (c == '*')
/* 122 */       typeKey(56, true);
/* 123 */     else if (c == '(')
/* 124 */       typeKey(57, true);
/* 125 */     else if (c == ')')
/* 126 */       typeKey(48, true);
/* 127 */     else if (c == ',')
/* 128 */       typeKey(44, false);
/* 129 */     else if (c == '.')
/* 130 */       typeKey(46, false);
/* 131 */     else if (c == '/')
/* 132 */       typeKey(47, false);
/* 133 */     else if (c == '?')
/* 134 */       typeKey(47, true);
/* 135 */     else if (c == ';')
/* 136 */       typeKey(59, false);
/* 137 */     else if (c == ':')
/* 138 */       typeKey(59, true);
/* 139 */     else if (c == '\n')
/* 140 */       typeKey(10, true);
/* 141 */     else if (c == ' ')
/* 142 */       typeKey(32, false);
/*     */   }
/*     */ 
/*     */   private void typeKey(int k, boolean uppercase) {
/* 146 */     if (uppercase)
/* 147 */       keyPress(16);
/* 148 */     keyPress(k);
/* 149 */     keyRelease(k);
/* 150 */     if (uppercase)
/* 151 */       keyRelease(16);
/*     */   }
/*     */ 
/*     */   private int fetchKey(char c) {
/*     */     try {
/* 156 */       return KeyEvent.class.getField("VK_" + Character.toUpperCase(c)).getInt(null); } catch (Exception ex) {
/*     */     }
/* 158 */     throw new RuntimeException(ex);
/*     */   }
/*     */ 
/*     */   private boolean clickX()
/*     */   {
/* 163 */     if (this.x == null) {
/* 164 */       System.out.println("x is null");
/* 165 */       clickMouse(this.closeClick.x, this.closeClick.y, 16);
/* 166 */       return false;
/*     */     }
/* 168 */     BufferedImage s = createScreenCapture(this.zone);
/* 169 */     int w = this.x.getWidth();
/* 170 */     int h = this.x.getHeight();
/* 171 */     for (int x = 0; x < s.getWidth() - w; x++) {
/* 172 */       for (int y = 0; y < s.getHeight() - h; y++) {
/* 173 */         if (s.getSubimage(x, y, w, h).equals(this.x)) {
/* 174 */           clickMouse(this.zone.x + x + this.zone.width / 2, this.zone.y + y + this.zone.height / 2, 16);
/* 175 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 179 */     return false;
/*     */   }
/*     */ 
/*     */   private void clickMouse(int x, int y, int button) {
/* 183 */     mouseMove(x, y);
/* 184 */     mousePress(button);
/* 185 */     mouseRelease(button);
/*     */   }
/*     */ }

/* Location:           C:\Users\ramos\Downloads\AdBot4.jar
 * Qualified Name:     adbot.Advertiser
 * JD-Core Version:    0.6.0
 */