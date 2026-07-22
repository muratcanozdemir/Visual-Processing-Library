/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class NonUniformHSY733
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:   */   public Image input;
/* 12:   */   public Image output;
/* 13:   */   
/* 14:   */   public NonUniformHSY733()
/* 15:   */   {
/* 16:18 */     this.inputFields = "input";
/* 17:19 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:24 */     int cdim = this.input.getCDim();
/* 24:25 */     int xdim = this.input.getXDim();
/* 25:26 */     int ydim = this.input.getYDim();
/* 26:28 */     if (cdim != 3) {
/* 27:28 */       throw new GlobalException("The input image must have 3 channels!");
/* 28:   */     }
/* 29:30 */     this.output = this.input.newInstance(true);
/* 30:   */     
/* 31:32 */     double coeff = 1.411764705882353D;
/* 32:34 */     for (int x = 0; x < xdim; x++) {
/* 33:35 */       for (int y = 0; y < ydim; y++)
/* 34:   */       {
/* 35:37 */         int h = this.input.getXYCByte(x, y, 0);
/* 36:38 */         int s = this.input.getXYCByte(x, y, 1);
/* 37:39 */         int i = this.input.getXYCByte(x, y, 2);
/* 38:   */         
/* 39:41 */         s = (int)Math.floor(s / 86.0D);
/* 40:42 */         i = (int)Math.floor(i / 86.0D);
/* 41:   */         
/* 42:   */ 
/* 43:   */ 
/* 44:46 */         h = (int)Math.floor(h * 1.411764705882353D);
/* 45:49 */         if ((h >= 330) || (h <= 22)) {
/* 46:49 */           h = 0;
/* 47:52 */         } else if ((h >= 22) && (h <= 45)) {
/* 48:52 */           h = 1;
/* 49:55 */         } else if ((h >= 45) && (h <= 70)) {
/* 50:55 */           h = 2;
/* 51:58 */         } else if ((h >= 70) && (h <= 155)) {
/* 52:58 */           h = 3;
/* 53:61 */         } else if ((h >= 155) && (h <= 186)) {
/* 54:61 */           h = 4;
/* 55:64 */         } else if ((h >= 186) && (h <= 278)) {
/* 56:64 */           h = 5;
/* 57:67 */         } else if ((h >= 278) && (h <= 330)) {
/* 58:67 */           h = 6;
/* 59:   */         } else {
/* 60:70 */           System.err.println("sorun var " + h);
/* 61:   */         }
/* 62:72 */         this.output.setXYCByte(x, y, 0, h);
/* 63:73 */         this.output.setXYCByte(x, y, 1, s);
/* 64:74 */         this.output.setXYCByte(x, y, 2, i);
/* 65:   */       }
/* 66:   */     }
/* 67:   */   }
/* 68:   */   
/* 69:   */   public static Image invoke(Image image)
/* 70:   */   {
/* 71:   */     try
/* 72:   */     {
/* 73:82 */       return (Image)new NonUniformHSY733().preprocess(new Object[] { image });
/* 74:   */     }
/* 75:   */     catch (GlobalException e)
/* 76:   */     {
/* 77:84 */       e.printStackTrace();
/* 78:   */     }
/* 79:85 */     return null;
/* 80:   */   }
/* 81:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.NonUniformHSY733
 * JD-Core Version:    0.7.0.1
 */