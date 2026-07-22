/*  1:   */ package vpt.algorithms.frequential;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class IDFT2D
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:   */   public DoubleImage real;
/* 12:   */   public DoubleImage imag;
/* 13:   */   public Image output;
/* 14:   */   
/* 15:   */   public IDFT2D()
/* 16:   */   {
/* 17:23 */     this.inputFields = "real, imag";
/* 18:24 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:28 */     int xdim = this.real.getXDim();
/* 25:29 */     int ydim = this.real.getYDim();
/* 26:   */     
/* 27:31 */     this.output = this.real.newInstance(false);
/* 28:33 */     for (int x = 0; x < xdim; x++) {
/* 29:34 */       for (int y = 0; y < ydim; y++)
/* 30:   */       {
/* 31:36 */         double pReal = 0.0D;
/* 32:37 */         double pImag = 0.0D;
/* 33:39 */         for (int _x = 0; _x < xdim; _x++) {
/* 34:40 */           for (int _y = 0; _y < ydim; _y++)
/* 35:   */           {
/* 36:41 */             double tmp = (_x * x / xdim + _y * y / ydim) * 6.283185307179586D;
/* 37:42 */             double rp = this.real.getXYDouble(_x, _y);
/* 38:43 */             double ip = this.imag.getXYDouble(_x, _y);
/* 39:   */             
/* 40:45 */             pReal += rp * Math.cos(tmp) - ip * Math.sin(tmp);
/* 41:46 */             pImag += ip * Math.cos(tmp) + rp * Math.sin(tmp);
/* 42:   */           }
/* 43:   */         }
/* 44:50 */         pReal /= Math.sqrt(xdim * ydim);
/* 45:51 */         pImag /= Math.sqrt(xdim * ydim);
/* 46:   */         
/* 47:53 */         this.output.setXYDouble(x, y, Math.sqrt(pReal * pReal + pImag * pImag));
/* 48:   */       }
/* 49:   */     }
/* 50:   */   }
/* 51:   */   
/* 52:   */   public static Image invoke(DoubleImage real, DoubleImage imag)
/* 53:   */   {
/* 54:   */     try
/* 55:   */     {
/* 56:61 */       return (Image)new IDFT2D().preprocess(new Object[] { real, imag });
/* 57:   */     }
/* 58:   */     catch (GlobalException e)
/* 59:   */     {
/* 60:63 */       e.printStackTrace();
/* 61:   */     }
/* 62:64 */     return null;
/* 63:   */   }
/* 64:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.frequential.IDFT2D
 * JD-Core Version:    0.7.0.1
 */