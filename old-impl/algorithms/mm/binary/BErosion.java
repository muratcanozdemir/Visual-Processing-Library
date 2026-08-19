/*   1:    */ package vpt.algorithms.mm.binary;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.util.se.FlatSE;
/*   8:    */ 
/*   9:    */ public class BErosion
/*  10:    */   extends Algorithm
/*  11:    */ {
/*  12: 16 */   public Image output = null;
/*  13: 17 */   public Image input = null;
/*  14: 18 */   public FlatSE se = null;
/*  15: 19 */   public Boolean flag = null;
/*  16:    */   private int xdim;
/*  17:    */   private int ydim;
/*  18:    */   private int cdim;
/*  19:    */   
/*  20:    */   public BErosion()
/*  21:    */   {
/*  22: 26 */     this.inputFields = "input, se, flag";
/*  23: 27 */     this.outputFields = "output";
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void execute()
/*  27:    */     throws GlobalException
/*  28:    */   {
/*  29: 31 */     this.output = this.input.newInstance(false);
/*  30:    */     
/*  31: 33 */     this.xdim = this.output.getXDim();
/*  32: 34 */     this.ydim = this.output.getYDim();
/*  33: 35 */     this.cdim = this.output.getCDim();
/*  34:    */     
/*  35:    */ 
/*  36:    */ 
/*  37: 39 */     this.se = this.se.reflection();
/*  38: 42 */     if (!this.se.BOX)
/*  39:    */     {
/*  40: 43 */       for (int x = 0; x < this.xdim; x++) {
/*  41: 44 */         for (int y = 0; y < this.ydim; y++) {
/*  42: 45 */           for (int c = 0; c < this.cdim; c++) {
/*  43: 46 */             this.output.setXYCBoolean(x, y, c, getMin(this.input, x, y, c, this.se.getCoords()));
/*  44:    */           }
/*  45:    */         }
/*  46:    */       }
/*  47:    */     }
/*  48:    */     else
/*  49:    */     {
/*  50: 49 */       FlatSE hline = this.se.getHLine();
/*  51: 50 */       FlatSE vline = this.se.getVLine();
/*  52: 51 */       Image tmp = this.input.newInstance(false);
/*  53: 53 */       for (int x = 0; x < this.xdim; x++) {
/*  54: 54 */         for (int y = 0; y < this.ydim; y++) {
/*  55: 55 */           for (int c = 0; c < this.cdim; c++) {
/*  56: 56 */             tmp.setXYCBoolean(x, y, c, getMin(this.input, x, y, c, hline.getCoords()));
/*  57:    */           }
/*  58:    */         }
/*  59:    */       }
/*  60: 58 */       for (int x = 0; x < this.xdim; x++) {
/*  61: 59 */         for (int y = 0; y < this.ydim; y++) {
/*  62: 60 */           for (int c = 0; c < this.cdim; c++) {
/*  63: 61 */             this.output.setXYCBoolean(x, y, c, getMin(tmp, x, y, c, vline.getCoords()));
/*  64:    */           }
/*  65:    */         }
/*  66:    */       }
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   private boolean getMin(Image img, int x, int y, int c, Point[] coords)
/*  71:    */   {
/*  72: 68 */     for (int i = 0; i < coords.length; i++)
/*  73:    */     {
/*  74: 71 */       int _x = x - coords[i].x;
/*  75: 72 */       int _y = y - coords[i].y;
/*  76: 76 */       if ((_x < 0) || (_y < 0) || (_x >= this.xdim) || (_y >= this.ydim))
/*  77:    */       {
/*  78: 77 */         if (!this.flag.booleanValue()) {
/*  79: 78 */           return false;
/*  80:    */         }
/*  81:    */       }
/*  82:    */       else
/*  83:    */       {
/*  84: 80 */         boolean pixel = img.getXYCBoolean(_x, _y, c);
/*  85: 82 */         if (!pixel) {
/*  86: 83 */           return false;
/*  87:    */         }
/*  88:    */       }
/*  89:    */     }
/*  90: 87 */     return true;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public static Image invoke(Image image, FlatSE se)
/*  94:    */   {
/*  95:    */     try
/*  96:    */     {
/*  97: 93 */       return (Image)new BErosion().preprocess(new Object[] { image, se, Boolean.valueOf(true) });
/*  98:    */     }
/*  99:    */     catch (GlobalException e)
/* 100:    */     {
/* 101: 95 */       e.printStackTrace();
/* 102:    */     }
/* 103: 96 */     return null;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public static Image invoke(Image image, FlatSE se, Boolean flag)
/* 107:    */   {
/* 108:    */     try
/* 109:    */     {
/* 110:103 */       return (Image)new BErosion().preprocess(new Object[] { image, se, flag });
/* 111:    */     }
/* 112:    */     catch (GlobalException e)
/* 113:    */     {
/* 114:105 */       e.printStackTrace();
/* 115:    */     }
/* 116:106 */     return null;
/* 117:    */   }
/* 118:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.BErosion
 * JD-Core Version:    0.7.0.1
 */