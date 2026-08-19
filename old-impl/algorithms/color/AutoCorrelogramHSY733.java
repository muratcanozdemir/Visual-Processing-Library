/*  1:   */ package vpt.algorithms.color;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class AutoCorrelogramHSY733
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:   */   public Image input;
/* 13:   */   public double[] output;
/* 14:   */   
/* 15:   */   public AutoCorrelogramHSY733()
/* 16:   */   {
/* 17:24 */     this.inputFields = "input";
/* 18:25 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:30 */     int NumberOfColors = 63;
/* 25:31 */     int[] distances = { 1, 3, 5, 7 };
/* 26:   */     
/* 27:33 */     this.output = new double[63 * distances.length];
/* 28:   */     
/* 29:35 */     double[] histo = new double[63];
/* 30:38 */     for (int d = 0; d < distances.length; d++)
/* 31:   */     {
/* 32:40 */       FlatSE se = FlatSE.hollowSquare(distances[d] * 2 + 1);
/* 33:41 */       Point[] coords = se.getCoords();
/* 34:44 */       for (int x = 0; x < this.input.getXDim(); x++) {
/* 35:45 */         for (int y = 0; y < this.input.getYDim(); y++)
/* 36:   */         {
/* 37:48 */           int[] p = new int[3];
/* 38:49 */           p[0] = this.input.getXYCByte(x, y, 0);
/* 39:50 */           p[1] = this.input.getXYCByte(x, y, 1);
/* 40:51 */           p[2] = this.input.getXYCByte(x, y, 2);
/* 41:55 */           if (d == 0) {
/* 42:55 */             histo[(p[0] * 9 + p[1] * 3 + p[2])] += 1.0D;
/* 43:   */           }
/* 44:59 */           for (int i = 0; i < coords.length; i++)
/* 45:   */           {
/* 46:60 */             int _x = x - coords[i].x;
/* 47:61 */             int _y = y - coords[i].y;
/* 48:63 */             if ((_x >= 0) && (_x < this.input.getXDim()) && (_y >= 0) && (_y < this.input.getYDim()))
/* 49:   */             {
/* 50:66 */               int[] p2 = new int[3];
/* 51:67 */               p2[0] = this.input.getXYCByte(_x, _y, 0);
/* 52:68 */               p2[1] = this.input.getXYCByte(_x, _y, 1);
/* 53:69 */               p2[2] = this.input.getXYCByte(_x, _y, 2);
/* 54:71 */               if ((p[0] == p2[0]) && (p[1] == p2[1]) && (p[2] == p2[2])) {
/* 55:72 */                 this.output[(d * 63 + p[0] * 9 + p[1] * 3 + p[2])] += 1.0D;
/* 56:   */               }
/* 57:   */             }
/* 58:   */           }
/* 59:   */         }
/* 60:   */       }
/* 61:78 */       for (int i = 0; i < 63; i++) {
/* 62:79 */         if (histo[i] > 0.0D) {
/* 63:79 */           this.output[(d * 63 + i)] /= 8 * distances[d] * histo[i];
/* 64:   */         }
/* 65:   */       }
/* 66:   */     }
/* 67:   */   }
/* 68:   */   
/* 69:   */   public static double[] invoke(Image image)
/* 70:   */   {
/* 71:   */     try
/* 72:   */     {
/* 73:85 */       return (double[])new AutoCorrelogramHSY733().preprocess(new Object[] { image });
/* 74:   */     }
/* 75:   */     catch (GlobalException e)
/* 76:   */     {
/* 77:87 */       e.printStackTrace();
/* 78:   */     }
/* 79:88 */     return null;
/* 80:   */   }
/* 81:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.color.AutoCorrelogramHSY733
 * JD-Core Version:    0.7.0.1
 */