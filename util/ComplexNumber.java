/*  1:   */ package vpt.util;
/*  2:   */ 
/*  3:   */ public class ComplexNumber
/*  4:   */ {
/*  5:   */   public double real;
/*  6:   */   public double imag;
/*  7:   */   
/*  8:   */   public ComplexNumber() {}
/*  9:   */   
/* 10:   */   public ComplexNumber(double r, double i)
/* 11:   */   {
/* 12:14 */     this.real = r;
/* 13:15 */     this.imag = i;
/* 14:   */   }
/* 15:   */   
/* 16:   */   public ComplexNumber(ComplexNumber c)
/* 17:   */   {
/* 18:19 */     this.real = c.real;
/* 19:20 */     this.imag = c.imag;
/* 20:   */   }
/* 21:   */   
/* 22:   */   public double magnitude()
/* 23:   */   {
/* 24:24 */     return Math.sqrt(cNorm());
/* 25:   */   }
/* 26:   */   
/* 27:   */   public double phaseAngle()
/* 28:   */   {
/* 29:28 */     if ((this.real == 0.0D) && (this.imag == 0.0D)) {
/* 30:28 */       return 0.0D;
/* 31:   */     }
/* 32:29 */     return Math.atan(this.imag / this.real);
/* 33:   */   }
/* 34:   */   
/* 35:   */   double cNorm()
/* 36:   */   {
/* 37:33 */     return this.real * this.real + this.imag * this.imag;
/* 38:   */   }
/* 39:   */   
/* 40:   */   public static ComplexNumber cExp(ComplexNumber z)
/* 41:   */   {
/* 42:39 */     ComplexNumber x = new ComplexNumber(Math.exp(z.real), 0.0D);
/* 43:40 */     ComplexNumber y = new ComplexNumber(Math.cos(z.imag), Math.sin(z.imag));
/* 44:   */     
/* 45:42 */     return cMult(x, y);
/* 46:   */   }
/* 47:   */   
/* 48:   */   public static ComplexNumber cMult(ComplexNumber z1, ComplexNumber z2)
/* 49:   */   {
/* 50:46 */     ComplexNumber z3 = new ComplexNumber();
/* 51:   */     
/* 52:48 */     z3.real = (z1.real * z2.real - z1.imag * z2.imag);
/* 53:49 */     z3.imag = (z1.real * z2.imag + z1.imag * z2.real);
/* 54:   */     
/* 55:51 */     return z3;
/* 56:   */   }
/* 57:   */   
/* 58:   */   public static ComplexNumber cSum(ComplexNumber z1, ComplexNumber z2)
/* 59:   */   {
/* 60:55 */     ComplexNumber z3 = new ComplexNumber();
/* 61:   */     
/* 62:57 */     z1.real += z2.real;
/* 63:58 */     z1.imag += z2.imag;
/* 64:   */     
/* 65:60 */     return z3;
/* 66:   */   }
/* 67:   */   
/* 68:   */   public static ComplexNumber cDiv(ComplexNumber z1, ComplexNumber z2)
/* 69:   */   {
/* 70:64 */     ComplexNumber z3 = new ComplexNumber();
/* 71:   */     
/* 72:66 */     double n = z2.cNorm();
/* 73:   */     
/* 74:68 */     z3.real = ((z1.real * z2.real + z1.imag * z2.imag) / n);
/* 75:69 */     z3.imag = ((z2.real * z1.imag - z1.real * z2.imag) / n);
/* 76:   */     
/* 77:71 */     return z3;
/* 78:   */   }
/* 79:   */   
/* 80:   */   public static ComplexNumber cDiff(ComplexNumber z1, ComplexNumber z2)
/* 81:   */   {
/* 82:76 */     ComplexNumber z3 = new ComplexNumber();
/* 83:   */     
/* 84:78 */     z1.real -= z2.real;
/* 85:79 */     z1.imag -= z2.imag;
/* 86:   */     
/* 87:81 */     return z3;
/* 88:   */   }
/* 89:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.ComplexNumber
 * JD-Core Version:    0.7.0.1
 */