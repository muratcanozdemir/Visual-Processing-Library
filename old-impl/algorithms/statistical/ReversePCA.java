/*  1:   */ package vpt.algorithms.statistical;
/*  2:   */ 
/*  3:   */ import Jama.Matrix;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class ReversePCA
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:   */   public Image input;
/* 12:19 */   public double[][] eigenvectors = null;
/* 13:20 */   public double[] mean = null;
/* 14:   */   public Image output;
/* 15:   */   
/* 16:   */   public ReversePCA()
/* 17:   */   {
/* 18:28 */     this.inputFields = "input,eigenvectors,mean";
/* 19:29 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:33 */     int cDim = this.input.getCDim();
/* 26:34 */     int xDim = this.input.getXDim();
/* 27:35 */     int yDim = this.input.getYDim();
/* 28:   */     
/* 29:37 */     int size = xDim * yDim;
/* 30:   */     
/* 31:39 */     this.output = this.input.newInstance(false);
/* 32:   */     
/* 33:   */ 
/* 34:42 */     double[][] pixels = new double[cDim][size];
/* 35:44 */     for (int c = 0; c < cDim; c++) {
/* 36:45 */       for (int x = 0; x < xDim; x++) {
/* 37:46 */         for (int y = 0; y < yDim; y++) {
/* 38:47 */           pixels[c][(y * xDim + x)] = this.input.getXYCDouble(x, y, c);
/* 39:   */         }
/* 40:   */       }
/* 41:   */     }
/* 42:49 */     Matrix E = new Matrix(pixels, cDim, size);
/* 43:50 */     Matrix V = new Matrix(this.eigenvectors, this.eigenvectors.length, cDim);
/* 44:   */     
/* 45:52 */     Matrix M = V.times(E);
/* 46:   */     
/* 47:54 */     double[][] sonuc = M.getArray();
/* 48:57 */     for (int c = 0; c < cDim; c++) {
/* 49:58 */       for (int x = 0; x < xDim; x++) {
/* 50:59 */         for (int y = 0; y < yDim; y++)
/* 51:   */         {
/* 52:60 */           double d = sonuc[c][(y * xDim + x)];
/* 53:61 */           this.output.setXYCDouble(x, y, c, d + this.mean[c]);
/* 54:   */         }
/* 55:   */       }
/* 56:   */     }
/* 57:   */   }
/* 58:   */   
/* 59:   */   public static Image invoke(Image input, double[][] eigenvectors, double[] mean)
/* 60:   */   {
/* 61:   */     try
/* 62:   */     {
/* 63:69 */       return (Image)new ReversePCA().preprocess(new Object[] { input, eigenvectors, mean });
/* 64:   */     }
/* 65:   */     catch (GlobalException e)
/* 66:   */     {
/* 67:71 */       e.printStackTrace();
/* 68:   */     }
/* 69:72 */     return null;
/* 70:   */   }
/* 71:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.statistical.ReversePCA
 * JD-Core Version:    0.7.0.1
 */