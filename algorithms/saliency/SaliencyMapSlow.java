/*   1:    */ package vpt.algorithms.saliency;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.ByteImage;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.conversion.RGB2XYZ;
/*   9:    */ import vpt.algorithms.conversion.XYZ2LAB;
/*  10:    */ import vpt.algorithms.flatzones.color.ColorQFZSoille;
/*  11:    */ import vpt.util.Distance;
/*  12:    */ 
/*  13:    */ public class SaliencyMapSlow
/*  14:    */   extends Algorithm
/*  15:    */ {
/*  16: 27 */   public Image output = null;
/*  17: 28 */   public Image input = null;
/*  18:    */   
/*  19:    */   public SaliencyMapSlow()
/*  20:    */   {
/*  21: 31 */     this.inputFields = "input";
/*  22: 32 */     this.outputFields = "output";
/*  23:    */   }
/*  24:    */   
/*  25:    */   public void execute()
/*  26:    */     throws GlobalException
/*  27:    */   {
/*  28: 36 */     int cdim = this.input.getCDim();
/*  29: 37 */     int xdim = this.input.getXDim();
/*  30: 38 */     int ydim = this.input.getYDim();
/*  31: 40 */     if (cdim != 3) {
/*  32: 40 */       throw new GlobalException("Sorry, only color images are accepted !");
/*  33:    */     }
/*  34: 42 */     int minDim = Math.min(xdim, ydim);
/*  35:    */     
/*  36:    */ 
/*  37: 45 */     int[] R2 = { minDim / 8, minDim / 4, minDim / 2 };
/*  38:    */     
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42: 50 */     Image inputOriginal = this.input.newInstance(true);
/*  43:    */     
/*  44: 52 */     this.input = RGB2XYZ.invoke(this.input);
/*  45: 53 */     this.input = XYZ2LAB.invoke(this.input);
/*  46: 54 */     this.input = XYZ2LAB.scaleToByte(this.input);
/*  47:    */     
/*  48:    */ 
/*  49: 57 */     Image[] scales = new ByteImage[R2.length];
/*  50: 60 */     for (int i = 0; i < R2.length; i++)
/*  51:    */     {
/*  52: 62 */       scales[i] = new ByteImage(xdim, ydim, 1);
/*  53: 65 */       for (int x = 0; x < xdim; x++) {
/*  54: 66 */         for (int y = 0; y < ydim; y++)
/*  55:    */         {
/*  56: 68 */           double[] sum = new double[3];
/*  57: 69 */           int counter = 0;
/*  58: 72 */           for (int _x = -1 * R2[i] / 2; _x <= R2[i] / 2; _x++) {
/*  59: 73 */             for (int _y = -1 * R2[i] / 2; _y <= R2[i] / 2; _y++) {
/*  60: 75 */               if ((x + _x >= 0) && (x + _x <= xdim - 1) && (y + _y >= 0) && (y + _y <= ydim - 1))
/*  61:    */               {
/*  62: 77 */                 sum[0] += this.input.getXYCDouble(x + _x, y + _y, 0);
/*  63: 78 */                 sum[1] += this.input.getXYCDouble(x + _x, y + _y, 1);
/*  64: 79 */                 sum[2] += this.input.getXYCDouble(x + _x, y + _y, 2);
/*  65: 80 */                 counter++;
/*  66:    */               }
/*  67:    */             }
/*  68:    */           }
/*  69: 84 */           sum[0] /= counter;
/*  70: 85 */           sum[1] /= counter;
/*  71: 86 */           sum[2] /= counter;
/*  72:    */           
/*  73:    */ 
/*  74: 89 */           double dist = Distance.euclidean(sum, this.input.getVXYDouble(x, y)) / Math.sqrt(3.0D);
/*  75: 90 */           scales[i].setXYByte(x, y, (int)Math.round(dist * 255.0D));
/*  76:    */         }
/*  77:    */       }
/*  78:    */     }
/*  79: 96 */     Image saliencyMap = new ByteImage(xdim, ydim, 1);
/*  80: 97 */     for (int x = 0; x < xdim; x++) {
/*  81: 98 */       for (int y = 0; y < ydim; y++)
/*  82:    */       {
/*  83:100 */         int sum = 0;
/*  84:102 */         for (int i = 0; i < R2.length; i++) {
/*  85:103 */           sum += scales[i].getXYByte(x, y);
/*  86:    */         }
/*  87:105 */         sum /= R2.length;
/*  88:    */         
/*  89:107 */         saliencyMap.setXYByte(x, y, sum);
/*  90:    */       }
/*  91:    */     }
/*  92:112 */     Image labelImage = ColorQFZSoille.invoke(inputOriginal, 25, 25);
/*  93:    */     
/*  94:    */ 
/*  95:115 */     Point p = labelImage.getXYCMaximum(0);
/*  96:116 */     int[][] labels = new int[labelImage.getXYInt(p.x, p.y) + 1][2];
/*  97:119 */     for (int x = 0; x < xdim; x++) {
/*  98:120 */       for (int y = 0; y < ydim; y++)
/*  99:    */       {
/* 100:121 */         int label = labelImage.getXYInt(x, y);
/* 101:    */         
/* 102:    */ 
/* 103:    */ 
/* 104:    */ 
/* 105:126 */         labels[label][0] += 1;
/* 106:127 */         labels[label][1] += saliencyMap.getXYByte(x, y);
/* 107:    */       }
/* 108:    */     }
/* 109:133 */     for (int i = 1; i < labels.length; i++) {
/* 110:134 */       labels[i][1] /= labels[i][0];
/* 111:    */     }
/* 112:137 */     this.output = new ByteImage(xdim, ydim, 1);
/* 113:139 */     for (int x = 0; x < xdim; x++) {
/* 114:140 */       for (int y = 0; y < ydim; y++)
/* 115:    */       {
/* 116:141 */         int label = labelImage.getXYInt(x, y);
/* 117:142 */         this.output.setXYByte(x, y, labels[label][1]);
/* 118:    */       }
/* 119:    */     }
/* 120:    */   }
/* 121:    */   
/* 122:    */   public static Image invoke(Image img)
/* 123:    */   {
/* 124:    */     try
/* 125:    */     {
/* 126:150 */       return (Image)new SaliencyMapSlow().preprocess(new Object[] { img });
/* 127:    */     }
/* 128:    */     catch (GlobalException e)
/* 129:    */     {
/* 130:152 */       e.printStackTrace();
/* 131:    */     }
/* 132:153 */     return null;
/* 133:    */   }
/* 134:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.saliency.SaliencyMapSlow
 * JD-Core Version:    0.7.0.1
 */