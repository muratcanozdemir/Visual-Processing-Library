/*   1:    */ package vpt.algorithms.statistical;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.GlobalException;
/*   5:    */ import vpt.Image;
/*   6:    */ import vpt.util.ComplexNumber;
/*   7:    */ 
/*   8:    */ public class ZernikeMoment
/*   9:    */   extends Algorithm
/*  10:    */ {
/*  11: 18 */   public Double moment = null;
/*  12: 19 */   public Image input = null;
/*  13: 20 */   public Integer m = null;
/*  14: 21 */   public Integer n = null;
/*  15:    */   private int xdim;
/*  16:    */   private int ydim;
/*  17:    */   
/*  18:    */   public ZernikeMoment()
/*  19:    */   {
/*  20: 27 */     this.inputFields = "input, n, m";
/*  21: 28 */     this.outputFields = "moment";
/*  22:    */   }
/*  23:    */   
/*  24:    */   public void execute()
/*  25:    */     throws GlobalException
/*  26:    */   {
/*  27: 33 */     if (this.input.getCDim() != 1) {
/*  28: 33 */       throw new GlobalException("Only grayscale images please!");
/*  29:    */     }
/*  30: 34 */     if (((this.n.intValue() - Math.abs(this.m.intValue())) % 2 != 0) || (Math.abs(this.m.intValue()) > this.n.intValue())) {
/*  31: 34 */       throw new GlobalException("Invalid n, m values!");
/*  32:    */     }
/*  33: 36 */     this.m = Integer.valueOf(Math.abs(this.m.intValue()));
/*  34:    */     
/*  35: 38 */     this.xdim = this.input.getXDim();
/*  36: 39 */     this.ydim = this.input.getYDim();
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43: 46 */     int diff = this.n.intValue() - Math.abs(this.m.intValue());
/*  44: 47 */     if ((this.n.intValue() < 0) || (Math.abs(this.m.intValue()) > this.n.intValue()) || (diff % 2 != 0)) {
/*  45: 48 */       throw new IllegalArgumentException("zer_mom: n = " + this.n + ", m = " + this.m + ", n - |m| = " + diff);
/*  46:    */     }
/*  47: 50 */     double i_0 = this.xdim / 2;
/*  48: 51 */     double j_0 = this.ydim / 2;
/*  49:    */     
/*  50: 53 */     double radius = this.xdim / 2;
/*  51: 54 */     double i_scale = Math.sqrt(2.0D) * radius;
/*  52:    */     
/*  53: 56 */     radius = this.ydim / 2;
/*  54: 57 */     double j_scale = Math.sqrt(2.0D) * radius;
/*  55:    */     
/*  56: 59 */     ComplexNumber res = new ComplexNumber();
/*  57: 60 */     double lambdaN = 0.0D;
/*  58: 62 */     for (int _y = 0; _y < this.ydim; _y++) {
/*  59: 63 */       for (int _x = 0; _x < this.xdim; _x++)
/*  60:    */       {
/*  61: 64 */         double x = (_x - i_0) / i_scale;
/*  62: 65 */         double y = (_y - j_0) / j_scale;
/*  63:    */         
/*  64: 67 */         double p = this.input.getXYDouble(_x, _y);
/*  65: 69 */         if (x * x + y * y <= 1.0D)
/*  66:    */         {
/*  67: 70 */           ComplexNumber v = zer_pol(this.n.intValue(), this.m.intValue(), x, y);
/*  68: 71 */           res.real += p * v.real;
/*  69: 72 */           res.imag += p * v.imag;
/*  70: 73 */           lambdaN += p;
/*  71:    */         }
/*  72:    */       }
/*  73:    */     }
/*  74: 78 */     res.real = (res.real * (this.n.intValue() + 1) / lambdaN);
/*  75: 79 */     res.imag = (res.imag * (this.n.intValue() + 1) / lambdaN);
/*  76:    */     
/*  77: 81 */     this.moment = Double.valueOf(res.magnitude());
/*  78:    */   }
/*  79:    */   
/*  80:    */   private ComplexNumber zer_pol(int n, int m, double x, double y)
/*  81:    */   {
/*  82: 86 */     if (x * x + y * y > 1.0D) {
/*  83: 87 */       return new ComplexNumber(0.0D, 0.0D);
/*  84:    */     }
/*  85: 89 */     double r = zer_pol_R(n, m, x, y);
/*  86:    */     
/*  87: 91 */     double arg = m * Math.atan2(y, x);
/*  88: 92 */     double real = r * Math.cos(arg);
/*  89: 93 */     double imag = r * Math.sin(arg);
/*  90:    */     
/*  91: 95 */     return new ComplexNumber(real, imag);
/*  92:    */   }
/*  93:    */   
/*  94:    */   private double zer_pol_R(int n, int m_in, double x, double y)
/*  95:    */   {
/*  96:106 */     int m = Math.abs(m_in);
/*  97:108 */     if ((n - m) % 2 != 0) {
/*  98:108 */       throw new IllegalArgumentException("zer_pol_R: n - |m| is odd");
/*  99:    */     }
/* 100:110 */     double res = 0.0D;
/* 101:112 */     if (x * x + y * y <= 1.0D)
/* 102:    */     {
/* 103:113 */       int sign = 1;
/* 104:114 */       int a = 1;
/* 105:116 */       for (int i = 2; i <= n; i++) {
/* 106:116 */         a *= i;
/* 107:    */       }
/* 108:118 */       int b = 1;
/* 109:119 */       int c = 1;
/* 110:121 */       for (int i = 2; i <= (n + m) / 2; i++) {
/* 111:121 */         c *= i;
/* 112:    */       }
/* 113:123 */       int d = 1;
/* 114:125 */       for (int i = 2; i <= (n - m) / 2; i++) {
/* 115:125 */         d *= i;
/* 116:    */       }
/* 117:127 */       for (int s = 0; s <= (n - m) / 2; s++)
/* 118:    */       {
/* 119:128 */         res += sign * (a * 1.0D / (b * c * d)) * Math.pow(x * x + y * y, n / 2.0D - s);
/* 120:130 */         if (s < (n - m) / 2)
/* 121:    */         {
/* 122:131 */           sign *= -1;
/* 123:132 */           a /= (n - s);
/* 124:133 */           b *= (s + 1);
/* 125:134 */           c /= ((n + m) / 2 - s);
/* 126:135 */           d /= ((n - m) / 2 - s);
/* 127:    */         }
/* 128:    */       }
/* 129:    */     }
/* 130:139 */     return res;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public static Double invoke(Image input, Integer n, Integer m)
/* 134:    */   {
/* 135:    */     try
/* 136:    */     {
/* 137:145 */       return (Double)new ZernikeMoment().preprocess(new Object[] { input, n, m });
/* 138:    */     }
/* 139:    */     catch (GlobalException e)
/* 140:    */     {
/* 141:147 */       e.printStackTrace();
/* 142:    */     }
/* 143:148 */     return null;
/* 144:    */   }
/* 145:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.statistical.ZernikeMoment
 * JD-Core Version:    0.7.0.1
 */