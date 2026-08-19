/*  1:   */ package vpt.algorithms.arithmetic;
/*  2:   */ 
/*  3:   */ /*  6:   */ import vpt.Image;
/*  4:   */ 
/*  5:   */ 
/*  7:   */ 
/*  8:   */ public class Subtraction
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:17 */   public Image output = null;
/* 12:18 */   public Image inputImage1 = null;
/* 13:19 */   public Image inputImage2 = null;
/* 14:   */   
/* 15:   */   public Subtraction()
/* 16:   */   {
/* 17:22 */     this.inputFields = "inputImage1,inputImage2";
/* 18:23 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:28 */     if (!this.inputImage1.hasSameDimensionsAs(this.inputImage2)) {
/* 25:29 */       throw new GlobalException("Arguments must be of same dimensions!");
/* 26:   */     }
/* 27:32 */     if (((this.inputImage1 instanceof DoubleImage)) || ((this.inputImage2 instanceof DoubleImage))) {
/* 28:33 */       this.output = new DoubleImage(this.inputImage1, false);
/* 29:   */     } else {
/* 30:35 */       this.output = this.inputImage1.newInstance(false);
/* 31:   */     }
/* 32:37 */     int size = this.output.getSize();
/* 33:39 */     for (int i = 0; i < size; i++)
/* 34:   */     {
/* 35:40 */       double p1 = this.inputImage1.getDouble(i);
/* 36:41 */       double p2 = this.inputImage2.getDouble(i);
/* 37:   */       
/* 38:43 */       this.output.setDouble(i, Math.min(1.0D, Math.max(p1 - p2, 0.0D)));
/* 39:   */     }
/* 40:   */   }
/* 41:   */   
/* 42:   */   public static Image invoke(Image image1, Image image2)
/* 43:   */   {
/* 44:   */     try
/* 45:   */     {
/* 46:57 */       return (Image)new Subtraction().preprocess(new Object[] { image1, image2 });
/* 47:   */     }
/* 48:   */     catch (GlobalException e)
/* 49:   */     {
/* 50:60 */       e.printStackTrace();
/* 51:   */     }
/* 52:61 */     return null;
/* 53:   */   }
/* 54:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.arithmetic.Subtraction
 * JD-Core Version:    0.7.0.1
 */