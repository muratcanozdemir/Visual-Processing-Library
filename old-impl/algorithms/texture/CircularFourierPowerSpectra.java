/*   1:    */ package vpt.algorithms.texture;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.DoubleImage;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.conversion.RGB2Gray;
/*   9:    */ import vpt.algorithms.frequential.FFT;
/*  10:    */ import vpt.algorithms.io.Load;
/*  11:    */ import vpt.experimental.texture.Experimental5;
/*  12:    */ import vpt.util.Tools;
/*  13:    */ 
/*  14:    */ public class CircularFourierPowerSpectra
/*  15:    */   extends Algorithm
/*  16:    */ {
/*  17: 23 */   public double[] output = null;
/*  18: 24 */   public Image input = null;
/*  19: 25 */   public Integer howManyCircles = null;
/*  20:    */   
/*  21:    */   public CircularFourierPowerSpectra()
/*  22:    */   {
/*  23: 28 */     this.inputFields = "input,howManyCircles";
/*  24: 29 */     this.outputFields = "output";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void execute()
/*  28:    */     throws GlobalException
/*  29:    */   {
/*  30: 33 */     int cdim = this.input.getCDim();
/*  31: 34 */     if (cdim != 1) {
/*  32: 34 */       throw new GlobalException("Sorry, only grayscale for now");
/*  33:    */     }
/*  34: 36 */     this.output = new double[this.howManyCircles.intValue()];
/*  35:    */     
/*  36: 38 */     DoubleImage[] fft = FFT.invoke(this.input, Boolean.valueOf(true), Integer.valueOf(2));
/*  37: 40 */     for (int i = 0; i < this.howManyCircles.intValue(); i++) {
/*  38: 41 */       this.output[i] = (Tools.irregularCircularBandVolumes2(fft[0], i, 7) / Tools.volume(fft[0], 0));
/*  39:    */     }
/*  40:    */   }
/*  41:    */   
/*  42:    */   public static double[] invoke(Image image, Integer howManyCircles)
/*  43:    */   {
/*  44:    */     try
/*  45:    */     {
/*  46: 46 */       return (double[])new CircularFourierPowerSpectra().preprocess(new Object[] { image, howManyCircles });
/*  47:    */     }
/*  48:    */     catch (GlobalException e)
/*  49:    */     {
/*  50: 48 */       e.printStackTrace();
/*  51:    */     }
/*  52: 49 */     return null;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public static void main(String[] args)
/*  56:    */   {
/*  57: 54 */     Image img = Load.invoke("/media/data/arge/veri_yedegi/LandUse/Images/chaparral/chaparral00.png");
/*  58: 55 */     Image img2 = Load.invoke("/media/data/arge/veri_yedegi/LandUse/Images/agricultural/agricultural05.png");
/*  59:    */     
/*  60:    */ 
/*  61:    */ 
/*  62:    */ 
/*  63:    */ 
/*  64: 61 */     img = RGB2Gray.invoke(img);
/*  65: 62 */     img2 = RGB2Gray.invoke(img2);
/*  66:    */     
/*  67:    */ 
/*  68:    */ 
/*  69:    */ 
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:    */ 
/*  76:    */ 
/*  77:    */ 
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81:    */ 
/*  82:    */ 
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86:    */ 
/*  87:    */ 
/*  88:    */ 
/*  89:    */ 
/*  90:    */ 
/*  91:    */ 
/*  92:    */ 
/*  93:    */ 
/*  94:    */ 
/*  95:    */ 
/*  96:    */ 
/*  97:    */ 
/*  98: 95 */     double[] feature = Experimental5.invoke(img);
/*  99: 96 */     double[] feature2 = Experimental5.invoke(img2);
/* 100: 98 */     for (int i = 0; i < 10; i++)
/* 101:    */     {
/* 102:100 */       System.err.print(i + "\t");
/* 103:102 */       for (int j = 3; j < 4; j++) {
/* 104:103 */         System.err.print(feature[(i * 4 + j)] + "\t" + feature2[(i * 4 + j)]);
/* 105:    */       }
/* 106:105 */       System.err.println("");
/* 107:    */     }
/* 108:    */   }
/* 109:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.CircularFourierPowerSpectra
 * JD-Core Version:    0.7.0.1
 */