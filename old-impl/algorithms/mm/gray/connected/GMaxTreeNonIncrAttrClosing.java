/*  1:   */ package vpt.algorithms.mm.gray.connected;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.gray.connected.attribute.Attribute;
/*  7:   */ import vpt.algorithms.point.Inversion;
/*  8:   */ 
/*  9:   */ public class GMaxTreeNonIncrAttrClosing
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:20 */   public Image input = null;
/* 13:21 */   public Attribute crit = null;
/* 14:22 */   public Image output = null;
/* 15:23 */   public Integer filteringStrategy = null;
/* 16:   */   
/* 17:   */   public GMaxTreeNonIncrAttrClosing()
/* 18:   */   {
/* 19:26 */     this.inputFields = "input, crit, filteringStrategy";
/* 20:27 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:31 */     this.output = Inversion.invoke(GMaxTreeNonIncrAttrOpening.invoke(Inversion.invoke(this.input), this.crit, this.filteringStrategy));
/* 27:   */   }
/* 28:   */   
/* 29:   */   public static Image invoke(Image input, Attribute c)
/* 30:   */   {
/* 31:   */     try
/* 32:   */     {
/* 33:36 */       return (Image)new GMaxTreeNonIncrAttrClosing().preprocess(new Object[] { input, c, null });
/* 34:   */     }
/* 35:   */     catch (GlobalException e)
/* 36:   */     {
/* 37:38 */       e.printStackTrace();
/* 38:   */     }
/* 39:39 */     return null;
/* 40:   */   }
/* 41:   */   
/* 42:   */   public static Image invoke(Image input, Attribute c, Integer filteringStrategy)
/* 43:   */   {
/* 44:   */     try
/* 45:   */     {
/* 46:45 */       return (Image)new GMaxTreeNonIncrAttrClosing().preprocess(new Object[] { input, c, filteringStrategy });
/* 47:   */     }
/* 48:   */     catch (GlobalException e)
/* 49:   */     {
/* 50:47 */       e.printStackTrace();
/* 51:   */     }
/* 52:48 */     return null;
/* 53:   */   }
/* 54:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.GMaxTreeNonIncrAttrClosing
 * JD-Core Version:    0.7.0.1
 */