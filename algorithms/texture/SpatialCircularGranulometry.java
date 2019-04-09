/*  1:   */ package vpt.algorithms.texture;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.gray.GClosing;
/*  7:   */ import vpt.algorithms.mm.gray.GOpening;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class SpatialCircularGranulometry
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:25 */   public double[] output = null;
/* 14:26 */   public Image input = null;
/* 15:27 */   public Integer len = null;
/* 16:28 */   public Integer step = null;
/* 17:29 */   public Integer momentX = null;
/* 18:30 */   public Integer momentY = null;
/* 19:   */   
/* 20:   */   public SpatialCircularGranulometry()
/* 21:   */   {
/* 22:33 */     this.inputFields = "input,len,step,momentX,momentY";
/* 23:34 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:38 */     int cdim = this.input.getCDim();
/* 30:39 */     int size = this.len.intValue() * cdim * 2;
/* 31:40 */     this.output = new double[size];
/* 32:   */     
/* 33:42 */     double[] originalValues = new double[cdim];
/* 34:44 */     for (int c = 0; c < cdim; c++) {
/* 35:45 */       originalValues[c] = valuation(this.input, c);
/* 36:   */     }
/* 37:47 */     for (int i = 1; i <= this.len.intValue(); i++)
/* 38:   */     {
/* 39:48 */       FlatSE se = FlatSE.disk((i - 1) * this.step.intValue() + 1);
/* 40:   */       
/* 41:50 */       Image tmp = GOpening.invoke(this.input, se);
/* 42:52 */       for (int c = 0; c < cdim; c++) {
/* 43:53 */         this.output[(c * this.len.intValue() * 2 + i - 1)] = (1.0D - valuation(tmp, c) / originalValues[c]);
/* 44:   */       }
/* 45:55 */       tmp = GClosing.invoke(this.input, se);
/* 46:57 */       for (int c = 0; c < cdim; c++) {
/* 47:58 */         this.output[(c * this.len.intValue() * 2 + this.len.intValue() + i - 1)] = (valuation(tmp, c) / originalValues[c]);
/* 48:   */       }
/* 49:   */     }
/* 50:   */   }
/* 51:   */   
/* 52:   */   private double valuation(Image img, int channel)
/* 53:   */   {
/* 54:63 */     return vpt.algorithms.statistical.SpatialMoment.invoke(img.getChannel(channel), this.momentX, this.momentY)[0];
/* 55:   */   }
/* 56:   */   
/* 57:   */   public static double[] invoke(Image image, Integer len, Integer step, Integer momentX, Integer momentY)
/* 58:   */   {
/* 59:   */     try
/* 60:   */     {
/* 61:68 */       return (double[])new SpatialCircularGranulometry().preprocess(new Object[] { image, len, step, momentX, momentY });
/* 62:   */     }
/* 63:   */     catch (GlobalException e)
/* 64:   */     {
/* 65:70 */       e.printStackTrace();
/* 66:   */     }
/* 67:71 */     return null;
/* 68:   */   }
/* 69:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.SpatialCircularGranulometry
 * JD-Core Version:    0.7.0.1
 */