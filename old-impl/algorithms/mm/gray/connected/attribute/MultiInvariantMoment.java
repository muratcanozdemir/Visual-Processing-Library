/*   1:    */ package vpt.algorithms.mm.gray.connected.attribute;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.util.Tools;
/*   8:    */ 
/*   9:    */ public class MultiInvariantMoment
/*  10:    */   implements Attribute
/*  11:    */ {
/*  12:    */   private double threshold;
/*  13:    */   private int order;
/*  14:    */   
/*  15:    */   public MultiInvariantMoment(double threshold, int order)
/*  16:    */   {
/*  17: 24 */     this.threshold = threshold;
/*  18: 25 */     this.order = order;
/*  19:    */   }
/*  20:    */   
/*  21:    */   public boolean test(ArrayList<Point> pixels, Image img)
/*  22:    */   {
/*  23: 35 */     double moment = invariantMoment(pixels, img, this.order);
/*  24: 40 */     if (moment < this.threshold) {
/*  25: 40 */       return false;
/*  26:    */     }
/*  27: 41 */     return true;
/*  28:    */   }
/*  29:    */   
/*  30:    */   private double moment(ArrayList<Point> pixels, Image img, int momentX, int momentY)
/*  31:    */   {
/*  32: 46 */     double output = 0.0D;
/*  33: 48 */     for (Point p : pixels)
/*  34:    */     {
/*  35: 49 */       double tmp = Tools.EuclideanNorm(img.getVXYDouble(p.x, p.y));
/*  36: 50 */       output += Math.pow(p.x + 1, momentX) * Math.pow(p.y + 1, momentY) * tmp;
/*  37:    */     }
/*  38: 53 */     return output;
/*  39:    */   }
/*  40:    */   
/*  41:    */   private double centralMoment(ArrayList<Point> pixels, Image img, int momentX, int momentY)
/*  42:    */   {
/*  43: 57 */     double m00 = moment(pixels, img, 0, 0);
/*  44: 58 */     double m01 = moment(pixels, img, 0, 1);
/*  45: 59 */     double m10 = moment(pixels, img, 1, 0);
/*  46:    */     
/*  47: 61 */     double centroidX = m10 / m00;
/*  48: 62 */     double centroidY = m01 / m00;
/*  49:    */     
/*  50: 64 */     double output = 0.0D;
/*  51: 66 */     for (Point p : pixels)
/*  52:    */     {
/*  53: 67 */       double tmp = Tools.EuclideanNorm(img.getVXYDouble(p.x, p.y));
/*  54: 68 */       output += Math.pow(p.x + 1 - centroidX, momentX) * Math.pow(p.y + 1 - centroidY, momentY) * tmp;
/*  55:    */     }
/*  56: 71 */     return output;
/*  57:    */   }
/*  58:    */   
/*  59:    */   private double spatialMoment(ArrayList<Point> pixels, Image img, int momentX, int momentY)
/*  60:    */   {
/*  61: 75 */     if (momentX + momentY < 2) {
/*  62: 76 */       System.err.println("The sum of momentX and momentY must be >= 2 !");
/*  63:    */     }
/*  64: 78 */     double output = 0.0D;
/*  65:    */     
/*  66: 80 */     double muXY = centralMoment(pixels, img, momentX, momentY);
/*  67: 81 */     double m00 = moment(pixels, img, 0, 0);
/*  68: 82 */     double alpha = (momentX + momentY) / 2.0D + 1.0D;
/*  69:    */     
/*  70: 84 */     m00 = Math.pow(m00, alpha);
/*  71: 85 */     output = muXY / m00;
/*  72:    */     
/*  73: 87 */     return output;
/*  74:    */   }
/*  75:    */   
/*  76:    */   private double invariantMoment(ArrayList<Point> pixels, Image img, int order)
/*  77:    */   {
/*  78: 91 */     if ((order < 1) || (order > 8)) {
/*  79: 91 */       System.err.println("The order must be \\in [1,8] !");
/*  80:    */     }
/*  81: 93 */     double output = 0.0D;
/*  82: 95 */     switch (order)
/*  83:    */     {
/*  84:    */     case 1: 
/*  85: 97 */       double n20 = spatialMoment(pixels, img, 2, 0);
/*  86: 98 */       double n02 = spatialMoment(pixels, img, 0, 2);
/*  87:    */       
/*  88:100 */       output = n20 + n02;
/*  89:    */       
/*  90:102 */       break;
/*  91:    */     case 2: 
/*  92:104 */       double n20 = spatialMoment(pixels, img, 2, 0);
/*  93:105 */       double n02 = spatialMoment(pixels, img, 0, 2);
/*  94:106 */       double n11 = spatialMoment(pixels, img, 1, 1);
/*  95:    */       
/*  96:108 */       output = (n20 - n02) * (n20 - n02) + 4.0D * n11 * n11;
/*  97:109 */       break;
/*  98:    */     case 3: 
/*  99:111 */       double n30 = spatialMoment(pixels, img, 3, 0);
/* 100:112 */       double n03 = spatialMoment(pixels, img, 0, 3);
/* 101:113 */       double n21 = spatialMoment(pixels, img, 2, 1);
/* 102:114 */       double n12 = spatialMoment(pixels, img, 1, 2);
/* 103:    */       
/* 104:116 */       output = (n30 - 3.0D * n12) * (n30 - 3.0D * n12) + (3.0D * n21 - n03) * (3.0D * n21 - n03);
/* 105:    */       
/* 106:118 */       break;
/* 107:    */     case 4: 
/* 108:120 */       double n30 = spatialMoment(pixels, img, 3, 0);
/* 109:121 */       double n03 = spatialMoment(pixels, img, 0, 3);
/* 110:122 */       double n21 = spatialMoment(pixels, img, 2, 1);
/* 111:123 */       double n12 = spatialMoment(pixels, img, 1, 2);
/* 112:    */       
/* 113:125 */       output = (n30 + n12) * (n30 + n12) + (n03 + n21) * (n03 + n21);
/* 114:    */       
/* 115:127 */       break;
/* 116:    */     case 5: 
/* 117:129 */       double n30 = spatialMoment(pixels, img, 3, 0);
/* 118:130 */       double n03 = spatialMoment(pixels, img, 0, 3);
/* 119:131 */       double n21 = spatialMoment(pixels, img, 2, 1);
/* 120:132 */       double n12 = spatialMoment(pixels, img, 1, 2);
/* 121:    */       
/* 122:134 */       output = (n30 - 3.0D * n12) * (n30 + n12) * ((n30 + n12) * (n30 + n12) - 3.0D * (n21 + n03) * (n21 + n03)) + 
/* 123:135 */         (3.0D * n21 - n03) * (n21 + n03) * (3.0D * (n30 + n12) * (n30 + n12) - (n21 - n03) * (n21 - n03));
/* 124:136 */       break;
/* 125:    */     case 6: 
/* 126:138 */       double n30 = spatialMoment(pixels, img, 3, 0);
/* 127:139 */       double n03 = spatialMoment(pixels, img, 0, 3);
/* 128:140 */       double n21 = spatialMoment(pixels, img, 2, 1);
/* 129:141 */       double n12 = spatialMoment(pixels, img, 1, 2);
/* 130:142 */       double n20 = spatialMoment(pixels, img, 2, 0);
/* 131:143 */       double n02 = spatialMoment(pixels, img, 0, 2);
/* 132:144 */       double n11 = spatialMoment(pixels, img, 1, 1);
/* 133:    */       
/* 134:146 */       output = (n20 - n02) * ((n30 + n12) * (n30 + n12) - (n21 + n03) * (n21 + n03)) + 4.0D * n11 * (n30 + n12) * (n21 + n03);
/* 135:    */       
/* 136:148 */       break;
/* 137:    */     case 7: 
/* 138:150 */       double n30 = spatialMoment(pixels, img, 3, 0);
/* 139:151 */       double n03 = spatialMoment(pixels, img, 0, 3);
/* 140:152 */       double n21 = spatialMoment(pixels, img, 2, 1);
/* 141:153 */       double n12 = spatialMoment(pixels, img, 1, 2);
/* 142:    */       
/* 143:155 */       output = (3.0D * n21 - n03) * (n30 + n12) * ((n30 + n12) * (n30 + n12) - 3.0D * (n21 + n03) * (n21 + n03)) - 
/* 144:156 */         (n03 - 3.0D * n12) * (n21 + n03) * (3.0D * (n30 + n12) * (n30 + n12) - (n21 + n03) * (n21 + n03));
/* 145:157 */       break;
/* 146:    */     case 8: 
/* 147:159 */       double n30 = spatialMoment(pixels, img, 3, 0);
/* 148:160 */       double n03 = spatialMoment(pixels, img, 0, 3);
/* 149:161 */       double n21 = spatialMoment(pixels, img, 2, 1);
/* 150:162 */       double n12 = spatialMoment(pixels, img, 1, 2);
/* 151:163 */       double n20 = spatialMoment(pixels, img, 2, 0);
/* 152:164 */       double n02 = spatialMoment(pixels, img, 0, 2);
/* 153:165 */       double n11 = spatialMoment(pixels, img, 1, 1);
/* 154:    */       
/* 155:167 */       output = n11 * ((n30 + n12) * (n30 + n12) - (n03 + n21) * (n03 + n21)) - (n20 - n02) * (n30 + n12) * (n03 + n21);
/* 156:    */       
/* 157:169 */       break;
/* 158:    */     default: 
/* 159:171 */       System.err.println("Never supposed to be here!");
/* 160:    */     }
/* 161:174 */     return output;
/* 162:    */   }
/* 163:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.MultiInvariantMoment
 * JD-Core Version:    0.7.0.1
 */