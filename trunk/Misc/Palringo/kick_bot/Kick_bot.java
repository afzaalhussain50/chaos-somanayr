/*     */ package kick_bot;
/*     */ 
/*     */ import java.awt.AWTException;
/*     */ import java.awt.Color;
/*     */ import java.awt.MouseInfo;
/*     */ import java.awt.Point;
/*     */ import java.awt.PointerInfo;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Robot;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.io.PrintStream;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSlider;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JToggleButton;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ 
/*     */ public class Kick_bot extends JFrame
/*     */ {
/*  31 */   private static boolean detect_bans = false;
/*     */ 
/*  33 */   private static boolean ban = false;
/*     */   private static Color ban_color;
/*     */   private static final long serialVersionUID = 1L;
/* 119 */   private JPanel jContentPane = null;
/*     */ 
/* 121 */   private JButton Start = null;
/*     */ 
/* 123 */   private JButton Set_1 = null;
/*     */ 
/* 125 */   private static JTextField x_1 = null;
/*     */ 
/* 127 */   private static JTextField x_3 = null;
/*     */ 
/* 129 */   private static JTextField y_1 = null;
/*     */ 
/* 131 */   private static JTextField y_3 = null;
/*     */ 
/* 133 */   private JLabel jLabel = null;
/*     */ 
/* 135 */   private JLabel jLabel2 = null;
/*     */ 
/* 137 */   private static JSlider interval = null;
/*     */ 
/* 139 */   private JLabel jLabel3 = null;
/*     */ 
/* 141 */   private static JTextField num_message = null;
/*     */ 
/* 143 */   private JLabel jLabel4 = null;
/*     */ 
/* 145 */   private JLabel interval_num = null;
/*     */ 
/* 147 */   private JLabel status = null;
/*     */ 
/* 149 */   private JLabel jLabel1 = null;
/*     */ 
/* 151 */   private static JTextField ban_x = null;
/*     */ 
/* 153 */   private static JTextField ban_y = null;
/*     */ 
/* 155 */   private JToggleButton detect_ban = null;
/*     */ 
/* 157 */   private JButton Set_2 = null;
/*     */ 
/* 159 */   private JButton Set_3 = null;
/*     */ 
/* 161 */   private static JTextField Ban_color = null;
/*     */ 
/* 163 */   private JLabel jLabel5 = null;
/*     */ 
/*     */   public static void ban_check()
/*     */     throws AWTException
/*     */   {
/*  38 */     System.out.println("checking...");
/*  39 */     Robot unban = new Robot();
/*  40 */     int x_loc = Integer.parseInt(ban_x.getText());
/*  41 */     int y_loc = Integer.parseInt(ban_y.getText());
/*  42 */     unban.mouseMove(x_loc, y_loc);
/*  43 */     unban.delay(100);
/*  44 */     Color check_ban = unban.getPixelColor(x_loc, y_loc);
/*  45 */     System.out.print(check_ban.toString());
/*  46 */     System.out.print("vs");
/*  47 */     System.out.print(Ban_color.getText());
/*  48 */     int check_blue = check_ban.getBlue();
/*  49 */     int ban_blue = ban_color.getBlue();
/*  50 */     if (check_blue == ban_blue) {
/*  51 */       System.out.println("Ban confirmed");
/*  52 */       Click(x_loc, y_loc, 1);
/*  53 */       ban = true;
/*     */     }
/*     */     else {
/*  56 */       System.out.println("Ban is false");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void Click(int x, int y, int button) throws AWTException {
/*  61 */     Robot mouseclick = new Robot();
/*  62 */     mouseclick.mouseMove(x, y);
/*  63 */     if (button == 1) {
/*  64 */       mouseclick.mousePress(16);
/*  65 */       mouseclick.mouseRelease(16);
/*     */     }
/*  67 */     if (button == 2) {
/*  68 */       mouseclick.mousePress(8);
/*  69 */       mouseclick.mouseRelease(8);
/*     */     }
/*  71 */     if (button == 3) {
/*  72 */       mouseclick.mousePress(4);
/*  73 */       mouseclick.mouseRelease(4);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void kick() throws AWTException {
/*  78 */     Robot mouse = new Robot();
/*  79 */     int x_one = Integer.parseInt(x_1.getText());
/*  80 */     int y_one = Integer.parseInt(y_1.getText());
/*  81 */     int x_three = Integer.parseInt(x_3.getText());
/*  82 */     int y_three = Integer.parseInt(y_3.getText());
/*  83 */     int delay = interval.getValue();
/*  84 */     int messages = Integer.parseInt(num_message.getText());
/*  85 */     while (messages > 0) {
/*  86 */       Click(x_one, y_one, 3);
/*  87 */       String s = String.valueOf(detect_bans);
/*  88 */       System.out.println(s);
/*  89 */       if (detect_bans) {
/*  90 */         System.out.println("Preparing to check");
/*  91 */         ban_check();
/*     */       }
/*  93 */       if (!ban) {
/*  94 */         Click(x_three, y_three, 1);
/*     */       }
/*  96 */       mouse.delay(delay);
/*  97 */       messages--;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static int getLocationX()
/*     */   {
/* 103 */     PointerInfo a = MouseInfo.getPointerInfo();
/* 104 */     Point b = a.getLocation();
/* 105 */     int x = (int)b.getX();
/* 106 */     return x;
/*     */   }
/*     */ 
/*     */   private static int getLocationY()
/*     */   {
/* 111 */     PointerInfo a = MouseInfo.getPointerInfo();
/* 112 */     Point b = a.getLocation();
/* 113 */     int y = (int)b.getY();
/* 114 */     return y;
/*     */   }
/*     */ 
/*     */   public Kick_bot()
/*     */   {
/* 170 */     initialize();
/*     */   }
/*     */ 
/*     */   private void initialize()
/*     */   {
/* 179 */     setSize(348, 280);
/* 180 */     setContentPane(getJContentPane());
/* 181 */     setTitle("Kick 'em hard");
/*     */   }
/*     */ 
/*     */   private JPanel getJContentPane()
/*     */   {
/* 190 */     if (this.jContentPane == null) {
/* 191 */       this.jLabel5 = new JLabel();
/* 192 */       this.jLabel5.setBounds(new Rectangle(22, 104, 74, 18));
/* 193 */       this.jLabel5.setText("Ban color");
/* 194 */       this.jLabel1 = new JLabel();
/* 195 */       this.jLabel1.setBounds(new Rectangle(25, 74, 71, 18));
/* 196 */       this.jLabel1.setText("Detect bans");
/* 197 */       this.status = new JLabel();
/* 198 */       this.status.setBounds(new Rectangle(19, 4, 266, 12));
/* 199 */       this.status.setText("Press \"set\" to beigin");
/* 200 */       this.interval_num = new JLabel();
/* 201 */       this.interval_num.setBounds(new Rectangle(293, 136, 39, 13));
/* 202 */       this.interval_num.setText("750");
/* 203 */       this.jLabel4 = new JLabel();
/* 204 */       this.jLabel4.setBounds(new Rectangle(14, 165, 129, 16));
/* 205 */       this.jLabel4.setDisplayedMnemonic(0);
/* 206 */       this.jLabel4.setText("Number of messages");
/* 207 */       this.jLabel3 = new JLabel();
/* 208 */       this.jLabel3.setBounds(new Rectangle(18, 137, 54, 17));
/* 209 */       this.jLabel3.setText("Interval");
/* 210 */       this.jLabel2 = new JLabel();
/* 211 */       this.jLabel2.setBounds(new Rectangle(17, 51, 80, 17));
/* 212 */       this.jLabel2.setText("Kick loc");
/* 213 */       this.jLabel = new JLabel();
/* 214 */       this.jLabel.setBounds(new Rectangle(19, 21, 75, 18));
/* 215 */       this.jLabel.setText("Rt Clck loc");
/* 216 */       this.jContentPane = new JPanel();
/* 217 */       this.jContentPane.setLayout(null);
/* 218 */       this.jContentPane.add(getStart(), null);
/* 219 */       this.jContentPane.add(getSet_1(), null);
/* 220 */       this.jContentPane.add(getX_1(), null);
/* 221 */       this.jContentPane.add(getX_3(), null);
/* 222 */       this.jContentPane.add(getY_1(), null);
/* 223 */       this.jContentPane.add(getY_3(), null);
/* 224 */       this.jContentPane.add(this.jLabel, null);
/* 225 */       this.jContentPane.add(this.jLabel2, null);
/* 226 */       this.jContentPane.add(getInterval(), null);
/* 227 */       this.jContentPane.add(this.jLabel3, null);
/* 228 */       this.jContentPane.add(getNum_message(), null);
/* 229 */       this.jContentPane.add(this.jLabel4, null);
/* 230 */       this.jContentPane.add(this.interval_num, null);
/* 231 */       this.jContentPane.add(this.status, null);
/* 232 */       this.jContentPane.add(this.jLabel1, null);
/* 233 */       this.jContentPane.add(getBan_x(), null);
/* 234 */       this.jContentPane.add(getBan_y(), null);
/* 235 */       this.jContentPane.add(getDetect_ban(), null);
/* 236 */       this.jContentPane.add(getSet_2(), null);
/* 237 */       this.jContentPane.add(getSet_3(), null);
/* 238 */       this.jContentPane.add(getBan_color(), null);
/* 239 */       this.jContentPane.add(this.jLabel5, null);
/*     */     }
/* 241 */     return this.jContentPane;
/*     */   }
/*     */ 
/*     */   private JButton getStart()
/*     */   {
/* 250 */     if (this.Start == null) {
/* 251 */       this.Start = new JButton();
/* 252 */       this.Start.setBounds(new Rectangle(235, 213, 90, 25));
/* 253 */       this.Start.setText("Start");
/* 254 */       this.Start.addActionListener(new ActionListener() {
/*     */         public void actionPerformed(ActionEvent e) {
/* 256 */           String s = String.valueOf(Kick_bot.detect_bans);
/* 257 */           System.out.println(s);
/*     */           try {
/* 259 */             Kick_bot.access$1();
/*     */           }
/*     */           catch (AWTException e1) {
/* 262 */             e1.printStackTrace();
/*     */           }
/*     */         } } );
/*     */     }
/* 267 */     return this.Start;
/*     */   }
/*     */ 
/*     */   private JButton getSet_1()
/*     */   {
/* 276 */     if (this.Set_1 == null) {
/* 277 */       this.Set_1 = new JButton();
/* 278 */       this.Set_1.setBounds(new Rectangle(249, 21, 63, 21));
/* 279 */       this.Set_1.setText("Set");
/* 280 */       this.Set_1.addMouseListener(new MouseListener()
/*     */       {
/*     */         public void mouseClicked(MouseEvent e) {
/* 283 */           Kick_bot.this.status.setText("Move mouse to the contacts list under any group");
/*     */           try {
/* 285 */             Robot delay = new Robot();
/* 286 */             delay.delay(2000);
/*     */           }
/*     */           catch (AWTException e1) {
/* 289 */             e1.printStackTrace();
/*     */           }
/* 291 */           String x = String.valueOf(Kick_bot.access$3());
/* 292 */           Kick_bot.x_1.setText(x);
/* 293 */           String y = String.valueOf(Kick_bot.access$5());
/* 294 */           Kick_bot.y_1.setText(y);
/*     */         }
/*     */         public void mousePressed(MouseEvent e) {
/*     */         }
/*     */         public void mouseReleased(MouseEvent e) {
/*     */         }
/*     */         public void mouseEntered(MouseEvent e) {
/*     */         }
/*     */         public void mouseExited(MouseEvent e) {
/*     */         } } );
/*     */     }
/* 306 */     return this.Set_1;
/*     */   }
/*     */ 
/*     */   private JTextField getX_1()
/*     */   {
/* 315 */     if (x_1 == null) {
/* 316 */       x_1 = new JTextField();
/* 317 */       x_1.setBounds(new Rectangle(113, 21, 49, 19));
/* 318 */       x_1.setText("0");
/*     */     }
/* 320 */     return x_1;
/*     */   }
/*     */ 
/*     */   private JTextField getX_3()
/*     */   {
/* 329 */     if (x_3 == null) {
/* 330 */       x_3 = new JTextField();
/* 331 */       x_3.setBounds(new Rectangle(114, 51, 50, 21));
/* 332 */       x_3.setText("0");
/*     */     }
/* 334 */     return x_3;
/*     */   }
/*     */ 
/*     */   private JTextField getY_1()
/*     */   {
/* 343 */     if (y_1 == null) {
/* 344 */       y_1 = new JTextField();
/* 345 */       y_1.setBounds(new Rectangle(181, 21, 49, 20));
/* 346 */       y_1.setText("0");
/*     */     }
/* 348 */     return y_1;
/*     */   }
/*     */ 
/*     */   private JTextField getY_3()
/*     */   {
/* 357 */     if (y_3 == null) {
/* 358 */       y_3 = new JTextField();
/* 359 */       y_3.setBounds(new Rectangle(181, 50, 49, 22));
/* 360 */       y_3.setText("0");
/*     */     }
/* 362 */     return y_3;
/*     */   }
/*     */ 
/*     */   private JSlider getInterval()
/*     */   {
/* 371 */     if (interval == null) {
/* 372 */       interval = new JSlider();
/* 373 */       interval.setBounds(new Rectangle(77, 129, 212, 32));
/* 374 */       interval.setMajorTickSpacing(20);
/* 375 */       interval.setValue(750);
/* 376 */       interval.setMinimum(500);
/* 377 */       interval.setMaximum(2000);
/* 378 */       interval.addChangeListener(new ChangeListener() {
/*     */         public void stateChanged(ChangeEvent e) {
/* 380 */           Kick_bot.this.interval_num.setText(String.valueOf(Kick_bot.interval.getValue()));
/*     */         } } );
/*     */     }
/* 384 */     return interval;
/*     */   }
/*     */ 
/*     */   private JTextField getNum_message()
/*     */   {
/* 393 */     if (num_message == null) {
/* 394 */       num_message = new JTextField();
/* 395 */       num_message.setBounds(new Rectangle(152, 163, 83, 19));
/*     */     }
/* 397 */     return num_message;
/*     */   }
/*     */ 
/*     */   private JTextField getBan_x()
/*     */   {
/* 408 */     if (ban_x == null) {
/* 409 */       ban_x = new JTextField();
/* 410 */       ban_x.setBounds(new Rectangle(115, 78, 53, 20));
/*     */     }
/* 412 */     return ban_x;
/*     */   }
/*     */ 
/*     */   private JTextField getBan_y()
/*     */   {
/* 421 */     if (ban_y == null) {
/* 422 */       ban_y = new JTextField();
/* 423 */       ban_y.setBounds(new Rectangle(183, 79, 53, 20));
/*     */     }
/* 425 */     return ban_y;
/*     */   }
/*     */ 
/*     */   private JToggleButton getDetect_ban()
/*     */   {
/* 434 */     if (this.detect_ban == null) {
/* 435 */       this.detect_ban = new JToggleButton();
/* 436 */       this.detect_ban.setBounds(new Rectangle(6, 77, 12, 13));
/* 437 */       this.detect_ban.addMouseListener(new MouseListener() {
/*     */         public void mouseClicked(MouseEvent e) {
/* 439 */           Kick_bot.detect_bans = true;
/* 440 */           System.out.println("Detect banning is enabled");
/* 441 */           String s = String.valueOf(Kick_bot.detect_bans);
/* 442 */           System.out.println(s);
/*     */         }
/*     */         public void mousePressed(MouseEvent e) {
/*     */         }
/*     */         public void mouseReleased(MouseEvent e) {
/*     */         }
/*     */         public void mouseEntered(MouseEvent e) {
/*     */         }
/*     */         public void mouseExited(MouseEvent e) {
/*     */         } } );
/*     */     }
/* 454 */     return this.detect_ban;
/*     */   }
/*     */ 
/*     */   private JButton getSet_2()
/*     */   {
/* 463 */     if (this.Set_2 == null) {
/* 464 */       this.Set_2 = new JButton();
/* 465 */       this.Set_2.setBounds(new Rectangle(250, 50, 63, 21));
/* 466 */       this.Set_2.setText("Set");
/* 467 */       this.Set_2.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 470 */           int x_one = Integer.parseInt(Kick_bot.x_1.getText());
/* 471 */           int y_one = Integer.parseInt(Kick_bot.y_1.getText());
/* 472 */           Kick_bot.this.status.setText("Hover over \"kick\"");
/*     */           try {
/* 474 */             Kick_bot.Click(x_one, y_one, 3);
/*     */           }
/*     */           catch (AWTException e2) {
/* 477 */             e2.printStackTrace();
/*     */           }
/*     */           try {
/* 480 */             Robot delay = new Robot();
/* 481 */             delay.delay(2000);
/*     */           } catch (AWTException e1) {
/* 483 */             e1.printStackTrace();
/*     */           }
/* 485 */           String x = String.valueOf(Kick_bot.access$3());
/* 486 */           Kick_bot.x_3.setText(x);
/* 487 */           String y = String.valueOf(Kick_bot.access$5());
/* 488 */           Kick_bot.y_3.setText(y);
/*     */         } } );
/*     */     }
/* 492 */     return this.Set_2;
/*     */   }
/*     */ 
/*     */   private JButton getSet_3()
/*     */   {
/* 501 */     if (this.Set_3 == null) {
/* 502 */       this.Set_3 = new JButton();
/* 503 */       this.Set_3.setBounds(new Rectangle(248, 78, 63, 21));
/* 504 */       this.Set_3.setText("Set");
/* 505 */       this.Set_3.addActionListener(new ActionListener() {
/*     */         public void actionPerformed(ActionEvent e) {
/* 507 */           int x_one = Integer.parseInt(Kick_bot.x_1.getText());
/* 508 */           int y_one = Integer.parseInt(Kick_bot.y_1.getText());
/* 509 */           int x_three = Integer.parseInt(Kick_bot.x_3.getText());
/* 510 */           int y_three = Integer.parseInt(Kick_bot.y_3.getText());
/* 511 */           Kick_bot.this.status.setText("Hover over the ban check mark");
/*     */           try {
/* 513 */             Kick_bot.Click(x_one, y_one, 3);
/* 514 */             Robot mouse = new Robot();
/* 515 */             mouse.mouseMove(x_three, y_three);
/*     */           }
/*     */           catch (AWTException e1) {
/* 518 */             e1.printStackTrace();
/*     */           }
/*     */           try {
/* 521 */             Robot delay = new Robot();
/* 522 */             delay = new Robot();
/* 523 */             delay.delay(2000);
/*     */           } catch (AWTException e1) {
/* 525 */             e1.printStackTrace();
/*     */           }
/* 527 */           String x = String.valueOf(Kick_bot.access$3());
/* 528 */           Kick_bot.ban_x.setText(x);
/* 529 */           String y = String.valueOf(Kick_bot.access$5());
/* 530 */           Kick_bot.ban_y.setText(y);
/*     */           try
/*     */           {
/* 533 */             Robot delay = new Robot();
/* 534 */             Kick_bot.ban_color = delay.getPixelColor(Integer.parseInt(Kick_bot.ban_x.getText()), Integer.parseInt(Kick_bot.ban_y.getText()));
/*     */           }
/*     */           catch (AWTException e1) {
/* 537 */             e1.printStackTrace();
/*     */           }
/* 539 */           Kick_bot.Ban_color.setText(Kick_bot.ban_color.toString());
/*     */         } } );
/*     */     }
/* 543 */     return this.Set_3;
/*     */   }
/*     */ 
/*     */   private static JTextField getBan_color()
/*     */   {
/* 552 */     if (Ban_color == null) {
/* 553 */       Ban_color = new JTextField();
/* 554 */       Ban_color.setBounds(new Rectangle(115, 104, 195, 20));
/*     */     }
/* 556 */     return Ban_color;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 561 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 563 */         Kick_bot thisClass = new Kick_bot();
/* 564 */         thisClass.setDefaultCloseOperation(3);
/* 565 */         thisClass.setVisible(true);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           C:\Users\ramos\Downloads\AutoKicker1.1.jar
 * Qualified Name:     kick_bot.Kick_bot
 * JD-Core Version:    0.6.0
 */