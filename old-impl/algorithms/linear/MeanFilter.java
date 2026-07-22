/*  1:   */ package vpt.algorithms.linear;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.se.NonFlatSE;
/*  8:   */ 
/*  9:   */ public class MeanFilter
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12: 9 */   public Image output = null;
/* 13:10 */   public Image inputImage = null;
/* 14:11 */   public Integer size = null;
/* 15:   */   
/* 16:   */   public MeanFilter()
/* 17:   */   {
/* 18:14 */     this.inputFields = "inputImage,size";
/* 19:15 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:19 */     if (this.size.intValue() < 3)
/* 26:   */     {
/* 27:20 */       this.size = Integer.valueOf(3);
/* 28:21 */       System.err.println("Warning: kernel size too small, increased to 3");
/* 29:   */     }
/* 30:24 */     NonFlatSE kernel = NonFlatSE.box(this.size.intValue(), this.size.intValue(), 1.0D);
/* 31:25 */     this.output = Convolution.invoke(this.inputImage, kernel);
/* 32:   */   }
/* 33:   */   
/* 34:   */   public static Image invoke(Image image, Integer size)
/* 35:   */   {
/* 36:   */     try
/* 37:   */     {
/* 38:31 */       return (Image)new MeanFilter().preprocess(new Object[] { image, size });
/* 39:   */     }
/* 40:   */     catch (GlobalException e)
/* 41:   */     {
/* 42:33 */       e.printStackTrace();
/* 43:   */     }
/* 44:34 */     return null;
/* 45:   */   }
/* 46:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.linear.MeanFilter
 * JD-Core Version:    0.7.0.1
 */