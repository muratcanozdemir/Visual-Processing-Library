/*  1:   */ package vpt.algorithms.noise;
/*  2:   */ 
/*  3:   */ import java.util.Random;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class Speckle
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:17 */   public Image output = null;
/* 12:18 */   public Image input = null;
/* 13:19 */   public Double propability = null;
/* 14:   */   
/* 15:   */   public Speckle()
/* 16:   */   {
/* 17:22 */     this.inputFields = "input, propability";
/* 18:23 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:28 */     Random rand = new Random();
/* 25:29 */     this.output = this.input.newInstance(true);
/* 26:31 */     for (int i = 0; i < this.output.getSize(); i++) {
/* 27:32 */       if (rand.nextDouble() < this.propability.doubleValue()) {
/* 28:33 */         if (rand.nextDouble() < 0.5D) {
/* 29:34 */           this.output.setDouble(i, 1.0D);
/* 30:   */         } else {
/* 31:36 */           this.output.setDouble(i, 0.0D);
/* 32:   */         }
/* 33:   */       }
/* 34:   */     }
/* 35:   */   }
/* 36:   */   
/* 37:   */   public static Image invoke(Image image, Double propability)
/* 38:   */   {
/* 39:   */     try
/* 40:   */     {
/* 41:46 */       return (Image)new Speckle().preprocess(new Object[] { image, propability });
/* 42:   */     }
/* 43:   */     catch (GlobalException e)
/* 44:   */     {
/* 45:48 */       e.printStackTrace();
/* 46:   */     }
/* 47:49 */     return null;
/* 48:   */   }
/* 49:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.noise.Speckle
 * JD-Core Version:    0.7.0.1
 */