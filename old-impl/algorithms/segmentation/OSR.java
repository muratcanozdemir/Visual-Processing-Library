/*   1:    */ package vpt.algorithms.segmentation;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Stack;
/*   6:    */ import vpt.Algorithm;
/*   7:    */ import vpt.BooleanImage;
/*   8:    */ import vpt.GlobalException;
/*   9:    */ import vpt.Image;
/*  10:    */ import vpt.IntegerImage;
/*  11:    */ 
/*  12:    */ public class OSR
/*  13:    */   extends Algorithm
/*  14:    */ {
/*  15:    */   public Image map;
/*  16:    */   public Image reference;
/*  17:    */   public Double osr;
/*  18:    */   
/*  19:    */   public OSR()
/*  20:    */   {
/*  21: 35 */     this.inputFields = "map,reference";
/*  22: 36 */     this.outputFields = "osr";
/*  23:    */   }
/*  24:    */   
/*  25:    */   public void execute()
/*  26:    */     throws GlobalException
/*  27:    */   {
/*  28: 44 */     int rn1 = numberOfFZ(this.map);
/*  29: 45 */     int rn2 = numberOfFZ(this.reference);
/*  30:    */     
/*  31: 47 */     this.osr = Double.valueOf(rn1 / rn2);
/*  32:    */   }
/*  33:    */   
/*  34:    */   private int numberOfFZ(Image img)
/*  35:    */   {
/*  36: 51 */     int xdim = img.getXDim();
/*  37: 52 */     int ydim = img.getYDim();
/*  38:    */     
/*  39: 54 */     Image temp2 = new IntegerImage(xdim, ydim, 1);
/*  40: 55 */     temp2.fill(0);
/*  41:    */     
/*  42: 57 */     BooleanImage temp = new BooleanImage(xdim, ydim, 1);
/*  43: 58 */     temp.fill(false);
/*  44:    */     
/*  45: 60 */     int label = 1;
/*  46: 62 */     for (int x = 0; x < xdim; x++) {
/*  47: 64 */       for (int y = 0; y < ydim; y++) {
/*  48: 66 */         if (temp2.getXYInt(x, y) <= 0)
/*  49:    */         {
/*  50: 68 */           Point t = new Point(x, y);
/*  51:    */           
/*  52: 70 */           createFZ(img, t, label++, temp, temp2, xdim, ydim);
/*  53:    */         }
/*  54:    */       }
/*  55:    */     }
/*  56: 74 */     return label - 1;
/*  57:    */   }
/*  58:    */   
/*  59:    */   private void createFZ(Image input, Point r, int label, BooleanImage temp, Image temp2, int xdim, int ydim)
/*  60:    */   {
/*  61: 79 */     ArrayList<Point> list2 = new ArrayList();
/*  62: 80 */     Stack<Point> s = new Stack();
/*  63:    */     
/*  64:    */ 
/*  65: 83 */     Point[] N = { new Point(1, 0), new Point(0, 1), new Point(-1, 0), new Point(0, -1), 
/*  66: 84 */       new Point(1, 1), new Point(-1, -1), new Point(-1, 1), new Point(1, -1) };
/*  67:    */     
/*  68: 86 */     s.clear();
/*  69: 87 */     s.add(r);
/*  70: 88 */     temp.setXYBoolean(r.x, r.y, true);
/*  71: 89 */     list2.add(r);
/*  72:    */     int x;
/*  73:    */     int i;
/*  74: 91 */     for (; !s.isEmpty(); i < N.length)
/*  75:    */     {
/*  76: 93 */       Point tmp = (Point)s.pop();
/*  77:    */       
/*  78: 95 */       x = tmp.x;
/*  79: 96 */       int y = tmp.y;
/*  80:    */       
/*  81: 98 */       int p = input.getXYInt(x, y);
/*  82: 99 */       temp2.setXYInt(x, y, label);
/*  83:    */       
/*  84:101 */       i = 0; continue;
/*  85:102 */       int _x = x + N[i].x;
/*  86:103 */       int _y = y + N[i].y;
/*  87:105 */       if ((_x >= 0) && (_x < xdim) && (_y >= 0) && (_y < ydim)) {
/*  88:107 */         if (!temp.getXYBoolean(_x, _y))
/*  89:    */         {
/*  90:109 */           int q = input.getXYInt(_x, _y);
/*  91:111 */           if (q == p)
/*  92:    */           {
/*  93:113 */             Point t = new Point(_x, _y);
/*  94:114 */             s.add(t);
/*  95:    */             
/*  96:116 */             temp.setXYBoolean(t.x, t.y, true);
/*  97:117 */             list2.add(t);
/*  98:    */           }
/*  99:    */         }
/* 100:    */       }
/* 101:101 */       i++;
/* 102:    */     }
/* 103:122 */     for (Point t : list2) {
/* 104:123 */       temp.setXYBoolean(t.x, t.y, false);
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public static Double invoke(Image map, Image reference)
/* 109:    */   {
/* 110:    */     try
/* 111:    */     {
/* 112:128 */       return (Double)new OSR().preprocess(new Object[] { map, reference });
/* 113:    */     }
/* 114:    */     catch (GlobalException e)
/* 115:    */     {
/* 116:130 */       e.printStackTrace();
/* 117:    */     }
/* 118:131 */     return null;
/* 119:    */   }
/* 120:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.OSR
 * JD-Core Version:    0.7.0.1
 */