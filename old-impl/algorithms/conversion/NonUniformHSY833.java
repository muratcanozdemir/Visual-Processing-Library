/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class NonUniformHSY833
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:   */   public Image input;
/* 12:   */   public Image output;
/* 13:   */   
/* 14:   */   public NonUniformHSY833()
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
/* 26:30 */     if (cdim != 3) {
/* 27:30 */       throw new GlobalException("The input image must have 3 channels!");
/* 28:   */     }
/* 29:32 */     this.output = this.input.newInstance(true);
/* 30:   */     
/* 31:34 */     double coeff = 1.411764705882353D;
/* 32:36 */     for (int x = 0; x < xdim; x++) {
/* 33:37 */       for (int y = 0; y < ydim; y++)
/* 34:   */       {
/* 35:39 */         int h = this.input.getXYCByte(x, y, 0);
/* 36:40 */         int s = this.input.getXYCByte(x, y, 1);
/* 37:41 */         int i = this.input.getXYCByte(x, y, 2);
/* 38:   */         
/* 39:43 */         s = (int)Math.floor(s / 86.0D);
/* 40:44 */         i = (int)Math.floor(i / 86.0D);
/* 41:   */         
/* 42:   */ 
/* 43:   */ 
/* 44:48 */         h = (int)Math.floor(h * 1.411764705882353D);
/* 45:51 */         if ((h >= 330) || (h <= 22)) {
/* 46:51 */           h = 0;
/* 47:54 */         } else if ((h >= 22) && (h <= 45)) {
/* 48:54 */           h = 1;
/* 49:57 */         } else if ((h >= 45) && (h <= 70)) {
/* 50:57 */           h = 2;
/* 51:60 */         } else if ((h >= 70) && (h <= 110)) {
/* 52:60 */           h = 3;
/* 53:63 */         } else if ((h >= 110) && (h <= 155)) {
/* 54:63 */           h = 4;
/* 55:66 */         } else if ((h >= 155) && (h <= 186)) {
/* 56:66 */           h = 5;
/* 57:69 */         } else if ((h >= 186) && (h <= 278)) {
/* 58:69 */           h = 6;
/* 59:72 */         } else if ((h >= 278) && (h <= 330)) {
/* 60:72 */           h = 7;
/* 61:   */         } else {
/* 62:75 */           System.err.println("sorun var " + h);
/* 63:   */         }
/* 64:77 */         this.output.setXYCByte(x, y, 0, h);
/* 65:78 */         this.output.setXYCByte(x, y, 1, s);
/* 66:79 */         this.output.setXYCByte(x, y, 2, i);
/* 67:   */       }
/* 68:   */     }
/* 69:   */   }
/* 70:   */   
/* 71:   */   public static Image invoke(Image image)
/* 72:   */   {
/* 73:   */     try
/* 74:   */     {
/* 75:87 */       return (Image)new NonUniformHSY833().preprocess(new Object[] { image });
/* 76:   */     }
/* 77:   */     catch (GlobalException e)
/* 78:   */     {
/* 79:89 */       e.printStackTrace();
/* 80:   */     }
/* 81:90 */     return null;
/* 82:   */   }
/* 83:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.NonUniformHSY833
 * JD-Core Version:    0.7.0.1
 */