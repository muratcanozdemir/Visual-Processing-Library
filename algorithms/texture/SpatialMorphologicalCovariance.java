/*  1:   */ package vpt.algorithms.texture;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.mm.gray.GErosion;
/*  8:   */ import vpt.algorithms.statistical.SpatialMoment;
/*  9:   */ import vpt.util.se.FlatSE;
/* 10:   */ 
/* 11:   */ public class SpatialMorphologicalCovariance
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:20 */   public double[] output = null;
/* 15:21 */   public Image input = null;
/* 16:22 */   public Integer len = null;
/* 17:23 */   public Integer momentX = null;
/* 18:24 */   public Integer momentY = null;
/* 19:   */   
/* 20:   */   public SpatialMorphologicalCovariance()
/* 21:   */   {
/* 22:27 */     this.inputFields = "input,len, momentX, momentY";
/* 23:28 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:32 */     int cdim = this.input.getCDim();
/* 30:33 */     int size = this.len.intValue() * 4 * cdim;
/* 31:34 */     this.output = new double[size];
/* 32:   */     
/* 33:36 */     double[] originalVolumes = SpatialMoment.invoke(this.input, this.momentX, this.momentY);
/* 34:38 */     for (int i = 1; i <= this.len.intValue(); i++)
/* 35:   */     {
/* 36:39 */       int side = i * 2 + 1;
/* 37:   */       
/* 38:   */ 
/* 39:42 */       FlatSE se = new FlatSE(1, side, new Point(0, i));
/* 40:43 */       se.setXYBoolean(0, 0, true);
/* 41:44 */       se.setXYBoolean(0, side - 1, true);
/* 42:   */       
/* 43:46 */       Image tmp = GErosion.invoke(this.input, se);
/* 44:   */       
/* 45:48 */       double[] moments = SpatialMoment.invoke(tmp, this.momentX, this.momentY);
/* 46:50 */       for (int c = 0; c < cdim; c++) {
/* 47:51 */         this.output[(c * 4 * this.len.intValue() + i - 1)] = (moments[c] / originalVolumes[c]);
/* 48:   */       }
/* 49:54 */       se = new FlatSE(side, side, new Point(i, i));
/* 50:55 */       se.setXYBoolean(0, 0, true);
/* 51:56 */       se.setXYBoolean(side - 1, side - 1, true);
/* 52:   */       
/* 53:58 */       tmp = GErosion.invoke(this.input, se);
/* 54:   */       
/* 55:60 */       moments = SpatialMoment.invoke(tmp, this.momentX, this.momentY);
/* 56:62 */       for (int c = 0; c < cdim; c++) {
/* 57:63 */         this.output[(c * 4 * this.len.intValue() + this.len.intValue() + i - 1)] = (moments[c] / originalVolumes[c]);
/* 58:   */       }
/* 59:66 */       se = new FlatSE(side, 1, new Point(i, 0));
/* 60:67 */       se.setXYBoolean(0, 0, true);
/* 61:68 */       se.setXYBoolean(side - 1, 0, true);
/* 62:   */       
/* 63:70 */       tmp = GErosion.invoke(this.input, se);
/* 64:   */       
/* 65:72 */       moments = SpatialMoment.invoke(tmp, this.momentX, this.momentY);
/* 66:74 */       for (int c = 0; c < cdim; c++) {
/* 67:75 */         this.output[(c * 4 * this.len.intValue() + 2 * this.len.intValue() + i - 1)] = (moments[c] / originalVolumes[c]);
/* 68:   */       }
/* 69:78 */       se = new FlatSE(side, side, new Point(i, i));
/* 70:79 */       se.setXYBoolean(side - 1, 0, true);
/* 71:80 */       se.setXYBoolean(0, side - 1, true);
/* 72:   */       
/* 73:82 */       tmp = GErosion.invoke(this.input, se);
/* 74:   */       
/* 75:84 */       moments = SpatialMoment.invoke(tmp, this.momentX, this.momentY);
/* 76:86 */       for (int c = 0; c < cdim; c++) {
/* 77:87 */         this.output[(c * 4 * this.len.intValue() + 3 * this.len.intValue() + i - 1)] = (moments[c] / originalVolumes[c]);
/* 78:   */       }
/* 79:   */     }
/* 80:   */   }
/* 81:   */   
/* 82:   */   public static double[] invoke(Image image, Integer len, Integer momentX, Integer momentY)
/* 83:   */   {
/* 84:   */     try
/* 85:   */     {
/* 86:93 */       return (double[])new SpatialMorphologicalCovariance().preprocess(new Object[] { image, len, momentX, momentY });
/* 87:   */     }
/* 88:   */     catch (GlobalException e)
/* 89:   */     {
/* 90:95 */       e.printStackTrace();
/* 91:   */     }
/* 92:96 */     return null;
/* 93:   */   }
/* 94:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.SpatialMorphologicalCovariance
 * JD-Core Version:    0.7.0.1
 */