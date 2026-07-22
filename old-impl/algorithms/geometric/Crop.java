/*  1:   */ package vpt.algorithms.geometric;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class Crop
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:16 */   public Image output = null;
/* 12:18 */   public Image input = null;
/* 13:19 */   public Point p1 = null;
/* 14:20 */   public Point p2 = null;
/* 15:   */   
/* 16:   */   public Crop()
/* 17:   */   {
/* 18:24 */     this.inputFields = "input,p1,p2";
/* 19:25 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:30 */     int cdim = this.input.getCDim();
/* 26:31 */     int xdim = this.input.getXDim();
/* 27:32 */     int ydim = this.input.getYDim();
/* 28:34 */     if ((this.p1.x < 0) || (this.p2.x < 0) || (this.p1.x > xdim) || (this.p2.x > xdim) || 
/* 29:35 */       (this.p1.y < 0) || (this.p2.y < 0) || (this.p1.y > ydim) || (this.p2.y > ydim)) {
/* 30:36 */       throw new GlobalException("Error: coordinates outside of image");
/* 31:   */     }
/* 32:38 */     int upLeftX = Math.min(this.p1.x, this.p2.x);
/* 33:39 */     int upLeftY = Math.min(this.p1.y, this.p2.y);
/* 34:   */     
/* 35:41 */     int downRightX = Math.max(this.p1.x, this.p2.x);
/* 36:42 */     int downRightY = Math.max(this.p1.y, this.p2.y);
/* 37:   */     
/* 38:44 */     this.output = this.input.newInstance(downRightX - upLeftX, downRightY - upLeftY, cdim);
/* 39:46 */     for (int c = 0; c < cdim; c++) {
/* 40:47 */       for (int x = upLeftX; x < downRightX; x++) {
/* 41:48 */         for (int y = upLeftY; y < downRightY; y++)
/* 42:   */         {
/* 43:49 */           double p = this.input.getXYCDouble(x, y, c);
/* 44:50 */           this.output.setXYCDouble(x - upLeftX, y - upLeftY, c, p);
/* 45:   */         }
/* 46:   */       }
/* 47:   */     }
/* 48:   */   }
/* 49:   */   
/* 50:   */   public static Image invoke(Image image, Point p1, Point p2)
/* 51:   */   {
/* 52:   */     try
/* 53:   */     {
/* 54:59 */       return (Image)new Crop().preprocess(new Object[] { image, p1, p2 });
/* 55:   */     }
/* 56:   */     catch (GlobalException e)
/* 57:   */     {
/* 58:61 */       e.printStackTrace();
/* 59:   */     }
/* 60:62 */     return null;
/* 61:   */   }
/* 62:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.geometric.Crop
 * JD-Core Version:    0.7.0.1
 */