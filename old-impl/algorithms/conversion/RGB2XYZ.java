/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class RGB2XYZ
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:19 */   public Image input = null;
/* 12:20 */   public Image output = null;
/* 13:   */   
/* 14:   */   public RGB2XYZ()
/* 15:   */   {
/* 16:23 */     this.inputFields = "input";
/* 17:24 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:29 */     this.output = new DoubleImage(this.input, false);
/* 24:   */     
/* 25:31 */     int xdim = this.input.getXDim();
/* 26:32 */     int ydim = this.input.getYDim();
/* 27:33 */     int cdim = this.input.getCDim();
/* 28:35 */     if (cdim != 3) {
/* 29:36 */       throw new GlobalException("The input must be a tristumulus RGB image");
/* 30:   */     }
/* 31:38 */     for (int x = 0; x < xdim; x++) {
/* 32:39 */       for (int y = 0; y < ydim; y++)
/* 33:   */       {
/* 34:40 */         int R = this.input.getXYCByte(x, y, 0);
/* 35:41 */         int G = this.input.getXYCByte(x, y, 1);
/* 36:42 */         int B = this.input.getXYCByte(x, y, 2);
/* 37:   */         
/* 38:44 */         double[] xyz = convert(R, G, B);
/* 39:   */         
/* 40:46 */         this.output.setXYCDouble(x, y, 0, xyz[0]);
/* 41:47 */         this.output.setXYCDouble(x, y, 1, xyz[1]);
/* 42:48 */         this.output.setXYCDouble(x, y, 2, xyz[2]);
/* 43:   */       }
/* 44:   */     }
/* 45:   */   }
/* 46:   */   
/* 47:   */   public static double[] convert(int r, int g, int b)
/* 48:   */   {
/* 49:63 */     double rN = 0.00392156862745098D * r;
/* 50:64 */     double gN = 0.00392156862745098D * g;
/* 51:65 */     double bN = 0.00392156862745098D * b;
/* 52:   */     
/* 53:67 */     double[] xyz = new double[3];
/* 54:   */     
/* 55:69 */     xyz[0] = (0.4124564D * rN + 0.3575761D * gN + 0.1804375D * bN);
/* 56:70 */     xyz[1] = (0.2126729D * rN + 0.7151522D * gN + 0.072175D * bN);
/* 57:71 */     xyz[2] = (0.0193339D * rN + 0.119192D * gN + 0.9503041D * bN);
/* 58:   */     
/* 59:73 */     return xyz;
/* 60:   */   }
/* 61:   */   
/* 62:   */   public static Image invoke(Image image)
/* 63:   */   {
/* 64:   */     try
/* 65:   */     {
/* 66:86 */       return (Image)new RGB2XYZ().preprocess(new Object[] { image });
/* 67:   */     }
/* 68:   */     catch (GlobalException e)
/* 69:   */     {
/* 70:88 */       e.printStackTrace();
/* 71:   */     }
/* 72:89 */     return null;
/* 73:   */   }
/* 74:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.RGB2XYZ
 * JD-Core Version:    0.7.0.1
 */