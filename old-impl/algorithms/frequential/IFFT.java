/*   1:    */ package vpt.algorithms.frequential;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.DoubleImage;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.util.Complex2DArray;
/*   8:    */ import vpt.util.ComplexNumber;
/*   9:    */ 
/*  10:    */ public class IFFT
/*  11:    */   extends Algorithm
/*  12:    */ {
/*  13:    */   public Image output;
/*  14:    */   public DoubleImage real;
/*  15:    */   public DoubleImage imag;
/*  16:    */   
/*  17:    */   public IFFT()
/*  18:    */   {
/*  19: 19 */     this.inputFields = "real, imag";
/*  20: 20 */     this.outputFields = "output";
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void execute()
/*  24:    */     throws GlobalException
/*  25:    */   {
/*  26: 24 */     int xdim = this.real.getXDim();
/*  27: 25 */     int ydim = this.real.getYDim();
/*  28:    */     
/*  29:    */ 
/*  30: 28 */     ComplexNumber[][] content = new ComplexNumber[xdim][ydim];
/*  31:    */     
/*  32: 30 */     int n = 0;
/*  33: 31 */     while (Math.pow(2.0D, n) < Math.max(xdim, ydim)) {
/*  34: 32 */       n++;
/*  35:    */     }
/*  36: 34 */     for (int x = 0; x < xdim; x++) {
/*  37: 35 */       for (int y = 0; y < ydim; y++)
/*  38:    */       {
/*  39: 36 */         double realP = this.real.getXYDouble(x, y) * Math.pow(2.0D, n);
/*  40: 37 */         double imagP = this.imag.getXYDouble(x, y) * Math.pow(2.0D, n);
/*  41: 38 */         content[x][y] = new ComplexNumber(realP, imagP);
/*  42:    */       }
/*  43:    */     }
/*  44: 42 */     Complex2DArray inputArray = new Complex2DArray(content, xdim, ydim);
/*  45: 43 */     Complex2DArray intermediateArray = new Complex2DArray(xdim, ydim);
/*  46: 44 */     Complex2DArray outputArray = new Complex2DArray(xdim, ydim);
/*  47: 46 */     for (int i = 0; i < inputArray.size; i++) {
/*  48: 47 */       intermediateArray.putColumn(i, recursiveInverseFFT(inputArray.getColumn(i)));
/*  49:    */     }
/*  50: 49 */     for (int i = 0; i < intermediateArray.size; i++) {
/*  51: 50 */       outputArray.putRow(i, recursiveInverseFFT(intermediateArray.getRow(i)));
/*  52:    */     }
/*  53: 52 */     this.output = new DoubleImage(this.real, false);
/*  54:    */     
/*  55: 54 */     double[][] mags = outputArray.getReals();
/*  56: 56 */     for (int x = 0; x < xdim; x++) {
/*  57: 57 */       for (int y = 0; y < ydim; y++) {
/*  58: 58 */         this.output.setXYDouble(x, y, mags[x][y]);
/*  59:    */       }
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   private ComplexNumber[] recursiveInverseFFT(ComplexNumber[] x)
/*  64:    */   {
/*  65: 63 */     int n = x.length;
/*  66: 64 */     int m = n / 2;
/*  67:    */     
/*  68: 66 */     ComplexNumber[] result = new ComplexNumber[n];
/*  69: 67 */     ComplexNumber[] even = new ComplexNumber[m];
/*  70: 68 */     ComplexNumber[] odd = new ComplexNumber[m];
/*  71: 69 */     ComplexNumber[] sum = new ComplexNumber[m];
/*  72: 70 */     ComplexNumber[] diff = new ComplexNumber[m];
/*  73:    */     
/*  74: 72 */     ComplexNumber cTwo = new ComplexNumber(2.0D, 0.0D);
/*  75: 74 */     if (n == 1)
/*  76:    */     {
/*  77: 74 */       result[0] = x[0];
/*  78:    */     }
/*  79:    */     else
/*  80:    */     {
/*  81: 76 */       ComplexNumber z1 = new ComplexNumber(0.0D, 6.283185307179586D / n);
/*  82: 77 */       ComplexNumber tmp = ComplexNumber.cExp(z1);
/*  83: 78 */       z1 = new ComplexNumber(1.0D, 0.0D);
/*  84: 80 */       for (int i = 0; i < m; i++)
/*  85:    */       {
/*  86: 81 */         ComplexNumber z3 = ComplexNumber.cSum(x[i], x[(i + m)]);
/*  87: 82 */         sum[i] = ComplexNumber.cDiv(z3, cTwo);
/*  88:    */         
/*  89: 84 */         z3 = ComplexNumber.cDiff(x[i], x[(i + m)]);
/*  90: 85 */         ComplexNumber z4 = ComplexNumber.cMult(z3, z1);
/*  91: 86 */         diff[i] = ComplexNumber.cDiv(z4, cTwo);
/*  92:    */         
/*  93: 88 */         ComplexNumber z2 = ComplexNumber.cMult(z1, tmp);
/*  94: 89 */         z1 = new ComplexNumber(z2);
/*  95:    */       }
/*  96: 91 */       even = recursiveInverseFFT(sum);
/*  97: 92 */       odd = recursiveInverseFFT(diff);
/*  98: 94 */       for (int i = 0; i < m; i++)
/*  99:    */       {
/* 100: 95 */         result[(i * 2)] = new ComplexNumber(even[i]);
/* 101: 96 */         result[(i * 2 + 1)] = new ComplexNumber(odd[i]);
/* 102:    */       }
/* 103:    */     }
/* 104:100 */     return result;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public static Image invoke(DoubleImage real, DoubleImage imag)
/* 108:    */   {
/* 109:    */     try
/* 110:    */     {
/* 111:106 */       return (Image)new IFFT().preprocess(new Object[] { real, imag });
/* 112:    */     }
/* 113:    */     catch (GlobalException e)
/* 114:    */     {
/* 115:108 */       e.printStackTrace();
/* 116:    */     }
/* 117:109 */     return null;
/* 118:    */   }
/* 119:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.frequential.IFFT
 * JD-Core Version:    0.7.0.1
 */