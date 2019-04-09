/*   1:    */ package vpt.algorithms.shape;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Stack;
/*   6:    */ import vpt.Algorithm;
/*   7:    */ import vpt.BooleanImage;
/*   8:    */ import vpt.GlobalException;
/*   9:    */ import vpt.Image;
/*  10:    */ 
/*  11:    */ public class ConnectedComponentFromSeed
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14: 19 */   public ArrayList<Point> output = null;
/*  15: 20 */   public Image input = null;
/*  16: 21 */   public Point seed = null;
/*  17:    */   private int xdim;
/*  18:    */   private int ydim;
/*  19: 26 */   private BooleanImage temp = null;
/*  20: 27 */   private BooleanImage temp2 = null;
/*  21: 28 */   private Stack<Point> s = null;
/*  22: 29 */   private ArrayList<Point> list2 = null;
/*  23:    */   
/*  24:    */   public ConnectedComponentFromSeed()
/*  25:    */   {
/*  26: 32 */     this.inputFields = "input, seed";
/*  27: 33 */     this.outputFields = "output";
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void execute()
/*  31:    */     throws GlobalException
/*  32:    */   {
/*  33: 37 */     if (this.input.getCDim() != 1) {
/*  34: 37 */       throw new GlobalException("Sorry, only mono-channel images for now...");
/*  35:    */     }
/*  36: 39 */     this.xdim = this.input.getXDim();
/*  37: 40 */     this.ydim = this.input.getYDim();
/*  38:    */     
/*  39: 42 */     this.output = new ArrayList();
/*  40:    */     
/*  41: 44 */     this.temp = new BooleanImage(this.input, false);
/*  42: 45 */     this.temp.fill(false);
/*  43:    */     
/*  44: 47 */     this.temp2 = new BooleanImage(this.input, false);
/*  45: 48 */     this.temp2.fill(false);
/*  46:    */     
/*  47: 50 */     this.s = new Stack();
/*  48:    */     
/*  49: 52 */     this.list2 = new ArrayList();
/*  50:    */     
/*  51: 54 */     this.output = extractCC(this.seed, this.input);
/*  52:    */   }
/*  53:    */   
/*  54:    */   private ArrayList<Point> extractCC(Point r, Image img)
/*  55:    */   {
/*  56: 58 */     this.s.clear();
/*  57: 59 */     this.s.add(r);
/*  58: 60 */     this.temp.setXYBoolean(r.x, r.y, true);
/*  59: 61 */     this.list2.add(r);
/*  60:    */     
/*  61: 63 */     Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  62: 64 */       new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  63:    */     
/*  64: 66 */     ArrayList<Point> pixels = new ArrayList();
/*  65:    */     int x;
/*  66:    */     int i;
/*  67: 68 */     for (; !this.s.isEmpty(); i < N.length)
/*  68:    */     {
/*  69: 70 */       Point tmp = (Point)this.s.pop();
/*  70:    */       
/*  71: 72 */       x = tmp.x;
/*  72: 73 */       int y = tmp.y;
/*  73: 74 */       pixels.add(tmp);
/*  74:    */       
/*  75: 76 */       this.temp2.setXYBoolean(x, y, true);
/*  76:    */       
/*  77: 78 */       i = 0; continue;
/*  78: 79 */       int _x = x + N[i].x;
/*  79: 80 */       int _y = y + N[i].y;
/*  80: 82 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/*  81: 84 */         if (!this.temp.getXYBoolean(_x, _y))
/*  82:    */         {
/*  83: 86 */           boolean q = img.getXYBoolean(_x, _y);
/*  84: 88 */           if (q)
/*  85:    */           {
/*  86: 90 */             Point t = new Point(_x, _y);
/*  87: 91 */             this.s.add(t);
/*  88:    */             
/*  89: 93 */             this.temp.setXYBoolean(t.x, t.y, true);
/*  90: 94 */             this.list2.add(t);
/*  91:    */           }
/*  92:    */         }
/*  93:    */       }
/*  94: 78 */       i++;
/*  95:    */     }
/*  96: 99 */     for (Point t : this.list2) {
/*  97:100 */       this.temp.setXYBoolean(t.x, t.y, false);
/*  98:    */     }
/*  99:101 */     this.list2.clear();
/* 100:    */     
/* 101:103 */     return pixels;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public static ArrayList<Point> invoke(Image img, Point seed)
/* 105:    */   {
/* 106:    */     try
/* 107:    */     {
/* 108:109 */       return (ArrayList)new ConnectedComponentFromSeed().preprocess(new Object[] { img, seed });
/* 109:    */     }
/* 110:    */     catch (GlobalException e)
/* 111:    */     {
/* 112:111 */       e.printStackTrace();
/* 113:    */     }
/* 114:112 */     return null;
/* 115:    */   }
/* 116:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.ConnectedComponentFromSeed
 * JD-Core Version:    0.7.0.1
 */