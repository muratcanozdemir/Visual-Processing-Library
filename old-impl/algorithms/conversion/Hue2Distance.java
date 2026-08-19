/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.Tools;
/*  8:   */ 
/*  9:   */ public class Hue2Distance
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:18 */   public Image input = null;
/* 13:19 */   public Double refHue = null;
/* 14:20 */   public Image output = null;
/* 15:   */   
/* 16:   */   public Hue2Distance()
/* 17:   */   {
/* 18:23 */     this.inputFields = "input, refHue";
/* 19:24 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:29 */     this.output = new DoubleImage(this.input.getXDim(), this.input.getYDim(), 1);
/* 26:   */     
/* 27:31 */     int cdim = this.input.getCDim();
/* 28:32 */     int size = this.input.getSize();
/* 29:34 */     if (cdim != 1) {
/* 30:35 */       throw new GlobalException("The input must be a greyscale hue image");
/* 31:   */     }
/* 32:37 */     for (int i = 0; i < size; i++)
/* 33:   */     {
/* 34:38 */       double hue = this.input.getDouble(i);
/* 35:39 */       this.output.setDouble(i, 2.0D * Tools.hueDistance(hue, this.refHue.doubleValue()));
/* 36:   */     }
/* 37:   */   }
/* 38:   */   
/* 39:   */   public static Image invoke(Image image, Double refHue)
/* 40:   */   {
/* 41:   */     try
/* 42:   */     {
/* 43:53 */       return (Image)new Hue2Distance().preprocess(new Object[] { image, refHue });
/* 44:   */     }
/* 45:   */     catch (GlobalException e)
/* 46:   */     {
/* 47:55 */       e.printStackTrace();
/* 48:   */     }
/* 49:56 */     return null;
/* 50:   */   }
/* 51:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.Hue2Distance
 * JD-Core Version:    0.7.0.1
 */