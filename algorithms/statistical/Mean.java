/*  1:   */ package vpt.algorithms.statistical;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class Mean
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:14 */   public Double output = null;
/* 11:15 */   public Image input = null;
/* 12:   */   
/* 13:   */   public Mean()
/* 14:   */   {
/* 15:18 */     this.inputFields = "input";
/* 16:19 */     this.outputFields = "output";
/* 17:   */   }
/* 18:   */   
/* 19:   */   public void execute()
/* 20:   */     throws GlobalException
/* 21:   */   {
/* 22:23 */     int cdim = this.input.getCDim();
/* 23:25 */     if (cdim != 1) {
/* 24:25 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 25:   */     }
/* 26:27 */     this.output = Double.valueOf(0.0D);
/* 27:28 */     int size = this.input.getSize();
/* 28:30 */     for (int i = 0; i < size; i++) {
/* 29:31 */       this.output = Double.valueOf(this.output.doubleValue() + this.input.getDouble(i));
/* 30:   */     }
/* 31:33 */     this.output = Double.valueOf(this.output.doubleValue() / size);
/* 32:   */   }
/* 33:   */   
/* 34:   */   public static Double invoke(Image img)
/* 35:   */   {
/* 36:   */     try
/* 37:   */     {
/* 38:39 */       return (Double)new Mean().preprocess(new Object[] { img });
/* 39:   */     }
/* 40:   */     catch (GlobalException e)
/* 41:   */     {
/* 42:41 */       e.printStackTrace();
/* 43:   */     }
/* 44:42 */     return null;
/* 45:   */   }
/* 46:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.statistical.Mean
 * JD-Core Version:    0.7.0.1
 */