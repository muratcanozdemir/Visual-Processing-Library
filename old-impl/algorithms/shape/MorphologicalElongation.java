/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.binary.BErosion;
/*  7:   */ import vpt.util.Tools;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class MorphologicalElongation
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:20 */   public Double output = null;
/* 14:21 */   public Image input = null;
/* 15:22 */   public FlatSE se = null;
/* 16:   */   
/* 17:   */   public MorphologicalElongation()
/* 18:   */   {
/* 19:25 */     this.inputFields = "input, se";
/* 20:26 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:30 */     int cdim = this.input.getCDim();
/* 27:32 */     if (cdim != 1) {
/* 28:32 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 29:   */     }
/* 30:38 */     Image tmp = this.input.newInstance(true);
/* 31:   */     
/* 32:40 */     double area = Tools.volume(this.input, 0);
/* 33:41 */     int syc = 0;
/* 34:43 */     while (!tmp.isEmpty(0))
/* 35:   */     {
/* 36:44 */       syc++;
/* 37:45 */       tmp = BErosion.invoke(tmp, this.se);
/* 38:   */     }
/* 39:48 */     this.output = Double.valueOf(area / (4 * syc * syc));
/* 40:   */   }
/* 41:   */   
/* 42:   */   public static Double invoke(Image img, FlatSE se)
/* 43:   */   {
/* 44:   */     try
/* 45:   */     {
/* 46:54 */       return (Double)new MorphologicalElongation().preprocess(new Object[] { img, se });
/* 47:   */     }
/* 48:   */     catch (GlobalException e)
/* 49:   */     {
/* 50:56 */       e.printStackTrace();
/* 51:   */     }
/* 52:57 */     return null;
/* 53:   */   }
/* 54:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.MorphologicalElongation
 * JD-Core Version:    0.7.0.1
 */