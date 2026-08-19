/*   1:    */ package vpt.algorithms.statistical;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.GlobalException;
/*   5:    */ import vpt.Image;
/*   6:    */ 
/*   7:    */ public class InvariantMoment
/*   8:    */   extends Algorithm
/*   9:    */ {
/*  10: 15 */   public double[] output = null;
/*  11: 16 */   public Image input = null;
/*  12: 17 */   public Integer order = null;
/*  13:    */   
/*  14:    */   public InvariantMoment()
/*  15:    */   {
/*  16: 20 */     this.inputFields = "input, order";
/*  17: 21 */     this.outputFields = "output";
/*  18:    */   }
/*  19:    */   
/*  20:    */   public void execute()
/*  21:    */     throws GlobalException
/*  22:    */   {
/*  23: 25 */     if ((this.order.intValue() < 1) || (this.order.intValue() > 8)) {
/*  24: 25 */       throw new GlobalException("The order must be \\in [1,8] !");
/*  25:    */     }
/*  26: 27 */     int cdim = this.input.getCDim();
/*  27:    */     
/*  28: 29 */     this.output = new double[cdim];
/*  29: 31 */     switch (this.order.intValue())
/*  30:    */     {
/*  31:    */     case 1: 
/*  32: 33 */       double[] n20 = SpatialMoment.invoke(this.input, Integer.valueOf(2), Integer.valueOf(0));
/*  33: 34 */       double[] n02 = SpatialMoment.invoke(this.input, Integer.valueOf(0), Integer.valueOf(2));
/*  34: 36 */       for (int c = 0; c < cdim; c++) {
/*  35: 37 */         this.output[c] = (n20[c] + n02[c]);
/*  36:    */       }
/*  37: 38 */       break;
/*  38:    */     case 2: 
/*  39: 40 */       double[] n20 = SpatialMoment.invoke(this.input, Integer.valueOf(2), Integer.valueOf(0));
/*  40: 41 */       double[] n02 = SpatialMoment.invoke(this.input, Integer.valueOf(0), Integer.valueOf(2));
/*  41: 42 */       double[] n11 = SpatialMoment.invoke(this.input, Integer.valueOf(1), Integer.valueOf(1));
/*  42: 44 */       for (int c = 0; c < cdim; c++) {
/*  43: 45 */         this.output[c] = ((n20[c] - n02[c]) * (n20[c] - n02[c]) + 4.0D * n11[c] * n11[c]);
/*  44:    */       }
/*  45: 46 */       break;
/*  46:    */     case 3: 
/*  47: 48 */       double[] n30 = SpatialMoment.invoke(this.input, Integer.valueOf(3), Integer.valueOf(0));
/*  48: 49 */       double[] n03 = SpatialMoment.invoke(this.input, Integer.valueOf(0), Integer.valueOf(3));
/*  49: 50 */       double[] n21 = SpatialMoment.invoke(this.input, Integer.valueOf(2), Integer.valueOf(1));
/*  50: 51 */       double[] n12 = SpatialMoment.invoke(this.input, Integer.valueOf(1), Integer.valueOf(2));
/*  51: 53 */       for (int c = 0; c < cdim; c++) {
/*  52: 54 */         this.output[c] = ((n30[c] - 3.0D * n12[c]) * (n30[c] - 3.0D * n12[c]) + (3.0D * n21[c] - n03[c]) * (3.0D * n21[c] - n03[c]));
/*  53:    */       }
/*  54: 55 */       break;
/*  55:    */     case 4: 
/*  56: 57 */       double[] n30 = SpatialMoment.invoke(this.input, Integer.valueOf(3), Integer.valueOf(0));
/*  57: 58 */       double[] n03 = SpatialMoment.invoke(this.input, Integer.valueOf(0), Integer.valueOf(3));
/*  58: 59 */       double[] n21 = SpatialMoment.invoke(this.input, Integer.valueOf(2), Integer.valueOf(1));
/*  59: 60 */       double[] n12 = SpatialMoment.invoke(this.input, Integer.valueOf(1), Integer.valueOf(2));
/*  60: 62 */       for (int c = 0; c < cdim; c++) {
/*  61: 63 */         this.output[c] = ((n30[c] + n12[c]) * (n30[c] + n12[c]) + (n03[c] + n21[c]) * (n03[c] + n21[c]));
/*  62:    */       }
/*  63: 64 */       break;
/*  64:    */     case 5: 
/*  65: 66 */       double[] n30 = SpatialMoment.invoke(this.input, Integer.valueOf(3), Integer.valueOf(0));
/*  66: 67 */       double[] n03 = SpatialMoment.invoke(this.input, Integer.valueOf(0), Integer.valueOf(3));
/*  67: 68 */       double[] n21 = SpatialMoment.invoke(this.input, Integer.valueOf(2), Integer.valueOf(1));
/*  68: 69 */       double[] n12 = SpatialMoment.invoke(this.input, Integer.valueOf(1), Integer.valueOf(2));
/*  69: 71 */       for (int c = 0; c < cdim; c++) {
/*  70: 72 */         this.output[c] = 
/*  71: 73 */           ((n30[c] - 3.0D * n12[c]) * (n30[c] + n12[c]) * ((n30[c] + n12[c]) * (n30[c] + n12[c]) - 3.0D * (n21[c] + n03[c]) * (n21[c] + n03[c])) + (3.0D * n21[c] - n03[c]) * (n21[c] + n03[c]) * (3.0D * (n30[c] + n12[c]) * (n30[c] + n12[c]) - (n21[c] - n03[c]) * (n21[c] - n03[c])));
/*  72:    */       }
/*  73: 74 */       break;
/*  74:    */     case 6: 
/*  75: 76 */       double[] n30 = SpatialMoment.invoke(this.input, Integer.valueOf(3), Integer.valueOf(0));
/*  76: 77 */       double[] n03 = SpatialMoment.invoke(this.input, Integer.valueOf(0), Integer.valueOf(3));
/*  77: 78 */       double[] n21 = SpatialMoment.invoke(this.input, Integer.valueOf(2), Integer.valueOf(1));
/*  78: 79 */       double[] n12 = SpatialMoment.invoke(this.input, Integer.valueOf(1), Integer.valueOf(2));
/*  79: 80 */       double[] n20 = SpatialMoment.invoke(this.input, Integer.valueOf(2), Integer.valueOf(0));
/*  80: 81 */       double[] n02 = SpatialMoment.invoke(this.input, Integer.valueOf(0), Integer.valueOf(2));
/*  81: 82 */       double[] n11 = SpatialMoment.invoke(this.input, Integer.valueOf(1), Integer.valueOf(1));
/*  82: 84 */       for (int c = 0; c < cdim; c++) {
/*  83: 85 */         this.output[c] = ((n20[c] - n02[c]) * ((n30[c] + n12[c]) * (n30[c] + n12[c]) - (n21[c] + n03[c]) * (n21[c] + n03[c])) + 4.0D * n11[c] * (n30[c] + n12[c]) * (n21[c] + n03[c]));
/*  84:    */       }
/*  85: 86 */       break;
/*  86:    */     case 7: 
/*  87: 88 */       double[] n30 = SpatialMoment.invoke(this.input, Integer.valueOf(3), Integer.valueOf(0));
/*  88: 89 */       double[] n03 = SpatialMoment.invoke(this.input, Integer.valueOf(0), Integer.valueOf(3));
/*  89: 90 */       double[] n21 = SpatialMoment.invoke(this.input, Integer.valueOf(2), Integer.valueOf(1));
/*  90: 91 */       double[] n12 = SpatialMoment.invoke(this.input, Integer.valueOf(1), Integer.valueOf(2));
/*  91: 93 */       for (int c = 0; c < cdim; c++) {
/*  92: 94 */         this.output[c] = 
/*  93: 95 */           ((3.0D * n21[c] - n03[c]) * (n30[c] + n12[c]) * ((n30[c] + n12[c]) * (n30[c] + n12[c]) - 3.0D * (n21[c] + n03[c]) * (n21[c] + n03[c])) - (n03[c] - 3.0D * n12[c]) * (n21[c] + n03[c]) * (3.0D * (n30[c] + n12[c]) * (n30[c] + n12[c]) - (n21[c] + n03[c]) * (n21[c] + n03[c])));
/*  94:    */       }
/*  95: 96 */       break;
/*  96:    */     case 8: 
/*  97: 98 */       double[] n30 = SpatialMoment.invoke(this.input, Integer.valueOf(3), Integer.valueOf(0));
/*  98: 99 */       double[] n03 = SpatialMoment.invoke(this.input, Integer.valueOf(0), Integer.valueOf(3));
/*  99:100 */       double[] n21 = SpatialMoment.invoke(this.input, Integer.valueOf(2), Integer.valueOf(1));
/* 100:101 */       double[] n12 = SpatialMoment.invoke(this.input, Integer.valueOf(1), Integer.valueOf(2));
/* 101:102 */       double[] n20 = SpatialMoment.invoke(this.input, Integer.valueOf(2), Integer.valueOf(0));
/* 102:103 */       double[] n02 = SpatialMoment.invoke(this.input, Integer.valueOf(0), Integer.valueOf(2));
/* 103:104 */       double[] n11 = SpatialMoment.invoke(this.input, Integer.valueOf(1), Integer.valueOf(1));
/* 104:106 */       for (int c = 0; c < cdim; c++) {
/* 105:107 */         this.output[c] = (n11[c] * ((n30[c] + n12[c]) * (n30[c] + n12[c]) - (n03[c] + n21[c]) * (n03[c] + n21[c])) - (n20[c] - n02[c]) * (n30[c] + n12[c]) * (n03[c] + n21[c]));
/* 106:    */       }
/* 107:108 */       break;
/* 108:    */     default: 
/* 109:110 */       throw new GlobalException("Never supposed to be here!");
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   public static double[] invoke(Image input, Integer order)
/* 114:    */   {
/* 115:    */     try
/* 116:    */     {
/* 117:118 */       return (double[])new InvariantMoment().preprocess(new Object[] { input, order });
/* 118:    */     }
/* 119:    */     catch (GlobalException e)
/* 120:    */     {
/* 121:120 */       e.printStackTrace();
/* 122:    */     }
/* 123:121 */     return null;
/* 124:    */   }
/* 125:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.statistical.InvariantMoment
 * JD-Core Version:    0.7.0.1
 */