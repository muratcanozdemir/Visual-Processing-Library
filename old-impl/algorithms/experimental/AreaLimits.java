/*  1:   */ package vpt.algorithms.experimental;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import vpt.Algorithm;
/*  6:   */ import vpt.GlobalException;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.algorithms.flatzones.gray.GrayFZ2;
/*  9:   */ 
/* 10:   */ public class AreaLimits
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:20 */   public Image output = null;
/* 14:21 */   public Integer ulimit = null;
/* 15:22 */   public Integer llimit = null;
/* 16:23 */   public Image input = null;
/* 17:   */   private int xdim;
/* 18:   */   private int ydim;
/* 19:   */   
/* 20:   */   public AreaLimits()
/* 21:   */   {
/* 22:29 */     this.inputFields = "input, ulimit, llimit";
/* 23:30 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:35 */     this.xdim = this.input.getXDim();
/* 30:36 */     this.ydim = this.input.getYDim();
/* 31:37 */     int cdim = this.input.getCDim();
/* 32:39 */     if (cdim != 1) {
/* 33:39 */       throw new GlobalException("For now only grayscale images...");
/* 34:   */     }
/* 35:41 */     this.output = this.input.newInstance(true);
/* 36:   */     
/* 37:   */ 
/* 38:44 */     ArrayList<ArrayList<Point>> qfz = GrayFZ2.invoke(this.input);
/* 39:46 */     for (int i = 0; i < qfz.size(); i++)
/* 40:   */     {
/* 41:47 */       ArrayList<Point> tmp = (ArrayList)qfz.get(i);
/* 42:49 */       if ((tmp.size() > this.ulimit.intValue()) || (tmp.size() < this.llimit.intValue())) {
/* 43:51 */         for (Point p : tmp) {
/* 44:52 */           this.output.setXYByte(p.x, p.y, 0);
/* 45:   */         }
/* 46:   */       }
/* 47:   */     }
/* 48:   */   }
/* 49:   */   
/* 50:   */   public static Image invoke(Image input, Integer ulimit, Integer llimit)
/* 51:   */   {
/* 52:   */     try
/* 53:   */     {
/* 54:62 */       return (Image)new AreaLimits().preprocess(new Object[] { input, ulimit, llimit });
/* 55:   */     }
/* 56:   */     catch (GlobalException e)
/* 57:   */     {
/* 58:64 */       e.printStackTrace();
/* 59:   */     }
/* 60:65 */     return null;
/* 61:   */   }
/* 62:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.experimental.AreaLimits
 * JD-Core Version:    0.7.0.1
 */