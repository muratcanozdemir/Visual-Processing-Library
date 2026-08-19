/*   1:    */ package vpt.algorithms.color;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.GlobalException;
/*   5:    */ import vpt.Image;
/*   6:    */ 
/*   7:    */ public class BIC
/*   8:    */   extends Algorithm
/*   9:    */ {
/*  10:    */   public Image input;
/*  11:    */   public double[] output;
/*  12:    */   
/*  13:    */   public BIC()
/*  14:    */   {
/*  15: 23 */     this.inputFields = "input";
/*  16: 24 */     this.outputFields = "output";
/*  17:    */   }
/*  18:    */   
/*  19:    */   public void execute()
/*  20:    */     throws GlobalException
/*  21:    */   {
/*  22: 29 */     this.output = new double[''];
/*  23:    */     
/*  24: 31 */     long borderPixels = 0L;
/*  25: 32 */     long interiorPixels = 0L;
/*  26:    */     
/*  27: 34 */     int xdim = this.input.getXDim();
/*  28: 35 */     int ydim = this.input.getYDim();
/*  29: 36 */     int cdim = this.input.getCDim();
/*  30: 38 */     if (cdim != 3) {
/*  31: 38 */       throw new GlobalException("BIC operates only on RGB color images");
/*  32:    */     }
/*  33: 40 */     Image subq = this.input.newInstance(true);
/*  34: 43 */     for (int y = 0; y < ydim; y++) {
/*  35: 44 */       for (int x = 0; x < xdim; x++)
/*  36:    */       {
/*  37: 45 */         int[] p = this.input.getVXYByte(x, y);
/*  38:    */         
/*  39: 47 */         p[0] /= 64;
/*  40: 48 */         p[1] /= 64;
/*  41: 49 */         p[2] /= 64;
/*  42:    */         
/*  43: 51 */         subq.setVXYByte(x, y, p);
/*  44:    */       }
/*  45:    */     }
/*  46: 55 */     for (int y = 0; y < ydim; y++) {
/*  47: 56 */       for (int x = 0; x < xdim; x++)
/*  48:    */       {
/*  49: 58 */         int[] p = subq.getVXYByte(x, y);
/*  50: 62 */         if (interiorPixel(x, y, p, subq))
/*  51:    */         {
/*  52: 63 */           interiorPixels += 1L;
/*  53: 64 */           this.output[(p[0] * 16 + p[1] * 4 + p[2])] += 1.0D;
/*  54:    */         }
/*  55:    */         else
/*  56:    */         {
/*  57: 66 */           borderPixels += 1L;
/*  58: 67 */           this.output[(64 + p[0] * 16 + p[1] * 4 + p[2])] += 1.0D;
/*  59:    */         }
/*  60:    */       }
/*  61:    */     }
/*  62: 74 */     for (int i = 0; i < 64; i++) {
/*  63: 75 */       this.output[i] /= interiorPixels;
/*  64:    */     }
/*  65: 77 */     for (int i = 64; i < 128; i++) {
/*  66: 78 */       this.output[i] /= borderPixels;
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   private boolean interiorPixel(int x, int y, int[] p, Image img)
/*  71:    */   {
/*  72: 83 */     if (y - 1 >= 0)
/*  73:    */     {
/*  74: 84 */       int[] q = img.getVXYByte(x, y - 1);
/*  75: 85 */       if ((p[0] != q[0]) || (p[1] != q[1]) || (p[2] != q[2])) {
/*  76: 85 */         return false;
/*  77:    */       }
/*  78:    */     }
/*  79: 88 */     if (y + 1 < img.getYDim())
/*  80:    */     {
/*  81: 89 */       int[] q = img.getVXYByte(x, y + 1);
/*  82: 90 */       if ((p[0] != q[0]) || (p[1] != q[1]) || (p[2] != q[2])) {
/*  83: 90 */         return false;
/*  84:    */       }
/*  85:    */     }
/*  86: 93 */     if (x - 1 >= 0)
/*  87:    */     {
/*  88: 94 */       int[] q = img.getVXYByte(x - 1, y);
/*  89: 95 */       if ((p[0] != q[0]) || (p[1] != q[1]) || (p[2] != q[2])) {
/*  90: 95 */         return false;
/*  91:    */       }
/*  92:    */     }
/*  93: 98 */     if (x + 1 < img.getXDim())
/*  94:    */     {
/*  95: 99 */       int[] q = img.getVXYByte(x + 1, y);
/*  96:100 */       if ((p[0] != q[0]) || (p[1] != q[1]) || (p[2] != q[2])) {
/*  97:100 */         return false;
/*  98:    */       }
/*  99:    */     }
/* 100:103 */     return true;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public static double[] invoke(Image image)
/* 104:    */   {
/* 105:    */     try
/* 106:    */     {
/* 107:108 */       return (double[])new BIC().preprocess(new Object[] { image });
/* 108:    */     }
/* 109:    */     catch (GlobalException e)
/* 110:    */     {
/* 111:110 */       e.printStackTrace();
/* 112:    */     }
/* 113:111 */     return null;
/* 114:    */   }
/* 115:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.color.BIC
 * JD-Core Version:    0.7.0.1
 */