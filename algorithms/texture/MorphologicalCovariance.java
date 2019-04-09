/*  1:   */ package vpt.algorithms.texture;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.mm.gray.GErosion;
/*  8:   */ import vpt.util.Tools;
/*  9:   */ import vpt.util.se.FlatSE;
/* 10:   */ 
/* 11:   */ public class MorphologicalCovariance
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:25 */   public double[] output = null;
/* 15:26 */   public Image input = null;
/* 16:27 */   public Integer len = null;
/* 17:   */   
/* 18:   */   public MorphologicalCovariance()
/* 19:   */   {
/* 20:30 */     this.inputFields = "input,len";
/* 21:31 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:35 */     int cdim = this.input.getCDim();
/* 28:36 */     int size = this.len.intValue() * 4 * cdim;
/* 29:37 */     this.output = new double[size];
/* 30:   */     
/* 31:39 */     double[] originalVolumes = new double[cdim];
/* 32:41 */     for (int c = 0; c < cdim; c++) {
/* 33:42 */       originalVolumes[c] = Tools.volume(this.input, c);
/* 34:   */     }
/* 35:44 */     for (int i = 1; i <= this.len.intValue(); i++)
/* 36:   */     {
/* 37:45 */       int side = i * 2 + 1;
/* 38:   */       
/* 39:   */ 
/* 40:48 */       FlatSE se = new FlatSE(1, side, new Point(0, i));
/* 41:49 */       se.setXYBoolean(0, 0, true);
/* 42:50 */       se.setXYBoolean(0, side - 1, true);
/* 43:   */       
/* 44:52 */       Image tmp = GErosion.invoke(this.input, se);
/* 45:54 */       for (int c = 0; c < cdim; c++) {
/* 46:55 */         this.output[(c * 4 * this.len.intValue() + i - 1)] = (Tools.volume(tmp, c) / originalVolumes[c]);
/* 47:   */       }
/* 48:58 */       se = new FlatSE(side, side, new Point(i, i));
/* 49:59 */       se.setXYBoolean(0, 0, true);
/* 50:60 */       se.setXYBoolean(side - 1, side - 1, true);
/* 51:   */       
/* 52:62 */       tmp = GErosion.invoke(this.input, se);
/* 53:64 */       for (int c = 0; c < cdim; c++) {
/* 54:65 */         this.output[(c * 4 * this.len.intValue() + this.len.intValue() + i - 1)] = (Tools.volume(tmp, c) / originalVolumes[c]);
/* 55:   */       }
/* 56:68 */       se = new FlatSE(side, 1, new Point(i, 0));
/* 57:69 */       se.setXYBoolean(0, 0, true);
/* 58:70 */       se.setXYBoolean(side - 1, 0, true);
/* 59:   */       
/* 60:72 */       tmp = GErosion.invoke(this.input, se);
/* 61:74 */       for (int c = 0; c < cdim; c++) {
/* 62:75 */         this.output[(c * 4 * this.len.intValue() + 2 * this.len.intValue() + i - 1)] = (Tools.volume(tmp, c) / originalVolumes[c]);
/* 63:   */       }
/* 64:78 */       se = new FlatSE(side, side, new Point(i, i));
/* 65:79 */       se.setXYBoolean(side - 1, 0, true);
/* 66:80 */       se.setXYBoolean(0, side - 1, true);
/* 67:   */       
/* 68:82 */       tmp = GErosion.invoke(this.input, se);
/* 69:84 */       for (int c = 0; c < cdim; c++) {
/* 70:85 */         this.output[(c * 4 * this.len.intValue() + 3 * this.len.intValue() + i - 1)] = (Tools.volume(tmp, c) / originalVolumes[c]);
/* 71:   */       }
/* 72:   */     }
/* 73:   */   }
/* 74:   */   
/* 75:   */   public static double[] invoke(Image image, Integer len)
/* 76:   */   {
/* 77:   */     try
/* 78:   */     {
/* 79:91 */       return (double[])new MorphologicalCovariance().preprocess(new Object[] { image, len });
/* 80:   */     }
/* 81:   */     catch (GlobalException e)
/* 82:   */     {
/* 83:93 */       e.printStackTrace();
/* 84:   */     }
/* 85:94 */     return null;
/* 86:   */   }
/* 87:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.MorphologicalCovariance
 * JD-Core Version:    0.7.0.1
 */