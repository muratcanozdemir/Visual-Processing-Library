/*  1:   */ package vpt.algorithms.mm.multi;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.AbsSubtraction;
/*  7:   */ import vpt.algorithms.mm.multi.geo.MFastReconstruction;
/*  8:   */ import vpt.util.ordering.AlgebraicOrdering;
/*  9:   */ import vpt.util.se.FlatSE;
/* 10:   */ 
/* 11:   */ public class MDMP
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:19 */   public Image[] output = null;
/* 15:20 */   public Image input = null;
/* 16:21 */   public Integer size = null;
/* 17:22 */   public AlgebraicOrdering or = null;
/* 18:   */   
/* 19:   */   public MDMP()
/* 20:   */   {
/* 21:25 */     this.inputFields = "input,size,or";
/* 22:26 */     this.outputFields = "output";
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void execute()
/* 26:   */     throws GlobalException
/* 27:   */   {
/* 28:31 */     if (this.size.intValue() <= 0) {
/* 29:31 */       throw new GlobalException("Invalid DMP size, must be > 0");
/* 30:   */     }
/* 31:33 */     this.output = new Image[this.size.intValue() * 2];
/* 32:   */     
/* 33:35 */     FlatSE se = FlatSE.cross(3);
/* 34:   */     
/* 35:   */ 
/* 36:38 */     Image marker = this.input.newInstance(true);
/* 37:39 */     Image previous = this.input;
/* 38:41 */     for (int i = 0; i < this.size.intValue(); i++)
/* 39:   */     {
/* 40:42 */       marker = MErosion.invoke(marker, se, this.or);
/* 41:   */       
/* 42:44 */       Image current = MFastReconstruction.invoke(marker, this.input, MFastReconstruction.CONNEXITY8, false, this.or);
/* 43:   */       
/* 44:46 */       this.output[(this.size.intValue() + i)] = AbsSubtraction.invoke(previous, current);
/* 45:   */       
/* 46:48 */       previous = current;
/* 47:   */     }
/* 48:52 */     marker = this.input.newInstance(true);
/* 49:53 */     previous = this.input;
/* 50:55 */     for (int i = 0; i < this.size.intValue(); i++)
/* 51:   */     {
/* 52:56 */       marker = MDilation.invoke(marker, se, this.or);
/* 53:   */       
/* 54:58 */       Image current = MFastReconstruction.invoke(marker, this.input, MFastReconstruction.CONNEXITY8, true, this.or);
/* 55:   */       
/* 56:60 */       this.output[i] = AbsSubtraction.invoke(previous, current);
/* 57:   */       
/* 58:62 */       previous = current;
/* 59:   */     }
/* 60:   */   }
/* 61:   */   
/* 62:   */   public static Image[] invoke(Image image, Integer size, AlgebraicOrdering or)
/* 63:   */   {
/* 64:   */     try
/* 65:   */     {
/* 66:69 */       return (Image[])new MDMP().preprocess(new Object[] { image, size, or });
/* 67:   */     }
/* 68:   */     catch (GlobalException e)
/* 69:   */     {
/* 70:71 */       e.printStackTrace();
/* 71:   */     }
/* 72:72 */     return null;
/* 73:   */   }
/* 74:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.MDMP
 * JD-Core Version:    0.7.0.1
 */