/*  1:   */ package vpt.algorithms.mm.gray.connected;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.gray.connected.attribute.Attribute;
/*  7:   */ import vpt.algorithms.point.Inversion;
/*  8:   */ 
/*  9:   */ public class GMaxTreeIncrAttrClosing
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:20 */   public Image input = null;
/* 13:21 */   public Attribute crit = null;
/* 14:22 */   public Image output = null;
/* 15:   */   
/* 16:   */   public GMaxTreeIncrAttrClosing()
/* 17:   */   {
/* 18:25 */     this.inputFields = "input, crit";
/* 19:26 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:31 */     this.output = Inversion.invoke(GMaxTreeIncrAttrOpening.invoke(Inversion.invoke(this.input), this.crit));
/* 26:   */   }
/* 27:   */   
/* 28:   */   public static Image invoke(Image input, Attribute c)
/* 29:   */   {
/* 30:   */     try
/* 31:   */     {
/* 32:36 */       return (Image)new GMaxTreeIncrAttrClosing().preprocess(new Object[] { input, c });
/* 33:   */     }
/* 34:   */     catch (GlobalException e)
/* 35:   */     {
/* 36:38 */       e.printStackTrace();
/* 37:   */     }
/* 38:39 */     return null;
/* 39:   */   }
/* 40:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.GMaxTreeIncrAttrClosing
 * JD-Core Version:    0.7.0.1
 */