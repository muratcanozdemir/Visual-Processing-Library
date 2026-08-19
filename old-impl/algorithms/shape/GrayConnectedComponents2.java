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
/*  11:    */ public class GrayConnectedComponents2
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14: 21 */   public ArrayList<Point>[][] output = null;
/*  15: 22 */   public Image input = null;
/*  16:    */   private int xdim;
/*  17:    */   private int ydim;
/*  18: 27 */   private BooleanImage temp = null;
/*  19: 28 */   private BooleanImage temp2 = null;
/*  20: 29 */   private Stack<Point> s = null;
/*  21: 30 */   private ArrayList<Point> list2 = null;
/*  22:    */   
/*  23:    */   public GrayConnectedComponents2()
/*  24:    */   {
/*  25: 33 */     this.inputFields = "input";
/*  26: 34 */     this.outputFields = "output";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void execute()
/*  30:    */     throws GlobalException
/*  31:    */   {
/*  32: 38 */     int cdim = this.input.getCDim();
/*  33: 39 */     if (cdim != 1) {
/*  34: 39 */       throw new GlobalException("Sorry, only mono-channel images for now...");
/*  35:    */     }
/*  36: 41 */     this.xdim = this.input.getXDim();
/*  37: 42 */     this.ydim = this.input.getYDim();
/*  38:    */     
/*  39: 44 */     this.output = new ArrayList[this.xdim][this.ydim];
/*  40:    */     
/*  41:    */ 
/*  42: 47 */     this.temp = new BooleanImage(this.input, false);
/*  43: 48 */     this.temp.fill(false);
/*  44:    */     
/*  45:    */ 
/*  46: 51 */     this.temp2 = new BooleanImage(this.input, false);
/*  47: 52 */     this.temp2.fill(false);
/*  48:    */     
/*  49: 54 */     this.s = new Stack();
/*  50:    */     
/*  51:    */ 
/*  52: 57 */     this.list2 = new ArrayList();
/*  53: 60 */     for (int y = 0; y < this.ydim; y++) {
/*  54: 61 */       for (int x = 0; x < this.xdim; x++) {
/*  55: 64 */         if (!this.temp2.getXYBoolean(x, y))
/*  56:    */         {
/*  57: 67 */           ArrayList<Point> cc = extractCC(new Point(x, y), this.input, this.input.getXYInt(x, y));
/*  58: 70 */           for (Point p : cc) {
/*  59: 71 */             this.output[p.x][p.y] = cc;
/*  60:    */           }
/*  61:    */         }
/*  62:    */       }
/*  63:    */     }
/*  64:    */   }
/*  65:    */   
/*  66:    */   private ArrayList<Point> extractCC(Point r, Image img, int label)
/*  67:    */   {
/*  68: 77 */     this.s.clear();
/*  69: 78 */     this.s.add(r);
/*  70: 79 */     this.temp.setXYBoolean(r.x, r.y, true);
/*  71: 80 */     this.list2.add(r);
/*  72:    */     
/*  73: 82 */     Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  74: 83 */       new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  75:    */     
/*  76: 85 */     ArrayList<Point> pixels = new ArrayList();
/*  77:    */     int x;
/*  78:    */     int i;
/*  79: 87 */     for (; !this.s.isEmpty(); i < N.length)
/*  80:    */     {
/*  81: 89 */       Point tmp = (Point)this.s.pop();
/*  82:    */       
/*  83: 91 */       x = tmp.x;
/*  84: 92 */       int y = tmp.y;
/*  85: 93 */       pixels.add(tmp);
/*  86:    */       
/*  87:    */ 
/*  88: 96 */       this.temp2.setXYBoolean(x, y, true);
/*  89:    */       
/*  90: 98 */       i = 0; continue;
/*  91: 99 */       int _x = x + N[i].x;
/*  92:100 */       int _y = y + N[i].y;
/*  93:102 */       if ((_x >= 0) && (_x < this.xdim) && (_y >= 0) && (_y < this.ydim)) {
/*  94:105 */         if (!this.temp.getXYBoolean(_x, _y)) {
/*  95:108 */           if (img.getXYInt(_x, _y) == label)
/*  96:    */           {
/*  97:110 */             Point t = new Point(_x, _y);
/*  98:111 */             this.s.add(t);
/*  99:    */             
/* 100:113 */             this.temp.setXYBoolean(t.x, t.y, true);
/* 101:114 */             this.list2.add(t);
/* 102:    */           }
/* 103:    */         }
/* 104:    */       }
/* 105: 98 */       i++;
/* 106:    */     }
/* 107:119 */     for (Point t : this.list2) {
/* 108:120 */       this.temp.setXYBoolean(t.x, t.y, false);
/* 109:    */     }
/* 110:121 */     this.list2.clear();
/* 111:    */     
/* 112:123 */     return pixels;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public static ArrayList<Point>[][] invoke(Image img)
/* 116:    */   {
/* 117:    */     try
/* 118:    */     {
/* 119:129 */       return (ArrayList[][])new GrayConnectedComponents2().preprocess(new Object[] { img });
/* 120:    */     }
/* 121:    */     catch (GlobalException e)
/* 122:    */     {
/* 123:131 */       e.printStackTrace();
/* 124:    */     }
/* 125:132 */     return null;
/* 126:    */   }
/* 127:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.GrayConnectedComponents2
 * JD-Core Version:    0.7.0.1
 */