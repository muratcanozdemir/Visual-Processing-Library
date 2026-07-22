/*   1:    */ package vpt.algorithms.linear;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.DoubleImage;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.histogram.ContrastStretch;
/*   9:    */ import vpt.util.se.NonFlatSE;
/*  10:    */ 
/*  11:    */ public class Sobel
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14: 19 */   public Image output = null;
/*  15: 20 */   public Image input = null;
/*  16: 21 */   public Integer type = null;
/*  17: 22 */   public Boolean normalized = null;
/*  18:    */   public static final int GX = 0;
/*  19:    */   public static final int GY = 1;
/*  20:    */   public static final int GRAD = 2;
/*  21:    */   public static final int THETA = 3;
/*  22:    */   
/*  23:    */   public Sobel()
/*  24:    */   {
/*  25: 30 */     this.inputFields = "input,type,normalized";
/*  26: 31 */     this.outputFields = "output";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void execute()
/*  30:    */     throws GlobalException
/*  31:    */   {
/*  32: 36 */     switch (this.type.intValue())
/*  33:    */     {
/*  34:    */     case 0: 
/*  35: 38 */       NonFlatSE kernel = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0D, 0.0D, 1.0D }, { -2.0D, 0.0D, 2.0D }, { -1.0D, 0.0D, 1.0D } });
/*  36: 39 */       this.output = Convolution.invoke(this.input, kernel);
/*  37: 40 */       if (this.normalized.booleanValue()) {
/*  38: 40 */         this.output = ContrastStretch.invoke(this.output);
/*  39:    */       }
/*  40: 41 */       break;
/*  41:    */     case 1: 
/*  42: 44 */       NonFlatSE kernel = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0D, -2.0D, -1.0D }, { 0.0D, 0.0D, 0.0D }, { 1.0D, 2.0D, 1.0D } });
/*  43: 45 */       this.output = Convolution.invoke(this.input, kernel);
/*  44: 46 */       if (this.normalized.booleanValue()) {
/*  45: 46 */         this.output = ContrastStretch.invoke(this.output);
/*  46:    */       }
/*  47: 47 */       break;
/*  48:    */     case 2: 
/*  49: 50 */       NonFlatSE kernel1 = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0D, 0.0D, 1.0D }, { -2.0D, 0.0D, 2.0D }, { -1.0D, 0.0D, 1.0D } });
/*  50: 51 */       NonFlatSE kernel2 = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0D, -2.0D, -1.0D }, { 0.0D, 0.0D, 0.0D }, { 1.0D, 2.0D, 1.0D } });
/*  51:    */       
/*  52: 53 */       Image gradx = Convolution.invoke(this.input, kernel1);
/*  53: 54 */       Image grady = Convolution.invoke(this.input, kernel2);
/*  54:    */       
/*  55: 56 */       this.output = new DoubleImage(this.input, false);
/*  56: 58 */       for (int i = 0; i < gradx.getSize(); i++)
/*  57:    */       {
/*  58: 59 */         double px = gradx.getDouble(i);
/*  59: 60 */         double py = grady.getDouble(i);
/*  60:    */         
/*  61: 62 */         this.output.setDouble(i, Math.sqrt(px * px + py * py));
/*  62:    */       }
/*  63: 65 */       if (this.normalized.booleanValue()) {
/*  64: 65 */         this.output = ContrastStretch.invoke(this.output);
/*  65:    */       }
/*  66: 67 */       break;
/*  67:    */     case 3: 
/*  68: 69 */       NonFlatSE kernel1 = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0D, 0.0D, 1.0D }, { -2.0D, 0.0D, 2.0D }, { -1.0D, 0.0D, 1.0D } });
/*  69: 70 */       NonFlatSE kernel2 = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0D, -2.0D, -1.0D }, { 0.0D, 0.0D, 0.0D }, { 1.0D, 2.0D, 1.0D } });
/*  70:    */       
/*  71: 72 */       Image gradx = Convolution.invoke(this.input, kernel1);
/*  72: 73 */       Image grady = Convolution.invoke(this.input, kernel2);
/*  73:    */       
/*  74: 75 */       this.output = new DoubleImage(this.input, false);
/*  75: 77 */       for (int i = 0; i < this.output.getSize(); i++)
/*  76:    */       {
/*  77: 78 */         double px = gradx.getDouble(i);
/*  78: 79 */         double py = grady.getDouble(i);
/*  79:    */         
/*  80:    */ 
/*  81: 82 */         double tmp = Math.atan2(py, px);
/*  82:    */         
/*  83: 84 */         this.output.setDouble(i, tmp);
/*  84:    */       }
/*  85: 87 */       if (this.normalized.booleanValue()) {
/*  86: 87 */         this.output = ContrastStretch.invoke(this.output);
/*  87:    */       }
/*  88: 89 */       break;
/*  89:    */     default: 
/*  90: 92 */       throw new GlobalException("Unsupported operation type");
/*  91:    */     }
/*  92:    */   }
/*  93:    */   
/*  94:    */   public static Image invoke(Image image, Integer type, Boolean normalized)
/*  95:    */   {
/*  96:    */     try
/*  97:    */     {
/*  98:100 */       return (Image)new Sobel().preprocess(new Object[] { image, type, normalized });
/*  99:    */     }
/* 100:    */     catch (GlobalException e)
/* 101:    */     {
/* 102:102 */       e.printStackTrace();
/* 103:    */     }
/* 104:103 */     return null;
/* 105:    */   }
/* 106:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.linear.Sobel
 * JD-Core Version:    0.7.0.1
 */