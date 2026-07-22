/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.io.Load;
/*  8:   */ import vpt.util.Tools;
/*  9:   */ 
/* 10:   */ public class Orientation
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:17 */   public Double output = null;
/* 14:18 */   public Image input = null;
/* 15:   */   
/* 16:   */   public Orientation()
/* 17:   */   {
/* 18:21 */     this.inputFields = "input";
/* 19:22 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:27 */     if (this.input.getCDim() != 1) {
/* 26:27 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 27:   */     }
/* 28:29 */     double m00 = vpt.algorithms.statistical.CentralMoment.invoke(this.input, java.lang.Integer.valueOf(0), java.lang.Integer.valueOf(0))[0];
/* 29:30 */     double m11 = vpt.algorithms.statistical.CentralMoment.invoke(this.input, java.lang.Integer.valueOf(1), java.lang.Integer.valueOf(1))[0];
/* 30:31 */     double m02 = vpt.algorithms.statistical.CentralMoment.invoke(this.input, java.lang.Integer.valueOf(0), java.lang.Integer.valueOf(2))[0];
/* 31:32 */     double m20 = vpt.algorithms.statistical.CentralMoment.invoke(this.input, java.lang.Integer.valueOf(2), java.lang.Integer.valueOf(0))[0];
/* 32:   */     
/* 33:34 */     this.output = Double.valueOf(0.5D * Math.atan2(2.0D * m11 / m00, m20 / m00 - m02 / m00));
/* 34:   */   }
/* 35:   */   
/* 36:   */   public static Double invoke(Image img)
/* 37:   */   {
/* 38:   */     try
/* 39:   */     {
/* 40:40 */       return (Double)new Orientation().preprocess(new Object[] { img });
/* 41:   */     }
/* 42:   */     catch (GlobalException e)
/* 43:   */     {
/* 44:42 */       e.printStackTrace();
/* 45:   */     }
/* 46:43 */     return null;
/* 47:   */   }
/* 48:   */   
/* 49:   */   public static void main(String[] args)
/* 50:   */   {
/* 51:48 */     Image img = Load.invoke("samples/shape2.png");
/* 52:49 */     System.err.println(Tools.radian2degree(invoke(img).doubleValue()));
/* 53:   */     
/* 54:51 */     img = Load.invoke("samples/shape3.png");
/* 55:52 */     System.err.println(Tools.radian2degree(invoke(img).doubleValue()));
/* 56:   */     
/* 57:54 */     img = Load.invoke("samples/shape4.png");
/* 58:55 */     System.err.println(Tools.radian2degree(invoke(img).doubleValue()));
/* 59:   */   }
/* 60:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.Orientation
 * JD-Core Version:    0.7.0.1
 */