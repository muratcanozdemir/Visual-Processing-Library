/*   1:    */ package vpt.algorithms.frequential;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.DoubleImage;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.util.Complex2DArray;
/*   8:    */ import vpt.util.ComplexNumber;
/*   9:    */ import vpt.util.Tools;
/*  10:    */ 
/*  11:    */ public class FFT
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14:    */   public DoubleImage[] output;
/*  15:    */   public Image input;
/*  16:    */   public Boolean shifted;
/*  17:    */   public int outputType;
/*  18:    */   public static final int REAL = 0;
/*  19:    */   public static final int REAL_IMAG = 1;
/*  20:    */   public static final int MAG = 2;
/*  21:    */   public static final int MAG_PHASE = 3;
/*  22:    */   
/*  23:    */   public FFT()
/*  24:    */   {
/*  25: 29 */     this.inputFields = "input, shifted, outputType";
/*  26: 30 */     this.outputFields = "output";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void execute()
/*  30:    */     throws GlobalException
/*  31:    */   {
/*  32: 35 */     double[] pixels = new double[this.input.getSize()];
/*  33:    */     
/*  34: 37 */     int size = this.input.getSize();
/*  35: 38 */     int xdim = this.input.getXDim();
/*  36: 39 */     int ydim = this.input.getYDim();
/*  37:    */     
/*  38: 41 */     int n = 0;
/*  39: 42 */     while (Math.pow(2.0D, n) < Math.max(xdim, ydim)) {
/*  40: 43 */       n++;
/*  41:    */     }
/*  42: 45 */     for (int i = 0; i < size; i++) {
/*  43: 46 */       pixels[i] = (this.input.getDouble(i) * Math.pow(2.0D, n));
/*  44:    */     }
/*  45: 48 */     Complex2DArray inputArray = new Complex2DArray(pixels, xdim, ydim);
/*  46: 49 */     Complex2DArray intermediateArray = new Complex2DArray(pixels, xdim, ydim);
/*  47: 50 */     Complex2DArray outputArray = new Complex2DArray(pixels, xdim, ydim);
/*  48: 52 */     for (int i = 0; i < inputArray.size; i++) {
/*  49: 53 */       intermediateArray.putColumn(i, recursiveFFT(inputArray.getColumn(i)));
/*  50:    */     }
/*  51: 55 */     for (int i = 0; i < intermediateArray.size; i++) {
/*  52: 56 */       outputArray.putRow(i, recursiveFFT(intermediateArray.getRow(i)));
/*  53:    */     }
/*  54: 58 */     switch (this.outputType)
/*  55:    */     {
/*  56:    */     case 0: 
/*  57: 60 */       this.output = new DoubleImage[1];
/*  58: 61 */       this.output[0] = new DoubleImage(this.input, false);
/*  59:    */       
/*  60: 63 */       double[][] reals = outputArray.getReals();
/*  61: 64 */       for (int x = 0; x < xdim; x++) {
/*  62: 65 */         for (int y = 0; y < ydim; y++) {
/*  63: 66 */           this.output[0].setXYDouble(x, y, reals[x][y]);
/*  64:    */         }
/*  65:    */       }
/*  66: 67 */       break;
/*  67:    */     case 1: 
/*  68: 69 */       this.output = new DoubleImage[2];
/*  69: 70 */       this.output[0] = new DoubleImage(this.input, false);
/*  70: 71 */       this.output[1] = new DoubleImage(this.input, false);
/*  71:    */       
/*  72: 73 */       double[][] reals = outputArray.getReals();
/*  73: 74 */       for (int x = 0; x < xdim; x++) {
/*  74: 75 */         for (int y = 0; y < ydim; y++) {
/*  75: 76 */           this.output[0].setXYDouble(x, y, reals[x][y]);
/*  76:    */         }
/*  77:    */       }
/*  78: 78 */       double[][] imags = outputArray.getImags();
/*  79: 79 */       for (int x = 0; x < xdim; x++) {
/*  80: 80 */         for (int y = 0; y < ydim; y++) {
/*  81: 81 */           this.output[1].setXYDouble(x, y, imags[x][y]);
/*  82:    */         }
/*  83:    */       }
/*  84: 83 */       break;
/*  85:    */     case 2: 
/*  86: 85 */       this.output = new DoubleImage[1];
/*  87: 86 */       this.output[0] = new DoubleImage(this.input, false);
/*  88:    */       
/*  89: 88 */       double[][] mags = outputArray.getMagnitudes();
/*  90: 89 */       for (int x = 0; x < xdim; x++) {
/*  91: 90 */         for (int y = 0; y < ydim; y++) {
/*  92: 91 */           this.output[0].setXYDouble(x, y, mags[x][y]);
/*  93:    */         }
/*  94:    */       }
/*  95: 93 */       if (this.shifted.booleanValue()) {
/*  96: 94 */         this.output[0] = ((DoubleImage)Tools.shiftOrigin(this.output[0]));
/*  97:    */       }
/*  98: 96 */       break;
/*  99:    */     case 3: 
/* 100: 98 */       this.output = new DoubleImage[2];
/* 101: 99 */       this.output[0] = new DoubleImage(this.input, false);
/* 102:100 */       this.output[1] = new DoubleImage(this.input, false);
/* 103:    */       
/* 104:102 */       double[][] mags = outputArray.getMagnitudes();
/* 105:103 */       for (int x = 0; x < xdim; x++) {
/* 106:104 */         for (int y = 0; y < ydim; y++) {
/* 107:105 */           this.output[0].setXYDouble(x, y, mags[x][y]);
/* 108:    */         }
/* 109:    */       }
/* 110:107 */       double[][] phases = outputArray.getPhases();
/* 111:108 */       for (int x = 0; x < xdim; x++) {
/* 112:109 */         for (int y = 0; y < ydim; y++) {
/* 113:110 */           this.output[1].setXYDouble(x, y, phases[x][y]);
/* 114:    */         }
/* 115:    */       }
/* 116:112 */       if (this.shifted.booleanValue()) {
/* 117:113 */         this.output[0] = ((DoubleImage)Tools.shiftOrigin(this.output[0]));
/* 118:    */       }
/* 119:115 */       break;
/* 120:    */     default: 
/* 121:117 */       throw new GlobalException("Invalid output type");
/* 122:    */     }
/* 123:    */   }
/* 124:    */   
/* 125:    */   private ComplexNumber[] recursiveFFT(ComplexNumber[] x)
/* 126:    */   {
/* 127:123 */     int n = x.length;
/* 128:124 */     int m = n / 2;
/* 129:125 */     ComplexNumber[] result = new ComplexNumber[n];
/* 130:126 */     ComplexNumber[] even = new ComplexNumber[m];
/* 131:127 */     ComplexNumber[] odd = new ComplexNumber[m];
/* 132:128 */     ComplexNumber[] sum = new ComplexNumber[m];
/* 133:129 */     ComplexNumber[] diff = new ComplexNumber[m];
/* 134:    */     
/* 135:131 */     ComplexNumber cTwo = new ComplexNumber(2.0D, 0.0D);
/* 136:133 */     if (n == 1)
/* 137:    */     {
/* 138:133 */       result[0] = x[0];
/* 139:    */     }
/* 140:    */     else
/* 141:    */     {
/* 142:135 */       ComplexNumber z1 = new ComplexNumber(0.0D, -6.283185307179586D / n);
/* 143:136 */       ComplexNumber tmp = ComplexNumber.cExp(z1);
/* 144:137 */       z1 = new ComplexNumber(1.0D, 0.0D);
/* 145:139 */       for (int i = 0; i < m; i++)
/* 146:    */       {
/* 147:140 */         ComplexNumber z3 = ComplexNumber.cSum(x[i], x[(i + m)]);
/* 148:141 */         sum[i] = ComplexNumber.cDiv(z3, cTwo);
/* 149:    */         
/* 150:143 */         z3 = ComplexNumber.cDiff(x[i], x[(i + m)]);
/* 151:144 */         ComplexNumber z4 = ComplexNumber.cMult(z3, z1);
/* 152:145 */         diff[i] = ComplexNumber.cDiv(z4, cTwo);
/* 153:    */         
/* 154:147 */         ComplexNumber z2 = ComplexNumber.cMult(z1, tmp);
/* 155:148 */         z1 = new ComplexNumber(z2);
/* 156:    */       }
/* 157:151 */       even = recursiveFFT(sum);
/* 158:152 */       odd = recursiveFFT(diff);
/* 159:154 */       for (int i = 0; i < m; i++)
/* 160:    */       {
/* 161:155 */         result[(i * 2)] = new ComplexNumber(even[i]);
/* 162:156 */         result[(i * 2 + 1)] = new ComplexNumber(odd[i]);
/* 163:    */       }
/* 164:    */     }
/* 165:160 */     return result;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public static DoubleImage[] invoke(Image image, Boolean shifted, Integer outputType)
/* 169:    */   {
/* 170:    */     try
/* 171:    */     {
/* 172:166 */       return (DoubleImage[])new FFT().preprocess(new Object[] { image, shifted, outputType });
/* 173:    */     }
/* 174:    */     catch (GlobalException e)
/* 175:    */     {
/* 176:168 */       e.printStackTrace();
/* 177:    */     }
/* 178:169 */     return null;
/* 179:    */   }
/* 180:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.frequential.FFT
 * JD-Core Version:    0.7.0.1
 */