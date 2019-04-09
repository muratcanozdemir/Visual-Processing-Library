/*   1:    */ package vpt.algorithms.geometric;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.LinkedList;
/*   6:    */ import vpt.Algorithm;
/*   7:    */ import vpt.BooleanImage;
/*   8:    */ import vpt.GlobalException;
/*   9:    */ import vpt.Image;
/*  10:    */ 
/*  11:    */ public class KeepLargestCC
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14: 19 */   public BooleanImage output = null;
/*  15: 20 */   public Image input = null;
/*  16:    */   private BooleanImage temp;
/*  17: 26 */   private LinkedList<Point> fifo = null;
/*  18: 28 */   private Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  19: 29 */     new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  20:    */   
/*  21:    */   public KeepLargestCC()
/*  22:    */   {
/*  23: 32 */     this.inputFields = "input";
/*  24: 33 */     this.outputFields = "output";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void execute()
/*  28:    */     throws GlobalException
/*  29:    */   {
/*  30: 37 */     int cdim = this.input.getCDim();
/*  31: 38 */     int xdim = this.input.getXDim();
/*  32: 39 */     int ydim = this.input.getYDim();
/*  33: 41 */     if (cdim != 1) {
/*  34: 41 */       throw new GlobalException("Only mono-channel images for now...");
/*  35:    */     }
/*  36: 43 */     this.output = new BooleanImage(this.input, false);
/*  37: 44 */     this.output.fill(false);
/*  38:    */     
/*  39: 46 */     Image mask = new BooleanImage(this.input, false);
/*  40: 47 */     mask.fill(false);
/*  41:    */     
/*  42: 49 */     this.fifo = new LinkedList();
/*  43:    */     
/*  44: 51 */     this.temp = new BooleanImage(xdim, ydim, 1);
/*  45: 52 */     this.temp.fill(false);
/*  46:    */     
/*  47:    */ 
/*  48: 55 */     ArrayList<ArrayList<Point>> CCs = new ArrayList();
/*  49: 57 */     for (int x = 0; x < xdim; x++) {
/*  50: 58 */       for (int y = 0; y < ydim; y++) {
/*  51: 61 */         if (this.input.getXYBoolean(x, y)) {
/*  52: 64 */           if (!mask.getXYBoolean(x, y))
/*  53:    */           {
/*  54: 67 */             ArrayList<Point> newCC = new ArrayList();
/*  55: 68 */             CCs.add(newCC);
/*  56:    */             
/*  57: 70 */             Point r = new Point(x, y);
/*  58:    */             
/*  59: 72 */             this.fifo.clear();
/*  60: 73 */             this.fifo.add(r);
/*  61: 74 */             this.temp.setXYBoolean(x, y, true);
/*  62:    */             
/*  63: 76 */             ArrayList<Point> list2 = new ArrayList();
/*  64: 77 */             list2.add(r);
/*  65:    */             int i;
/*  66: 79 */             for (; !this.fifo.isEmpty(); i < this.N.length)
/*  67:    */             {
/*  68: 80 */               Point p = (Point)this.fifo.removeFirst();
/*  69: 81 */               mask.setXYBoolean(p.x, p.y, true);
/*  70: 82 */               newCC.add(p);
/*  71:    */               
/*  72: 84 */               i = 0; continue;
/*  73: 85 */               int _x = p.x + this.N[i].x;
/*  74: 86 */               int _y = p.y + this.N[i].y;
/*  75: 88 */               if ((_x >= 0) && (_x < xdim) && (_y >= 0) && (_y < ydim)) {
/*  76: 91 */                 if (this.input.getXYBoolean(_x, _y)) {
/*  77: 94 */                   if (!this.temp.getXYBoolean(_x, _y))
/*  78:    */                   {
/*  79: 96 */                     Point t = new Point(_x, _y);
/*  80: 97 */                     this.fifo.add(t);
/*  81:    */                     
/*  82: 99 */                     this.temp.setXYBoolean(t.x, t.y, true);
/*  83:100 */                     list2.add(t);
/*  84:    */                   }
/*  85:    */                 }
/*  86:    */               }
/*  87: 84 */               i++;
/*  88:    */             }
/*  89:104 */             for (Point t : list2) {
/*  90:105 */               this.temp.setXYBoolean(t.x, t.y, false);
/*  91:    */             }
/*  92:    */           }
/*  93:    */         }
/*  94:    */       }
/*  95:    */     }
/*  96:110 */     int maxSize = 0;
/*  97:111 */     int index = 0;
/*  98:112 */     for (int i = 0; i < CCs.size(); i++) {
/*  99:113 */       if (((ArrayList)CCs.get(i)).size() > maxSize)
/* 100:    */       {
/* 101:114 */         maxSize = ((ArrayList)CCs.get(i)).size();
/* 102:115 */         index = i;
/* 103:    */       }
/* 104:    */     }
/* 105:120 */     ArrayList<Point> largestCC = (ArrayList)CCs.get(index);
/* 106:122 */     for (int i = 0; i < largestCC.size(); i++)
/* 107:    */     {
/* 108:123 */       Point p = (Point)largestCC.get(i);
/* 109:124 */       this.output.setXYBoolean(p.x, p.y, true);
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   public static Image invoke(Image image)
/* 114:    */   {
/* 115:    */     try
/* 116:    */     {
/* 117:131 */       return (Image)new KeepLargestCC().preprocess(new Object[] { image });
/* 118:    */     }
/* 119:    */     catch (GlobalException e)
/* 120:    */     {
/* 121:133 */       e.printStackTrace();
/* 122:    */     }
/* 123:134 */     return null;
/* 124:    */   }
/* 125:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.geometric.KeepLargestCC
 * JD-Core Version:    0.7.0.1
 */