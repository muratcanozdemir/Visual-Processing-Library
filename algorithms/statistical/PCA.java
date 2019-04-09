/*   1:    */ package vpt.algorithms.statistical;
/*   2:    */ 
/*   3:    */ import Jama.EigenvalueDecomposition;
/*   4:    */ import Jama.Matrix;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import vpt.Algorithm;
/*   7:    */ import vpt.DoubleImage;
/*   8:    */ import vpt.GlobalException;
/*   9:    */ import vpt.Image;
/*  10:    */ 
/*  11:    */ public class PCA
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14:    */   public Image input;
/*  15:    */   public Image output;
/*  16: 26 */   public double[][] eigenvectors = null;
/*  17: 27 */   public double[] eigenvalues = null;
/*  18: 28 */   public double[] mean = null;
/*  19:    */   
/*  20:    */   public PCA()
/*  21:    */   {
/*  22: 31 */     this.inputFields = "input";
/*  23: 32 */     this.outputFields = "output, eigenvectors, eigenvalues, mean";
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void execute()
/*  27:    */     throws GlobalException
/*  28:    */   {
/*  29: 36 */     int cDim = this.input.getCDim();
/*  30: 37 */     int xDim = this.input.getXDim();
/*  31: 38 */     int yDim = this.input.getYDim();
/*  32:    */     
/*  33: 40 */     int size = xDim * yDim;
/*  34: 42 */     if (cDim == 1)
/*  35:    */     {
/*  36: 43 */       this.output = this.input.newInstance(true);
/*  37: 44 */       return;
/*  38:    */     }
/*  39: 48 */     double[][] pixels = new double[cDim][xDim * yDim];
/*  40: 50 */     for (int c = 0; c < cDim; c++) {
/*  41: 51 */       for (int x = 0; x < xDim; x++) {
/*  42: 52 */         for (int y = 0; y < yDim; y++) {
/*  43: 53 */           pixels[c][(y * xDim + x)] = this.input.getXYCDouble(x, y, c);
/*  44:    */         }
/*  45:    */       }
/*  46:    */     }
/*  47: 56 */     this.mean = new double[cDim];
/*  48: 58 */     for (int i = 0; i < cDim; i++)
/*  49:    */     {
/*  50: 59 */       this.mean[i] = 0.0D;
/*  51: 61 */       for (int j = 0; j < xDim * yDim; j++) {
/*  52: 62 */         this.mean[i] += pixels[i][j];
/*  53:    */       }
/*  54: 64 */       this.mean[i] /= size;
/*  55:    */     }
/*  56: 68 */     for (int i = 0; i < cDim; i++) {
/*  57: 69 */       for (int j = 0; j < size; j++) {
/*  58: 70 */         pixels[i][j] -= this.mean[i];
/*  59:    */       }
/*  60:    */     }
/*  61: 74 */     Matrix M = new Matrix(pixels, cDim, size);
/*  62: 75 */     Matrix Mt = M.transpose();
/*  63: 76 */     M = M.times(Mt);
/*  64:    */     
/*  65: 78 */     System.gc();
/*  66:    */     
/*  67:    */ 
/*  68: 81 */     M = M.times(1.0D / (size - 1));
/*  69:    */     
/*  70: 83 */     EigenvalueDecomposition ev = new EigenvalueDecomposition(M);
/*  71: 84 */     Matrix V = ev.getV();
/*  72:    */     
/*  73: 86 */     this.eigenvalues = ev.getRealEigenvalues();
/*  74: 87 */     this.eigenvectors = V.getArray();
/*  75:    */     
/*  76: 89 */     sort(this.eigenvalues, this.eigenvectors, this.mean, cDim);
/*  77:    */     
/*  78: 91 */     V = new Matrix(this.eigenvectors);
/*  79:    */     
/*  80: 93 */     M = Mt.times(V);
/*  81: 94 */     M = M.transpose();
/*  82:    */     
/*  83: 96 */     System.gc();
/*  84:    */     
/*  85:    */ 
/*  86: 99 */     double[][] eigenImages = M.getArray();
/*  87:102 */     for (int j = 0; j < eigenImages.length; j++) {
/*  88:103 */       if (this.eigenvalues[j] < 0.0D)
/*  89:    */       {
/*  90:104 */         this.eigenvalues[j] = 0.0D;
/*  91:106 */         for (int i = 0; i < eigenImages[j].length; i++) {
/*  92:107 */           eigenImages[j][i] = 0.0D;
/*  93:    */         }
/*  94:    */       }
/*  95:    */     }
/*  96:112 */     this.output = new DoubleImage(this.input, false);
/*  97:113 */     for (int c = 0; c < cDim; c++) {
/*  98:114 */       for (int x = 0; x < xDim; x++) {
/*  99:115 */         for (int y = 0; y < yDim; y++)
/* 100:    */         {
/* 101:116 */           double d = eigenImages[c][(y * xDim + x)];
/* 102:117 */           this.output.setXYCDouble(x, y, c, d);
/* 103:    */         }
/* 104:    */       }
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public static Image invoke(Image input)
/* 109:    */   {
/* 110:    */     try
/* 111:    */     {
/* 112:125 */       return (Image)new PCA().preprocess(new Object[] { input });
/* 113:    */     }
/* 114:    */     catch (GlobalException e)
/* 115:    */     {
/* 116:127 */       e.printStackTrace();
/* 117:    */     }
/* 118:128 */     return null;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public static ArrayList<Object> invokeAll(Image input)
/* 122:    */   {
/* 123:    */     try
/* 124:    */     {
/* 125:134 */       return (ArrayList)new PCA().preprocessAll(new Object[] { input });
/* 126:    */     }
/* 127:    */     catch (GlobalException e)
/* 128:    */     {
/* 129:136 */       e.printStackTrace();
/* 130:    */     }
/* 131:137 */     return null;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public static double[] getVariances(double[] vals)
/* 135:    */   {
/* 136:142 */     double totalVariance = 0.0D;
/* 137:143 */     double[] variances = new double[vals.length];
/* 138:146 */     for (int j = 0; j < vals.length; j++) {
/* 139:147 */       totalVariance += vals[j];
/* 140:    */     }
/* 141:150 */     for (int j = 0; j < vals.length; j++) {
/* 142:151 */       vals[j] /= totalVariance;
/* 143:    */     }
/* 144:153 */     return variances;
/* 145:    */   }
/* 146:    */   
/* 147:    */   private void sort(double[] vals, double[][] eigenvectors, double[] mean, int n)
/* 148:    */   {
/* 149:157 */     for (int i = 0; i < n; i++)
/* 150:    */     {
/* 151:158 */       int k = i;
/* 152:159 */       double p = vals[k];
/* 153:161 */       for (int j = i + 1; j < n; j++) {
/* 154:162 */         if (vals[j] >= p)
/* 155:    */         {
/* 156:163 */           k = j;
/* 157:164 */           p = vals[k];
/* 158:    */         }
/* 159:    */       }
/* 160:168 */       if (k != i)
/* 161:    */       {
/* 162:169 */         vals[k] = vals[i];
/* 163:170 */         vals[i] = p;
/* 164:174 */         for (int j = 0; j < n; j++)
/* 165:    */         {
/* 166:175 */           p = eigenvectors[j][i];
/* 167:176 */           eigenvectors[j][i] = eigenvectors[j][k];
/* 168:177 */           eigenvectors[j][k] = p;
/* 169:    */         }
/* 170:    */       }
/* 171:    */     }
/* 172:    */   }
/* 173:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.statistical.PCA
 * JD-Core Version:    0.7.0.1
 */