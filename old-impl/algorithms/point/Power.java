/*  1:   */ package vpt.algorithms.point;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class Power
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10: 8 */   public Image output = null;
/* 11: 9 */   public Image input = null;
/* 12:10 */   public Double power = null;
/* 13:   */   
/* 14:   */   public Power()
/* 15:   */   {
/* 16:13 */     this.inputFields = "input,power";
/* 17:14 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:19 */     this.output = this.input.newInstance(true);
/* 24:21 */     for (int i = 0; i < this.output.getSize(); i++)
/* 25:   */     {
/* 26:22 */       double pixel = this.output.getDouble(i);
/* 27:23 */       this.output.setDouble(i, Math.pow(pixel, this.power.doubleValue()));
/* 28:   */     }
/* 29:   */   }
/* 30:   */   
/* 31:   */   public static Image invoke(Image image, Double power)
/* 32:   */   {
/* 33:   */     try
/* 34:   */     {
/* 35:31 */       return (Image)new Power().preprocess(new Object[] { image, power });
/* 36:   */     }
/* 37:   */     catch (GlobalException e)
/* 38:   */     {
/* 39:33 */       e.printStackTrace();
/* 40:   */     }
/* 41:34 */     return null;
/* 42:   */   }
/* 43:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.point.Power
 * JD-Core Version:    0.7.0.1
 */