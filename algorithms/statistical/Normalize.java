/*  1:   */ package vpt.algorithms.statistical;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class Normalize
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:15 */   public DoubleImage output = null;
/* 12:16 */   public Image input = null;
/* 13:17 */   public Double mean = null;
/* 14:18 */   public Double sdev = null;
/* 15:   */   
/* 16:   */   public Normalize()
/* 17:   */   {
/* 18:21 */     this.inputFields = "input, mean, sdev";
/* 19:22 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:26 */     int cdim = this.input.getCDim();
/* 26:28 */     if (cdim != 1) {
/* 27:28 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 28:   */     }
/* 29:30 */     this.output = new DoubleImage(this.input, true);
/* 30:   */     
/* 31:32 */     double imageMean = Mean.invoke(this.input).doubleValue();
/* 32:33 */     double imageSdev = SDev.invoke(this.input).doubleValue();
/* 33:   */     
/* 34:35 */     int size = this.input.getSize();
/* 35:37 */     for (int i = 0; i < size; i++) {
/* 36:38 */       this.output.setDouble(i, (this.input.getDouble(i) - imageMean + this.mean.doubleValue()) / (imageSdev / this.sdev.doubleValue()));
/* 37:   */     }
/* 38:   */   }
/* 39:   */   
/* 40:   */   public static DoubleImage invoke(Image img, Double mean, Double sdev)
/* 41:   */   {
/* 42:   */     try
/* 43:   */     {
/* 44:43 */       return (DoubleImage)new Normalize().preprocess(new Object[] { img, mean, sdev });
/* 45:   */     }
/* 46:   */     catch (GlobalException e)
/* 47:   */     {
/* 48:45 */       e.printStackTrace();
/* 49:   */     }
/* 50:46 */     return null;
/* 51:   */   }
/* 52:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.statistical.Normalize
 * JD-Core Version:    0.7.0.1
 */