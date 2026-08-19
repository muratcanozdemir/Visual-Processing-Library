/*  1:   */ package vpt.algorithms.texture;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.DoubleImage;
/*  6:   */ import vpt.GlobalException;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.algorithms.flatzones.gray.GrayQFZSoille;
/*  9:   */ import vpt.algorithms.frequential.FFT;
/* 10:   */ import vpt.algorithms.segmentation.LabelsToMeanValue;
/* 11:   */ import vpt.util.Tools;
/* 12:   */ 
/* 13:   */ public class OrderedAngularVolumesHistogram
/* 14:   */   extends Algorithm
/* 15:   */ {
/* 16:26 */   public double[] output = null;
/* 17:27 */   public Image input = null;
/* 18:28 */   public Integer howManyAngles = null;
/* 19:   */   
/* 20:   */   public OrderedAngularVolumesHistogram()
/* 21:   */   {
/* 22:31 */     this.inputFields = "input, howManyAngles";
/* 23:32 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:36 */     int cdim = this.input.getCDim();
/* 30:37 */     if (cdim != 1) {
/* 31:37 */       throw new GlobalException("Sorry, only grayscale for now");
/* 32:   */     }
/* 33:39 */     this.output = new double[this.howManyAngles.intValue()];
/* 34:   */     
/* 35:41 */     Image labels = GrayQFZSoille.invoke(this.input, 10, 10);
/* 36:42 */     this.input = LabelsToMeanValue.invoke(labels, this.input);
/* 37:   */     
/* 38:44 */     DoubleImage[] fft = FFT.invoke(this.input, Boolean.valueOf(true), Integer.valueOf(2));
/* 39:   */     
/* 40:46 */     double normalizer = Tools.volume(fft[0], 0) / 2.0D;
/* 41:48 */     for (int i = 0; i < this.howManyAngles.intValue(); i++) {
/* 42:49 */       this.output[i] = (Tools.regularAngularVolumes(fft[0], i, this.howManyAngles.intValue()) / normalizer);
/* 43:   */     }
/* 44:51 */     Arrays.sort(this.output);
/* 45:   */   }
/* 46:   */   
/* 47:   */   public static double[] invoke(Image image, Integer howManyAngles)
/* 48:   */   {
/* 49:   */     try
/* 50:   */     {
/* 51:56 */       return (double[])new OrderedAngularVolumesHistogram().preprocess(new Object[] { image, howManyAngles });
/* 52:   */     }
/* 53:   */     catch (GlobalException e)
/* 54:   */     {
/* 55:58 */       e.printStackTrace();
/* 56:   */     }
/* 57:59 */     return null;
/* 58:   */   }
/* 59:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.OrderedAngularVolumesHistogram
 * JD-Core Version:    0.7.0.1
 */