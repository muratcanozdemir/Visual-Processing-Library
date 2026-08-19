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
/*  11:    */ public class GrayConnectedComponents
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14: 19 */   public ArrayList<ArrayList<Point>> output = null;
/*  15: 20 */   public Image input = null;
/*  16:    */   private int xdim;
/*  17:    */   private int ydim;
/*  18: 25 */   private BooleanImage temp = null;
/*  19: 26 */   private BooleanImage temp2 = null;
/*  20: 27 */   private Stack<Point> s = null;
/*  21: 28 */   private ArrayList<Point> list2 = null;
/*  22:    */   
/*  23:    */   public GrayConnectedComponents()
/*  24:    */   {
/*  25: 31 */     this.inputFields = "input";
/*  26: 32 */     this.outputFields = "output";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void execute()
/*  30:    */     throws GlobalException
/*  31:    */   {
/*  32: 36 */     int cdim = this.input.getCDim();
/*  33: 37 */     if (cdim != 1) {
/*  34: 37 */       throw new GlobalException("Sorry, only mono-channel images for now...");
/*  35:    */     }
/*  36: 39 */     this.xdim = this.input.getXDim();
/*  37: 40 */     this.ydim = this.input.getYDim();
/*  38:    */     
/*  39: 42 */     this.output = new ArrayList();
/*  40:    */     
/*  41:    */ 
/*  42: 45 */     this.temp = new BooleanImage(this.input, false);
/*  43: 46 */     this.temp.fill(false);
/*  44:    */     
/*  45:    */ 
/*  46: 49 */     this.temp2 = new BooleanImage(this.input, false);
/*  47: 50 */     this.temp2.fill(false);
/*  48:    */     
/*  49: 52 */     this.s = new Stack();
/*  50:    */     
/*  51:    */ 
/*  52: 55 */     this.list2 = new ArrayList();
/*  53: 58 */     for (int y = 0; y < this.ydim; y++) {
/*  54: 59 */       for (int x = 0; x < this.xdim; x++) {
/*  55: 62 */         if (!this.temp2.getXYBoolean(x, y))
/*  56:    */         {
/*  57: 65 */           ArrayList<Point> cc = extractCC(new Point(x, y), this.input, this.input.getXYInt(x, y));
/*  58:    */           
/*  59: 67 */           this.output.add(cc);
/*  60:    */         }
/*  61:    */       }
/*  62:    */     }
/*  63:    */   }
/*  64:    */   
/*  65:    */   private ArrayList<Point> extractCC(Point r, Image img, int label)
/*  66:    */   {
/*  67: 73 */     this.s.clear();
/*  68: 74 */     this.s.add(r);
/*  69: 75 */     this.temp.setXYBoolean(r.x, r.y, true);
/*  70: 76 */     this.list2.add(r);
/*  71:    */     
/*  72: 78 */     Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  73: 79 */       new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  74:    */     
/*  75: 81 */     ArrayList<Point> pixels = new ArrayList();
/*  76:    */     int x;
/*  77:    */     int i;
/*  78: 83 */     for (; !this.s.isEmpty(); i < N.length)
/*  79:    */     {
/*  80: 85 */       Point tmp = (Point)this.s.pop();
/*  81:    */       
/*  82: 87 */       x = tmp.x;
/*  83: 88 */       int y = tmp.y;
/*  84: 89 */       pixels.add(tmp);
/*  85:    */       
/*  86:    */ 
/*  87: 92 */       this.temp2.setXYBoolean(x, y, true);
/*  88:    */       
/*  89: 94 */       i = 0; continue;
/*  90: 95 */       int _x = x + N[i].x;
/*  91: 96 */       int _y = y + N[i].y;
/*  92: 98 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/*  93:101 */         if (!this.temp.getXYBoolean(_x, _y)) {
/*  94:104 */           if (img.getXYInt(_x, _y) == label)
/*  95:    */           {
/*  96:106 */             Point t = new Point(_x, _y);
/*  97:107 */             this.s.add(t);
/*  98:    */             
/*  99:109 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 100:110 */             this.list2.add(t);
/* 101:    */           }
/* 102:    */         }
/* 103:    */       }
/* 104: 94 */       i++;
/* 105:    */     }
/* 106:115 */     for (Point t : this.list2) {
/* 107:116 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 108:    */     }
/* 109:117 */     this.list2.clear();
/* 110:    */     
/* 111:119 */     return pixels;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public static ArrayList<ArrayList<Point>> invoke(Image img)
/* 115:    */   {
/* 116:    */     try
/* 117:    */     {
/* 118:125 */       return (ArrayList)new GrayConnectedComponents().preprocess(new Object[] { img });
/* 119:    */     }
/* 120:    */     catch (GlobalException e)
/* 121:    */     {
/* 122:127 */       e.printStackTrace();
/* 123:    */     }
/* 124:128 */     return null;
/* 125:    */   }
/* 126:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.GrayConnectedComponents
 * JD-Core Version:    0.7.0.1
 */