/*   1:    */ package vpt.algorithms.shape;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import vpt.Algorithm;
/*   7:    */ import vpt.BooleanImage;
/*   8:    */ import vpt.GlobalException;
/*   9:    */ import vpt.Image;
/*  10:    */ import vpt.algorithms.geometric.ImageCentroid;
/*  11:    */ import vpt.algorithms.mm.binary.BInternGradient;
/*  12:    */ import vpt.util.Distance;
/*  13:    */ import vpt.util.se.FlatSE;
/*  14:    */ 
/*  15:    */ public class BasicShapeStatistics
/*  16:    */   extends Algorithm
/*  17:    */ {
/*  18: 27 */   public double[] output = null;
/*  19: 28 */   public Image input = null;
/*  20:    */   
/*  21:    */   public BasicShapeStatistics()
/*  22:    */   {
/*  23: 31 */     this.inputFields = "input";
/*  24: 32 */     this.outputFields = "output";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void execute()
/*  28:    */     throws GlobalException
/*  29:    */   {
/*  30: 36 */     int cdim = this.input.getCDim();
/*  31: 37 */     int xdim = this.input.getXDim();
/*  32: 38 */     int ydim = this.input.getYDim();
/*  33: 40 */     if (cdim != 1) {
/*  34: 40 */       throw new GlobalException("Sorry, only grayscale images for now...");
/*  35:    */     }
/*  36: 47 */     Point centroid = ImageCentroid.invoke(this.input);
/*  37:    */     
/*  38:    */ 
/*  39: 50 */     Image borderImage = BInternGradient.invoke(this.input, FlatSE.square(3));
/*  40:    */     
/*  41:    */ 
/*  42:    */ 
/*  43: 54 */     ArrayList<Double> borders = new ArrayList();
/*  44:    */     
/*  45: 56 */     BooleanImage mask = new BooleanImage(this.input, false);
/*  46: 57 */     mask.fill(false);
/*  47: 59 */     for (int x = 0; x < xdim; x++) {
/*  48: 60 */       for (int y = 0; y < ydim; y++)
/*  49:    */       {
/*  50: 61 */         boolean r = borderImage.getXYBoolean(x, y);
/*  51: 62 */         if (r)
/*  52:    */         {
/*  53: 64 */           boolean m = mask.getXYBoolean(x, y);
/*  54: 65 */           if (!m)
/*  55:    */           {
/*  56: 68 */             Point p = new Point(x, y);
/*  57:    */             for (;;)
/*  58:    */             {
/*  59: 71 */               mask.setXYBoolean(p.x, p.y, true);
/*  60: 72 */               borders.add(Double.valueOf(Distance.EuclideanDistance(p.x, p.y, centroid.x, centroid.y)));
/*  61: 75 */               if ((p.x + 1 < xdim) && (borderImage.getXYBoolean(p.x + 1, p.y)) && (!mask.getXYBoolean(p.x + 1, p.y)))
/*  62:    */               {
/*  63: 76 */                 p.x += 1;
/*  64:    */               }
/*  65: 77 */               else if ((p.y + 1 < ydim) && (borderImage.getXYBoolean(p.x, p.y + 1)) && (!mask.getXYBoolean(p.x, p.y + 1)))
/*  66:    */               {
/*  67: 78 */                 p.y += 1;
/*  68:    */               }
/*  69: 79 */               else if ((p.x - 1 >= 0) && (borderImage.getXYBoolean(p.x - 1, p.y)) && (!mask.getXYBoolean(p.x - 1, p.y)))
/*  70:    */               {
/*  71: 80 */                 p.x -= 1;
/*  72:    */               }
/*  73:    */               else
/*  74:    */               {
/*  75: 81 */                 if ((p.y - 1 < 0) || (!borderImage.getXYBoolean(p.x, p.y - 1)) || (mask.getXYBoolean(p.x, p.y - 1))) {
/*  76:    */                   break;
/*  77:    */                 }
/*  78: 82 */                 p.y -= 1;
/*  79:    */               }
/*  80:    */             }
/*  81:    */           }
/*  82:    */         }
/*  83:    */       }
/*  84:    */     }
/*  85: 90 */     this.output = new double[4];
/*  86: 91 */     double[] border2 = new double[borders.size()];
/*  87: 93 */     for (int i = 0; i < border2.length; i++) {
/*  88: 94 */       border2[i] = ((Double)borders.get(i)).doubleValue();
/*  89:    */     }
/*  90: 97 */     Arrays.sort(border2);
/*  91: 98 */     this.output[0] = border2[0];
/*  92: 99 */     this.output[1] = border2[(border2.length - 1)];
/*  93:100 */     this.output[2] = border2[(border2.length / 2)];
/*  94:    */     
/*  95:102 */     double mean = 0.0D;
/*  96:103 */     for (int i = 0; i < border2.length; i++) {
/*  97:104 */       mean += border2[i];
/*  98:    */     }
/*  99:105 */     mean /= border2.length;
/* 100:107 */     for (int i = 0; i < border2.length; i++) {
/* 101:108 */       this.output[3] += (border2[i] - mean) * (border2[i] - mean);
/* 102:    */     }
/* 103:109 */     this.output[3] /= border2.length;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public static double[] invoke(Image img)
/* 107:    */   {
/* 108:    */     try
/* 109:    */     {
/* 110:117 */       return (double[])new BasicShapeStatistics().preprocess(new Object[] { img });
/* 111:    */     }
/* 112:    */     catch (GlobalException e)
/* 113:    */     {
/* 114:119 */       e.printStackTrace();
/* 115:    */     }
/* 116:120 */     return null;
/* 117:    */   }
/* 118:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.BasicShapeStatistics
 * JD-Core Version:    0.7.0.1
 */