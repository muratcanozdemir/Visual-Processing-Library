/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.geometric.Crop;
/*  8:   */ 
/*  9:   */ public class RegionalMomentsOfInertia
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:20 */   public double[] output = null;
/* 13:21 */   public Image input = null;
/* 14:22 */   public Integer slides = null;
/* 15:   */   
/* 16:   */   public RegionalMomentsOfInertia()
/* 17:   */   {
/* 18:25 */     this.inputFields = "input, slides";
/* 19:26 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:30 */     int cdim = this.input.getCDim();
/* 26:31 */     int ydim = this.input.getYDim();
/* 27:32 */     int xdim = this.input.getXDim();
/* 28:34 */     if (cdim != 1) {
/* 29:34 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 30:   */     }
/* 31:36 */     this.output = new double[this.slides.intValue()];
/* 32:   */     
/* 33:   */ 
/* 34:   */ 
/* 35:40 */     int top = ydim;
/* 36:41 */     int bottom = 0;
/* 37:43 */     for (int y = 0; y < ydim; y++) {
/* 38:44 */       for (int x = 0; x < xdim; x++)
/* 39:   */       {
/* 40:45 */         boolean p = this.input.getXYBoolean(x, y);
/* 41:46 */         if (p)
/* 42:   */         {
/* 43:47 */           if (y < top) {
/* 44:47 */             top = y;
/* 45:   */           }
/* 46:48 */           if (y > bottom) {
/* 47:48 */             bottom = y;
/* 48:   */           }
/* 49:   */         }
/* 50:   */       }
/* 51:   */     }
/* 52:54 */     int diff = bottom - top;
/* 53:55 */     int slideWidth = diff / this.slides.intValue();
/* 54:57 */     for (int i = 0; i < this.slides.intValue(); i++)
/* 55:   */     {
/* 56:58 */       Image tmp = Crop.invoke(this.input, new Point(0, top), new Point(xdim - 1, top + slideWidth));
/* 57:59 */       top += slideWidth;
/* 58:60 */       this.output[i] = MeanDistanceToCentroid.invoke(tmp).doubleValue();
/* 59:   */     }
/* 60:   */   }
/* 61:   */   
/* 62:   */   public static double[] invoke(Image img, Integer slides)
/* 63:   */   {
/* 64:   */     try
/* 65:   */     {
/* 66:67 */       return (double[])new RegionalMomentsOfInertia().preprocess(new Object[] { img, slides });
/* 67:   */     }
/* 68:   */     catch (GlobalException e)
/* 69:   */     {
/* 70:69 */       e.printStackTrace();
/* 71:   */     }
/* 72:70 */     return null;
/* 73:   */   }
/* 74:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.RegionalMomentsOfInertia
 * JD-Core Version:    0.7.0.1
 */