/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.geometric.Crop;
/*  8:   */ import vpt.util.Tools;
/*  9:   */ 
/* 10:   */ public class NormalizedWidthFactor
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:18 */   public double[] output = null;
/* 14:19 */   public Image input = null;
/* 15:20 */   public Integer slides = null;
/* 16:   */   
/* 17:   */   public NormalizedWidthFactor()
/* 18:   */   {
/* 19:23 */     this.inputFields = "input, slides";
/* 20:24 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:28 */     int cdim = this.input.getCDim();
/* 27:29 */     int ydim = this.input.getYDim();
/* 28:30 */     int xdim = this.input.getXDim();
/* 29:32 */     if (cdim != 1) {
/* 30:32 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 31:   */     }
/* 32:34 */     this.output = new double[this.slides.intValue()];
/* 33:   */     
/* 34:   */ 
/* 35:37 */     int top = ydim;
/* 36:38 */     int bottom = 0;
/* 37:40 */     for (int y = 0; y < ydim; y++) {
/* 38:41 */       for (int x = 0; x < xdim; x++)
/* 39:   */       {
/* 40:42 */         boolean p = this.input.getXYBoolean(x, y);
/* 41:43 */         if (p)
/* 42:   */         {
/* 43:44 */           if (y < top) {
/* 44:44 */             top = y;
/* 45:   */           }
/* 46:45 */           if (y > bottom) {
/* 47:45 */             bottom = y;
/* 48:   */           }
/* 49:   */         }
/* 50:   */       }
/* 51:   */     }
/* 52:50 */     double area = Tools.volume(this.input, 0);
/* 53:   */     
/* 54:   */ 
/* 55:53 */     int diff = bottom - top;
/* 56:54 */     int slideWidth = diff / this.slides.intValue();
/* 57:56 */     for (int i = 0; i < this.slides.intValue(); i++)
/* 58:   */     {
/* 59:57 */       Image tmp = Crop.invoke(this.input, new Point(0, top), new Point(xdim - 1, top + slideWidth));
/* 60:58 */       top += slideWidth;
/* 61:   */       
/* 62:60 */       this.output[i] = (Tools.volume(tmp, 0) / area);
/* 63:   */     }
/* 64:   */   }
/* 65:   */   
/* 66:   */   public static double[] invoke(Image img, Integer slides)
/* 67:   */   {
/* 68:   */     try
/* 69:   */     {
/* 70:67 */       return (double[])new NormalizedWidthFactor().preprocess(new Object[] { img, slides });
/* 71:   */     }
/* 72:   */     catch (GlobalException e)
/* 73:   */     {
/* 74:69 */       e.printStackTrace();
/* 75:   */     }
/* 76:70 */     return null;
/* 77:   */   }
/* 78:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.NormalizedWidthFactor
 * JD-Core Version:    0.7.0.1
 */