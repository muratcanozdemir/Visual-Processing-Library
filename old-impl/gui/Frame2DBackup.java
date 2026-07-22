/*   1:    */ package vpt.gui;
/*   2:    */ 
/*   3:    */ import com.sun.media.jai.widget.DisplayJAI;
/*   4:    */ import java.awt.BorderLayout;
/*   5:    */ import java.awt.Color;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.Point;
/*   8:    */ import java.awt.Toolkit;
/*   9:    */ import java.awt.event.MouseEvent;
/*  10:    */ import java.awt.image.BufferedImage;
/*  11:    */ import java.awt.image.DataBufferByte;
/*  12:    */ import java.awt.image.Raster;
/*  13:    */ import java.awt.image.SampleModel;
/*  14:    */ import java.awt.image.WritableRaster;
/*  15:    */ import java.io.PrintStream;
/*  16:    */ import javax.media.jai.RasterFactory;
/*  17:    */ import javax.swing.BoxLayout;
/*  18:    */ import javax.swing.JFrame;
/*  19:    */ import javax.swing.JLabel;
/*  20:    */ import javax.swing.JPanel;
/*  21:    */ import javax.swing.JScrollPane;
/*  22:    */ import javax.swing.JSlider;
/*  23:    */ import javax.swing.JTextField;
/*  24:    */ import javax.swing.JViewport;
/*  25:    */ import javax.swing.JWindow;
/*  26:    */ import javax.swing.SwingUtilities;
/*  27:    */ import javax.swing.event.ChangeEvent;
/*  28:    */ import javax.swing.event.ChangeListener;
/*  29:    */ import javax.swing.event.MouseInputAdapter;
/*  30:    */ import vpt.Image;
/*  31:    */ 
/*  32:    */ public class Frame2DBackup
/*  33:    */   extends JFrame
/*  34:    */ {
/*  35:    */   private static final long serialVersionUID = 513861596834642798L;
/*  36:    */   private Image img;
/*  37:    */   private String title;
/*  38:    */   private boolean showInColor;
/*  39:    */   private int width;
/*  40:    */   private int height;
/*  41:    */   private int channels;
/*  42:    */   private int channelNumber;
/*  43:    */   private DisplayJAI[] display;
/*  44:    */   private JTextField statusBar;
/*  45:    */   private JScrollPane scrollPane;
/*  46:    */   private JSlider channelSlider;
/*  47:    */   private JLabel channelLabel;
/*  48:    */   private MouseHandler mouseHandler;
/*  49: 63 */   private Color textcolor = new Color(100, 100, 100);
/*  50:    */   
/*  51:    */   public Frame2DBackup(Image img, String title, Boolean showInColor)
/*  52:    */   {
/*  53: 67 */     this.img = img.newInstance(true);
/*  54: 68 */     this.title = title;
/*  55: 69 */     this.showInColor = showInColor.booleanValue();
/*  56: 71 */     if (title != null) {
/*  57: 71 */       setTitle(this.title);
/*  58:    */     }
/*  59: 73 */     this.width = img.getXDim();
/*  60: 74 */     this.height = img.getYDim();
/*  61: 75 */     this.channels = img.getCDim();
/*  62: 79 */     if ((this.channels != 3) && (this.showInColor))
/*  63:    */     {
/*  64: 80 */       System.err.println("Number of channels incompatible for color visualisation!");
/*  65: 81 */       this.showInColor = false;
/*  66:    */     }
/*  67: 84 */     setDefaultCloseOperation(2);
/*  68:    */     
/*  69: 86 */     JPanel root = new JPanel();
/*  70: 87 */     root.setLayout(new BorderLayout());
/*  71: 88 */     setContentPane(root);
/*  72:    */     
/*  73: 90 */     Toolkit tk = Toolkit.getDefaultToolkit();
/*  74: 91 */     Dimension screenSize = tk.getScreenSize();
/*  75: 94 */     if ((this.width > screenSize.width - 100) || (this.height > screenSize.height - 100)) {
/*  76: 95 */       root.setPreferredSize(new Dimension(screenSize.width - 100, screenSize.height - 100));
/*  77:    */     } else {
/*  78: 97 */       root.setPreferredSize(new Dimension(Math.max(this.width + 3, 50), Math.max(this.height + 38, 50)));
/*  79:    */     }
/*  80:100 */     if (!this.showInColor) {
/*  81:100 */       this.display = getGrayscaleDisplay();
/*  82:    */     } else {
/*  83:101 */       this.display = getColorDisplay();
/*  84:    */     }
/*  85:104 */     this.scrollPane = new JScrollPane(this.display[0], 
/*  86:105 */       20, 
/*  87:106 */       30);
/*  88:107 */     this.mouseHandler = new MouseHandler(this);
/*  89:108 */     this.channelNumber = 1;
/*  90:    */     
/*  91:110 */     this.scrollPane.addMouseListener(this.mouseHandler);
/*  92:111 */     this.scrollPane.addMouseMotionListener(this.mouseHandler);
/*  93:    */     
/*  94:113 */     root.add(this.scrollPane, "Center");
/*  95:    */     
/*  96:    */ 
/*  97:    */ 
/*  98:    */ 
/*  99:118 */     JPanel lowerPanel = new JPanel();
/* 100:119 */     lowerPanel.setLayout(new BorderLayout());
/* 101:120 */     root.add(lowerPanel, "South");
/* 102:    */     
/* 103:    */ 
/* 104:123 */     JPanel channelCtrlPanel = new JPanel();
/* 105:124 */     channelCtrlPanel.setLayout(new BoxLayout(channelCtrlPanel, 0));
/* 106:125 */     this.channelLabel = new JLabel(" Channel : 1 / " + this.channels + "  ");
/* 107:126 */     this.channelLabel.setForeground(this.textcolor);
/* 108:    */     
/* 109:128 */     channelCtrlPanel.add(this.channelLabel);
/* 110:    */     
/* 111:130 */     this.channelSlider = new JSlider(0, 1, this.channels, 1);
/* 112:131 */     this.channelSlider.setSnapToTicks(true);
/* 113:132 */     channelCtrlPanel.add(this.channelSlider);
/* 114:    */     
/* 115:134 */     this.channelSlider.addChangeListener(new ChangeListener()
/* 116:    */     {
/* 117:    */       public void stateChanged(ChangeEvent e)
/* 118:    */       {
/* 119:136 */         Frame2DBackup.this.channelNumber = Frame2DBackup.this.channelSlider.getValue();
/* 120:    */         
/* 121:138 */         Frame2DBackup.this.channelLabel.setText(" Channel : " + Integer.toString(Frame2DBackup.this.channelNumber) + " / " + Frame2DBackup.this.channels + "  ");
/* 122:139 */         Frame2DBackup.this.scrollPane.setViewportView(Frame2DBackup.this.display[(Frame2DBackup.this.channelSlider.getValue() - 1)]);
/* 123:    */       }
/* 124:    */     });
/* 125:143 */     if ((this.channels == 1) || (showInColor.booleanValue()))
/* 126:    */     {
/* 127:144 */       this.channelLabel.setEnabled(false);
/* 128:145 */       this.channelSlider.setEnabled(false);
/* 129:    */     }
/* 130:149 */     lowerPanel.add(channelCtrlPanel, "Center");
/* 131:    */     
/* 132:    */ 
/* 133:152 */     this.statusBar = new JTextField();
/* 134:153 */     this.statusBar.setEnabled(false);
/* 135:154 */     this.statusBar.setDisabledTextColor(this.textcolor);
/* 136:    */     
/* 137:    */ 
/* 138:157 */     lowerPanel.add(this.statusBar, "South");
/* 139:    */     
/* 140:159 */     pack();
/* 141:160 */     setVisible(true);
/* 142:    */   }
/* 143:    */   
/* 144:    */   private void statusBarMsg(String s)
/* 145:    */   {
/* 146:166 */     SwingUtilities.invokeLater(new MainFrameRunnable(this, this, s)
/* 147:    */     {
/* 148:    */       public void run()
/* 149:    */       {
/* 150:168 */         this.frame2d.statusBar.setText((String)this.obj);
/* 151:    */       }
/* 152:    */     });
/* 153:    */   }
/* 154:    */   
/* 155:    */   private class MainFrameRunnable
/* 156:    */     implements Runnable
/* 157:    */   {
/* 158:    */     Frame2DBackup frame2d;
/* 159:    */     Object obj;
/* 160:    */     
/* 161:    */     public MainFrameRunnable(Frame2DBackup fr2d, Object obj)
/* 162:    */     {
/* 163:178 */       this.frame2d = fr2d;
/* 164:179 */       this.obj = obj;
/* 165:    */     }
/* 166:    */     
/* 167:    */     public void run() {}
/* 168:    */   }
/* 169:    */   
/* 170:    */   private DisplayJAI[] getColorDisplay()
/* 171:    */   {
/* 172:187 */     DisplayJAI[] displayJAI = new DisplayJAI[1];
/* 173:    */     
/* 174:189 */     int[] channelOffsets = { 0, 1, 2 };
/* 175:    */     
/* 176:    */ 
/* 177:192 */     byte[] pixels = new byte[this.width * this.height * this.channels];
/* 178:193 */     for (int i = 0; i < pixels.length; i++) {
/* 179:194 */       pixels[i] = ((byte)this.img.getByte(i));
/* 180:    */     }
/* 181:198 */     DataBufferByte dbb = new DataBufferByte(pixels, pixels.length);
/* 182:    */     
/* 183:    */ 
/* 184:201 */     SampleModel smodel = RasterFactory.createPixelInterleavedSampleModel(0, this.width, this.height, this.channels, this.channels * this.width, channelOffsets);
/* 185:    */     
/* 186:    */ 
/* 187:204 */     WritableRaster raster = RasterFactory.createWritableRaster(smodel, dbb, null);
/* 188:    */     
/* 189:    */ 
/* 190:207 */     BufferedImage bimg = new BufferedImage(this.width, this.height, 5);
/* 191:208 */     bimg.setData(raster);
/* 192:    */     
/* 193:210 */     displayJAI[0] = new DisplayJAI(bimg);
/* 194:    */     
/* 195:212 */     return displayJAI;
/* 196:    */   }
/* 197:    */   
/* 198:    */   private DisplayJAI[] getGrayscaleDisplay()
/* 199:    */   {
/* 200:218 */     DisplayJAI[] displayJAI = new DisplayJAI[this.channels];
/* 201:220 */     for (int c = 0; c < this.channels; c++)
/* 202:    */     {
/* 203:222 */       Image tmp = this.img.getChannel(c);
/* 204:    */       
/* 205:    */ 
/* 206:225 */       byte[] pixels = new byte[this.width * this.height];
/* 207:227 */       for (int i = 0; i < pixels.length; i++) {
/* 208:228 */         pixels[i] = ((byte)tmp.getByte(i));
/* 209:    */       }
/* 210:232 */       DataBufferByte dbb = new DataBufferByte(pixels, pixels.length);
/* 211:    */       
/* 212:    */ 
/* 213:235 */       SampleModel smodel = RasterFactory.createBandedSampleModel(0, this.width, this.height, 1);
/* 214:    */       
/* 215:    */ 
/* 216:238 */       WritableRaster raster = RasterFactory.createWritableRaster(smodel, dbb, null);
/* 217:    */       
/* 218:    */ 
/* 219:241 */       BufferedImage bimg = new BufferedImage(this.width, this.height, 10);
/* 220:242 */       bimg.setData(raster);
/* 221:243 */       displayJAI[c] = new DisplayJAI(bimg);
/* 222:    */     }
/* 223:246 */     return displayJAI;
/* 224:    */   }
/* 225:    */   
/* 226:    */   private class MouseHandler
/* 227:    */     extends MouseInputAdapter
/* 228:    */   {
/* 229:    */     private Frame2DBackup parent;
/* 230:    */     private JWindow lens;
/* 231:    */     private JPanel lensPanel;
/* 232:256 */     private int widthX = 25;
/* 233:257 */     private int heightX = 25;
/* 234:260 */     private int lensWidth = 100;
/* 235:261 */     private int lensHeight = 100;
/* 236:263 */     private JScrollPane scrollX = null;
/* 237:    */     
/* 238:    */     MouseHandler(Frame2DBackup parent)
/* 239:    */     {
/* 240:266 */       this.parent = parent;
/* 241:    */       
/* 242:268 */       this.lens = new JWindow(parent);
/* 243:    */       
/* 244:270 */       this.lensPanel = new JPanel();
/* 245:271 */       this.lensPanel.setPreferredSize(new Dimension(this.lensWidth, this.lensHeight));
/* 246:272 */       this.lensPanel.setLayout(new BorderLayout());
/* 247:    */       
/* 248:274 */       this.lens.setContentPane(this.lensPanel);
/* 249:275 */       this.lens.pack();
/* 250:    */     }
/* 251:    */     
/* 252:    */     private void updateImage(int xdim, int ydim)
/* 253:    */     {
/* 254:279 */       JViewport view = this.parent.scrollPane.getViewport();
/* 255:280 */       Point local = view.getViewPosition();
/* 256:    */       
/* 257:282 */       byte[] pixels = null;
/* 258:    */       
/* 259:284 */       int[] ipixel = null;
/* 260:285 */       int[] bandOffsets = { 0, 1, 2 };
/* 261:286 */       int[] voidPixel = new int[3];
/* 262:288 */       if (!this.parent.showInColor)
/* 263:    */       {
/* 264:289 */         ipixel = new int[1];
/* 265:290 */         pixels = new byte[this.lensWidth * this.lensHeight];
/* 266:    */       }
/* 267:    */       else
/* 268:    */       {
/* 269:292 */         ipixel = new int[3];
/* 270:293 */         pixels = new byte[this.lensWidth * this.lensHeight * 3];
/* 271:    */       }
/* 272:296 */       int xref = xdim + local.x - this.widthX / 2;
/* 273:297 */       int yref = ydim + local.y - this.heightX / 2;
/* 274:299 */       for (int x = 0; x < this.widthX; x++) {
/* 275:300 */         for (int y = 0; y < this.heightX; y++) {
/* 276:301 */           if ((xref + x < 0) || (xref + x >= this.parent.width) || (yref + y < 0) || (yref + y >= this.parent.height))
/* 277:    */           {
/* 278:302 */             if (!this.parent.showInColor) {
/* 279:302 */               setPixel(pixels, x * 4, y * 4, (byte)0);
/* 280:    */             } else {
/* 281:303 */               setColorPixel(pixels, x * 4, y * 4, voidPixel);
/* 282:    */             }
/* 283:    */           }
/* 284:    */           else
/* 285:    */           {
/* 286:305 */             ipixel = Frame2DBackup.this.img.getVXYByte(xref + x, yref + y);
/* 287:306 */             if (!this.parent.showInColor) {
/* 288:306 */               setPixel(pixels, x * 4, y * 4, (byte)ipixel[0]);
/* 289:    */             } else {
/* 290:307 */               setColorPixel(pixels, x * 4, y * 4, ipixel);
/* 291:    */             }
/* 292:    */           }
/* 293:    */         }
/* 294:    */       }
/* 295:312 */       BufferedImage bimg = null;
/* 296:313 */       if (!this.parent.showInColor)
/* 297:    */       {
/* 298:314 */         DataBufferByte dbb = new DataBufferByte(pixels, this.lensWidth * this.lensHeight);
/* 299:315 */         SampleModel s = RasterFactory.createBandedSampleModel(0, this.lensWidth, this.lensHeight, 1);
/* 300:316 */         Raster r = RasterFactory.createWritableRaster(s, dbb, new Point(0, 0));
/* 301:317 */         bimg = new BufferedImage(this.lensWidth, this.lensHeight, 10);
/* 302:318 */         bimg.setData(r);
/* 303:    */       }
/* 304:    */       else
/* 305:    */       {
/* 306:320 */         DataBufferByte dbb = new DataBufferByte(pixels, this.lensWidth * this.lensHeight * 3);
/* 307:321 */         SampleModel s = RasterFactory.createPixelInterleavedSampleModel(0, this.lensWidth, this.lensHeight, 3, 3 * this.lensWidth, bandOffsets);
/* 308:322 */         Raster r = RasterFactory.createWritableRaster(s, dbb, new Point(0, 0));
/* 309:323 */         bimg = new BufferedImage(this.lensWidth, this.lensHeight, 5);
/* 310:324 */         bimg.setData(r);
/* 311:    */       }
/* 312:327 */       DisplayJAI disp = new DisplayJAI(bimg);
/* 313:329 */       if (this.scrollX == null)
/* 314:    */       {
/* 315:330 */         this.scrollX = new JScrollPane(disp, 21, 31);
/* 316:331 */         this.lensPanel.add(this.scrollX, "Center");
/* 317:    */         
/* 318:333 */         this.lens.pack();
/* 319:    */       }
/* 320:    */       else
/* 321:    */       {
/* 322:335 */         this.scrollX.setViewportView(disp);
/* 323:    */       }
/* 324:338 */       Point p = this.parent.scrollPane.getLocationOnScreen();
/* 325:339 */       this.lens.setLocation(p.x + xdim - this.lensWidth, p.y + ydim - this.lensHeight);
/* 326:340 */       this.lens.setVisible(true);
/* 327:    */     }
/* 328:    */     
/* 329:    */     private void setPixel(byte[] pixels, int x, int y, byte v)
/* 330:    */     {
/* 331:344 */       for (int _x = x; _x < x + 4; _x++) {
/* 332:345 */         for (int _y = y; _y < y + 4; _y++) {
/* 333:346 */           pixels[(_x + this.lensWidth * _y)] = v;
/* 334:    */         }
/* 335:    */       }
/* 336:    */     }
/* 337:    */     
/* 338:    */     private void setColorPixel(byte[] pixels, int x, int y, int[] v)
/* 339:    */     {
/* 340:352 */       for (int c = 0; c < 3; c++) {
/* 341:353 */         for (int _x = x; _x < x + 4; _x++) {
/* 342:354 */           for (int _y = y; _y < y + 4; _y++) {
/* 343:355 */             pixels[(c + 3 * _x + 3 * this.lensWidth * _y)] = ((byte)v[c]);
/* 344:    */           }
/* 345:    */         }
/* 346:    */       }
/* 347:    */     }
/* 348:    */     
/* 349:    */     public void mouseDragged(MouseEvent e)
/* 350:    */     {
/* 351:362 */       updateImage(e.getX(), e.getY());
/* 352:363 */       mouseMoved(e);
/* 353:    */     }
/* 354:    */     
/* 355:    */     public void mousePressed(MouseEvent e)
/* 356:    */     {
/* 357:367 */       if (e.getButton() == 1)
/* 358:    */       {
/* 359:368 */         updateImage(e.getX(), e.getY());
/* 360:369 */         mouseMoved(e);
/* 361:    */       }
/* 362:    */     }
/* 363:    */     
/* 364:    */     public void mouseMoved(MouseEvent e)
/* 365:    */     {
/* 366:374 */       int x = e.getX();
/* 367:375 */       int y = e.getY();
/* 368:    */       
/* 369:377 */       JViewport view = this.parent.scrollPane.getViewport();
/* 370:378 */       Point local = view.getViewPosition();
/* 371:    */       
/* 372:380 */       x += local.x;
/* 373:381 */       y += local.y;
/* 374:383 */       if ((x >= this.parent.width) || (y >= this.parent.height) || (x < 0) || (y < 0)) {
/* 375:383 */         return;
/* 376:    */       }
/* 377:385 */       if (Frame2DBackup.this.showInColor)
/* 378:    */       {
/* 379:386 */         int[] pixels = new int[Frame2DBackup.this.channels];
/* 380:388 */         for (int c = 0; c < Frame2DBackup.this.channels; c++) {
/* 381:389 */           pixels[c] = Frame2DBackup.this.img.getXYCByte(x, y, c);
/* 382:    */         }
/* 383:391 */         this.parent.statusBarMsg("(" + x + "," + y + "):(" + pixels[0] + "," + pixels[1] + "," + pixels[2] + ")");
/* 384:    */       }
/* 385:    */       else
/* 386:    */       {
/* 387:394 */         int pixel = Frame2DBackup.this.img.getXYCByte(x, y, Frame2DBackup.this.channelNumber - 1);
/* 388:395 */         this.parent.statusBarMsg("(" + x + "," + y + "):(" + pixel + ")");
/* 389:    */       }
/* 390:    */     }
/* 391:    */     
/* 392:    */     public void mouseReleased(MouseEvent e)
/* 393:    */     {
/* 394:400 */       this.lens.setVisible(false);
/* 395:    */     }
/* 396:    */   }
/* 397:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.gui.Frame2DBackup
 * JD-Core Version:    0.7.0.1
 */