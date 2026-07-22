/*  1:   */ package vpt.algorithms.arithmetic;
/*  2:   */ 
/*  3:   */ /*  5:   */ import vpt.Image;
/*  4:   */ 
/*  6:   */ 
/*  7:   */ public class Absolute
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:15 */   public Image inputImage = null;
/* 11:17 */   public Image output = null;
/* 12:   */   
/* 13:   */   public Absolute()
/* 14:   */   {
/* 15:20 */     this.inputFields = "inputImage";
/* 16:21 */     this.outputFields = "output";
/* 17:   */   }
/* 18:   */   
/* 19:   */   public void execute()
/* 20:   */     throws GlobalException
/* 21:   */   {
/* 22:26 */     this.output = this.inputImage.newInstance(false);
/* 23:   */     
/* 24:28 */     int size = this.output.getSize();
/* 25:30 */     for (int i = 0; i < size; i++)
/* 26:   */     {
/* 27:31 */       double p = this.inputImage.getDouble(i);
/* 28:   */       
/* 29:33 */       this.output.setDouble(i, Math.abs(p));
/* 30:   */     }
/* 31:   */   }
/* 32:   */   
/* 33:   */   public static Image invoke(Image image)
/* 34:   */     throws GlobalException
/* 35:   */   {
/* 36:46 */     return (Image)new Absolute().preprocess(new Object[] { image });
/* 37:   */   }
/* 38:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.arithmetic.Absolute
 * JD-Core Version:    0.7.0.1
 */