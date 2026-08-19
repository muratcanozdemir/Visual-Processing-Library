/*  1:   */ package vpt.algorithms.geometric;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import java.util.Vector;
/*  6:   */ import org.concord.swing.QuickHull;
/*  7:   */ import vpt.Algorithm;
/*  8:   */ import vpt.BooleanImage;
/*  9:   */ import vpt.GlobalException;
/* 10:   */ import vpt.Image;
/* 11:   */ import vpt.algorithms.mm.binary.BDilation;
/* 12:   */ import vpt.algorithms.mm.binary.BInternGradient;
/* 13:   */ import vpt.algorithms.mm.binary.geo.BFillHole;
/* 14:   */ import vpt.util.Tools;
/* 15:   */ import vpt.util.se.FlatSE;
/* 16:   */ 
/* 17:   */ public class ConvexHull
/* 18:   */   extends Algorithm
/* 19:   */ {
/* 20:28 */   public BooleanImage output = null;
/* 21:29 */   public Image input = null;
/* 22:30 */   public Boolean filled = null;
/* 23:   */   
/* 24:   */   public ConvexHull()
/* 25:   */   {
/* 26:33 */     this.inputFields = "input, filled";
/* 27:34 */     this.outputFields = "output";
/* 28:   */   }
/* 29:   */   
/* 30:   */   public void execute()
/* 31:   */     throws GlobalException
/* 32:   */   {
/* 33:38 */     int cdim = this.input.getCDim();
/* 34:39 */     int xdim = this.input.getXDim();
/* 35:40 */     int ydim = this.input.getYDim();
/* 36:42 */     if (cdim != 1) {
/* 37:42 */       throw new GlobalException("Only mono-channel images for now...");
/* 38:   */     }
/* 39:44 */     this.output = new BooleanImage(this.input, false);
/* 40:   */     
/* 41:46 */     Image borderImage = BInternGradient.invoke(this.input, FlatSE.square(3));
/* 42:   */     
/* 43:48 */     double numberOfPoints = Tools.volume(borderImage, 0);
/* 44:49 */     Point[] border = new Point[(int)numberOfPoints];
/* 45:50 */     int cnt = 0;
/* 46:52 */     for (int x = 0; x < xdim; x++) {
/* 47:53 */       for (int y = 0; y < ydim; y++)
/* 48:   */       {
/* 49:54 */         boolean p = borderImage.getXYBoolean(x, y);
/* 50:55 */         if (p) {
/* 51:55 */           border[(cnt++)] = new Point(x, y);
/* 52:   */         }
/* 53:   */       }
/* 54:   */     }
/* 55:59 */     QuickHull qh = new QuickHull(border);
/* 56:60 */     Vector<?> hull = qh.getHullPointsAsVector();
/* 57:   */     
/* 58:62 */     int hullSize = hull.size();
/* 59:64 */     for (int i = 0; i < hullSize; i++)
/* 60:   */     {
/* 61:65 */       Point p1 = (Point)hull.get(i);
/* 62:66 */       Point p2 = (Point)hull.get((i + 1) % hullSize);
/* 63:67 */       ArrayList<Point> line = Tools.drawLine(p1, p2);
/* 64:69 */       for (int j = 0; j < line.size(); j++) {
/* 65:70 */         this.output.setXYBoolean(((Point)line.get(j)).x, ((Point)line.get(j)).y, true);
/* 66:   */       }
/* 67:   */     }
/* 68:73 */     if (this.filled.booleanValue())
/* 69:   */     {
/* 70:74 */       this.output = ((BooleanImage)BDilation.invoke(this.output, FlatSE.square(3)));
/* 71:75 */       this.output = ((BooleanImage)BFillHole.invoke(this.output));
/* 72:   */     }
/* 73:   */   }
/* 74:   */   
/* 75:   */   public static Image invoke(Image image, Boolean filled)
/* 76:   */   {
/* 77:   */     try
/* 78:   */     {
/* 79:82 */       return (Image)new ConvexHull().preprocess(new Object[] { image, filled });
/* 80:   */     }
/* 81:   */     catch (GlobalException e)
/* 82:   */     {
/* 83:84 */       e.printStackTrace();
/* 84:   */     }
/* 85:85 */     return null;
/* 86:   */   }
/* 87:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.geometric.ConvexHull
 * JD-Core Version:    0.7.0.1
 */