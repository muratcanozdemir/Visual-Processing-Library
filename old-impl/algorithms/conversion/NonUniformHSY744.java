/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class NonUniformHSY744
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:   */   public Image input;
/* 12:   */   public Image output;
/* 13:   */   
/* 14:   */   public NonUniformHSY744()
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
/* 31:   */ 
/* 32:   */ 
/* 33:36 */     double coeff = 1.411764705882353D;
/* 34:38 */     for (int x = 0; x < xdim; x++) {
/* 35:39 */       for (int y = 0; y < ydim; y++)
/* 36:   */       {
/* 37:41 */         int h = this.input.getXYCByte(x, y, 0);
/* 38:42 */         int s = this.input.getXYCByte(x, y, 1);
/* 39:43 */         int i = this.input.getXYCByte(x, y, 2);
/* 40:   */         
/* 41:45 */         s = (int)Math.floor(s / 64.0D);
/* 42:46 */         i = (int)Math.floor(i / 64.0D);
/* 43:   */         
/* 44:   */ 
/* 45:   */ 
/* 46:50 */         h = (int)Math.floor(h * 1.411764705882353D);
/* 47:53 */         if ((h >= 330) || (h <= 22)) {
/* 48:53 */           h = 0;
/* 49:56 */         } else if ((h >= 22) && (h <= 45)) {
/* 50:56 */           h = 1;
/* 51:59 */         } else if ((h >= 45) && (h <= 70)) {
/* 52:59 */           h = 2;
/* 53:62 */         } else if ((h >= 70) && (h <= 155)) {
/* 54:62 */           h = 3;
/* 55:65 */         } else if ((h >= 155) && (h <= 186)) {
/* 56:65 */           h = 4;
/* 57:68 */         } else if ((h >= 186) && (h <= 278)) {
/* 58:68 */           h = 5;
/* 59:71 */         } else if ((h >= 278) && (h <= 330)) {
/* 60:71 */           h = 6;
/* 61:   */         } else {
/* 62:74 */           System.err.println("sorun var " + h);
/* 63:   */         }
/* 64:76 */         this.output.setXYCByte(x, y, 0, h);
/* 65:77 */         this.output.setXYCByte(x, y, 1, s);
/* 66:78 */         this.output.setXYCByte(x, y, 2, i);
/* 67:   */       }
/* 68:   */     }
/* 69:   */   }
/* 70:   */   
/* 71:   */   public static Image invoke(Image image)
/* 72:   */   {
/* 73:   */     try
/* 74:   */     {
/* 75:86 */       return (Image)new NonUniformHSY744().preprocess(new Object[] { image });
/* 76:   */     }
/* 77:   */     catch (GlobalException e)
/* 78:   */     {
/* 79:88 */       e.printStackTrace();
/* 80:   */     }
/* 81:89 */     return null;
/* 82:   */   }
/* 83:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.NonUniformHSY744
 * JD-Core Version:    0.7.0.1
 */