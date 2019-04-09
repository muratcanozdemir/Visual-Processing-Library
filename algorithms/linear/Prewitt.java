/*  1:   */ package vpt.algorithms.linear;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.DoubleImage;
/*  6:   */ import vpt.GlobalException;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.algorithms.histogram.ContrastStretch;
/*  9:   */ import vpt.util.se.NonFlatSE;
/* 10:   */ 
/* 11:   */ public class Prewitt
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:13 */   public Image output = null;
/* 15:14 */   public Image inputImage = null;
/* 16:15 */   public Integer type = null;
/* 17:   */   public static final int GX = 0;
/* 18:   */   public static final int GY = 1;
/* 19:   */   public static final int GRAD = 2;
/* 20:   */   public static final int THETA = 3;
/* 21:   */   
/* 22:   */   public Prewitt()
/* 23:   */   {
/* 24:23 */     this.inputFields = "inputImage,type";
/* 25:24 */     this.outputFields = "output";
/* 26:   */   }
/* 27:   */   
/* 28:   */   public void execute()
/* 29:   */     throws GlobalException
/* 30:   */   {
/* 31:29 */     switch (this.type.intValue())
/* 32:   */     {
/* 33:   */     case 0: 
/* 34:31 */       NonFlatSE kernel = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0D, 0.0D, 1.0D }, { -1.0D, 0.0D, 1.0D }, { -1.0D, 0.0D, 1.0D } });
/* 35:32 */       this.output = Convolution.invoke(this.inputImage, kernel);
/* 36:33 */       this.output = ContrastStretch.invoke(this.output);
/* 37:34 */       break;
/* 38:   */     case 1: 
/* 39:37 */       NonFlatSE kernel = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0D, -1.0D, -1.0D }, { 0.0D, 0.0D, 0.0D }, { 1.0D, 1.0D, 1.0D } });
/* 40:38 */       this.output = Convolution.invoke(this.inputImage, kernel);
/* 41:39 */       this.output = ContrastStretch.invoke(this.output);
/* 42:40 */       break;
/* 43:   */     case 2: 
/* 44:43 */       NonFlatSE kernel1 = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0D, 0.0D, 1.0D }, { -1.0D, 0.0D, 1.0D }, { -1.0D, 0.0D, 1.0D } });
/* 45:44 */       NonFlatSE kernel2 = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0D, -1.0D, -1.0D }, { 0.0D, 0.0D, 0.0D }, { 1.0D, 1.0D, 1.0D } });
/* 46:   */       
/* 47:46 */       Image gradx = Convolution.invoke(this.inputImage, kernel1);
/* 48:47 */       Image grady = Convolution.invoke(this.inputImage, kernel2);
/* 49:   */       
/* 50:49 */       this.output = new DoubleImage(this.inputImage, false);
/* 51:51 */       for (int i = 0; i < gradx.getSize(); i++)
/* 52:   */       {
/* 53:52 */         double px = gradx.getDouble(i);
/* 54:53 */         double py = grady.getDouble(i);
/* 55:   */         
/* 56:55 */         this.output.setDouble(i, Math.sqrt(px * px + py * py));
/* 57:   */       }
/* 58:58 */       this.output = ContrastStretch.invoke(this.output);
/* 59:   */       
/* 60:60 */       break;
/* 61:   */     case 3: 
/* 62:62 */       NonFlatSE kernel1 = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0D, 0.0D, 1.0D }, { -1.0D, 0.0D, 1.0D }, { -1.0D, 0.0D, 1.0D } });
/* 63:63 */       NonFlatSE kernel2 = new NonFlatSE(new Point(1, 1), new double[][] { { -1.0D, -1.0D, -1.0D }, { 0.0D, 0.0D, 0.0D }, { 1.0D, 1.0D, 1.0D } });
/* 64:   */       
/* 65:65 */       Image gradx = Convolution.invoke(this.inputImage, kernel1);
/* 66:66 */       Image grady = Convolution.invoke(this.inputImage, kernel2);
/* 67:   */       
/* 68:68 */       this.output = new DoubleImage(this.inputImage, false);
/* 69:70 */       for (int i = 0; i < gradx.getSize(); i++)
/* 70:   */       {
/* 71:71 */         double px = gradx.getDouble(i);
/* 72:72 */         double py = grady.getDouble(i);
/* 73:   */         
/* 74:74 */         this.output.setDouble(i, Math.atan(py / px));
/* 75:   */       }
/* 76:77 */       this.output = ContrastStretch.invoke(this.output);
/* 77:   */       
/* 78:79 */       break;
/* 79:   */     default: 
/* 80:82 */       throw new GlobalException("Unsupported operation type");
/* 81:   */     }
/* 82:   */   }
/* 83:   */   
/* 84:   */   public static Image invoke(Image image, Integer type)
/* 85:   */   {
/* 86:   */     try
/* 87:   */     {
/* 88:89 */       return (Image)new Prewitt().preprocess(new Object[] { image, type });
/* 89:   */     }
/* 90:   */     catch (GlobalException e)
/* 91:   */     {
/* 92:91 */       e.printStackTrace();
/* 93:   */     }
/* 94:92 */     return null;
/* 95:   */   }
/* 96:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.linear.Prewitt
 * JD-Core Version:    0.7.0.1
 */