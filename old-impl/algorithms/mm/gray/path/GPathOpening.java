/*  1:   */ package vpt.algorithms.mm.gray.path;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.BooleanImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.display.Display2D;
/*  8:   */ import vpt.algorithms.io.Load;
/*  9:   */ import vpt.algorithms.mm.binary.path.BPathOpening;
/* 10:   */ import vpt.algorithms.segmentation.Threshold;
/* 11:   */ 
/* 12:   */ public class GPathOpening
/* 13:   */   extends Algorithm
/* 14:   */ {
/* 15:25 */   public Image output = null;
/* 16:26 */   public Image input = null;
/* 17:27 */   public Integer length = null;
/* 18:   */   
/* 19:   */   public GPathOpening()
/* 20:   */   {
/* 21:30 */     this.inputFields = "input,length";
/* 22:31 */     this.outputFields = "output";
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void execute()
/* 26:   */     throws GlobalException
/* 27:   */   {
/* 28:36 */     this.output = this.input.newInstance(false);
/* 29:37 */     this.output.fill(0);
/* 30:39 */     for (int t = 1; t < 256; t++)
/* 31:   */     {
/* 32:41 */       BooleanImage stackLevel = Threshold.invoke(this.input, 0.00392156862745098D * t);
/* 33:   */       
/* 34:43 */       stackLevel = (BooleanImage)BPathOpening.invoke(stackLevel, this.length);
/* 35:45 */       for (int p = 0; p < stackLevel.size(); p++) {
/* 36:46 */         if (stackLevel.getBoolean(p)) {
/* 37:47 */           this.output.setByte(p, t);
/* 38:   */         }
/* 39:   */       }
/* 40:   */     }
/* 41:   */   }
/* 42:   */   
/* 43:   */   public static Image invoke(Image image, Integer length)
/* 44:   */   {
/* 45:   */     try
/* 46:   */     {
/* 47:55 */       return (Image)new GPathOpening().preprocess(new Object[] { image, length });
/* 48:   */     }
/* 49:   */     catch (GlobalException e)
/* 50:   */     {
/* 51:57 */       e.printStackTrace();
/* 52:   */     }
/* 53:58 */     return null;
/* 54:   */   }
/* 55:   */   
/* 56:   */   public static void main(String[] args)
/* 57:   */   {
/* 58:63 */     Image img = Load.invoke("samples/grayPaths.png");
/* 59:64 */     Display2D.invoke(img);
/* 60:   */     
/* 61:66 */     Image tmp = invoke(img, Integer.valueOf(30));
/* 62:   */     
/* 63:68 */     Display2D.invoke(tmp, "30");
/* 64:   */   }
/* 65:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.path.GPathOpening
 * JD-Core Version:    0.7.0.1
 */