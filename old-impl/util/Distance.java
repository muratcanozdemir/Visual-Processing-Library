/*   1:    */ package vpt.util;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ 
/*   6:    */ public class Distance
/*   7:    */ {
/*   8:    */   public static double euclidean(double[] p, double[] q)
/*   9:    */   {
/*  10: 20 */     double distance = 0.0D;
/*  11: 22 */     if (p.length != q.length)
/*  12:    */     {
/*  13: 23 */       System.err.println("Distance: Incompatible vector lengths");
/*  14: 24 */       return -1.0D;
/*  15:    */     }
/*  16: 27 */     for (int i = 0; i < p.length; i++) {
/*  17: 28 */       distance += (p[i] - q[i]) * (p[i] - q[i]);
/*  18:    */     }
/*  19: 30 */     return Math.sqrt(distance);
/*  20:    */   }
/*  21:    */   
/*  22:    */   public static double euclidean(int[] p, int[] q)
/*  23:    */   {
/*  24: 34 */     double distance = 0.0D;
/*  25: 36 */     if (p.length != q.length)
/*  26:    */     {
/*  27: 37 */       System.err.println("Distance: Incompatible vector lengths");
/*  28: 38 */       return -1.0D;
/*  29:    */     }
/*  30: 41 */     for (int i = 0; i < p.length; i++) {
/*  31: 42 */       distance += (p[i] - q[i]) * (p[i] - q[i]);
/*  32:    */     }
/*  33: 44 */     return Math.sqrt(distance);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public static double EuclideanDistance(int x1, int y1, int x2, int y2)
/*  37:    */   {
/*  38: 48 */     return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static double EuclideanDistance(double x1, double y1, double x2, double y2)
/*  42:    */   {
/*  43: 52 */     return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static double EuclideanDistance(Point p1, Point p2)
/*  47:    */   {
/*  48: 56 */     return EuclideanDistance(p1.x, p1.y, p2.x, p2.y);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static double intersection(double[] p, double[] q)
/*  52:    */   {
/*  53: 61 */     double distance = 0.0D;
/*  54: 63 */     if (p.length != q.length)
/*  55:    */     {
/*  56: 64 */       System.err.println("Distance: Incompatible vector lengths");
/*  57: 65 */       return -1.0D;
/*  58:    */     }
/*  59: 68 */     for (int i = 0; i < p.length; i++) {
/*  60: 69 */       distance += Math.min(p[i], q[i]);
/*  61:    */     }
/*  62: 71 */     return 1.0D - distance;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public static double bhattacharyya(double[] p, double[] q)
/*  66:    */   {
/*  67: 76 */     double distance = 0.0D;
/*  68: 78 */     if (p.length != q.length)
/*  69:    */     {
/*  70: 79 */       System.err.println("Distance: Incompatible vector lengths");
/*  71: 80 */       return -1.0D;
/*  72:    */     }
/*  73: 84 */     double sumP = 0.0D;
/*  74: 85 */     for (int i = 0; i < p.length; i++) {
/*  75: 86 */       sumP += p[i];
/*  76:    */     }
/*  77: 89 */     double sumQ = 0.0D;
/*  78: 90 */     for (int i = 0; i < p.length; i++) {
/*  79: 91 */       sumQ += q[i];
/*  80:    */     }
/*  81: 93 */     double temp = Math.sqrt(sumP * sumQ);
/*  82: 95 */     for (int i = 0; i < p.length; i++) {
/*  83: 96 */       distance += Math.sqrt(p[i] * q[i]) / temp;
/*  84:    */     }
/*  85: 98 */     return Math.sqrt(1.0D - distance);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public static double cosine(double[] p, double[] q)
/*  89:    */   {
/*  90:102 */     double distance = 0.0D;
/*  91:104 */     if (p.length != q.length)
/*  92:    */     {
/*  93:105 */       System.err.println("Distance: Incompatible vector lengths");
/*  94:106 */       return -1.0D;
/*  95:    */     }
/*  96:110 */     double sumP = 0.0D;
/*  97:111 */     for (int i = 0; i < p.length; i++) {
/*  98:112 */       sumP += p[i] * p[i];
/*  99:    */     }
/* 100:115 */     double sumQ = 0.0D;
/* 101:116 */     for (int i = 0; i < p.length; i++) {
/* 102:117 */       sumQ += q[i] * q[i];
/* 103:    */     }
/* 104:119 */     double temp = Math.sqrt(sumP * sumQ);
/* 105:121 */     for (int i = 0; i < p.length; i++) {
/* 106:122 */       distance += p[i] * q[i];
/* 107:    */     }
/* 108:124 */     return distance / temp;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public static double spectralAngleDistance(double[] p, double[] q)
/* 112:    */   {
/* 113:128 */     if (p.length != q.length)
/* 114:    */     {
/* 115:129 */       System.err.println("Distance: Incompatible vector lengths");
/* 116:130 */       return -1.0D;
/* 117:    */     }
/* 118:133 */     double dotProduct = Tools.dotProduct(p, q);
/* 119:134 */     double norm1 = Tools.norm(p);
/* 120:135 */     double norm2 = Tools.norm(q);
/* 121:    */     
/* 122:137 */     return Math.acos(dotProduct / (norm1 * norm2));
/* 123:    */   }
/* 124:    */   
/* 125:    */   public static double innerProduct(double[] p, double[] q)
/* 126:    */   {
/* 127:141 */     double distance = 0.0D;
/* 128:143 */     if (p.length != q.length)
/* 129:    */     {
/* 130:144 */       System.err.println("Distance: Incompatible vector lengths");
/* 131:145 */       return -1.0D;
/* 132:    */     }
/* 133:148 */     for (int i = 0; i < p.length; i++) {
/* 134:149 */       distance += p[i] * q[i];
/* 135:    */     }
/* 136:151 */     return distance;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public static double manhattan(double[] p, double[] q)
/* 140:    */   {
/* 141:155 */     double distance = 0.0D;
/* 142:157 */     if (p.length != q.length)
/* 143:    */     {
/* 144:158 */       System.err.println("Distance: Incompatible vector lengths");
/* 145:159 */       return -1.0D;
/* 146:    */     }
/* 147:162 */     for (int i = 0; i < p.length; i++) {
/* 148:163 */       distance += Math.abs(p[i] - q[i]);
/* 149:    */     }
/* 150:165 */     return distance;
/* 151:    */   }
/* 152:    */   
/* 153:    */   public static double chebyshev(double[] p, double[] q)
/* 154:    */   {
/* 155:170 */     double distance = 0.0D;
/* 156:172 */     if (p.length != q.length)
/* 157:    */     {
/* 158:173 */       System.err.println("Distance: Incompatible vector lengths");
/* 159:174 */       return -1.0D;
/* 160:    */     }
/* 161:177 */     distance = Math.abs(p[0] - q[0]);
/* 162:179 */     for (int i = 1; i < p.length; i++)
/* 163:    */     {
/* 164:180 */       double tmp = Math.abs(p[i] - q[i]);
/* 165:181 */       if (tmp > distance) {
/* 166:181 */         distance = tmp;
/* 167:    */       }
/* 168:    */     }
/* 169:184 */     return distance;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public static double chiSquare(double[] p, double[] q)
/* 173:    */   {
/* 174:188 */     double distance = 0.0D;
/* 175:190 */     if (p.length != q.length)
/* 176:    */     {
/* 177:191 */       System.err.println("Distance: Incompatible vector lengths");
/* 178:192 */       return -1.0D;
/* 179:    */     }
/* 180:195 */     for (int i = 0; i < p.length; i++) {
/* 181:196 */       if ((p[i] > 0.0D) || (q[i] > 0.0D)) {
/* 182:197 */         distance += (p[i] - q[i]) * (p[i] - q[i]) / (p[i] + q[i]);
/* 183:    */       }
/* 184:    */     }
/* 185:200 */     return distance;
/* 186:    */   }
/* 187:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.Distance
 * JD-Core Version:    0.7.0.1
 */