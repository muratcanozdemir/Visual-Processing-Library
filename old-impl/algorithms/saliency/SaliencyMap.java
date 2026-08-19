/*   1:    */ package vpt.algorithms.saliency;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.ByteImage;
/*   6:    */ import vpt.DoubleImage;
/*   7:    */ import vpt.GlobalException;
/*   8:    */ import vpt.Image;
/*   9:    */ import vpt.algorithms.conversion.RGB2XYZ;
/*  10:    */ import vpt.algorithms.conversion.XYZ2LAB;
/*  11:    */ import vpt.algorithms.flatzones.color.ColorQFZSoille;
/*  12:    */ import vpt.util.Distance;
/*  13:    */ 
/*  14:    */ public class SaliencyMap
/*  15:    */   extends Algorithm
/*  16:    */ {
/*  17: 30 */   public Image output = null;
/*  18: 31 */   public Image input = null;
/*  19:    */   
/*  20:    */   public SaliencyMap()
/*  21:    */   {
/*  22: 34 */     this.inputFields = "input";
/*  23: 35 */     this.outputFields = "output";
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void execute()
/*  27:    */     throws GlobalException
/*  28:    */   {
/*  29: 39 */     int cdim = this.input.getCDim();
/*  30: 40 */     int xdim = this.input.getXDim();
/*  31: 41 */     int ydim = this.input.getYDim();
/*  32: 43 */     if (cdim != 3) {
/*  33: 43 */       throw new GlobalException("Sorry, only color images are accepted !");
/*  34:    */     }
/*  35: 45 */     int minDim = Math.min(xdim, ydim);
/*  36:    */     
/*  37:    */ 
/*  38: 48 */     int[] R2 = { minDim / 8, minDim / 4, minDim / 2 };
/*  39:    */     
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43: 53 */     Image inputOriginal = this.input.newInstance(true);
/*  44:    */     
/*  45: 55 */     this.input = RGB2XYZ.invoke(this.input);
/*  46: 56 */     this.input = XYZ2LAB.invoke(this.input);
/*  47: 57 */     this.input = XYZ2LAB.scaleToByte(this.input);
/*  48:    */     
/*  49:    */ 
/*  50: 60 */     Image[] scales = new ByteImage[R2.length];
/*  51:    */     
/*  52:    */ 
/*  53:    */ 
/*  54: 64 */     Image ii = new DoubleImage(xdim, ydim, 3);
/*  55: 65 */     ii.fill(0);
/*  56:    */     
/*  57: 67 */     Image s = new DoubleImage(xdim, ydim, 3);
/*  58: 68 */     s.fill(0);
/*  59:    */     
/*  60: 70 */     double[] SXY1 = null;
/*  61: 71 */     double[] IIXY1 = null;
/*  62: 72 */     double[] zero = { 0.0D, 0.0D, 0.0D };
/*  63: 74 */     for (int x = 0; x < xdim; x++) {
/*  64: 75 */       for (int y = 0; y < ydim; y++)
/*  65:    */       {
/*  66: 77 */         if (y - 1 < 0)
/*  67:    */         {
/*  68: 77 */           SXY1 = this.input.getVXYDouble(x, y);
/*  69:    */         }
/*  70:    */         else
/*  71:    */         {
/*  72: 79 */           SXY1[0] = (s.getXYCDouble(x, y - 1, 0) + this.input.getXYCDouble(x, y, 0));
/*  73: 80 */           SXY1[1] = (s.getXYCDouble(x, y - 1, 1) + this.input.getXYCDouble(x, y, 1));
/*  74: 81 */           SXY1[2] = (s.getXYCDouble(x, y - 1, 2) + this.input.getXYCDouble(x, y, 2));
/*  75:    */         }
/*  76: 85 */         s.setVXYDouble(x, y, SXY1);
/*  77: 87 */         if (x - 1 < 0)
/*  78:    */         {
/*  79: 87 */           IIXY1 = s.getVXYDouble(x, y);
/*  80:    */         }
/*  81:    */         else
/*  82:    */         {
/*  83: 89 */           IIXY1[0] = (ii.getXYCDouble(x - 1, y, 0) + s.getXYCDouble(x, y, 0));
/*  84: 90 */           IIXY1[1] = (ii.getXYCDouble(x - 1, y, 1) + s.getXYCDouble(x, y, 1));
/*  85: 91 */           IIXY1[2] = (ii.getXYCDouble(x - 1, y, 2) + s.getXYCDouble(x, y, 2));
/*  86:    */         }
/*  87: 95 */         ii.setVXYDouble(x, y, IIXY1);
/*  88:    */       }
/*  89:    */     }
/*  90:101 */     double[] four = new double[3];
/*  91:102 */     double[] three = new double[3];
/*  92:103 */     double[] two = new double[3];
/*  93:104 */     double[] one = new double[3];
/*  94:105 */     double[] avg = new double[3];
/*  95:108 */     for (int i = 0; i < R2.length; i++)
/*  96:    */     {
/*  97:110 */       scales[i] = new ByteImage(xdim, ydim, 1);
/*  98:113 */       for (int x = 0; x < xdim; x++) {
/*  99:114 */         for (int y = 0; y < ydim; y++)
/* 100:    */         {
/* 101:117 */           int xMinus = Math.max(0, x - R2[i] / 2);
/* 102:118 */           int yMinus = Math.max(0, y - R2[i] / 2);
/* 103:119 */           int xPlus = Math.min(xdim - 1, x + R2[i] / 2);
/* 104:120 */           int yPlus = Math.min(ydim - 1, y + R2[i] / 2);
/* 105:    */           
/* 106:122 */           four = ii.getVXYDouble(xPlus, yPlus);
/* 107:123 */           three = ii.getVXYDouble(xMinus, yPlus);
/* 108:124 */           two = ii.getVXYDouble(xPlus, yMinus);
/* 109:125 */           one = ii.getVXYDouble(xMinus, yMinus);
/* 110:    */           
/* 111:127 */           avg[0] = (four[0] + one[0] - (two[0] + three[0]));
/* 112:128 */           avg[1] = (four[1] + one[1] - (two[1] + three[1]));
/* 113:129 */           avg[2] = (four[2] + one[2] - (two[2] + three[2]));
/* 114:    */           
/* 115:131 */           avg[0] /= (xPlus - xMinus) * (yPlus - yMinus);
/* 116:132 */           avg[1] /= (xPlus - xMinus) * (yPlus - yMinus);
/* 117:133 */           avg[2] /= (xPlus - xMinus) * (yPlus - yMinus);
/* 118:    */           
/* 119:    */ 
/* 120:136 */           double dist = Distance.euclidean(avg, this.input.getVXYDouble(x, y)) / Math.sqrt(3.0D);
/* 121:137 */           scales[i].setXYByte(x, y, (int)Math.round(dist * 255.0D));
/* 122:    */         }
/* 123:    */       }
/* 124:    */     }
/* 125:143 */     Image saliencyMap = new ByteImage(xdim, ydim, 1);
/* 126:145 */     for (int x = 0; x < xdim; x++) {
/* 127:146 */       for (int y = 0; y < ydim; y++)
/* 128:    */       {
/* 129:148 */         int sum = 0;
/* 130:150 */         for (int i = 0; i < R2.length; i++) {
/* 131:151 */           sum += scales[i].getXYByte(x, y);
/* 132:    */         }
/* 133:153 */         sum /= R2.length;
/* 134:    */         
/* 135:155 */         saliencyMap.setXYByte(x, y, sum);
/* 136:    */       }
/* 137:    */     }
/* 138:160 */     Image labelImage = ColorQFZSoille.invoke(inputOriginal, 50, 50);
/* 139:    */     
/* 140:    */ 
/* 141:163 */     Point p = labelImage.getXYCMaximum(0);
/* 142:164 */     int[][] labels = new int[labelImage.getXYInt(p.x, p.y) + 1][2];
/* 143:167 */     for (int x = 0; x < xdim; x++) {
/* 144:168 */       for (int y = 0; y < ydim; y++)
/* 145:    */       {
/* 146:169 */         int label = labelImage.getXYInt(x, y);
/* 147:    */         
/* 148:    */ 
/* 149:    */ 
/* 150:    */ 
/* 151:174 */         labels[label][0] += 1;
/* 152:175 */         labels[label][1] += saliencyMap.getXYByte(x, y);
/* 153:    */       }
/* 154:    */     }
/* 155:181 */     for (int i = 1; i < labels.length; i++) {
/* 156:182 */       labels[i][1] /= labels[i][0];
/* 157:    */     }
/* 158:185 */     this.output = new ByteImage(xdim, ydim, 1);
/* 159:187 */     for (int x = 0; x < xdim; x++) {
/* 160:188 */       for (int y = 0; y < ydim; y++)
/* 161:    */       {
/* 162:189 */         int label = labelImage.getXYInt(x, y);
/* 163:190 */         this.output.setXYByte(x, y, labels[label][1]);
/* 164:    */       }
/* 165:    */     }
/* 166:    */   }
/* 167:    */   
/* 168:    */   public static Image invoke(Image img)
/* 169:    */   {
/* 170:    */     try
/* 171:    */     {
/* 172:198 */       return (Image)new SaliencyMap().preprocess(new Object[] { img });
/* 173:    */     }
/* 174:    */     catch (GlobalException e)
/* 175:    */     {
/* 176:200 */       e.printStackTrace();
/* 177:    */     }
/* 178:201 */     return null;
/* 179:    */   }
/* 180:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.saliency.SaliencyMap
 * JD-Core Version:    0.7.0.1
 */