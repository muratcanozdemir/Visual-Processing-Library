/*  1:   */ package vpt.algorithms.noise;
/*  2:   */ 
/*  3:   */ import java.util.Random;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class Gaussian
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:18 */   public Image output = null;
/* 12:19 */   public Image input = null;
/* 13:20 */   public Double deviation = null;
/* 14:   */   
/* 15:   */   public Gaussian()
/* 16:   */   {
/* 17:23 */     this.inputFields = "input, deviation";
/* 18:24 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:29 */     Random rand = new Random();
/* 25:30 */     this.output = this.input.newInstance(true);
/* 26:32 */     for (int i = 0; i < this.output.getSize(); i++)
/* 27:   */     {
/* 28:33 */       double p = this.output.getDouble(i);
/* 29:34 */       p += this.deviation.doubleValue() * rand.nextGaussian();
/* 30:35 */       p = Math.max(0.0D, Math.min(p, 1.0D));
/* 31:36 */       this.output.setDouble(i, p);
/* 32:   */     }
/* 33:   */   }
/* 34:   */   
/* 35:   */   public static Image invoke(Image image, Double deviation)
/* 36:   */   {
/* 37:   */     try
/* 38:   */     {
/* 39:44 */       return (Image)new Gaussian().preprocess(new Object[] { image, deviation });
/* 40:   */     }
/* 41:   */     catch (GlobalException e)
/* 42:   */     {
/* 43:46 */       e.printStackTrace();
/* 44:   */     }
/* 45:47 */     return null;
/* 46:   */   }
/* 47:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.noise.Gaussian
 * JD-Core Version:    0.7.0.1
 */