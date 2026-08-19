/*  1:   */ package vpt.algorithms.mm.multi.connected;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.multi.connected.attribute.MultiAttribute;
/*  7:   */ import vpt.algorithms.point.Inversion;
/*  8:   */ import vpt.util.ordering.AlgebraicOrdering;
/*  9:   */ 
/* 10:   */ public class MMaxTreeIncrAttrClosing
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:21 */   public Image input = null;
/* 14:22 */   public MultiAttribute crit = null;
/* 15:23 */   public Image output = null;
/* 16:24 */   public AlgebraicOrdering vo = null;
/* 17:   */   
/* 18:   */   public MMaxTreeIncrAttrClosing()
/* 19:   */   {
/* 20:27 */     this.inputFields = "input, crit, vo";
/* 21:28 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:33 */     this.output = Inversion.invoke(MMaxTreeIncrAttrOpening.invoke(Inversion.invoke(this.input), this.crit, this.vo));
/* 28:   */   }
/* 29:   */   
/* 30:   */   public static Image invoke(Image input, MultiAttribute c, AlgebraicOrdering vo)
/* 31:   */   {
/* 32:   */     try
/* 33:   */     {
/* 34:38 */       return (Image)new MMaxTreeIncrAttrClosing().preprocess(new Object[] { input, c, vo });
/* 35:   */     }
/* 36:   */     catch (GlobalException e)
/* 37:   */     {
/* 38:40 */       e.printStackTrace();
/* 39:   */     }
/* 40:41 */     return null;
/* 41:   */   }
/* 42:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.MMaxTreeIncrAttrClosing
 * JD-Core Version:    0.7.0.1
 */