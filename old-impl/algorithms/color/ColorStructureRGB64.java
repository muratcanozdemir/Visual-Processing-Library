/*  1:   */ package vpt.algorithms.color;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class ColorStructureRGB64
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:   */   public Image input;
/* 13:   */   public double[] output;
/* 14:   */   
/* 15:   */   public ColorStructureRGB64()
/* 16:   */   {
/* 17:31 */     this.inputFields = "input";
/* 18:32 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:37 */     this.output = new double[64];
/* 25:   */     
/* 26:39 */     int cdim = this.input.getCDim();
/* 27:41 */     if (cdim != 3) {
/* 28:41 */       throw new GlobalException("Why is the input not in color ?");
/* 29:   */     }
/* 30:43 */     FlatSE se = FlatSE.square(9);
/* 31:44 */     Point[] coords = se.getCoords();
/* 32:   */     
/* 33:46 */     int syc = 0;
/* 34:48 */     for (int x = 0; x < this.input.getXDim(); x++) {
/* 35:49 */       for (int y = 0; y < this.input.getYDim(); y++)
/* 36:   */       {
/* 37:51 */         boolean[] flags = new boolean[256];
/* 38:53 */         for (int i = 0; i < coords.length; i++)
/* 39:   */         {
/* 40:54 */           int _x = x - coords[i].x;
/* 41:55 */           int _y = y - coords[i].y;
/* 42:57 */           if ((_x >= 0) && (_x < this.input.getXDim()) && (_y >= 0) && (_y < this.input.getYDim()))
/* 43:   */           {
/* 44:60 */             int[] p = new int[3];
/* 45:61 */             p[0] = this.input.getXYCByte(x, y, 0);
/* 46:62 */             p[0] /= 64;
/* 47:   */             
/* 48:64 */             p[1] = this.input.getXYCByte(x, y, 1);
/* 49:65 */             p[1] /= 64;
/* 50:   */             
/* 51:67 */             p[2] = this.input.getXYCByte(x, y, 2);
/* 52:68 */             p[2] /= 64;
/* 53:70 */             if (flags[(p[0] * 16 + p[1] * 4 + p[2])] == 0)
/* 54:   */             {
/* 55:72 */               this.output[(p[0] * 16 + p[1] * 4 + p[2])] += 1.0D;
/* 56:   */               
/* 57:74 */               flags[(p[0] * 16 + p[1] * 4 + p[2])] = true;
/* 58:   */               
/* 59:76 */               syc++;
/* 60:   */             }
/* 61:   */           }
/* 62:   */         }
/* 63:   */       }
/* 64:   */     }
/* 65:83 */     for (int i = 0; i < this.output.length; i++) {
/* 66:84 */       this.output[i] /= syc;
/* 67:   */     }
/* 68:   */   }
/* 69:   */   
/* 70:   */   public static double[] invoke(Image image)
/* 71:   */   {
/* 72:   */     try
/* 73:   */     {
/* 74:89 */       return (double[])new ColorStructureRGB64().preprocess(new Object[] { image });
/* 75:   */     }
/* 76:   */     catch (GlobalException e)
/* 77:   */     {
/* 78:91 */       e.printStackTrace();
/* 79:   */     }
/* 80:92 */     return null;
/* 81:   */   }
/* 82:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.color.ColorStructureRGB64
 * JD-Core Version:    0.7.0.1
 */