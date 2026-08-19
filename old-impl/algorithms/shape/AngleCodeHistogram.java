/*   1:    */ package vpt.algorithms.shape;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import vpt.Algorithm;
/*   6:    */ import vpt.BooleanImage;
/*   7:    */ import vpt.GlobalException;
/*   8:    */ import vpt.Image;
/*   9:    */ import vpt.algorithms.mm.binary.BInternGradient;
/*  10:    */ import vpt.util.Tools;
/*  11:    */ import vpt.util.se.FlatSE;
/*  12:    */ 
/*  13:    */ public class AngleCodeHistogram
/*  14:    */   extends Algorithm
/*  15:    */ {
/*  16: 21 */   public double[] output = null;
/*  17: 22 */   public Image input = null;
/*  18: 23 */   public Integer bins = null;
/*  19: 24 */   public Double fractionOfPerimeter = null;
/*  20:    */   
/*  21:    */   public AngleCodeHistogram()
/*  22:    */   {
/*  23: 27 */     this.inputFields = "input, bins, fractionOfPerimeter";
/*  24: 28 */     this.outputFields = "output";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void execute()
/*  28:    */     throws GlobalException
/*  29:    */   {
/*  30: 32 */     int cdim = this.input.getCDim();
/*  31: 33 */     int xdim = this.input.getXDim();
/*  32: 34 */     int ydim = this.input.getYDim();
/*  33: 36 */     if (cdim != 1) {
/*  34: 36 */       throw new GlobalException("Sorry, only mono-channel images for now...");
/*  35:    */     }
/*  36: 40 */     Image borderImage = BInternGradient.invoke(this.input, FlatSE.square(3), Boolean.valueOf(false));
/*  37:    */     
/*  38:    */ 
/*  39: 43 */     double totalBorderPixels = Tools.volume(borderImage, 0);
/*  40:    */     
/*  41:    */ 
/*  42:    */ 
/*  43: 47 */     int numberOfKeyPoints = (int)(this.fractionOfPerimeter.doubleValue() * totalBorderPixels);
/*  44: 48 */     int step = (int)totalBorderPixels / numberOfKeyPoints;
/*  45:    */     
/*  46: 50 */     BooleanImage mask = new BooleanImage(this.input, false);
/*  47: 51 */     mask.fill(false);
/*  48:    */     
/*  49: 53 */     ArrayList<Point> borderKeyPoints = new ArrayList();
/*  50: 54 */     int tmp = 0;
/*  51: 57 */     for (int x = 0; x < xdim; x++) {
/*  52: 58 */       for (int y = 0; y < ydim; y++)
/*  53:    */       {
/*  54: 59 */         boolean r = borderImage.getXYBoolean(x, y);
/*  55: 60 */         if (r)
/*  56:    */         {
/*  57: 62 */           boolean m = mask.getXYBoolean(x, y);
/*  58: 63 */           if (!m)
/*  59:    */           {
/*  60: 65 */             Point p = new Point(x, y);
/*  61:    */             for (;;)
/*  62:    */             {
/*  63: 68 */               mask.setXYBoolean(p.x, p.y, true);
/*  64: 69 */               tmp = (tmp + 1) % step;
/*  65: 71 */               if (tmp == 0) {
/*  66: 71 */                 borderKeyPoints.add(new Point(p.x, p.y));
/*  67:    */               }
/*  68: 74 */               if ((p.x + 1 < xdim) && (borderImage.getXYBoolean(p.x + 1, p.y)) && (!mask.getXYBoolean(p.x + 1, p.y)))
/*  69:    */               {
/*  70: 75 */                 p.x += 1;
/*  71:    */               }
/*  72: 76 */               else if ((p.y + 1 < ydim) && (borderImage.getXYBoolean(p.x, p.y + 1)) && (!mask.getXYBoolean(p.x, p.y + 1)))
/*  73:    */               {
/*  74: 77 */                 p.y += 1;
/*  75:    */               }
/*  76: 78 */               else if ((p.x - 1 >= 0) && (borderImage.getXYBoolean(p.x - 1, p.y)) && (!mask.getXYBoolean(p.x - 1, p.y)))
/*  77:    */               {
/*  78: 79 */                 p.x -= 1;
/*  79:    */               }
/*  80:    */               else
/*  81:    */               {
/*  82: 80 */                 if ((p.y - 1 < 0) || (!borderImage.getXYBoolean(p.x, p.y - 1)) || (mask.getXYBoolean(p.x, p.y - 1))) {
/*  83:    */                   break;
/*  84:    */                 }
/*  85: 81 */                 p.y -= 1;
/*  86:    */               }
/*  87:    */             }
/*  88:    */           }
/*  89:    */         }
/*  90:    */       }
/*  91:    */     }
/*  92: 91 */     this.output = new double[this.bins.intValue()];
/*  93:    */     
/*  94:    */ 
/*  95: 94 */     int keyPointCount = borderKeyPoints.size();
/*  96: 95 */     for (int i = 0; i < keyPointCount; i++)
/*  97:    */     {
/*  98: 98 */       Point p1 = (Point)borderKeyPoints.get(i);
/*  99: 99 */       Point p2 = (Point)borderKeyPoints.get((i + 1) % keyPointCount);
/* 100:100 */       Point p3 = (Point)borderKeyPoints.get((i + 2) % keyPointCount);
/* 101:    */       
/* 102:102 */       int angle = (int)Tools.getAngleABC(p1, p2, p3);
/* 103:    */       
/* 104:104 */       angle += 180;
/* 105:    */       
/* 106:    */ 
/* 107:107 */       this.output[((int)(angle / 361.0D * this.bins.intValue()))] += 1.0D;
/* 108:    */     }
/* 109:110 */     for (int i = 0; i < this.output.length; i++) {
/* 110:111 */       this.output[i] /= keyPointCount;
/* 111:    */     }
/* 112:    */   }
/* 113:    */   
/* 114:    */   public static double[] invoke(Image img, Integer bins, Double fractionOfPerimeter)
/* 115:    */   {
/* 116:    */     try
/* 117:    */     {
/* 118:117 */       return (double[])new AngleCodeHistogram().preprocess(new Object[] { img, bins, fractionOfPerimeter });
/* 119:    */     }
/* 120:    */     catch (GlobalException e)
/* 121:    */     {
/* 122:119 */       e.printStackTrace();
/* 123:    */     }
/* 124:120 */     return null;
/* 125:    */   }
/* 126:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.AngleCodeHistogram
 * JD-Core Version:    0.7.0.1
 */