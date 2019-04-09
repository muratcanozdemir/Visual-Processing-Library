/*  1:   */ package vpt.algorithms.hough;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.ByteImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.display.Display2D;
/*  8:   */ import vpt.algorithms.linear.Sobel;
/*  9:   */ 
/* 10:   */ public class HoughTransformCircle
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:18 */   public Image output = null;
/* 14:19 */   public Image input = null;
/* 15:   */   
/* 16:   */   public HoughTransformCircle()
/* 17:   */   {
/* 18:22 */     this.inputFields = "input,";
/* 19:23 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:29 */     this.output = new ByteImage(this.input, false);
/* 26:30 */     this.output.fill(0);
/* 27:   */     
/* 28:   */ 
/* 29:33 */     Image edges = Sobel.invoke(this.input, Integer.valueOf(2), Boolean.valueOf(true));
/* 30:34 */     Display2D.invoke(edges, "", Boolean.valueOf(false));
/* 31:   */     
/* 32:   */ 
/* 33:37 */     Image theta = Sobel.invoke(this.input, Integer.valueOf(3), Boolean.valueOf(false));
/* 34:39 */     for (int c = 0; c < edges.getCDim(); c++) {
/* 35:40 */       for (int x = 0; x < edges.getXDim(); x++) {
/* 36:41 */         for (int y = 0; y < edges.getYDim(); y++)
/* 37:   */         {
/* 38:42 */           int p = edges.getXYCByte(x, y, c);
/* 39:44 */           if (p != 0)
/* 40:   */           {
/* 41:46 */             double phi = theta.getXYCDouble(x, y, c);
/* 42:48 */             for (int r = 1; r < this.input.getDiagonal(); r++)
/* 43:   */             {
/* 44:50 */               int a = (int)Math.round(x - r * Math.cos(phi));
/* 45:51 */               int b = (int)Math.round(y - r * Math.sin(phi));
/* 46:53 */               if ((a < this.output.getXDim()) && (b < this.output.getXDim()) && (a >= 0) && (b >= 0)) {
/* 47:55 */                 this.output.setXYCByte(a, b, c, Math.min(this.output.getXYCByte(a, b, c) + 1, 255));
/* 48:   */               }
/* 49:   */             }
/* 50:   */           }
/* 51:   */         }
/* 52:   */       }
/* 53:   */     }
/* 54:   */   }
/* 55:   */   
/* 56:   */   public static Image invoke(Image image)
/* 57:   */   {
/* 58:   */     try
/* 59:   */     {
/* 60:65 */       return (Image)new HoughTransformCircle().preprocess(new Object[] { image });
/* 61:   */     }
/* 62:   */     catch (GlobalException e)
/* 63:   */     {
/* 64:67 */       e.printStackTrace();
/* 65:   */     }
/* 66:68 */     return null;
/* 67:   */   }
/* 68:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.hough.HoughTransformCircle
 * JD-Core Version:    0.7.0.1
 */