/*  1:   */ package vpt.algorithms.flatzones.angular;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.conversion.Hue2Distance;
/*  7:   */ import vpt.algorithms.flatzones.gray.GrayQFZ;
/*  8:   */ 
/*  9:   */ public class LabelBasedAngularQFZ
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:   */   public Image input;
/* 13:   */   public Image output;
/* 14:   */   public Integer alpha;
/* 15:   */   public Integer omega;
/* 16:   */   public Double refHue;
/* 17:   */   
/* 18:   */   public LabelBasedAngularQFZ()
/* 19:   */   {
/* 20:29 */     this.inputFields = "input,alpha,omega,refHue";
/* 21:30 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:35 */     if (this.input.getCDim() != 1) {
/* 28:35 */       throw new GlobalException("This implementation is only for monochannel angular data images.");
/* 29:   */     }
/* 30:36 */     if ((this.alpha.intValue() <= 0) || (this.alpha.intValue() >= 255) || (this.omega.intValue() <= 0) || (this.omega.intValue() >= 255)) {
/* 31:36 */       throw new GlobalException("Arguments out of range.");
/* 32:   */     }
/* 33:38 */     Image dist = Hue2Distance.invoke(this.input.getChannel(0), this.refHue);
/* 34:39 */     this.output = GrayQFZ.invoke(dist, this.alpha.intValue(), this.omega.intValue());
/* 35:   */   }
/* 36:   */   
/* 37:   */   public static Image invoke(Image img, int alpha, int omega, double refHue)
/* 38:   */   {
/* 39:   */     try
/* 40:   */     {
/* 41:45 */       return (Image)new LabelBasedAngularQFZ().preprocess(new Object[] { img, Integer.valueOf(alpha), Integer.valueOf(omega), Double.valueOf(refHue) });
/* 42:   */     }
/* 43:   */     catch (GlobalException e)
/* 44:   */     {
/* 45:47 */       e.printStackTrace();
/* 46:   */     }
/* 47:48 */     return null;
/* 48:   */   }
/* 49:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.flatzones.angular.LabelBasedAngularQFZ
 * JD-Core Version:    0.7.0.1
 */