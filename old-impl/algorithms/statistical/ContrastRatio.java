/*  1:   */ package vpt.algorithms.statistical;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class ContrastRatio
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:15 */   public Double output = null;
/* 11:16 */   public Image input = null;
/* 12:17 */   public Image mask = null;
/* 13:   */   
/* 14:   */   public ContrastRatio()
/* 15:   */   {
/* 16:20 */     this.inputFields = "input, mask";
/* 17:21 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:25 */     if (this.input.getCDim() != 1) {
/* 24:25 */       throw new GlobalException("Sorry, only monochannel images for now...");
/* 25:   */     }
/* 26:27 */     int xdim = this.input.getXDim();
/* 27:28 */     int ydim = this.input.getYDim();
/* 28:   */     
/* 29:   */ 
/* 30:31 */     double max = 0.0D;
/* 31:32 */     double min = 1.0D;
/* 32:34 */     if (this.mask == null) {
/* 33:35 */       for (int y = 0; y < ydim; y++) {
/* 34:36 */         for (int x = 0; x < xdim; x++)
/* 35:   */         {
/* 36:37 */           double d = this.input.getXYDouble(x, y);
/* 37:38 */           if (d > max) {
/* 38:38 */             max = d;
/* 39:   */           }
/* 40:39 */           if (d < min) {
/* 41:39 */             min = d;
/* 42:   */           }
/* 43:   */         }
/* 44:   */       }
/* 45:   */     } else {
/* 46:43 */       for (int y = 0; y < ydim; y++) {
/* 47:44 */         for (int x = 0; x < xdim; x++) {
/* 48:45 */           if (this.mask.getXYBoolean(x, y))
/* 49:   */           {
/* 50:46 */             double d = this.input.getXYDouble(x, y);
/* 51:47 */             if (d > max) {
/* 52:47 */               max = d;
/* 53:   */             }
/* 54:48 */             if (d < min) {
/* 55:48 */               min = d;
/* 56:   */             }
/* 57:   */           }
/* 58:   */         }
/* 59:   */       }
/* 60:   */     }
/* 61:53 */     this.output = Double.valueOf(max - min);
/* 62:   */   }
/* 63:   */   
/* 64:   */   public static Double invoke(Image img)
/* 65:   */   {
/* 66:   */     try
/* 67:   */     {
/* 68:59 */       return (Double)new ContrastRatio().preprocess(new Object[] { img, null });
/* 69:   */     }
/* 70:   */     catch (GlobalException e)
/* 71:   */     {
/* 72:61 */       e.printStackTrace();
/* 73:   */     }
/* 74:62 */     return null;
/* 75:   */   }
/* 76:   */   
/* 77:   */   public static Double invoke(Image img, Image mask)
/* 78:   */   {
/* 79:   */     try
/* 80:   */     {
/* 81:69 */       return (Double)new ContrastRatio().preprocess(new Object[] { img, mask });
/* 82:   */     }
/* 83:   */     catch (GlobalException e)
/* 84:   */     {
/* 85:71 */       e.printStackTrace();
/* 86:   */     }
/* 87:72 */     return null;
/* 88:   */   }
/* 89:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.statistical.ContrastRatio
 * JD-Core Version:    0.7.0.1
 */