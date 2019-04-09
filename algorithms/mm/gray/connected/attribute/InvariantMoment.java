/*   1:    */ package vpt.algorithms.mm.gray.connected.attribute;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import vpt.Image;
/*   7:    */ 
/*   8:    */ public class InvariantMoment
/*   9:    */   implements Attribute
/*  10:    */ {
/*  11:    */   private double threshold;
/*  12:    */   private int order;
/*  13:    */   
/*  14:    */   public InvariantMoment(double threshold, int order)
/*  15:    */   {
/*  16: 23 */     this.threshold = threshold;
/*  17: 24 */     this.order = order;
/*  18:    */   }
/*  19:    */   
/*  20:    */   public boolean test(ArrayList<Point> pixels, Image img)
/*  21:    */   {
/*  22: 34 */     double moment = invariantMoment(pixels, img, this.order);
/*  23: 38 */     if (moment < this.threshold) {
/*  24: 38 */       return false;
/*  25:    */     }
/*  26: 39 */     return true;
/*  27:    */   }
/*  28:    */   
/*  29:    */   private double moment(ArrayList<Point> pixels, Image img, int momentX, int momentY)
/*  30:    */   {
/*  31: 44 */     double output = 0.0D;
/*  32: 46 */     for (Point p : pixels)
/*  33:    */     {
/*  34: 47 */       double tmp = img.getXYDouble(p.x, p.y);
/*  35: 48 */       output += Math.pow(p.x + 1, momentX) * Math.pow(p.y + 1, momentY) * tmp;
/*  36:    */     }
/*  37: 51 */     return output;
/*  38:    */   }
/*  39:    */   
/*  40:    */   private double centralMoment(ArrayList<Point> pixels, Image img, int momentX, int momentY)
/*  41:    */   {
/*  42: 55 */     double m00 = moment(pixels, img, 0, 0);
/*  43: 56 */     double m01 = moment(pixels, img, 0, 1);
/*  44: 57 */     double m10 = moment(pixels, img, 1, 0);
/*  45:    */     
/*  46: 59 */     double centroidX = m10 / m00;
/*  47: 60 */     double centroidY = m01 / m00;
/*  48:    */     
/*  49: 62 */     double output = 0.0D;
/*  50: 64 */     for (Point p : pixels)
/*  51:    */     {
/*  52: 65 */       double tmp = img.getXYDouble(p.x, p.y);
/*  53: 66 */       output += Math.pow(p.x + 1 - centroidX, momentX) * Math.pow(p.y + 1 - centroidY, momentY) * tmp;
/*  54:    */     }
/*  55: 69 */     return output;
/*  56:    */   }
/*  57:    */   
/*  58:    */   private double spatialMoment(ArrayList<Point> pixels, Image img, int momentX, int momentY)
/*  59:    */   {
/*  60: 73 */     if (momentX + momentY < 2) {
/*  61: 74 */       System.err.println("The sum of momentX and momentY must be >= 2 !");
/*  62:    */     }
/*  63: 76 */     double output = 0.0D;
/*  64:    */     
/*  65: 78 */     double muXY = centralMoment(pixels, img, momentX, momentY);
/*  66: 79 */     double m00 = moment(pixels, img, 0, 0);
/*  67: 80 */     double alpha = (momentX + momentY) / 2.0D + 1.0D;
/*  68:    */     
/*  69: 82 */     m00 = Math.pow(m00, alpha);
/*  70: 83 */     output = muXY / m00;
/*  71:    */     
/*  72: 85 */     return output;
/*  73:    */   }
/*  74:    */   
/*  75:    */   private double invariantMoment(ArrayList<Point> pixels, Image img, int order)
/*  76:    */   {
/*  77: 89 */     if ((order < 1) || (order > 8)) {
/*  78: 89 */       System.err.println("The order must be \\in [1,8] !");
/*  79:    */     }
/*  80: 91 */     double output = 0.0D;
/*  81: 93 */     switch (order)
/*  82:    */     {
/*  83:    */     case 1: 
/*  84: 95 */       double n20 = spatialMoment(pixels, img, 2, 0);
/*  85: 96 */       double n02 = spatialMoment(pixels, img, 0, 2);
/*  86:    */       
/*  87: 98 */       output = n20 + n02;
/*  88:    */       
/*  89:100 */       break;
/*  90:    */     case 2: 
/*  91:102 */       double n20 = spatialMoment(pixels, img, 2, 0);
/*  92:103 */       double n02 = spatialMoment(pixels, img, 0, 2);
/*  93:104 */       double n11 = spatialMoment(pixels, img, 1, 1);
/*  94:    */       
/*  95:106 */       output = (n20 - n02) * (n20 - n02) + 4.0D * n11 * n11;
/*  96:107 */       break;
/*  97:    */     case 3: 
/*  98:109 */       double n30 = spatialMoment(pixels, img, 3, 0);
/*  99:110 */       double n03 = spatialMoment(pixels, img, 0, 3);
/* 100:111 */       double n21 = spatialMoment(pixels, img, 2, 1);
/* 101:112 */       double n12 = spatialMoment(pixels, img, 1, 2);
/* 102:    */       
/* 103:114 */       output = (n30 - 3.0D * n12) * (n30 - 3.0D * n12) + (3.0D * n21 - n03) * (3.0D * n21 - n03);
/* 104:    */       
/* 105:116 */       break;
/* 106:    */     case 4: 
/* 107:118 */       double n30 = spatialMoment(pixels, img, 3, 0);
/* 108:119 */       double n03 = spatialMoment(pixels, img, 0, 3);
/* 109:120 */       double n21 = spatialMoment(pixels, img, 2, 1);
/* 110:121 */       double n12 = spatialMoment(pixels, img, 1, 2);
/* 111:    */       
/* 112:123 */       output = (n30 + n12) * (n30 + n12) + (n03 + n21) * (n03 + n21);
/* 113:    */       
/* 114:125 */       break;
/* 115:    */     case 5: 
/* 116:127 */       double n30 = spatialMoment(pixels, img, 3, 0);
/* 117:128 */       double n03 = spatialMoment(pixels, img, 0, 3);
/* 118:129 */       double n21 = spatialMoment(pixels, img, 2, 1);
/* 119:130 */       double n12 = spatialMoment(pixels, img, 1, 2);
/* 120:    */       
/* 121:132 */       output = (n30 - 3.0D * n12) * (n30 + n12) * ((n30 + n12) * (n30 + n12) - 3.0D * (n21 + n03) * (n21 + n03)) + 
/* 122:133 */         (3.0D * n21 - n03) * (n21 + n03) * (3.0D * (n30 + n12) * (n30 + n12) - (n21 - n03) * (n21 - n03));
/* 123:134 */       break;
/* 124:    */     case 6: 
/* 125:136 */       double n30 = spatialMoment(pixels, img, 3, 0);
/* 126:137 */       double n03 = spatialMoment(pixels, img, 0, 3);
/* 127:138 */       double n21 = spatialMoment(pixels, img, 2, 1);
/* 128:139 */       double n12 = spatialMoment(pixels, img, 1, 2);
/* 129:140 */       double n20 = spatialMoment(pixels, img, 2, 0);
/* 130:141 */       double n02 = spatialMoment(pixels, img, 0, 2);
/* 131:142 */       double n11 = spatialMoment(pixels, img, 1, 1);
/* 132:    */       
/* 133:144 */       output = (n20 - n02) * ((n30 + n12) * (n30 + n12) - (n21 + n03) * (n21 + n03)) + 4.0D * n11 * (n30 + n12) * (n21 + n03);
/* 134:    */       
/* 135:146 */       break;
/* 136:    */     case 7: 
/* 137:148 */       double n30 = spatialMoment(pixels, img, 3, 0);
/* 138:149 */       double n03 = spatialMoment(pixels, img, 0, 3);
/* 139:150 */       double n21 = spatialMoment(pixels, img, 2, 1);
/* 140:151 */       double n12 = spatialMoment(pixels, img, 1, 2);
/* 141:    */       
/* 142:153 */       output = (3.0D * n21 - n03) * (n30 + n12) * ((n30 + n12) * (n30 + n12) - 3.0D * (n21 + n03) * (n21 + n03)) - 
/* 143:154 */         (n03 - 3.0D * n12) * (n21 + n03) * (3.0D * (n30 + n12) * (n30 + n12) - (n21 + n03) * (n21 + n03));
/* 144:155 */       break;
/* 145:    */     case 8: 
/* 146:157 */       double n30 = spatialMoment(pixels, img, 3, 0);
/* 147:158 */       double n03 = spatialMoment(pixels, img, 0, 3);
/* 148:159 */       double n21 = spatialMoment(pixels, img, 2, 1);
/* 149:160 */       double n12 = spatialMoment(pixels, img, 1, 2);
/* 150:161 */       double n20 = spatialMoment(pixels, img, 2, 0);
/* 151:162 */       double n02 = spatialMoment(pixels, img, 0, 2);
/* 152:163 */       double n11 = spatialMoment(pixels, img, 1, 1);
/* 153:    */       
/* 154:165 */       output = n11 * ((n30 + n12) * (n30 + n12) - (n03 + n21) * (n03 + n21)) - (n20 - n02) * (n30 + n12) * (n03 + n21);
/* 155:    */       
/* 156:167 */       break;
/* 157:    */     default: 
/* 158:169 */       System.err.println("Never supposed to be here!");
/* 159:    */     }
/* 160:172 */     return output;
/* 161:    */   }
/* 162:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.InvariantMoment
 * JD-Core Version:    0.7.0.1
 */