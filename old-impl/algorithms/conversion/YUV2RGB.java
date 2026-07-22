/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.ByteImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class YUV2RGB
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:16 */   public Image input = null;
/* 12:17 */   public Image output = null;
/* 13:   */   
/* 14:   */   public YUV2RGB()
/* 15:   */   {
/* 16:20 */     this.inputFields = "input";
/* 17:21 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:26 */     this.output = new ByteImage(this.input, false);
/* 24:   */     
/* 25:28 */     int xdim = this.input.getXDim();
/* 26:29 */     int ydim = this.input.getYDim();
/* 27:30 */     int cdim = this.input.getCDim();
/* 28:32 */     if (cdim != 3) {
/* 29:33 */       throw new GlobalException("The input must be a tristumulus YUV image");
/* 30:   */     }
/* 31:35 */     for (int x = 0; x < xdim; x++) {
/* 32:36 */       for (int y = 0; y < ydim; y++)
/* 33:   */       {
/* 34:37 */         double Y = this.input.getXYCDouble(x, y, 0);
/* 35:38 */         double U = this.input.getXYCDouble(x, y, 1);
/* 36:39 */         double V = this.input.getXYCDouble(x, y, 2);
/* 37:   */         
/* 38:41 */         int[] rgb = convert(Y, U, V);
/* 39:   */         
/* 40:43 */         this.output.setXYCByte(x, y, 0, rgb[0]);
/* 41:44 */         this.output.setXYCByte(x, y, 1, rgb[1]);
/* 42:45 */         this.output.setXYCByte(x, y, 2, rgb[2]);
/* 43:   */       }
/* 44:   */     }
/* 45:   */   }
/* 46:   */   
/* 47:   */   public static int[] convert(double Y, double U, double V)
/* 48:   */   {
/* 49:59 */     int[] rgb = new int[3];
/* 50:   */     
/* 51:   */ 
/* 52:   */ 
/* 53:   */ 
/* 54:64 */     rgb[0] = ((int)Math.round((Y + 1.14D * V) * 255.0D));
/* 55:65 */     if ((rgb[0] < 0) || (rgb[0] > 255)) {
/* 56:66 */       rgb[0] = 0;
/* 57:   */     }
/* 58:68 */     rgb[1] = ((int)Math.round((Y - 0.395D * U - 0.581D * V) * 255.0D));
/* 59:69 */     if ((rgb[1] < 0) || (rgb[1] > 255)) {
/* 60:70 */       rgb[1] = 0;
/* 61:   */     }
/* 62:72 */     rgb[2] = ((int)Math.round((Y + 2.032D * U) * 255.0D));
/* 63:73 */     if ((rgb[2] < 0) || (rgb[2] > 255)) {
/* 64:74 */       rgb[2] = 0;
/* 65:   */     }
/* 66:76 */     return rgb;
/* 67:   */   }
/* 68:   */   
/* 69:   */   public static Image invoke(Image image)
/* 70:   */   {
/* 71:   */     try
/* 72:   */     {
/* 73:89 */       return (Image)new YUV2RGB().preprocess(new Object[] { image });
/* 74:   */     }
/* 75:   */     catch (GlobalException e)
/* 76:   */     {
/* 77:91 */       e.printStackTrace();
/* 78:   */     }
/* 79:92 */     return null;
/* 80:   */   }
/* 81:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.YUV2RGB
 * JD-Core Version:    0.7.0.1
 */