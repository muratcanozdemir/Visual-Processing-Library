/*  1:   */ package vpt.algorithms.histogram;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class ContrastStretch
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:10 */   public Image output = null;
/* 12:11 */   public Image inputImage = null;
/* 13:   */   
/* 14:   */   public ContrastStretch()
/* 15:   */   {
/* 16:14 */     this.inputFields = "inputImage";
/* 17:15 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:20 */     this.output = this.inputImage.newInstance(true);
/* 24:22 */     for (int c = 0; c < this.output.getCDim(); c++)
/* 25:   */     {
/* 26:24 */       Point p = this.output.getXYCMaximum(c);
/* 27:25 */       double max = this.output.getXYCDouble(p.x, p.y, c);
/* 28:   */       
/* 29:27 */       p = this.output.getXYCMinimum(c);
/* 30:28 */       double min = this.output.getXYCDouble(p.x, p.y, c);
/* 31:30 */       for (int x = 0; x < this.output.getXDim(); x++) {
/* 32:31 */         for (int y = 0; y < this.output.getYDim(); y++)
/* 33:   */         {
/* 34:33 */           double pixel = this.output.getXYCDouble(x, y, c);
/* 35:35 */           if (max != min)
/* 36:   */           {
/* 37:36 */             double newPixel = (pixel - min) / (max - min);
/* 38:37 */             this.output.setXYCDouble(x, y, c, newPixel);
/* 39:   */           }
/* 40:   */         }
/* 41:   */       }
/* 42:   */     }
/* 43:   */   }
/* 44:   */   
/* 45:   */   public static Image invoke(Image image)
/* 46:   */   {
/* 47:   */     try
/* 48:   */     {
/* 49:47 */       return (Image)new ContrastStretch().preprocess(new Object[] { image });
/* 50:   */     }
/* 51:   */     catch (GlobalException e)
/* 52:   */     {
/* 53:49 */       e.printStackTrace();
/* 54:   */     }
/* 55:50 */     return null;
/* 56:   */   }
/* 57:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.histogram.ContrastStretch
 * JD-Core Version:    0.7.0.1
 */