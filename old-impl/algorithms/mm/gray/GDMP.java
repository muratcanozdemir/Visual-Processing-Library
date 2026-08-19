/*  1:   */ package vpt.algorithms.mm.gray;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.AbsSubtraction;
/*  7:   */ import vpt.algorithms.mm.gray.geo.FastGrayReconstruction;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class GDMP
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:18 */   public Image[] output = null;
/* 14:19 */   public Image input = null;
/* 15:20 */   public Integer size = null;
/* 16:   */   
/* 17:   */   public GDMP()
/* 18:   */   {
/* 19:23 */     this.inputFields = "input,size";
/* 20:24 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:29 */     if (this.size.intValue() <= 0) {
/* 27:29 */       throw new GlobalException("Invalid DMP size, must be > 0");
/* 28:   */     }
/* 29:31 */     this.output = new Image[this.size.intValue() * 2];
/* 30:   */     
/* 31:33 */     FlatSE se = FlatSE.cross(3);
/* 32:   */     
/* 33:   */ 
/* 34:36 */     Image marker = this.input.newInstance(true);
/* 35:37 */     Image previous = this.input;
/* 36:39 */     for (int i = 0; i < this.size.intValue(); i++)
/* 37:   */     {
/* 38:40 */       marker = GErosion.invoke(marker, se);
/* 39:   */       
/* 40:42 */       Image current = FastGrayReconstruction.invoke(marker, this.input, FastGrayReconstruction.CONNEXITY8, false);
/* 41:   */       
/* 42:44 */       this.output[(this.size.intValue() + i)] = AbsSubtraction.invoke(previous, current);
/* 43:   */       
/* 44:46 */       previous = current;
/* 45:   */     }
/* 46:50 */     marker = this.input.newInstance(true);
/* 47:51 */     previous = this.input;
/* 48:53 */     for (int i = 0; i < this.size.intValue(); i++)
/* 49:   */     {
/* 50:54 */       marker = GDilation.invoke(marker, se);
/* 51:   */       
/* 52:56 */       Image current = FastGrayReconstruction.invoke(marker, this.input, FastGrayReconstruction.CONNEXITY8, true);
/* 53:   */       
/* 54:58 */       this.output[i] = AbsSubtraction.invoke(previous, current);
/* 55:   */       
/* 56:60 */       previous = current;
/* 57:   */     }
/* 58:   */   }
/* 59:   */   
/* 60:   */   public static Image[] invoke(Image image, Integer size)
/* 61:   */   {
/* 62:   */     try
/* 63:   */     {
/* 64:67 */       return (Image[])new GDMP().preprocess(new Object[] { image, size });
/* 65:   */     }
/* 66:   */     catch (GlobalException e)
/* 67:   */     {
/* 68:69 */       e.printStackTrace();
/* 69:   */     }
/* 70:70 */     return null;
/* 71:   */   }
/* 72:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.GDMP
 * JD-Core Version:    0.7.0.1
 */