/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.statistical.ZernikeMoment;
/*  7:   */ 
/*  8:   */ public class ZernikeMoments
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:22 */   public double[] output = null;
/* 12:23 */   public Image input = null;
/* 13:24 */   public Integer n = null;
/* 14:   */   
/* 15:   */   public ZernikeMoments()
/* 16:   */   {
/* 17:27 */     this.inputFields = "input";
/* 18:28 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:32 */     if (this.input.getCDim() != 1) {
/* 25:32 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 26:   */     }
/* 27:34 */     this.output = new double[32];
/* 28:   */     
/* 29:36 */     this.output[0] = ZernikeMoment.invoke(this.input, Integer.valueOf(3), Integer.valueOf(1)).doubleValue();
/* 30:37 */     this.output[1] = ZernikeMoment.invoke(this.input, Integer.valueOf(3), Integer.valueOf(3)).doubleValue();
/* 31:   */     
/* 32:39 */     this.output[2] = ZernikeMoment.invoke(this.input, Integer.valueOf(4), Integer.valueOf(0)).doubleValue();
/* 33:40 */     this.output[3] = ZernikeMoment.invoke(this.input, Integer.valueOf(4), Integer.valueOf(2)).doubleValue();
/* 34:41 */     this.output[4] = ZernikeMoment.invoke(this.input, Integer.valueOf(4), Integer.valueOf(4)).doubleValue();
/* 35:   */     
/* 36:43 */     this.output[5] = ZernikeMoment.invoke(this.input, Integer.valueOf(5), Integer.valueOf(1)).doubleValue();
/* 37:44 */     this.output[6] = ZernikeMoment.invoke(this.input, Integer.valueOf(5), Integer.valueOf(3)).doubleValue();
/* 38:45 */     this.output[7] = ZernikeMoment.invoke(this.input, Integer.valueOf(5), Integer.valueOf(5)).doubleValue();
/* 39:   */     
/* 40:47 */     this.output[8] = ZernikeMoment.invoke(this.input, Integer.valueOf(6), Integer.valueOf(0)).doubleValue();
/* 41:48 */     this.output[9] = ZernikeMoment.invoke(this.input, Integer.valueOf(6), Integer.valueOf(2)).doubleValue();
/* 42:49 */     this.output[10] = ZernikeMoment.invoke(this.input, Integer.valueOf(6), Integer.valueOf(4)).doubleValue();
/* 43:50 */     this.output[11] = ZernikeMoment.invoke(this.input, Integer.valueOf(6), Integer.valueOf(6)).doubleValue();
/* 44:   */     
/* 45:52 */     this.output[12] = ZernikeMoment.invoke(this.input, Integer.valueOf(7), Integer.valueOf(1)).doubleValue();
/* 46:53 */     this.output[13] = ZernikeMoment.invoke(this.input, Integer.valueOf(7), Integer.valueOf(3)).doubleValue();
/* 47:54 */     this.output[14] = ZernikeMoment.invoke(this.input, Integer.valueOf(7), Integer.valueOf(5)).doubleValue();
/* 48:55 */     this.output[15] = ZernikeMoment.invoke(this.input, Integer.valueOf(7), Integer.valueOf(7)).doubleValue();
/* 49:   */     
/* 50:57 */     this.output[16] = ZernikeMoment.invoke(this.input, Integer.valueOf(8), Integer.valueOf(0)).doubleValue();
/* 51:58 */     this.output[17] = ZernikeMoment.invoke(this.input, Integer.valueOf(8), Integer.valueOf(2)).doubleValue();
/* 52:59 */     this.output[18] = ZernikeMoment.invoke(this.input, Integer.valueOf(8), Integer.valueOf(4)).doubleValue();
/* 53:60 */     this.output[19] = ZernikeMoment.invoke(this.input, Integer.valueOf(8), Integer.valueOf(6)).doubleValue();
/* 54:61 */     this.output[20] = ZernikeMoment.invoke(this.input, Integer.valueOf(8), Integer.valueOf(8)).doubleValue();
/* 55:   */     
/* 56:63 */     this.output[21] = ZernikeMoment.invoke(this.input, Integer.valueOf(9), Integer.valueOf(1)).doubleValue();
/* 57:64 */     this.output[22] = ZernikeMoment.invoke(this.input, Integer.valueOf(9), Integer.valueOf(3)).doubleValue();
/* 58:65 */     this.output[23] = ZernikeMoment.invoke(this.input, Integer.valueOf(9), Integer.valueOf(5)).doubleValue();
/* 59:66 */     this.output[24] = ZernikeMoment.invoke(this.input, Integer.valueOf(9), Integer.valueOf(7)).doubleValue();
/* 60:67 */     this.output[25] = ZernikeMoment.invoke(this.input, Integer.valueOf(9), Integer.valueOf(9)).doubleValue();
/* 61:   */     
/* 62:69 */     this.output[26] = ZernikeMoment.invoke(this.input, Integer.valueOf(10), Integer.valueOf(0)).doubleValue();
/* 63:70 */     this.output[27] = ZernikeMoment.invoke(this.input, Integer.valueOf(10), Integer.valueOf(2)).doubleValue();
/* 64:71 */     this.output[28] = ZernikeMoment.invoke(this.input, Integer.valueOf(10), Integer.valueOf(4)).doubleValue();
/* 65:72 */     this.output[29] = ZernikeMoment.invoke(this.input, Integer.valueOf(10), Integer.valueOf(6)).doubleValue();
/* 66:73 */     this.output[30] = ZernikeMoment.invoke(this.input, Integer.valueOf(10), Integer.valueOf(8)).doubleValue();
/* 67:74 */     this.output[31] = ZernikeMoment.invoke(this.input, Integer.valueOf(10), Integer.valueOf(10)).doubleValue();
/* 68:   */   }
/* 69:   */   
/* 70:   */   public static double[] invoke(Image img)
/* 71:   */   {
/* 72:   */     try
/* 73:   */     {
/* 74:80 */       return (double[])new ZernikeMoments().preprocess(new Object[] { img });
/* 75:   */     }
/* 76:   */     catch (GlobalException e)
/* 77:   */     {
/* 78:82 */       e.printStackTrace();
/* 79:   */     }
/* 80:83 */     return null;
/* 81:   */   }
/* 82:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.ZernikeMoments
 * JD-Core Version:    0.7.0.1
 */