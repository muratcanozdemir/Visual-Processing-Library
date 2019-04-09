/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.histogram.ContrastStretch;
/*  7:   */ import vpt.algorithms.statistical.ZernikeMoment;
/*  8:   */ 
/*  9:   */ public class DFTZernikeMoments
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:24 */   public double[] output = null;
/* 13:25 */   public Image input = null;
/* 14:26 */   public Integer n = null;
/* 15:   */   
/* 16:   */   public DFTZernikeMoments()
/* 17:   */   {
/* 18:29 */     this.inputFields = "input";
/* 19:30 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:34 */     if (this.input.getCDim() != 1) {
/* 26:34 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 27:   */     }
/* 28:36 */     this.output = new double[32];
/* 29:   */     
/* 30:38 */     this.input = vpt.algorithms.frequential.FFT.invoke(this.input, java.lang.Boolean.valueOf(true), Integer.valueOf(2))[0];
/* 31:39 */     this.input = ContrastStretch.invoke(this.input);
/* 32:   */     
/* 33:41 */     this.output[0] = ZernikeMoment.invoke(this.input, Integer.valueOf(3), Integer.valueOf(1)).doubleValue();
/* 34:42 */     this.output[1] = ZernikeMoment.invoke(this.input, Integer.valueOf(3), Integer.valueOf(3)).doubleValue();
/* 35:   */     
/* 36:44 */     this.output[2] = ZernikeMoment.invoke(this.input, Integer.valueOf(4), Integer.valueOf(0)).doubleValue();
/* 37:45 */     this.output[3] = ZernikeMoment.invoke(this.input, Integer.valueOf(4), Integer.valueOf(2)).doubleValue();
/* 38:46 */     this.output[4] = ZernikeMoment.invoke(this.input, Integer.valueOf(4), Integer.valueOf(4)).doubleValue();
/* 39:   */     
/* 40:48 */     this.output[5] = ZernikeMoment.invoke(this.input, Integer.valueOf(5), Integer.valueOf(1)).doubleValue();
/* 41:49 */     this.output[6] = ZernikeMoment.invoke(this.input, Integer.valueOf(5), Integer.valueOf(3)).doubleValue();
/* 42:50 */     this.output[7] = ZernikeMoment.invoke(this.input, Integer.valueOf(5), Integer.valueOf(5)).doubleValue();
/* 43:   */     
/* 44:52 */     this.output[8] = ZernikeMoment.invoke(this.input, Integer.valueOf(6), Integer.valueOf(0)).doubleValue();
/* 45:53 */     this.output[9] = ZernikeMoment.invoke(this.input, Integer.valueOf(6), Integer.valueOf(2)).doubleValue();
/* 46:54 */     this.output[10] = ZernikeMoment.invoke(this.input, Integer.valueOf(6), Integer.valueOf(4)).doubleValue();
/* 47:55 */     this.output[11] = ZernikeMoment.invoke(this.input, Integer.valueOf(6), Integer.valueOf(6)).doubleValue();
/* 48:   */     
/* 49:57 */     this.output[12] = ZernikeMoment.invoke(this.input, Integer.valueOf(7), Integer.valueOf(1)).doubleValue();
/* 50:58 */     this.output[13] = ZernikeMoment.invoke(this.input, Integer.valueOf(7), Integer.valueOf(3)).doubleValue();
/* 51:59 */     this.output[14] = ZernikeMoment.invoke(this.input, Integer.valueOf(7), Integer.valueOf(5)).doubleValue();
/* 52:60 */     this.output[15] = ZernikeMoment.invoke(this.input, Integer.valueOf(7), Integer.valueOf(7)).doubleValue();
/* 53:   */     
/* 54:62 */     this.output[16] = ZernikeMoment.invoke(this.input, Integer.valueOf(8), Integer.valueOf(0)).doubleValue();
/* 55:63 */     this.output[17] = ZernikeMoment.invoke(this.input, Integer.valueOf(8), Integer.valueOf(2)).doubleValue();
/* 56:64 */     this.output[18] = ZernikeMoment.invoke(this.input, Integer.valueOf(8), Integer.valueOf(4)).doubleValue();
/* 57:65 */     this.output[19] = ZernikeMoment.invoke(this.input, Integer.valueOf(8), Integer.valueOf(6)).doubleValue();
/* 58:66 */     this.output[20] = ZernikeMoment.invoke(this.input, Integer.valueOf(8), Integer.valueOf(8)).doubleValue();
/* 59:   */     
/* 60:68 */     this.output[21] = ZernikeMoment.invoke(this.input, Integer.valueOf(9), Integer.valueOf(1)).doubleValue();
/* 61:69 */     this.output[22] = ZernikeMoment.invoke(this.input, Integer.valueOf(9), Integer.valueOf(3)).doubleValue();
/* 62:70 */     this.output[23] = ZernikeMoment.invoke(this.input, Integer.valueOf(9), Integer.valueOf(5)).doubleValue();
/* 63:71 */     this.output[24] = ZernikeMoment.invoke(this.input, Integer.valueOf(9), Integer.valueOf(7)).doubleValue();
/* 64:72 */     this.output[25] = ZernikeMoment.invoke(this.input, Integer.valueOf(9), Integer.valueOf(9)).doubleValue();
/* 65:   */     
/* 66:74 */     this.output[26] = ZernikeMoment.invoke(this.input, Integer.valueOf(10), Integer.valueOf(0)).doubleValue();
/* 67:75 */     this.output[27] = ZernikeMoment.invoke(this.input, Integer.valueOf(10), Integer.valueOf(2)).doubleValue();
/* 68:76 */     this.output[28] = ZernikeMoment.invoke(this.input, Integer.valueOf(10), Integer.valueOf(4)).doubleValue();
/* 69:77 */     this.output[29] = ZernikeMoment.invoke(this.input, Integer.valueOf(10), Integer.valueOf(6)).doubleValue();
/* 70:78 */     this.output[30] = ZernikeMoment.invoke(this.input, Integer.valueOf(10), Integer.valueOf(8)).doubleValue();
/* 71:79 */     this.output[31] = ZernikeMoment.invoke(this.input, Integer.valueOf(10), Integer.valueOf(10)).doubleValue();
/* 72:   */   }
/* 73:   */   
/* 74:   */   public static double[] invoke(Image img)
/* 75:   */   {
/* 76:   */     try
/* 77:   */     {
/* 78:85 */       return (double[])new DFTZernikeMoments().preprocess(new Object[] { img });
/* 79:   */     }
/* 80:   */     catch (GlobalException e)
/* 81:   */     {
/* 82:87 */       e.printStackTrace();
/* 83:   */     }
/* 84:88 */     return null;
/* 85:   */   }
/* 86:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.DFTZernikeMoments
 * JD-Core Version:    0.7.0.1
 */