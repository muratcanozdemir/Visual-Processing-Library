/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.geometric.ToPolar;
/*  7:   */ 
/*  8:   */ public class FPFT
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:21 */   public Image output = null;
/* 12:22 */   public Image input = null;
/* 13:   */   
/* 14:   */   public FPFT()
/* 15:   */   {
/* 16:25 */     this.inputFields = "input";
/* 17:26 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:30 */     int cdim = this.input.getCDim();
/* 24:31 */     int ydim = this.input.getYDim();
/* 25:32 */     int xdim = this.input.getXDim();
/* 26:34 */     if (cdim != 1) {
/* 27:34 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 28:   */     }
/* 29:36 */     this.output = vpt.algorithms.frequential.FFT.invoke(this.input, java.lang.Boolean.valueOf(true), java.lang.Integer.valueOf(2))[0];
/* 30:38 */     for (int i = 0; i < this.output.getSize(); i++) {
/* 31:39 */       if ((this.output.getDouble(i) > 1.0D) || (this.output.getDouble(i) < 0.0D)) {
/* 32:39 */         this.output.setDouble(i, 1.0D);
/* 33:   */       }
/* 34:   */     }
/* 35:42 */     this.output = ToPolar.invoke(this.output);
/* 36:   */     
/* 37:44 */     this.output = vpt.algorithms.frequential.FFT.invoke(this.output, java.lang.Boolean.valueOf(true), java.lang.Integer.valueOf(2))[0];
/* 38:46 */     for (int i = 0; i < this.output.getSize(); i++) {
/* 39:47 */       if ((this.output.getDouble(i) > 1.0D) || (this.output.getDouble(i) < 0.0D)) {
/* 40:47 */         this.output.setDouble(i, 1.0D);
/* 41:   */       }
/* 42:   */     }
/* 43:   */   }
/* 44:   */   
/* 45:   */   public static Image invoke(Image img)
/* 46:   */   {
/* 47:   */     try
/* 48:   */     {
/* 49:54 */       return (Image)new FPFT().preprocess(new Object[] { img });
/* 50:   */     }
/* 51:   */     catch (GlobalException e)
/* 52:   */     {
/* 53:56 */       e.printStackTrace();
/* 54:   */     }
/* 55:57 */     return null;
/* 56:   */   }
/* 57:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.FPFT
 * JD-Core Version:    0.7.0.1
 */