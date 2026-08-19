/*   1:    */ package vpt.testing;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.conversion.RGB2Gray;
/*   9:    */ import vpt.algorithms.display.Display2D;
/*  10:    */ import vpt.algorithms.geometric.Resample;
/*  11:    */ import vpt.algorithms.io.Load;
/*  12:    */ import vpt.algorithms.mm.binary.geo.BFillHole;
/*  13:    */ import vpt.algorithms.mm.gray.GOpening;
/*  14:    */ import vpt.algorithms.mm.gray.geo.GTopHatByReconstruction;
/*  15:    */ import vpt.algorithms.segmentation.OtsuThreshold;
/*  16:    */ import vpt.algorithms.shape.ConnectedComponents;
/*  17:    */ import vpt.util.Tools;
/*  18:    */ import vpt.util.se.FlatSE;
/*  19:    */ 
/*  20:    */ public class PickAndPlace
/*  21:    */ {
/*  22:    */   public static void main(String[] args)
/*  23:    */   {
/*  24: 29 */     Image img = Load.invoke("samples/altesa/20140605_134819.jpg");
/*  25:    */     
/*  26:    */ 
/*  27: 32 */     img = Resample.invoke(img, Double.valueOf(0.25D), Double.valueOf(0.25D), Integer.valueOf(1));
/*  28:    */     
/*  29: 34 */     img = RGB2Gray.invoke(img);
/*  30: 35 */     Display2D.invoke(img);
/*  31:    */     
/*  32:    */ 
/*  33:    */ 
/*  34:    */ 
/*  35: 40 */     img = GTopHatByReconstruction.invoke(img, FlatSE.square(150));
/*  36: 41 */     Display2D.invoke(img);
/*  37:    */     
/*  38: 43 */     img = OtsuThreshold.invoke(img);
/*  39: 44 */     Display2D.invoke(img, "Otsu");
/*  40:    */     
/*  41: 46 */     img = BFillHole.invoke(img);
/*  42: 47 */     Display2D.invoke(img, "Dolu");
/*  43:    */     
/*  44:    */ 
/*  45:    */ 
/*  46: 51 */     img = GOpening.invoke(img, FlatSE.square(7));
/*  47: 52 */     Display2D.invoke(img, "Clean");
/*  48:    */     
/*  49: 54 */     ArrayList<ArrayList<Point>> CC = ConnectedComponents.invoke(img);
/*  50: 55 */     System.err.println("Bilesen sayisi: " + CC.size());
/*  51: 58 */     for (Iterator localIterator1 = CC.iterator(); localIterator1.hasNext(); ???.hasNext())
/*  52:    */     {
/*  53: 58 */       ArrayList<Point> cc = (ArrayList)localIterator1.next();
/*  54:    */       
/*  55: 60 */       int x = 0;
/*  56: 61 */       int y = 0;
/*  57: 63 */       for (Point p : cc)
/*  58:    */       {
/*  59: 64 */         x += p.x;
/*  60: 65 */         y += p.y;
/*  61:    */       }
/*  62: 68 */       x /= cc.size();
/*  63: 69 */       y /= cc.size();
/*  64:    */       
/*  65: 71 */       img.setXYBoolean(x, y, false);
/*  66:    */       
/*  67:    */ 
/*  68: 74 */       double theta = orientation(cc);
/*  69: 75 */       System.err.println(theta * 180.0D / 3.141592653589793D);
/*  70:    */       
/*  71: 77 */       int x1 = (int)(100.0D * Math.cos(theta));
/*  72: 78 */       int y1 = (int)(100.0D * Math.sin(theta));
/*  73:    */       
/*  74:    */ 
/*  75: 81 */       ArrayList<Point> line = Tools.drawLine(new Point(x, y), new Point(x + x1, y + y1));
/*  76: 83 */       for (Point p : line) {
/*  77: 84 */         img.setXYBoolean(p.x, p.y, true);
/*  78:    */       }
/*  79: 86 */       line = Tools.drawLine(new Point(x - x1, y - y1), new Point(x, y));
/*  80:    */       
/*  81: 88 */       ??? = line.iterator(); continue;Point p = (Point)???.next();
/*  82: 89 */       img.setXYBoolean(p.x, p.y, true);
/*  83:    */     }
/*  84: 91 */     Display2D.invoke(img, "Son");
/*  85:    */   }
/*  86:    */   
/*  87:    */   private static double orientation(ArrayList<Point> cc)
/*  88:    */   {
/*  89: 95 */     double m00 = centralMoment(cc, 0, 0);
/*  90: 96 */     double m11 = centralMoment(cc, 1, 1);
/*  91: 97 */     double m02 = centralMoment(cc, 0, 2);
/*  92: 98 */     double m20 = centralMoment(cc, 2, 0);
/*  93:    */     
/*  94:100 */     return 0.5D * Math.atan2(2.0D * m11 / m00, m20 / m00 - m02 / m00);
/*  95:    */   }
/*  96:    */   
/*  97:    */   private static double moment(ArrayList<Point> pixels, int momentX, int momentY)
/*  98:    */   {
/*  99:105 */     double output = 0.0D;
/* 100:107 */     for (Point p : pixels) {
/* 101:108 */       output += Math.pow(p.x + 1, momentX) * Math.pow(p.y + 1, momentY);
/* 102:    */     }
/* 103:111 */     return output;
/* 104:    */   }
/* 105:    */   
/* 106:    */   private static double centralMoment(ArrayList<Point> pixels, int momentX, int momentY)
/* 107:    */   {
/* 108:115 */     double m00 = moment(pixels, 0, 0);
/* 109:116 */     double m01 = moment(pixels, 0, 1);
/* 110:117 */     double m10 = moment(pixels, 1, 0);
/* 111:    */     
/* 112:119 */     double centroidX = m10 / m00;
/* 113:120 */     double centroidY = m01 / m00;
/* 114:    */     
/* 115:122 */     double output = 0.0D;
/* 116:124 */     for (Point p : pixels) {
/* 117:125 */       output += Math.pow(p.x + 1 - centroidX, momentX) * Math.pow(p.y + 1 - centroidY, momentY);
/* 118:    */     }
/* 119:127 */     return output;
/* 120:    */   }
/* 121:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.PickAndPlace
 * JD-Core Version:    0.7.0.1
 */