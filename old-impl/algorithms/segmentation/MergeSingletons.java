/*   1:    */ package vpt.algorithms.segmentation;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.GlobalException;
/*   5:    */ import vpt.Image;
/*   6:    */ import vpt.util.Tools;
/*   7:    */ 
/*   8:    */ public class MergeSingletons
/*   9:    */   extends Algorithm
/*  10:    */ {
/*  11: 23 */   public Image output = null;
/*  12: 24 */   public Image labels = null;
/*  13: 25 */   public Image original = null;
/*  14:    */   private int xdim;
/*  15:    */   private int ydim;
/*  16:    */   
/*  17:    */   public MergeSingletons()
/*  18:    */   {
/*  19: 31 */     this.inputFields = "labels, original";
/*  20: 32 */     this.outputFields = "output";
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void execute()
/*  24:    */     throws GlobalException
/*  25:    */   {
/*  26: 37 */     this.xdim = this.labels.getXDim();
/*  27: 38 */     this.ydim = this.labels.getYDim();
/*  28: 39 */     int cdim = this.labels.getCDim();
/*  29: 41 */     if (cdim != 1) {
/*  30: 41 */       throw new GlobalException("A label image cannot have more than one channel!");
/*  31:    */     }
/*  32: 43 */     if ((this.xdim != this.original.getXDim()) || (this.ydim != this.original.getYDim())) {
/*  33: 44 */       throw new GlobalException("The labels and original image do not have the same dimensions!");
/*  34:    */     }
/*  35: 46 */     this.output = this.labels.newInstance(true);
/*  36: 48 */     for (int y = 0; y < this.ydim; y++) {
/*  37: 49 */       for (int x = 0; x < this.xdim; x++) {
/*  38: 52 */         if (isSingle(x, y, this.output))
/*  39:    */         {
/*  40: 54 */           double distance = 1.7976931348623157E+308D;
/*  41:    */           
/*  42: 56 */           int[] p = this.original.getVXYByte(x, y);
/*  43: 57 */           int newLabel = this.output.getXYInt(x, y);
/*  44: 60 */           for (int _x = x - 1; _x <= x + 1; _x++) {
/*  45: 61 */             for (int _y = y - 1; _y <= y + 1; _y++) {
/*  46: 62 */               if ((_x >= 0) && (_y >= 0) && (_x <= this.xdim - 1) && (_y <= this.ydim - 1) && (
/*  47: 63 */                 (_x != x) || (_y != y)))
/*  48:    */               {
/*  49: 65 */                 double tmp = Tools.EuclideanDistance(p, this.original.getVXYByte(_x, _y));
/*  50: 67 */                 if (tmp < distance)
/*  51:    */                 {
/*  52: 68 */                   newLabel = this.output.getXYInt(_x, _y);
/*  53: 69 */                   distance = tmp;
/*  54:    */                 }
/*  55:    */               }
/*  56:    */             }
/*  57:    */           }
/*  58: 74 */           this.output.setXYInt(x, y, newLabel);
/*  59:    */         }
/*  60:    */       }
/*  61:    */     }
/*  62:    */   }
/*  63:    */   
/*  64:    */   private boolean isSingle(int x, int y, Image img)
/*  65:    */   {
/*  66: 81 */     int p = img.getXYInt(x, y);
/*  67: 83 */     for (int _x = x - 1; _x <= x + 1; _x++) {
/*  68: 84 */       for (int _y = y - 1; _y <= y + 1; _y++) {
/*  69: 85 */         if (((_x != x) || (_y != y)) && 
/*  70: 86 */           (_x >= 0) && (_y >= 0) && (_x <= this.xdim - 1) && (_y <= this.ydim - 1))
/*  71:    */         {
/*  72: 88 */           int q = img.getXYInt(_x, _y);
/*  73: 90 */           if (p == q) {
/*  74: 90 */             return false;
/*  75:    */           }
/*  76:    */         }
/*  77:    */       }
/*  78:    */     }
/*  79: 95 */     return true;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static Image invoke(Image labels, Image original)
/*  83:    */   {
/*  84:    */     try
/*  85:    */     {
/*  86:101 */       return (Image)new MergeSingletons().preprocess(new Object[] { labels, original });
/*  87:    */     }
/*  88:    */     catch (GlobalException e)
/*  89:    */     {
/*  90:103 */       e.printStackTrace();
/*  91:    */     }
/*  92:104 */     return null;
/*  93:    */   }
/*  94:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.MergeSingletons
 * JD-Core Version:    0.7.0.1
 */