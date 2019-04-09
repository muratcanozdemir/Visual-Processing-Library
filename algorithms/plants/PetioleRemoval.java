/*   1:    */ package vpt.algorithms.plants;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import vpt.Algorithm;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.geometric.KeepLargestCC;
/*   9:    */ import vpt.algorithms.mm.binary.BTopHat;
/*  10:    */ import vpt.algorithms.shape.ConnectedComponents;
/*  11:    */ import vpt.util.Tools;
/*  12:    */ import vpt.util.se.FlatSE;
/*  13:    */ 
/*  14:    */ public class PetioleRemoval
/*  15:    */   extends Algorithm
/*  16:    */ {
/*  17: 23 */   public Image output = null;
/*  18: 24 */   public Image input = null;
/*  19:    */   
/*  20:    */   public PetioleRemoval()
/*  21:    */   {
/*  22: 27 */     this.inputFields = "input";
/*  23: 28 */     this.outputFields = "output";
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void execute()
/*  27:    */     throws GlobalException
/*  28:    */   {
/*  29: 32 */     int cdim = this.input.getCDim();
/*  30: 34 */     if (cdim != 1) {
/*  31: 34 */       throw new GlobalException("Sorry, only mono-channel images for now...");
/*  32:    */     }
/*  33: 40 */     this.output = this.input.newInstance(true);
/*  34:    */     
/*  35:    */ 
/*  36: 43 */     this.input = BTopHat.invoke(this.input, FlatSE.hLine(31));
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40: 47 */     ArrayList<ArrayList<Point>> CCs = ConnectedComponents.invoke(this.input);
/*  41:    */     
/*  42:    */ 
/*  43:    */ 
/*  44: 51 */     int[] maxDims = new int[CCs.size()];
/*  45: 52 */     int i = 0;
/*  46: 53 */     for (ArrayList<Point> CC : CCs)
/*  47:    */     {
/*  48: 55 */       int leftX = 2147483647;
/*  49: 56 */       int rightX = 0;
/*  50:    */       
/*  51: 58 */       int topY = 2147483647;
/*  52: 59 */       int bottomY = 0;
/*  53: 61 */       for (Point p : CC)
/*  54:    */       {
/*  55: 62 */         if (p.x < leftX) {
/*  56: 62 */           leftX = p.x;
/*  57:    */         }
/*  58: 63 */         if (p.x > rightX) {
/*  59: 63 */           rightX = p.x;
/*  60:    */         }
/*  61: 64 */         if (p.y > bottomY) {
/*  62: 64 */           bottomY = p.y;
/*  63:    */         }
/*  64: 65 */         if (p.y < topY) {
/*  65: 65 */           topY = p.y;
/*  66:    */         }
/*  67:    */       }
/*  68: 68 */       maxDims[(i++)] = Math.max(rightX - leftX, bottomY - topY);
/*  69:    */     }
/*  70: 72 */     int max1 = 0;
/*  71: 73 */     int index1 = 0;
/*  72: 74 */     for (int j = 0; j < maxDims.length; j++) {
/*  73: 75 */       if (maxDims[j] > max1)
/*  74:    */       {
/*  75: 76 */         max1 = maxDims[j];
/*  76: 77 */         index1 = j;
/*  77:    */       }
/*  78:    */     }
/*  79: 82 */     int max2 = 0;
/*  80: 83 */     int index2 = 0;
/*  81: 84 */     for (int j = 0; j < maxDims.length; j++) {
/*  82: 85 */       if (j != index1) {
/*  83: 87 */         if (maxDims[j] > max2)
/*  84:    */         {
/*  85: 88 */           max2 = maxDims[j];
/*  86: 89 */           index2 = j;
/*  87:    */         }
/*  88:    */       }
/*  89:    */     }
/*  90: 95 */     if ((max1 < 20) || ((Tools.centroid((ArrayList)CCs.get(index1)).y < 400) && (Tools.centroid((ArrayList)CCs.get(index2)).y < 400))) {
/*  91: 95 */       return;
/*  92:    */     }
/*  93: 97 */     int indexMax = index1;
/*  94:    */     Point centroid2;
/*  95:101 */     if (max1 / max2 < 3.0D)
/*  96:    */     {
/*  97:104 */       Point centroid1 = Tools.centroid((ArrayList)CCs.get(index1));
/*  98:105 */       centroid2 = Tools.centroid((ArrayList)CCs.get(index2));
/*  99:107 */       if (centroid2.y > centroid1.y) {
/* 100:107 */         indexMax = index2;
/* 101:    */       }
/* 102:    */     }
/* 103:111 */     for (Point p : (ArrayList)CCs.get(indexMax)) {
/* 104:112 */       this.output.setXYBoolean(p.x, p.y, false);
/* 105:    */     }
/* 106:115 */     this.output = KeepLargestCC.invoke(this.output);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public static Image invoke(Image img)
/* 110:    */   {
/* 111:    */     try
/* 112:    */     {
/* 113:121 */       return (Image)new PetioleRemoval().preprocess(new Object[] { img });
/* 114:    */     }
/* 115:    */     catch (GlobalException e)
/* 116:    */     {
/* 117:123 */       e.printStackTrace();
/* 118:    */     }
/* 119:124 */     return null;
/* 120:    */   }
/* 121:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.plants.PetioleRemoval
 * JD-Core Version:    0.7.0.1
 */