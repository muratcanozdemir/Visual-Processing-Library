/*   1:    */ package vpt.gui;
/*   2:    */ 
/*   3:    */ import com.sun.media.jai.widget.DisplayJAI;
/*   4:    */ import java.awt.BorderLayout;
/*   5:    */ import java.awt.Color;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.Point;
/*   8:    */ import java.awt.Toolkit;
/*   9:    */ import java.awt.event.ActionEvent;
/*  10:    */ import java.awt.event.ActionListener;
/*  11:    */ import java.awt.event.MouseEvent;
/*  12:    */ import java.awt.image.BufferedImage;
/*  13:    */ import java.awt.image.DataBufferByte;
/*  14:    */ import java.awt.image.Raster;
/*  15:    */ import java.awt.image.SampleModel;
/*  16:    */ import java.awt.image.WritableRaster;
/*  17:    */ import java.io.PrintStream;
/*  18:    */ import javax.media.jai.RasterFactory;
/*  19:    */ import javax.swing.BoxLayout;
/*  20:    */ import javax.swing.ButtonGroup;
/*  21:    */ import javax.swing.JFrame;
/*  22:    */ import javax.swing.JLabel;
/*  23:    */ import javax.swing.JPanel;
/*  24:    */ import javax.swing.JRadioButton;
/*  25:    */ import javax.swing.JScrollPane;
/*  26:    */ import javax.swing.JSlider;
/*  27:    */ import javax.swing.JTextField;
/*  28:    */ import javax.swing.JViewport;
/*  29:    */ import javax.swing.JWindow;
/*  30:    */ import javax.swing.SwingUtilities;
/*  31:    */ import javax.swing.event.ChangeEvent;
/*  32:    */ import javax.swing.event.ChangeListener;
/*  33:    */ import javax.swing.event.MouseInputAdapter;
/*  34:    */ import vpt.Image;
/*  35:    */ 
/*  36:    */ public class Frame2D
/*  37:    */   extends JFrame
/*  38:    */ {
/*  39:    */   private static final long serialVersionUID = 513861596834642798L;
/*  40:    */   private Image img;
/*  41:    */   private String title;
/*  42:    */   private boolean showInColor;
/*  43:    */   private int width;
/*  44:    */   private int height;
/*  45:    */   private int channels;
/*  46:    */   private int channelNumber;
/*  47:    */   private DisplayJAI[] display;
/*  48:    */   private JTextField statusBar;
/*  49:    */   private JScrollPane scrollPane;
/*  50:    */   private JSlider channelSlider;
/*  51:    */   private JLabel channelLabel;
/*  52:    */   private MouseHandler mouseHandler;
/*  53: 68 */   private Color textcolor = new Color(100, 100, 100);
/*  54: 70 */   private int activeTool = 0;
/*  55: 72 */   private final int ZOOM = 0;
/*  56: 73 */   private final int DRAW = 1;
/*  57:    */   
/*  58:    */   public Frame2D(Image img, String title, Boolean showInColor)
/*  59:    */   {
/*  60: 77 */     this.img = img.newInstance(true);
/*  61: 78 */     this.title = title;
/*  62: 79 */     this.showInColor = showInColor.booleanValue();
/*  63: 81 */     if (title != null) {
/*  64: 81 */       setTitle(this.title);
/*  65:    */     }
/*  66: 83 */     this.width = img.getXDim();
/*  67: 84 */     this.height = img.getYDim();
/*  68: 85 */     this.channels = img.getCDim();
/*  69: 89 */     if ((this.channels != 3) && (this.showInColor))
/*  70:    */     {
/*  71: 90 */       System.err.println("Number of channels incompatible for color visualisation!");
/*  72: 91 */       this.showInColor = false;
/*  73:    */     }
/*  74: 94 */     setDefaultCloseOperation(2);
/*  75:    */     
/*  76: 96 */     JPanel root = new JPanel();
/*  77: 97 */     root.setLayout(new BorderLayout());
/*  78: 98 */     setContentPane(root);
/*  79:    */     
/*  80:100 */     Toolkit tk = Toolkit.getDefaultToolkit();
/*  81:101 */     Dimension screenSize = tk.getScreenSize();
/*  82:104 */     if ((this.width > screenSize.width - 100) || (this.height > screenSize.height - 100)) {
/*  83:105 */       root.setPreferredSize(new Dimension(screenSize.width - 100, screenSize.height - 100));
/*  84:    */     } else {
/*  85:107 */       root.setPreferredSize(new Dimension(Math.max(this.width + 10, 50), Math.max(this.height + 75, 50)));
/*  86:    */     }
/*  87:110 */     if (!this.showInColor) {
/*  88:110 */       this.display = getGrayscaleDisplay();
/*  89:    */     } else {
/*  90:111 */       this.display = getColorDisplay();
/*  91:    */     }
/*  92:114 */     this.scrollPane = new JScrollPane(this.display[0], 
/*  93:115 */       20, 
/*  94:116 */       30);
/*  95:117 */     this.mouseHandler = new MouseHandler(this);
/*  96:118 */     this.channelNumber = 1;
/*  97:    */     
/*  98:120 */     this.scrollPane.addMouseListener(this.mouseHandler);
/*  99:121 */     this.scrollPane.addMouseMotionListener(this.mouseHandler);
/* 100:    */     
/* 101:123 */     root.add(this.scrollPane, "Center");
/* 102:    */     
/* 103:    */ 
/* 104:    */ 
/* 105:    */ 
/* 106:128 */     JPanel lowerPanel = new JPanel();
/* 107:129 */     lowerPanel.setLayout(new BorderLayout());
/* 108:130 */     root.add(lowerPanel, "South");
/* 109:    */     
/* 110:    */ 
/* 111:133 */     JPanel channelCtrlPanel = new JPanel();
/* 112:134 */     channelCtrlPanel.setLayout(new BoxLayout(channelCtrlPanel, 0));
/* 113:135 */     this.channelLabel = new JLabel(" Channel : 1 / " + this.channels + "  ");
/* 114:136 */     this.channelLabel.setForeground(this.textcolor);
/* 115:    */     
/* 116:138 */     channelCtrlPanel.add(this.channelLabel);
/* 117:    */     
/* 118:140 */     this.channelSlider = new JSlider(0, 1, this.channels, 1);
/* 119:141 */     this.channelSlider.setSnapToTicks(true);
/* 120:142 */     channelCtrlPanel.add(this.channelSlider);
/* 121:    */     
/* 122:144 */     this.channelSlider.addChangeListener(new ChangeListener()
/* 123:    */     {
/* 124:    */       public void stateChanged(ChangeEvent e)
/* 125:    */       {
/* 126:146 */         Frame2D.this.channelNumber = Frame2D.this.channelSlider.getValue();
/* 127:    */         
/* 128:148 */         Frame2D.this.channelLabel.setText(" Channel : " + Integer.toString(Frame2D.this.channelNumber) + " / " + Frame2D.this.channels + "  ");
/* 129:149 */         Frame2D.this.scrollPane.setViewportView(Frame2D.this.display[(Frame2D.this.channelSlider.getValue() - 1)]);
/* 130:    */       }
/* 131:    */     });
/* 132:153 */     if ((this.channels == 1) || (showInColor.booleanValue()))
/* 133:    */     {
/* 134:154 */       this.channelLabel.setEnabled(false);
/* 135:155 */       this.channelSlider.setEnabled(false);
/* 136:    */     }
/* 137:159 */     lowerPanel.add(channelCtrlPanel, "Center");
/* 138:    */     
/* 139:    */ 
/* 140:162 */     JPanel toolPanel = new JPanel();
/* 141:    */     
/* 142:164 */     JRadioButton rb1 = new JRadioButton("Zoom");
/* 143:165 */     rb1.addActionListener(new ActionListener()
/* 144:    */     {
/* 145:    */       public void actionPerformed(ActionEvent arg0)
/* 146:    */       {
/* 147:167 */         Frame2D.this.activeTool = 0;
/* 148:    */       }
/* 149:170 */     });
/* 150:171 */     rb1.setSelected(true);
/* 151:172 */     JRadioButton rb2 = new JRadioButton("Draw");
/* 152:173 */     rb2.addActionListener(new ActionListener()
/* 153:    */     {
/* 154:    */       public void actionPerformed(ActionEvent arg0)
/* 155:    */       {
/* 156:175 */         Frame2D.this.activeTool = 1;
/* 157:    */       }
/* 158:178 */     });
/* 159:179 */     ButtonGroup group = new ButtonGroup();
/* 160:180 */     group.add(rb1);
/* 161:181 */     group.add(rb2);
/* 162:    */     
/* 163:183 */     toolPanel.add(rb1);
/* 164:184 */     toolPanel.add(rb2);
/* 165:    */     
/* 166:186 */     lowerPanel.add(toolPanel, "North");
/* 167:    */     
/* 168:    */ 
/* 169:189 */     this.statusBar = new JTextField();
/* 170:190 */     this.statusBar.setEnabled(false);
/* 171:191 */     this.statusBar.setDisabledTextColor(this.textcolor);
/* 172:    */     
/* 173:    */ 
/* 174:194 */     lowerPanel.add(this.statusBar, "South");
/* 175:    */     
/* 176:196 */     pack();
/* 177:197 */     setVisible(true);
/* 178:    */   }
/* 179:    */   
/* 180:    */   private void statusBarMsg(String s)
/* 181:    */   {
/* 182:203 */     SwingUtilities.invokeLater(new MainFrameRunnable(this, this, s)
/* 183:    */     {
/* 184:    */       public void run()
/* 185:    */       {
/* 186:205 */         this.frame2d.statusBar.setText((String)this.obj);
/* 187:    */       }
/* 188:    */     });
/* 189:    */   }
/* 190:    */   
/* 191:    */   private class MainFrameRunnable
/* 192:    */     implements Runnable
/* 193:    */   {
/* 194:    */     Frame2D frame2d;
/* 195:    */     Object obj;
/* 196:    */     
/* 197:    */     public MainFrameRunnable(Frame2D fr2d, Object obj)
/* 198:    */     {
/* 199:215 */       this.frame2d = fr2d;
/* 200:216 */       this.obj = obj;
/* 201:    */     }
/* 202:    */     
/* 203:    */     public void run() {}
/* 204:    */   }
/* 205:    */   
/* 206:    */   private DisplayJAI[] getColorDisplay()
/* 207:    */   {
/* 208:224 */     DisplayJAI[] displayJAI = new DisplayJAI[1];
/* 209:    */     
/* 210:226 */     int[] channelOffsets = { 0, 1, 2 };
/* 211:    */     
/* 212:    */ 
/* 213:229 */     byte[] pixels = new byte[this.width * this.height * this.channels];
/* 214:230 */     for (int i = 0; i < pixels.length; i++) {
/* 215:231 */       pixels[i] = ((byte)this.img.getByte(i));
/* 216:    */     }
/* 217:235 */     DataBufferByte dbb = new DataBufferByte(pixels, pixels.length);
/* 218:    */     
/* 219:    */ 
/* 220:238 */     SampleModel smodel = RasterFactory.createPixelInterleavedSampleModel(0, this.width, this.height, this.channels, this.channels * this.width, channelOffsets);
/* 221:    */     
/* 222:    */ 
/* 223:241 */     WritableRaster raster = RasterFactory.createWritableRaster(smodel, dbb, null);
/* 224:    */     
/* 225:    */ 
/* 226:244 */     BufferedImage bimg = new BufferedImage(this.width, this.height, 5);
/* 227:245 */     bimg.setData(raster);
/* 228:    */     
/* 229:247 */     displayJAI[0] = new DisplayJAI(bimg);
/* 230:    */     
/* 231:249 */     return displayJAI;
/* 232:    */   }
/* 233:    */   
/* 234:    */   private DisplayJAI[] getGrayscaleDisplay()
/* 235:    */   {
/* 236:255 */     DisplayJAI[] displayJAI = new DisplayJAI[this.channels];
/* 237:257 */     for (int c = 0; c < this.channels; c++)
/* 238:    */     {
/* 239:259 */       Image tmp = this.img.getChannel(c);
/* 240:    */       
/* 241:    */ 
/* 242:262 */       byte[] pixels = new byte[this.width * this.height];
/* 243:264 */       for (int i = 0; i < pixels.length; i++) {
/* 244:265 */         pixels[i] = ((byte)tmp.getByte(i));
/* 245:    */       }
/* 246:269 */       DataBufferByte dbb = new DataBufferByte(pixels, pixels.length);
/* 247:    */       
/* 248:    */ 
/* 249:272 */       SampleModel smodel = RasterFactory.createBandedSampleModel(0, this.width, this.height, 1);
/* 250:    */       
/* 251:    */ 
/* 252:275 */       WritableRaster raster = RasterFactory.createWritableRaster(smodel, dbb, null);
/* 253:    */       
/* 254:    */ 
/* 255:278 */       BufferedImage bimg = new BufferedImage(this.width, this.height, 10);
/* 256:279 */       bimg.setData(raster);
/* 257:280 */       displayJAI[c] = new DisplayJAI(bimg);
/* 258:    */     }
/* 259:283 */     return displayJAI;
/* 260:    */   }
/* 261:    */   
/* 262:    */   private class MouseHandler
/* 263:    */     extends MouseInputAdapter
/* 264:    */   {
/* 265:    */     private Frame2D parent;
/* 266:    */     private JWindow lens;
/* 267:    */     private JPanel lensPanel;
/* 268:293 */     private int widthX = 25;
/* 269:294 */     private int heightX = 25;
/* 270:297 */     private int lensWidth = 100;
/* 271:298 */     private int lensHeight = 100;
/* 272:300 */     private JScrollPane scrollX = null;
/* 273:    */     
/* 274:    */     MouseHandler(Frame2D parent)
/* 275:    */     {
/* 276:303 */       this.parent = parent;
/* 277:    */       
/* 278:305 */       this.lens = new JWindow(parent);
/* 279:    */       
/* 280:307 */       this.lensPanel = new JPanel();
/* 281:308 */       this.lensPanel.setPreferredSize(new Dimension(this.lensWidth, this.lensHeight));
/* 282:309 */       this.lensPanel.setLayout(new BorderLayout());
/* 283:    */       
/* 284:311 */       this.lens.setContentPane(this.lensPanel);
/* 285:312 */       this.lens.pack();
/* 286:    */     }
/* 287:    */     
/* 288:    */     private void updateImage(int xdim, int ydim)
/* 289:    */     {
/* 290:316 */       JViewport view = this.parent.scrollPane.getViewport();
/* 291:317 */       Point local = view.getViewPosition();
/* 292:    */       
/* 293:319 */       byte[] pixels = null;
/* 294:    */       
/* 295:321 */       int[] ipixel = null;
/* 296:322 */       int[] bandOffsets = { 0, 1, 2 };
/* 297:323 */       int[] voidPixel = new int[3];
/* 298:325 */       if (!this.parent.showInColor)
/* 299:    */       {
/* 300:326 */         ipixel = new int[1];
/* 301:327 */         pixels = new byte[this.lensWidth * this.lensHeight];
/* 302:    */       }
/* 303:    */       else
/* 304:    */       {
/* 305:329 */         ipixel = new int[3];
/* 306:330 */         pixels = new byte[this.lensWidth * this.lensHeight * 3];
/* 307:    */       }
/* 308:333 */       int xref = xdim + local.x - this.widthX / 2;
/* 309:334 */       int yref = ydim + local.y - this.heightX / 2;
/* 310:336 */       for (int x = 0; x < this.widthX; x++) {
/* 311:337 */         for (int y = 0; y < this.heightX; y++) {
/* 312:338 */           if ((xref + x < 0) || (xref + x >= this.parent.width) || (yref + y < 0) || (yref + y >= this.parent.height))
/* 313:    */           {
/* 314:339 */             if (!this.parent.showInColor) {
/* 315:339 */               setPixel(pixels, x * 4, y * 4, (byte)0);
/* 316:    */             } else {
/* 317:340 */               setColorPixel(pixels, x * 4, y * 4, voidPixel);
/* 318:    */             }
/* 319:    */           }
/* 320:    */           else
/* 321:    */           {
/* 322:342 */             ipixel = Frame2D.this.img.getVXYByte(xref + x, yref + y);
/* 323:343 */             if (!this.parent.showInColor) {
/* 324:343 */               setPixel(pixels, x * 4, y * 4, (byte)ipixel[0]);
/* 325:    */             } else {
/* 326:344 */               setColorPixel(pixels, x * 4, y * 4, ipixel);
/* 327:    */             }
/* 328:    */           }
/* 329:    */         }
/* 330:    */       }
/* 331:349 */       BufferedImage bimg = null;
/* 332:350 */       if (!this.parent.showInColor)
/* 333:    */       {
/* 334:351 */         DataBufferByte dbb = new DataBufferByte(pixels, this.lensWidth * this.lensHeight);
/* 335:352 */         SampleModel s = RasterFactory.createBandedSampleModel(0, this.lensWidth, this.lensHeight, 1);
/* 336:353 */         Raster r = RasterFactory.createWritableRaster(s, dbb, new Point(0, 0));
/* 337:354 */         bimg = new BufferedImage(this.lensWidth, this.lensHeight, 10);
/* 338:355 */         bimg.setData(r);
/* 339:    */       }
/* 340:    */       else
/* 341:    */       {
/* 342:357 */         DataBufferByte dbb = new DataBufferByte(pixels, this.lensWidth * this.lensHeight * 3);
/* 343:358 */         SampleModel s = RasterFactory.createPixelInterleavedSampleModel(0, this.lensWidth, this.lensHeight, 3, 3 * this.lensWidth, bandOffsets);
/* 344:359 */         Raster r = RasterFactory.createWritableRaster(s, dbb, new Point(0, 0));
/* 345:360 */         bimg = new BufferedImage(this.lensWidth, this.lensHeight, 5);
/* 346:361 */         bimg.setData(r);
/* 347:    */       }
/* 348:364 */       DisplayJAI disp = new DisplayJAI(bimg);
/* 349:366 */       if (this.scrollX == null)
/* 350:    */       {
/* 351:367 */         this.scrollX = new JScrollPane(disp, 21, 31);
/* 352:368 */         this.lensPanel.add(this.scrollX, "Center");
/* 353:    */         
/* 354:370 */         this.lens.pack();
/* 355:    */       }
/* 356:    */       else
/* 357:    */       {
/* 358:372 */         this.scrollX.setViewportView(disp);
/* 359:    */       }
/* 360:375 */       Point p = this.parent.scrollPane.getLocationOnScreen();
/* 361:376 */       this.lens.setLocation(p.x + xdim - this.lensWidth, p.y + ydim - this.lensHeight);
/* 362:377 */       this.lens.setVisible(true);
/* 363:    */     }
/* 364:    */     
/* 365:    */     private void setPixel(byte[] pixels, int x, int y, byte v)
/* 366:    */     {
/* 367:381 */       for (int _x = x; _x < x + 4; _x++) {
/* 368:382 */         for (int _y = y; _y < y + 4; _y++) {
/* 369:383 */           pixels[(_x + this.lensWidth * _y)] = v;
/* 370:    */         }
/* 371:    */       }
/* 372:    */     }
/* 373:    */     
/* 374:    */     private void setColorPixel(byte[] pixels, int x, int y, int[] v)
/* 375:    */     {
/* 376:389 */       for (int c = 0; c < 3; c++) {
/* 377:390 */         for (int _x = x; _x < x + 4; _x++) {
/* 378:391 */           for (int _y = y; _y < y + 4; _y++) {
/* 379:392 */             pixels[(c + 3 * _x + 3 * this.lensWidth * _y)] = ((byte)v[c]);
/* 380:    */           }
/* 381:    */         }
/* 382:    */       }
/* 383:    */     }
/* 384:    */     
/* 385:    */     public void mouseDragged(MouseEvent e)
/* 386:    */     {
/* 387:400 */       if (Frame2D.this.activeTool == 0) {
/* 388:401 */         updateImage(e.getX(), e.getY());
/* 389:    */       }
/* 390:404 */       mouseMoved(e);
/* 391:    */     }
/* 392:    */     
/* 393:    */     public void mousePressed(MouseEvent e)
/* 394:    */     {
/* 395:408 */       if (e.getButton() == 1)
/* 396:    */       {
/* 397:410 */         if (Frame2D.this.activeTool == 0) {
/* 398:411 */           updateImage(e.getX(), e.getY());
/* 399:    */         }
/* 400:414 */         mouseMoved(e);
/* 401:    */       }
/* 402:    */     }
/* 403:    */     
/* 404:    */     public void mouseMoved(MouseEvent e)
/* 405:    */     {
/* 406:419 */       int x = e.getX();
/* 407:420 */       int y = e.getY();
/* 408:    */       
/* 409:422 */       JViewport view = this.parent.scrollPane.getViewport();
/* 410:423 */       Point local = view.getViewPosition();
/* 411:    */       
/* 412:425 */       x += local.x;
/* 413:426 */       y += local.y;
/* 414:428 */       if ((x >= this.parent.width) || (y >= this.parent.height) || (x < 0) || (y < 0)) {
/* 415:428 */         return;
/* 416:    */       }
/* 417:430 */       if (Frame2D.this.showInColor)
/* 418:    */       {
/* 419:431 */         int[] pixels = new int[Frame2D.this.channels];
/* 420:433 */         for (int c = 0; c < Frame2D.this.channels; c++) {
/* 421:434 */           pixels[c] = Frame2D.this.img.getXYCByte(x, y, c);
/* 422:    */         }
/* 423:436 */         this.parent.statusBarMsg("(" + x + "," + y + "):(" + pixels[0] + "," + pixels[1] + "," + pixels[2] + ")");
/* 424:    */       }
/* 425:    */       else
/* 426:    */       {
/* 427:439 */         int pixel = Frame2D.this.img.getXYCByte(x, y, Frame2D.this.channelNumber - 1);
/* 428:440 */         this.parent.statusBarMsg("(" + x + "," + y + "):(" + pixel + ")");
/* 429:    */       }
/* 430:    */     }
/* 431:    */     
/* 432:    */     public void mouseReleased(MouseEvent e)
/* 433:    */     {
/* 434:445 */       this.lens.setVisible(false);
/* 435:    */     }
/* 436:    */   }
/* 437:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.gui.Frame2D
 * JD-Core Version:    0.7.0.1
 */