/*   1:    */ package vpt.algorithms.plants;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.algorithms.arithmetic.Minimum;
/*   8:    */ import vpt.algorithms.point.Inversion;
/*   9:    */ 
/*  10:    */ public class Angles
/*  11:    */   extends Algorithm
/*  12:    */ {
/*  13: 19 */   public double[] output = null;
/*  14: 20 */   public Image input = null;
/*  15:    */   
/*  16:    */   public Angles()
/*  17:    */   {
/*  18: 23 */     this.inputFields = "input";
/*  19: 24 */     this.outputFields = "output";
/*  20:    */   }
/*  21:    */   
/*  22:    */   public void execute()
/*  23:    */     throws GlobalException
/*  24:    */   {
/*  25: 28 */     int cdim = this.input.getCDim();
/*  26: 30 */     if (cdim != 1) {
/*  27: 30 */       throw new GlobalException("Sorry, only mono-channel images for now...");
/*  28:    */     }
/*  29: 32 */     Image inputPR = PetioleRemoval.invoke(this.input);
/*  30: 33 */     Image petiole = Minimum.invoke(this.input, Inversion.invoke(inputPR));
/*  31:    */     
/*  32:    */ 
/*  33: 36 */     Point stalk = new Point(0, 0);
/*  34: 37 */     Point tip = new Point(0, 0);
/*  35: 38 */     Point left = new Point(0, 0);
/*  36: 39 */     Point right = new Point(0, 0);
/*  37: 42 */     if (!petiole.isEmpty(0))
/*  38:    */     {
/*  39: 44 */       stalk.x = 0;
/*  40: 45 */       stalk.y = 600;
/*  41: 46 */       for (int x = 0; x < petiole.getXDim(); x++) {
/*  42: 47 */         for (int y = 0; y < petiole.getYDim(); y++) {
/*  43: 48 */           if ((petiole.getXYBoolean(x, y)) && (y < stalk.y))
/*  44:    */           {
/*  45: 49 */             stalk.y = y;
/*  46: 50 */             stalk.x = x;
/*  47:    */           }
/*  48:    */         }
/*  49:    */       }
/*  50:    */     }
/*  51:    */     else
/*  52:    */     {
/*  53: 57 */       stalk.x = (this.input.getXDim() / 2);
/*  54: 58 */       stalk.y = (this.input.getYDim() - 1);
/*  55:    */     }
/*  56: 62 */     tip.x = 0;
/*  57: 63 */     tip.y = 600;
/*  58:    */     
/*  59: 65 */     left.x = 1000;
/*  60: 66 */     left.y = 0;
/*  61:    */     
/*  62: 68 */     right.x = 0;
/*  63: 69 */     right.y = 0;
/*  64: 71 */     for (int x = 0; x < petiole.getXDim(); x++) {
/*  65: 72 */       for (int y = 0; y < petiole.getYDim(); y++) {
/*  66: 73 */         if (inputPR.getXYBoolean(x, y))
/*  67:    */         {
/*  68: 75 */           if (y < tip.y)
/*  69:    */           {
/*  70: 76 */             tip.y = y;
/*  71: 77 */             tip.x = x;
/*  72:    */           }
/*  73: 80 */           if (x < left.x)
/*  74:    */           {
/*  75: 81 */             left.y = y;
/*  76: 82 */             left.x = x;
/*  77:    */           }
/*  78: 85 */           if (x > right.x)
/*  79:    */           {
/*  80: 86 */             right.y = y;
/*  81: 87 */             right.x = x;
/*  82:    */           }
/*  83:    */         }
/*  84:    */       }
/*  85:    */     }
/*  86: 93 */     this.output = new double[5];
/*  87:    */     
/*  88: 95 */     this.output[0] = Math.abs(getAngleABC(left, tip, right));
/*  89: 96 */     this.output[1] = Math.abs(getAngleABC(stalk, left, tip));
/*  90: 97 */     this.output[2] = Math.abs(getAngleABC(right, stalk, left));
/*  91: 98 */     this.output[3] = Math.abs(getAngleABC(tip, right, stalk));
/*  92: 99 */     this.output[4] = ((stalk.y - tip.y) / (right.x - left.x));
/*  93:    */   }
/*  94:    */   
/*  95:    */   private double getAngleABC(Point a, Point b, Point c)
/*  96:    */   {
/*  97:103 */     Point ab = new Point(b.x - a.x, b.y - a.y);
/*  98:104 */     Point cb = new Point(b.x - c.x, b.y - c.y);
/*  99:    */     
/* 100:    */ 
/* 101:107 */     double dot = ab.x * cb.x + ab.y * cb.y;
/* 102:    */     
/* 103:    */ 
/* 104:110 */     double abSqr = ab.x * ab.x + ab.y * ab.y;
/* 105:111 */     double cbSqr = cb.x * cb.x + cb.y * cb.y;
/* 106:    */     
/* 107:    */ 
/* 108:114 */     double cosSqr = dot * dot / abSqr / cbSqr;
/* 109:    */     
/* 110:    */ 
/* 111:    */ 
/* 112:118 */     double cos2 = 2.0D * cosSqr - 1.0D;
/* 113:    */     
/* 114:    */ 
/* 115:    */ 
/* 116:    */ 
/* 117:123 */     double alpha2 = 
/* 118:    */     
/* 119:125 */       cos2 >= 1.0D ? 0.0D : cos2 <= -1.0D ? 3.141592653589793D : 
/* 120:126 */       Math.acos(cos2);
/* 121:    */     
/* 122:128 */     double rslt = alpha2 / 2.0D;
/* 123:    */     
/* 124:130 */     double rs = rslt * 180.0D / 3.141592653589793D;
/* 125:140 */     if (dot < 0.0D) {
/* 126:140 */       rs = 180.0D - rs;
/* 127:    */     }
/* 128:143 */     float det = ab.x * cb.y - ab.y * cb.y;
/* 129:144 */     if (det < 0.0F) {
/* 130:144 */       rs = -rs;
/* 131:    */     }
/* 132:146 */     return Math.floor(rs + 0.5D);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public static double[] invoke(Image img)
/* 136:    */   {
/* 137:    */     try
/* 138:    */     {
/* 139:152 */       return (double[])new Angles().preprocess(new Object[] { img });
/* 140:    */     }
/* 141:    */     catch (GlobalException e)
/* 142:    */     {
/* 143:154 */       e.printStackTrace();
/* 144:    */     }
/* 145:155 */     return null;
/* 146:    */   }
/* 147:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.plants.Angles
 * JD-Core Version:    0.7.0.1
 */