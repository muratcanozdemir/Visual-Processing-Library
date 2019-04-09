/*  1:   */ package vpt.algorithms.geometric;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.util.Tools;
/*  7:   */ 
/*  8:   */ public class ToPolar
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:15 */   public Image output = null;
/* 12:16 */   public Image input = null;
/* 13:   */   
/* 14:   */   public ToPolar()
/* 15:   */   {
/* 16:20 */     this.inputFields = "input";
/* 17:21 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:26 */     int cdim = this.input.getCDim();
/* 24:27 */     int xdim = this.input.getXDim();
/* 25:28 */     int ydim = this.input.getYDim();
/* 26:   */     
/* 27:30 */     int diagonal = this.input.getDiagonal() / 2;
/* 28:   */     
/* 29:32 */     this.output = this.input.newInstance(360, diagonal, cdim);
/* 30:34 */     for (int c = 0; c < cdim; c++) {
/* 31:35 */       for (int theta = 0; theta < 360; theta++) {
/* 32:36 */         for (int rho = 0; rho < diagonal; rho++)
/* 33:   */         {
/* 34:38 */           double x = rho * Math.cos(Tools.degree2radian(theta));
/* 35:39 */           double y = rho * Math.sin(Tools.degree2radian(theta));
/* 36:   */           
/* 37:   */ 
/* 38:   */ 
/* 39:43 */           x += xdim / 2;
/* 40:44 */           y += ydim / 2;
/* 41:   */           
/* 42:46 */           x = Math.min(x, xdim - 1);
/* 43:47 */           y = Math.min(y, ydim - 1);
/* 44:   */           
/* 45:49 */           x = Math.max(x, 0.0D);
/* 46:50 */           y = Math.max(y, 0.0D);
/* 47:   */           
/* 48:52 */           double p = this.input.getXYCDouble((int)x, (int)y, c);
/* 49:53 */           this.output.setXYCDouble(theta, rho, c, p);
/* 50:   */         }
/* 51:   */       }
/* 52:   */     }
/* 53:   */   }
/* 54:   */   
/* 55:   */   public static Image invoke(Image image)
/* 56:   */   {
/* 57:   */     try
/* 58:   */     {
/* 59:62 */       return (Image)new ToPolar().preprocess(new Object[] { image });
/* 60:   */     }
/* 61:   */     catch (GlobalException e)
/* 62:   */     {
/* 63:64 */       e.printStackTrace();
/* 64:   */     }
/* 65:65 */     return null;
/* 66:   */   }
/* 67:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.geometric.ToPolar
 * JD-Core Version:    0.7.0.1
 */