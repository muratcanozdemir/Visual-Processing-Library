/*  1:   */ package vpt.algorithms.mm.gray.connected;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.gray.connected.attribute.Attribute;
/*  7:   */ import vpt.algorithms.point.Inversion;
/*  8:   */ 
/*  9:   */ public class GMaxTreeIncrMultiAttrClosing
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:20 */   public Image input = null;
/* 13:21 */   public Integer channel = null;
/* 14:22 */   public Attribute crit = null;
/* 15:23 */   public Image output = null;
/* 16:   */   
/* 17:   */   public GMaxTreeIncrMultiAttrClosing()
/* 18:   */   {
/* 19:26 */     this.inputFields = "input, channel, crit";
/* 20:27 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:32 */     this.output = Inversion.invoke(GMaxTreeIncrMultiAttrOpening.invoke(Inversion.invoke(this.input), this.channel, this.crit));
/* 27:   */   }
/* 28:   */   
/* 29:   */   public static Image invoke(Image input, Integer channel, Attribute c)
/* 30:   */   {
/* 31:   */     try
/* 32:   */     {
/* 33:37 */       return (Image)new GMaxTreeIncrMultiAttrClosing().preprocess(new Object[] { input, channel, c });
/* 34:   */     }
/* 35:   */     catch (GlobalException e)
/* 36:   */     {
/* 37:39 */       e.printStackTrace();
/* 38:   */     }
/* 39:40 */     return null;
/* 40:   */   }
/* 41:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.GMaxTreeIncrMultiAttrClosing
 * JD-Core Version:    0.7.0.1
 */