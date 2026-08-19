/*  1:   */ package vpt.algorithms.plants;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.Minimum;
/*  7:   */ import vpt.algorithms.mm.binary.BGradient;
/*  8:   */ import vpt.algorithms.mm.gray.GInternGradient;
/*  9:   */ import vpt.algorithms.mm.gray.geo.GClosingByReconstruction;
/* 10:   */ import vpt.algorithms.mm.gray.geo.GOpeningByReconstruction;
/* 11:   */ import vpt.algorithms.mm.gray.path.GPathOpening;
/* 12:   */ import vpt.algorithms.point.Inversion;
/* 13:   */ import vpt.algorithms.segmentation.Threshold;
/* 14:   */ import vpt.util.Tools;
/* 15:   */ import vpt.util.se.FlatSE;
/* 16:   */ 
/* 17:   */ public class VeinProfile
/* 18:   */   extends Algorithm
/* 19:   */ {
/* 20:49 */   public double[] output = null;
/* 21:50 */   public Image input = null;
/* 22:51 */   public Integer size = null;
/* 23:52 */   public Integer initialLength = null;
/* 24:53 */   public Integer step = null;
/* 25:   */   
/* 26:   */   public VeinProfile()
/* 27:   */   {
/* 28:56 */     this.inputFields = "input, size, initialLength, step";
/* 29:57 */     this.outputFields = "output";
/* 30:   */   }
/* 31:   */   
/* 32:   */   public void execute()
/* 33:   */     throws GlobalException
/* 34:   */   {
/* 35:61 */     if (this.input.getCDim() != 1) {
/* 36:61 */       throw new GlobalException("Sorry, only mono-channel images for now...");
/* 37:   */     }
/* 38:64 */     Image mask = Threshold.invoke(this.input, 0.00392156862745098D);
/* 39:   */     
/* 40:   */ 
/* 41:67 */     Image outerBorders = BGradient.invoke(mask, FlatSE.square(7));
/* 42:68 */     outerBorders = Inversion.invoke(outerBorders);
/* 43:   */     
/* 44:   */ 
/* 45:71 */     this.input = GOpeningByReconstruction.invoke(this.input, FlatSE.square(3));
/* 46:72 */     this.input = GClosingByReconstruction.invoke(this.input, FlatSE.square(3));
/* 47:   */     
/* 48:74 */     Image veins = GInternGradient.invoke(this.input, FlatSE.cross(3));
/* 49:   */     
/* 50:76 */     veins = Minimum.invoke(veins, outerBorders);
/* 51:77 */     double originalVol = Tools.volume(veins, 0);
/* 52:   */     
/* 53:79 */     this.output = new double[this.size.intValue()];
/* 54:81 */     for (int i = 0; i < this.size.intValue(); i++)
/* 55:   */     {
/* 56:83 */       Image tmp = GPathOpening.invoke(veins, Integer.valueOf(this.initialLength.intValue() + this.step.intValue() * i));
/* 57:84 */       this.output[i] = (Tools.volume(tmp, 0) / originalVol);
/* 58:   */     }
/* 59:   */   }
/* 60:   */   
/* 61:   */   public static double[] invoke(Image img, Integer size, Integer initialLen, Integer step)
/* 62:   */   {
/* 63:   */     try
/* 64:   */     {
/* 65:91 */       return (double[])new VeinProfile().preprocess(new Object[] { img, size, initialLen, step });
/* 66:   */     }
/* 67:   */     catch (GlobalException e)
/* 68:   */     {
/* 69:93 */       e.printStackTrace();
/* 70:   */     }
/* 71:94 */     return null;
/* 72:   */   }
/* 73:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.plants.VeinProfile
 * JD-Core Version:    0.7.0.1
 */