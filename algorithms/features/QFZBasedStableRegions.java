/*   1:    */ package vpt.algorithms.features;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import vpt.Algorithm;
/*   7:    */ import vpt.BooleanImage;
/*   8:    */ import vpt.GlobalException;
/*   9:    */ import vpt.Image;
/*  10:    */ import vpt.algorithms.conversion.RGB2Gray;
/*  11:    */ import vpt.algorithms.display.Display2D;
/*  12:    */ import vpt.algorithms.flatzones.gray.GrayQFZSoille;
/*  13:    */ import vpt.algorithms.io.Load;
/*  14:    */ import vpt.algorithms.shape.GrayConnectedComponents2;
/*  15:    */ 
/*  16:    */ public class QFZBasedStableRegions
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19: 34 */   public Image output = null;
/*  20: 35 */   public Image input = null;
/*  21: 37 */   private final int scaleCount = 15;
/*  22: 38 */   private final int minArea = 250;
/*  23: 39 */   private final int successive = 9;
/*  24: 40 */   private final double areaDiff = 0.2D;
/*  25:    */   private int xdim;
/*  26:    */   private int ydim;
/*  27:    */   private int cdim;
/*  28:    */   
/*  29:    */   public QFZBasedStableRegions()
/*  30:    */   {
/*  31: 47 */     this.inputFields = "input";
/*  32: 48 */     this.outputFields = "output";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void execute()
/*  36:    */     throws GlobalException
/*  37:    */   {
/*  38: 52 */     this.cdim = this.input.getCDim();
/*  39: 53 */     this.xdim = this.input.getXDim();
/*  40: 54 */     this.ydim = this.input.getYDim();
/*  41: 55 */     if (this.cdim != 1) {
/*  42: 55 */       throw new GlobalException("Sorry, only grayscale images for now...");
/*  43:    */     }
/*  44: 57 */     long time = System.currentTimeMillis();
/*  45:    */     
/*  46:    */ 
/*  47: 60 */     Image[] scales = new Image[15];
/*  48: 61 */     for (int i = 1; i <= scales.length; i++) {
/*  49: 62 */       scales[(i - 1)] = GrayQFZSoille.invoke(this.input, i * 10, i * 10);
/*  50:    */     }
/*  51: 66 */     ArrayList[][][] zones = new ArrayList[scales.length][][];
/*  52: 68 */     for (int i = 0; i < scales.length; i++) {
/*  53: 69 */       zones[i] = GrayConnectedComponents2.invoke(scales[i]);
/*  54:    */     }
/*  55: 73 */     ArrayList[][] bestQfz = new ArrayList[this.xdim][this.ydim];
/*  56:    */     int max;
/*  57: 75 */     for (int y = 0; y < this.ydim; y++) {
/*  58: 76 */       for (int x = 0; x < this.xdim; x++)
/*  59:    */       {
/*  60: 78 */         int[] sizes = new int[15];
/*  61: 81 */         for (int s = 14; s >= 0; s--)
/*  62:    */         {
/*  63: 83 */           int numberOfSuccessiveAndConstantSizedScales = 0;
/*  64: 85 */           for (int t = s; (t >= 0) && (relativeAreaDiff(zones[s][x][y].size(), zones[t][x][y].size()) < 0.2D); t--) {}
/*  65: 86 */           numberOfSuccessiveAndConstantSizedScales = s - t + 1;
/*  66:    */           
/*  67: 88 */           sizes[s] = numberOfSuccessiveAndConstantSizedScales;
/*  68:    */         }
/*  69: 92 */         max = sizes[0];
/*  70: 93 */         int maxIndex = 0;
/*  71: 95 */         for (int s = 1; s < sizes.length; s++) {
/*  72: 96 */           if (sizes[s] > max)
/*  73:    */           {
/*  74: 97 */             max = sizes[s];
/*  75: 98 */             maxIndex = s;
/*  76:    */           }
/*  77:    */         }
/*  78:104 */         if ((max >= 9) && (zones[maxIndex][x][y].size() >= 250)) {
/*  79:105 */           bestQfz[x][y] = zones[maxIndex][x][y];
/*  80:    */         } else {
/*  81:107 */           bestQfz[x][y] = null;
/*  82:    */         }
/*  83:    */       }
/*  84:    */     }
/*  85:112 */     this.output = new BooleanImage(this.input, false);
/*  86:113 */     this.output.fill(false);
/*  87:115 */     for (int x = 0; x < this.xdim; x++) {
/*  88:116 */       for (int y = 0; y < this.ydim; y++) {
/*  89:119 */         if ((bestQfz[x][y] != null) && 
/*  90:120 */           (!this.output.getXYBoolean(((Point)bestQfz[x][y].get(0)).x, ((Point)bestQfz[x][y].get(0)).y))) {
/*  91:122 */           for (Point p : bestQfz[x][y]) {
/*  92:123 */             this.output.setXYBoolean(p.x, p.y, true);
/*  93:    */           }
/*  94:    */         }
/*  95:    */       }
/*  96:    */     }
/*  97:127 */     System.err.println(System.currentTimeMillis() - time);
/*  98:    */   }
/*  99:    */   
/* 100:    */   private double relativeAreaDiff(int a1, int a2)
/* 101:    */   {
/* 102:132 */     return (a1 - a2) / a1;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static Image invoke(Image image)
/* 106:    */   {
/* 107:    */     try
/* 108:    */     {
/* 109:137 */       return (Image)new QFZBasedStableRegions().preprocess(new Object[] { image });
/* 110:    */     }
/* 111:    */     catch (GlobalException e)
/* 112:    */     {
/* 113:139 */       e.printStackTrace();
/* 114:    */     }
/* 115:140 */     return null;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public static void main(String[] args)
/* 119:    */   {
/* 120:145 */     String root = "/media/data/arge/veri_yedegi/LandUse/Images/";
/* 121:    */     
/* 122:147 */     String[] categories = { "agricultural", "airplane", "baseballdiamond", "beach", "buildings", 
/* 123:148 */       "chaparral", "denseresidential", "forest", "freeway", "golfcourse", 
/* 124:149 */       "harbor", "intersection", "mediumresidential", "mobilehomepark", "overpass", 
/* 125:150 */       "parkinglot", "river", "runway", "sparseresidential", "storagetanks", "tenniscourt" };
/* 126:152 */     for (int i = 0; i < categories.length; i++)
/* 127:    */     {
/* 128:153 */       Image img = Load.invoke(root + categories[i] + "/" + categories[i] + "00.png");
/* 129:154 */       Display2D.invoke(img, Boolean.valueOf(true));
/* 130:    */       
/* 131:156 */       img = RGB2Gray.invoke(img);
/* 132:157 */       Image mask = invoke(img);
/* 133:158 */       Display2D.invoke(mask);
/* 134:    */     }
/* 135:    */   }
/* 136:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.features.QFZBasedStableRegions
 * JD-Core Version:    0.7.0.1
 */