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
/*  11:    */ public class ConnectedComponents
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
/*  23:    */   public ConnectedComponents()
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
/*  41: 44 */     this.temp = new BooleanImage(this.input, false);
/*  42: 45 */     this.temp.fill(false);
/*  43:    */     
/*  44: 47 */     this.temp2 = new BooleanImage(this.input, false);
/*  45: 48 */     this.temp2.fill(false);
/*  46:    */     
/*  47: 50 */     this.s = new Stack();
/*  48:    */     
/*  49: 52 */     this.list2 = new ArrayList();
/*  50: 55 */     for (int y = 0; y < this.ydim; y++) {
/*  51: 56 */       for (int x = 0; x < this.xdim; x++)
/*  52:    */       {
/*  53: 58 */         boolean b = this.input.getXYBoolean(x, y);
/*  54: 59 */         if (b)
/*  55:    */         {
/*  56: 62 */           b = this.temp2.getXYBoolean(x, y);
/*  57: 63 */           if (!b)
/*  58:    */           {
/*  59: 65 */             ArrayList<Point> cc = extractCC(new Point(x, y), this.input);
/*  60:    */             
/*  61: 67 */             this.output.add(cc);
/*  62:    */           }
/*  63:    */         }
/*  64:    */       }
/*  65:    */     }
/*  66:    */   }
/*  67:    */   
/*  68:    */   private ArrayList<Point> extractCC(Point r, Image img)
/*  69:    */   {
/*  70: 73 */     this.s.clear();
/*  71: 74 */     this.s.add(r);
/*  72: 75 */     this.temp.setXYBoolean(r.x, r.y, true);
/*  73: 76 */     this.list2.add(r);
/*  74:    */     
/*  75: 78 */     Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  76: 79 */       new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  77:    */     
/*  78: 81 */     ArrayList<Point> pixels = new ArrayList();
/*  79:    */     int x;
/*  80:    */     int i;
/*  81: 83 */     for (; !this.s.isEmpty(); i < N.length)
/*  82:    */     {
/*  83: 85 */       Point tmp = (Point)this.s.pop();
/*  84:    */       
/*  85: 87 */       x = tmp.x;
/*  86: 88 */       int y = tmp.y;
/*  87: 89 */       pixels.add(tmp);
/*  88:    */       
/*  89: 91 */       this.temp2.setXYBoolean(x, y, true);
/*  90:    */       
/*  91: 93 */       i = 0; continue;
/*  92: 94 */       int _x = x + N[i].x;
/*  93: 95 */       int _y = y + N[i].y;
/*  94: 97 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/*  95: 99 */         if (!this.temp.getXYBoolean(_x, _y))
/*  96:    */         {
/*  97:101 */           boolean q = img.getXYBoolean(_x, _y);
/*  98:103 */           if (q)
/*  99:    */           {
/* 100:105 */             Point t = new Point(_x, _y);
/* 101:106 */             this.s.add(t);
/* 102:    */             
/* 103:108 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 104:109 */             this.list2.add(t);
/* 105:    */           }
/* 106:    */         }
/* 107:    */       }
/* 108: 93 */       i++;
/* 109:    */     }
/* 110:114 */     for (Point t : this.list2) {
/* 111:115 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 112:    */     }
/* 113:116 */     this.list2.clear();
/* 114:    */     
/* 115:118 */     return pixels;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public static ArrayList<ArrayList<Point>> invoke(Image img)
/* 119:    */   {
/* 120:    */     try
/* 121:    */     {
/* 122:124 */       return (ArrayList)new ConnectedComponents().preprocess(new Object[] { img });
/* 123:    */     }
/* 124:    */     catch (GlobalException e)
/* 125:    */     {
/* 126:126 */       e.printStackTrace();
/* 127:    */     }
/* 128:127 */     return null;
/* 129:    */   }
/* 130:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.ConnectedComponents
 * JD-Core Version:    0.7.0.1
 */