/*  1:   */ package vpt.algorithms.mm.gray;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.Addition;
/*  7:   */ import vpt.algorithms.arithmetic.Subtraction;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class GContrastEnhancement
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:17 */   public Image output = null;
/* 14:18 */   public Image input = null;
/* 15:19 */   public FlatSE se = null;
/* 16:   */   
/* 17:   */   public GContrastEnhancement()
/* 18:   */   {
/* 19:22 */     this.inputFields = "input,se";
/* 20:23 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:28 */     this.output = Addition.invoke(this.input, GTopHat.invoke(this.input, this.se));
/* 27:29 */     this.output = Subtraction.invoke(this.output, GBotHat.invoke(this.input, this.se));
/* 28:   */   }
/* 29:   */   
/* 30:   */   public static Image invoke(Image image, FlatSE se)
/* 31:   */   {
/* 32:   */     try
/* 33:   */     {
/* 34:34 */       return (Image)new GContrastEnhancement().preprocess(new Object[] { image, se });
/* 35:   */     }
/* 36:   */     catch (GlobalException e)
/* 37:   */     {
/* 38:36 */       e.printStackTrace();
/* 39:   */     }
/* 40:37 */     return null;
/* 41:   */   }
/* 42:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.GContrastEnhancement
 * JD-Core Version:    0.7.0.1
 */