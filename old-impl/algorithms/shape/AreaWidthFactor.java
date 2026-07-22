/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.geometric.Crop;
/*  8:   */ import vpt.util.Tools;
/*  9:   */ 
/* 10:   */ public class AreaWidthFactor
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:19 */   public double[] output = null;
/* 14:20 */   public Image input = null;
/* 15:21 */   public Integer slides = null;
/* 16:   */   
/* 17:   */   public AreaWidthFactor()
/* 18:   */   {
/* 19:24 */     this.inputFields = "input, slides";
/* 20:25 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:29 */     int cdim = this.input.getCDim();
/* 27:30 */     int ydim = this.input.getYDim();
/* 28:31 */     int xdim = this.input.getXDim();
/* 29:33 */     if (cdim != 1) {
/* 30:33 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 31:   */     }
/* 32:35 */     this.output = new double[this.slides.intValue()];
/* 33:   */     
/* 34:   */ 
/* 35:   */ 
/* 36:39 */     int top = ydim;
/* 37:40 */     int bottom = 0;
/* 38:42 */     for (int y = 0; y < ydim; y++) {
/* 39:43 */       for (int x = 0; x < xdim; x++)
/* 40:   */       {
/* 41:44 */         boolean p = this.input.getXYBoolean(x, y);
/* 42:45 */         if (p)
/* 43:   */         {
/* 44:46 */           if (y < top) {
/* 45:46 */             top = y;
/* 46:   */           }
/* 47:47 */           if (y > bottom) {
/* 48:47 */             bottom = y;
/* 49:   */           }
/* 50:   */         }
/* 51:   */       }
/* 52:   */     }
/* 53:52 */     double area = Tools.volume(this.input, 0);
/* 54:   */     
/* 55:   */ 
/* 56:55 */     int diff = bottom - top;
/* 57:56 */     int slideWidth = diff / this.slides.intValue();
/* 58:58 */     for (int i = 0; i < this.slides.intValue(); i++)
/* 59:   */     {
/* 60:59 */       Image tmp = Crop.invoke(this.input, new Point(0, top), new Point(xdim - 1, top + slideWidth));
/* 61:60 */       top += slideWidth;
/* 62:   */       
/* 63:62 */       this.output[i] = (Tools.volume(tmp, 0) / area);
/* 64:   */     }
/* 65:   */   }
/* 66:   */   
/* 67:   */   public static double[] invoke(Image img, Integer slides)
/* 68:   */   {
/* 69:   */     try
/* 70:   */     {
/* 71:69 */       return (double[])new AreaWidthFactor().preprocess(new Object[] { img, slides });
/* 72:   */     }
/* 73:   */     catch (GlobalException e)
/* 74:   */     {
/* 75:71 */       e.printStackTrace();
/* 76:   */     }
/* 77:72 */     return null;
/* 78:   */   }
/* 79:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.AreaWidthFactor
 * JD-Core Version:    0.7.0.1
 */