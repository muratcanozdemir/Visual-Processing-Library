/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.ByteImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class XYZ2RGB
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:16 */   public Image input = null;
/* 12:17 */   public Image output = null;
/* 13:   */   
/* 14:   */   public XYZ2RGB()
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
/* 29:33 */       throw new GlobalException("The input must be a tristumulus XYZ image");
/* 30:   */     }
/* 31:35 */     for (int x = 0; x < xdim; x++) {
/* 32:36 */       for (int y = 0; y < ydim; y++)
/* 33:   */       {
/* 34:37 */         double X = this.input.getXYCDouble(x, y, 0);
/* 35:38 */         double Y = this.input.getXYCDouble(x, y, 1);
/* 36:39 */         double Z = this.input.getXYCDouble(x, y, 2);
/* 37:   */         
/* 38:41 */         int[] rgb = convert(X, Y, Z);
/* 39:   */         
/* 40:43 */         this.output.setXYCByte(x, y, 0, rgb[0]);
/* 41:44 */         this.output.setXYCByte(x, y, 1, rgb[1]);
/* 42:45 */         this.output.setXYCByte(x, y, 2, rgb[2]);
/* 43:   */       }
/* 44:   */     }
/* 45:   */   }
/* 46:   */   
/* 47:   */   public static int[] convert(double X, double Y, double Z)
/* 48:   */   {
/* 49:59 */     int[] rgb = new int[3];
/* 50:   */     
/* 51:   */ 
/* 52:   */ 
/* 53:63 */     rgb[0] = ((int)Math.round((3.2404542D * X - 1.5371385D * Y - 0.4985314D * Z) * 255.0D));
/* 54:65 */     if (rgb[0] < 0) {
/* 55:66 */       rgb[0] = 0;
/* 56:67 */     } else if (rgb[0] > 255) {
/* 57:68 */       rgb[0] = 255;
/* 58:   */     }
/* 59:70 */     rgb[1] = ((int)Math.round((-0.969266D * X + 1.8760108D * Y + 0.041556D * Z) * 255.0D));
/* 60:72 */     if (rgb[1] < 0) {
/* 61:73 */       rgb[1] = 0;
/* 62:74 */     } else if (rgb[1] > 255) {
/* 63:75 */       rgb[1] = 255;
/* 64:   */     }
/* 65:77 */     rgb[2] = ((int)Math.round((0.0556434D * X - 0.2040259D * Y + 1.0572252D * Z) * 255.0D));
/* 66:78 */     if (rgb[2] < 0) {
/* 67:79 */       rgb[2] = 0;
/* 68:80 */     } else if (rgb[2] > 255) {
/* 69:81 */       rgb[2] = 255;
/* 70:   */     }
/* 71:83 */     return rgb;
/* 72:   */   }
/* 73:   */   
/* 74:   */   public static Image invoke(Image image)
/* 75:   */   {
/* 76:   */     try
/* 77:   */     {
/* 78:96 */       return (Image)new XYZ2RGB().preprocess(new Object[] { image });
/* 79:   */     }
/* 80:   */     catch (GlobalException e)
/* 81:   */     {
/* 82:98 */       e.printStackTrace();
/* 83:   */     }
/* 84:99 */     return null;
/* 85:   */   }
/* 86:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.XYZ2RGB
 * JD-Core Version:    0.7.0.1
 */